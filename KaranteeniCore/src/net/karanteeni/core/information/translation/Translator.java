package net.karanteeni.core.information.translation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.players.KPlayer;

public class Translator {
	
	public static enum TranslationType {
		STATIC,
		RANDOM,
		UNKNOWN
	}
	
	//Locales used by this plugin
	private final List<Locale> locales = new ArrayList<Locale>();
	private final List<String> localesString = new ArrayList<String>(); 
	private Locale defaultLocale = null;
	private KaranteeniCore plugin = null;
	private static final String configLocalesTag = "Locales";
	private static final String LANGUAGE = "language";
	private static String LANG_SPLITTER = "-";
	private static final String KEY_FORMAT = "%s.%s.%s"; // plugin-language-key
	private final HashMap<String, String> translations = new HashMap<String, String>();
	private final HashMap<String, List<String>> randomTranslations = new HashMap<String, List<String>>();
	
	/**
	 * Initializes the default languages which are always used
	 */
	public Translator() {
		plugin = KaranteeniCore.getPlugin(KaranteeniCore.class);
		initConfig();
		loadLocales();
	}
	
	
	/**
	 * Load the locales from config
	 */
	private void loadLocales() {
		List<String> lcs = plugin.getConfig().getStringList(configLocalesTag);
		boolean first = true;
		for(String locale : lcs) {
			String[] l = locale.split(LANG_SPLITTER);
			
			if(l.length == 2) {
				locales.add(new Locale(l[0], l[1]));
				localesString.add(l[0]+LANG_SPLITTER+l[1]);
				
				if(first) {
					defaultLocale = locales.get(0);
					first = false;
				}
			}
			else //Log illegal config log
				Bukkit.getLogger().log(
						Level.CONFIG, 
						"Illegal locale variable \""+locale+"\"! Please use format x-x!");
		}
		
		//Register default locale to usage if no lang is set
		if(locales.size() == 0) {
			locales.add(new Locale("en", "US"));
			localesString.add("en"+LANG_SPLITTER+"US");
			defaultLocale = new Locale("en","US");
		}
	}
	
	
	/**
	 * Loads the translations of the given plugin to memory
	 * @param plugin
	 */
	public void loadPluginTranslations(KaranteeniPlugin plugin) {
		//try {
		for(Entry<Locale, YamlConfiguration> entry : KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin).entrySet()) {
			for(String key : entry.getValue().getKeys(true)) {
				// Accept only values, no sections
				if(entry.getValue().isConfigurationSection(key))
					continue;

				String translationKey = getTranslationKey(plugin, entry.getKey(), key);
				
				if(entry.getValue().isList(key)) {
					List<String> list = new ArrayList<String>();
					
					// apply formatting
					for(String translation : entry.getValue().getStringList(key)) {
						translation = net.karanteeni.core.information.ChatColor.translateHexColorCodes(translation);
						translation = ChatColor.translateAlternateColorCodes(net.karanteeni.core.information.ChatColor.COLOR_CHAR, translation);
						list.add(translation);
					}
					
					this.randomTranslations.put(translationKey, list);
				} else {
					String translation = entry.getValue().getString(key);
					
					// apply formatting
					translation = net.karanteeni.core.information.ChatColor.translateHexColorCodes(translation);
					translation = ChatColor.translateAlternateColorCodes(net.karanteeni.core.information.ChatColor.COLOR_CHAR, translation);
					
					this.translations.put(translationKey, translation);
				}
			}
		}
	}
	
	
	/**
	 * Creates the locales used on server to config
	 */
	private void initConfig() {
		//Get the locales used on server
		if(!plugin.getConfig().isSet(configLocalesTag)) {
			plugin.getConfig().set(configLocalesTag, 
					new ArrayList<String>(Arrays.asList("en"+LANG_SPLITTER+"US")));
			plugin.saveConfig();
		}
	}
	
	
	/**
	 * Returns the default locale used on this server
	 * @return
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}
	
	
	/**
	 * Converts a locale string to locale.
	 * If no locales are found, null is returned
	 * @param loc
	 * @return
	 */
	public Locale getLocale(String loc) {
		if(!this.localesString.contains(loc))
			return null;
		
		String[] parts = loc.split(LANG_SPLITTER);
		
		return this.locales.get(this.locales.indexOf(new Locale(parts[0], parts[1])));
	}
	
	
	/**
	 * Gives all the locales as stringlist used by the plugin in the format
	 * "xx-XX"
	 * @return list of locales as string in format "xx-XX"
	 */
	public List<String> getStringLocales()
	{ return this.localesString; }
	
	
	private String getTranslationKey(KaranteeniPlugin plugin, Locale locale, String key) {
		return String.format(KEY_FORMAT, plugin.getName(), locale.toLanguageTag(), key);
	}
	
	
	public TranslationType getTranslationType(KaranteeniPlugin plugin, Locale locale, String key) {
		String translationKey = getTranslationKey(plugin, locale, key);
		if(this.translations.containsKey(translationKey))
			return TranslationType.STATIC;
		else if(this.randomTranslations.containsKey(translationKey))
			return TranslationType.RANDOM;
		return TranslationType.UNKNOWN;
	}
	
	
	public TranslationType getTranslationType(String key) {
		if(this.translations.containsKey(key))
			return TranslationType.STATIC;
		else if(this.randomTranslations.containsKey(key))
			return TranslationType.RANDOM;
		return TranslationType.UNKNOWN;
	}
	
	
	/**
	 * Registers a new plugin translation to a key
	 * @param plugin
	 * @param key
	 * @param defaultText
	 */
	public void registerTranslation(KaranteeniPlugin plugin, String key, String defaultText) {
		try {
			for(Locale locale : locales) {
				// Add translation to translations
				String translationKey = getTranslationKey(plugin, locale, key);
				if(translations.containsKey(translationKey)) {				
					continue;
				} else {
					translations.put(translationKey, defaultText);
				}
				
				// Add translation to config
				YamlConfiguration config = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
				if(config.isSet(key)) {
					config.set(key, defaultText);
				}
				config.save(KaranteeniCore.getConfigManager().getPluginTranslationYMLFile(plugin, locale));
			}
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Tried to register translation before enabling plugin!");
			Bukkit.getLogger().log(Level.SEVERE, "Error given:", e);
		}
	}
	
	
	/**
	 * Registers a list of translations to one key
	 * @param plugin
	 * @param key
	 * @param defaultText
	 */
	public void registerRandomTranslation(KaranteeniPlugin plugin, String key, String defaultText) {
		try {
			for(Locale locale : locales) {
				// Add translation to translations
				String translationKey = getTranslationKey(plugin, locale, key);
				if(randomTranslations.containsKey(translationKey)) {				
					continue;
				} else {
					randomTranslations.put(translationKey, Arrays.asList(defaultText));
				}
				
				// Add translation to config
				YamlConfiguration config = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
				if(config.isSet(key)) {
					config.set(key, Arrays.asList(defaultText));
				}
				config.save(KaranteeniCore.getConfigManager().getPluginTranslationYMLFile(plugin, locale));
			}
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Tried to register translation before enabling plugin!");
			Bukkit.getLogger().log(Level.SEVERE, "Error given:", e);
		}
	}
	
	
	/**
	 * Sets the translation of given key to the config
	 * @param plugin
	 * @param locale
	 * @param key
	 * @param newValue
	 */
	public boolean setTranslation(KaranteeniPlugin plugin, Locale locale, String key, String newValue) {
		// Add translation to translations
		String translationKey = getTranslationKey(plugin, locale, key);
		if(!this.translations.containsKey(translationKey))
			return false;
		
		String stored = net.karanteeni.core.information.ChatColor.translateHexColorCodes(newValue);
		stored = ChatColor.translateAlternateColorCodes(net.karanteeni.core.information.ChatColor.COLOR_CHAR, stored);
		translations.put(translationKey, stored);
		
		// Add translation to config
		YamlConfiguration config = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		config.set(key, newValue);
		try {
			config.save(KaranteeniCore.getConfigManager().getPluginTranslationYMLFile(plugin, locale));
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save translation, modifications to translation files will be lost.");
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Sets the translation of given key to the config
	 * @param plugin
	 * @param locale
	 * @param key
	 * @param newValue
	 */
	public void addRandomTranslation(KaranteeniPlugin plugin, Locale locale, String key, String newValue) {
		// Add translation to translations
		String translationKey = getTranslationKey(plugin, locale, key);
		List<String> translations = randomTranslations.get(translationKey);
		if(translations != null) {
			String stored = net.karanteeni.core.information.ChatColor.translateHexColorCodes(newValue);
			stored = ChatColor.translateAlternateColorCodes(net.karanteeni.core.information.ChatColor.COLOR_CHAR, stored);
			translations.add(stored);			
		}
		
		// Add translation to config
		YamlConfiguration config = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		config.set(key, translations);
		try {
			config.save(KaranteeniCore.getConfigManager().getPluginTranslationYMLFile(plugin, locale));
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save translation, modifications to translation files will be lost.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Removes a random translation. if only one translation remains it will not be removed, as the plugin requires 
	 * 1 translation at the minimum
	 * @param plugin
	 * @param locale
	 * @param key
	 * @param newValue
	 */
	public boolean removeRandomTranslation(KaranteeniPlugin plugin, Locale locale, String key, int index) {
		// Add translation to translations
		String translationKey = getTranslationKey(plugin, locale, key);
		List<String> translations = randomTranslations.get(translationKey);
		if(translations == null || translations.size() <= 1)
			return false;
		
		if(translations.size() <= index)
			return false;
		
		translations.remove(index);
		
		// Add translation to config
		YamlConfiguration config = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		config.set(key, translations);
		try {
			config.save(KaranteeniCore.getConfigManager().getPluginTranslationYMLFile(plugin, locale));
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save translation, modifications to translation files will be lost.");
			e.printStackTrace();
		}
		return true;
	}
	
	
	public List<String> getAllTranslationKeys() {
		LinkedList<String> keys = new LinkedList<String>(this.randomTranslations.keySet());
		keys.addAll(this.translations.keySet());
		return keys;
	}
	
	
	public List<String> getRandomTranslationKeys() {
		return new LinkedList<String>(this.randomTranslations.keySet());
	}
	
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		String translationKey = getTranslationKey(plugin, locale, key);
		return translations.get(translationKey);
	}
	
	
	/**
	 * Checks if a translation exists with the given locale
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public boolean hasTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		String translationKey = getTranslationKey(plugin, locale, key);
		return translations.containsKey(translationKey);
	}
	
	
	/**
	 * Checks if a translation exists with the given locale
	 * @param plugin
	 * @param sender
	 * @param key
	 * @return
	 */
	public boolean hasTranslation(KaranteeniPlugin plugin, CommandSender sender, String key) {
		if(sender instanceof Player)
			return hasTranslation(plugin, getLocale((Player)sender), key);
		else
			return hasTranslation(plugin, defaultLocale, key);
	}
	
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param player
	 * @param key
	 * @return
	 */
	public String getTranslation(KaranteeniPlugin plugin, CommandSender sender, String key) {
		if(sender instanceof Player)
			return getTranslation(plugin, (Player)sender, key);
		else
			return getTranslation(plugin, defaultLocale, key);
	}
	
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param player
	 * @param key
	 * @return
	 */
	public String getTranslation(KaranteeniPlugin plugin, Player player, String key) {
		String translationKey = getTranslationKey(plugin, getLocale(player), key);
		return translations.get(translationKey);
	}
	
	
	/**
	 * Returns a random translation from a list of translations
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getRandomTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		String translationKey = getTranslationKey(plugin, locale, key);
		List<String> translations = randomTranslations.get(translationKey);
		
		Random r = new Random();
		
		return translations.get((int)r.nextInt(translations.size()));
	}
	
	
	/**
	 * Returns a list of all random translations
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public List<String> getRandomTranslations(KaranteeniPlugin plugin, Locale locale, String key) {
		String translationKey = getTranslationKey(plugin, locale, key);
		return randomTranslations.get(translationKey);
	}
	
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param player
	 * @param key
	 * @return
	 */
	public String getRandomTranslation(KaranteeniPlugin plugin, CommandSender sender, String key) {
		if(sender instanceof Player)
			return getRandomTranslation(plugin, (Player)sender, key);
		else
			return getRandomTranslation(plugin, defaultLocale, key);
	}
	
	
	/**
	 * Returns a random translation from a list of translations
	 * @param plugin
	 * @param player
	 * @param key
	 * @return
	 */
	public String getRandomTranslation(KaranteeniPlugin plugin, Player player, String key) {
		String translationKey = getTranslationKey(plugin, getLocale(player), key);
		List<String> translations = randomTranslations.get(translationKey);
		
		Random r = new Random();
		
		return translations.get((int)r.nextInt(translations.size()));
	}
	
	
	/**
	 * Returns all the locales used by this plugin
	 * @return
	 */
	public final List<Locale> getLocales() {
		return locales;
	}

	
	/**
	 * Initializes the database tables for language saving
	 */
	public static void initTable() {
		Connection conn = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS language(\n"
					+ "UUID VARCHAR(60) NOT NULL,\n"
					+ "locale VARCHAR(10) NOT NULL,\n"
					+ "PRIMARY KEY (UUID),"
					+ "FOREIGN KEY (UUID) REFERENCES player(UUID));");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Loads a players language to the memory
	 * @param player player whose language will be loaded to memory
	 */
	public void loadLocale(UUID uuid) {
		// verify that the player is online
		KPlayer kp = KPlayer.getKPlayer(uuid);
		if(kp == null) return;
		
		Connection conn = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT locale FROM language WHERE UUID = ?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet set = stmt.executeQuery();
			
			// a custom language was found, get and set it
			if(set.next()) {
				String[] parts = set.getString(1).split(LANG_SPLITTER);
				
				if(parts.length == 2)
				kp.setData(KaranteeniCore.getPlugin(KaranteeniCore.class), LANGUAGE, new Locale(parts[0], parts[1]));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Get the language of the given player
	 * @param player player whose language is retrieved
	 * @return language of the player
	 */
	public Locale getLocale(Player player) {
		// verify that the player is online
		if(!player.isOnline()) return this.defaultLocale;
		KPlayer kp = KPlayer.getKPlayer(player);
		if(kp == null) return this.defaultLocale;
		
		Object obj = kp.getData(plugin, LANGUAGE);
		
		// verify that the locale was found from the cache
		if(obj == null || !(obj instanceof Locale)) {
			// if not found take players locale tag or use default locale
			String[] parts = player.getLocale().split("_");
			if(parts.length == 2 && parts[0] != null && parts[1] != null) {
				Locale loc = new Locale(parts[0], parts[1]);
				if(this.locales.contains(loc)) {
					return loc;
				} else {
					return this.defaultLocale;
				}
			}
			return this.defaultLocale;
		} else {
			return (Locale)obj;
		}
	}
	
	
	/**
	 * Sets the language of the given player
	 * @param player player whose language is being set
	 * @param locale locale to set the players language to
	 * @return true if save was successful, false otherwise
	 */
	public boolean setLocale(Player player, Locale locale) {
		boolean online = true;
		
		// verify that the player is online
		if(!player.isOnline()) online = false;
		KPlayer kp;
		if(online) {
			kp = KPlayer.getKPlayer(player);
			if(kp == null) online = false;
			
			// if locale does not exist use the default locale
			if(locale != null && !KaranteeniCore.getTranslator().getLocales().contains(locale)) {
				locale = this.defaultLocale;
			}
			
			// set the locale for the online player
			if(locale != null && online)
				kp.setData(plugin, LANGUAGE, locale);
			else if(online)
				kp.removeData(plugin, LANGUAGE);
		}
		
		Connection conn = null;
		boolean saved = false;
		
		// save the locale to the database
		if(locale != null) {
			try {
				conn = KaranteeniCore.getDatabaseConnector().openConnection();
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO language (UUID, locale) VALUES (?,?) ON DUPLICATE KEY UPDATE locale = ?;");
				stmt.setString(1, player.getUniqueId().toString());
				stmt.setString(2, locale.toLanguageTag());
				stmt.setString(3, locale.toLanguageTag());
				
				stmt.executeUpdate();
				saved = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		} else { // remove the set locale from the database
			try {
				conn = KaranteeniCore.getDatabaseConnector().openConnection();
				PreparedStatement stmt = conn.prepareStatement("DELETE FROM language WHERE UUID = ?;");
				stmt.setString(1, player.getUniqueId().toString());
				
				stmt.executeUpdate();
				saved = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return saved;
	}
}
