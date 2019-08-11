package net.karanteeni.core.information.translation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.players.KPlayer;

public class Translator {
	
	//Locales used by this plugin
	private final List<Locale> locales = new ArrayList<Locale>();
	private final List<String> localesString = new ArrayList<String>(); 
	private Locale defaultLocale = null;
	private KaranteeniCore plugin = null;
	private static final String configLocalesTag = "Locales";
	private static final String LANGUAGE = "language";
	private static String LANG_SPLITTER = "-";
	
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
	 * Returns the locale of player or if not able to
	 * identify locale, then default locale
	 * @param player
	 * @return
	 */
	/*public Locale getLocale(Player player) {
		String[] parts = player.getLocale().split("_");
		if(parts.length == 2 && parts[0] != null && parts[1] != null)
			return new Locale(parts[0], parts[1]);
		return this.defaultLocale;
	}*/
	
	
	/**
	 * Gives all the locales as stringlist used by the plugin in the format
	 * "xx-XX"
	 * @return list of locales as string in format "xx-XX"
	 */
	public List<String> getStringLocales()
	{ return this.localesString; }
	
	
	/**
	 * Registers a new plugin translation to a key
	 * @param plugin
	 * @param key
	 * @param defaultText
	 */
	public void registerTranslation(KaranteeniPlugin plugin, String key, String defaultText) {
		try {
			Map<Locale, FileConfiguration> configs = KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin);
			
			for(Entry<Locale, FileConfiguration> entry : configs.entrySet()) {
				if(!entry.getValue().isSet(key))
					entry.getValue().set(key, defaultText);
				
				KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, entry.getKey());
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
			Map<Locale, FileConfiguration> configs = KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin);
			
			for(Entry<Locale, FileConfiguration> entry : configs.entrySet()) {
				if(!entry.getValue().isSet(key))
					entry.getValue().set(key, new ArrayList<String>(Arrays.asList(defaultText)));
				
				KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, entry.getKey());
			}
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Tried to register translation before enabling plugin!");
		}
	}
	
	
	/**
	 * Sets the translation of given key to the config
	 * @param plugin
	 * @param locale
	 * @param key
	 * @param newValue
	 */
	public void setTranslation(KaranteeniPlugin plugin, Locale locale, String key, String newValue) {
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		yml.set(key, newValue);
		KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, locale);
	}
	
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.getString(key);
	}
	
	
	/**
	 * Checks if a translation exists with the given locale
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public boolean hasTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.isSet(key);
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
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, getLocale(player));
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.getString(key);
	}
	
	
	/**
	 * Returns a random translation from a list of translations
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getRandomTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		List<String> translations = yml.getStringList(key);
		
		Random r = new Random();
		
		return translations.get((int)r.nextInt(translations.size()));
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
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, getLocale(player));
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		List<String> translations = yml.getStringList(key);
		
		Random r = new Random();
		
		if(translations.size() < 1) return "No random translations defined for plugin " + plugin.getName() + " with key " + key; 
		return translations.get(r.nextInt(translations.size()));
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
		Statement stmt = KaranteeniCore.getDatabaseConnector().getStatement();
		
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS language(\n"
					+ "UUID VARCHAR(60) NOT NULL,\n"
					+ "locale VARCHAR(10) NOT NULL,\n"
					+ "PRIMARY KEY (UUID),"
					+ "FOREIGN KEY (UUID) REFERENCES player(UUID));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Loads a players language to the memory
	 * @param player player whose language will be loaded to memory
	 */
	public void loadLocale(Player player) {
		// verify that the player is online
		if(!player.isOnline()) return;
		KPlayer kp = KPlayer.getKPlayer(player);
		if(kp == null) return;
		
		PreparedStatement stmt = KaranteeniCore.getDatabaseConnector().prepareStatement(
				"SELECT locale FROM language WHERE UUID = ?;");
		
		try {
			stmt.setString(1, player.getUniqueId().toString());
			
			ResultSet set = stmt.executeQuery();
			
			// a custom language was found, get and set it
			if(set.next()) {
				String[] parts = set.getString(1).split(LANG_SPLITTER);
				
				if(parts.length == 2)
				kp.setData(KaranteeniCore.getPlugin(KaranteeniCore.class), LANGUAGE, new Locale(parts[0], parts[1]));
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
			if(parts.length == 2 && parts[0] != null && parts[1] != null)
				return new Locale(parts[0], parts[1]);
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
		
		// verify that the locale exists
		if(locale != null && !KaranteeniCore.getTranslator().getLocales().contains(locale)) 
			return false;
		
		// verify that the player is online
		if(!player.isOnline()) online = false;
		KPlayer kp;
		if(online) {
			kp = KPlayer.getKPlayer(player);
			if(kp == null) online = false;
			
			// set the locale for the online player
			if(locale != null && online)
				kp.setData(plugin, LANGUAGE, locale);
			else if(online)
				kp.removeData(plugin, LANGUAGE);
		}
		
		// save the locale to the database
		if(locale != null) {
			PreparedStatement stmt = KaranteeniCore.getDatabaseConnector().prepareStatement(
					"INSERT INTO language (UUID, locale) VALUES (?,?) ON DUPLICATE KEY UPDATE locale = ?;");
			try {
				stmt.setString(1, player.getUniqueId().toString());
				stmt.setString(2, locale.toLanguageTag());
				stmt.setString(3, locale.toLanguageTag());
				
				stmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			
		} else { // remove the set locale from the database
			PreparedStatement stmt = KaranteeniCore.getDatabaseConnector().prepareStatement(
					"DELETE FROM language WHERE UUID = ?;");
			try {
				stmt.setString(1, player.getUniqueId().toString());
				
				stmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}
