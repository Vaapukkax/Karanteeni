package net.karanteeni.bungee.core.information.translation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import net.karanteeni.bungee.core.KaranteeniCore;
import net.karanteeni.bungee.core.KaranteeniPlugin;
import net.karanteeni.bungee.core.configuration.YamlConfig;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Translator {
	//Locales used by this plugin
	private final List<Locale> locales = new ArrayList<Locale>();
	private final List<String> localesString = new ArrayList<String>(); 
	private Locale defaultLocale = null;
	private KaranteeniCore plugin = null;
	private String configLocalesTag = "Locales";
	private static String LANG_SPLITTER = "-";
	
	/**
	 * Initializes the default languages which are always used
	 */
	public Translator() {
		plugin = KaranteeniCore.getInstance();
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
			} else //Log illegal config log
				KaranteeniCore.getInstance().getLogger().log(
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
		if(!plugin.getConfig().contains(configLocalesTag)) {
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
	public Locale getLocale(ProxiedPlayer player) {
		String[] parts = player.getLocale().toLanguageTag().split("_");
		if(parts.length == 2 && parts[0] != null && parts[1] != null)
			return new Locale(parts[0], parts[1]);
		return this.defaultLocale;
	}
	
	
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
		try{
			Map<Locale, YamlConfig> configs = KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin);
			
			for(Entry<Locale, YamlConfig> entry : configs.entrySet()) {
				if(!entry.getValue().getConfig().contains(key))
					entry.getValue().getConfig().set(key, defaultText);
				
				KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, entry.getKey());
			}
		} catch(Exception e) {
			KaranteeniCore.getInstance().getLogger().log(Level.SEVERE, "Tried to register translation before enabling plugin!");
			KaranteeniCore.getInstance().getLogger().log(Level.SEVERE, "Error given:", e);
		}
	}
	
	
	/**
	 * Registers a list of translations to one key
	 * @param plugin
	 * @param key
	 * @param defaultText
	 */
	public void registerRandomTranslation(KaranteeniPlugin plugin, String key, String defaultText) {
		try{
			Map<Locale, YamlConfig> configs = KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin);
			
			for(Entry<Locale, YamlConfig> entry : configs.entrySet()) {
				if(!entry.getValue().getConfig().contains(key))
					entry.getValue().getConfig().set(key, new ArrayList<String>(Arrays.asList(defaultText)));
				
				KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, entry.getKey());
			}
		} catch(Exception e) {
			KaranteeniCore.getInstance().getLogger().log(Level.SEVERE, "Tried to register translation before enabling plugin!");
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
		YamlConfig yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		yml.getConfig().set(key, newValue);
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
		YamlConfig yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.getConfig().getString(key);
	}
	
	
	/**
	 * Checks if a translation exists with the given locale
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public boolean hasTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		YamlConfig yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.getConfig().contains(key);
	}
	
	
	/**
	 * Checks if a translation exists with the given locale
	 * @param plugin
	 * @param sender
	 * @param key
	 * @return
	 */
	public boolean hasTranslation(KaranteeniPlugin plugin, CommandSender sender, String key) {
		if(sender instanceof ProxiedPlayer)
			return hasTranslation(plugin, getLocale((ProxiedPlayer)sender), key);
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
		if(sender instanceof ProxiedPlayer)
			return getTranslation(plugin, (ProxiedPlayer)sender, key);
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
	public String getTranslation(KaranteeniPlugin plugin, ProxiedPlayer player, String key) {
		YamlConfig yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, getLocale(player));
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.getConfig().getString(key);
	}
	
	
	/**
	 * Returns a random translation from a list of translations
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getRandomTranslation(KaranteeniPlugin plugin, Locale locale, String key) {
		YamlConfig yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		List<String> translations = yml.getConfig().getStringList(key);
		
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
		if(sender instanceof ProxiedPlayer)
			return getRandomTranslation(plugin, (ProxiedPlayer)sender, key);
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
	public String getRandomTranslation(KaranteeniPlugin plugin, ProxiedPlayer player, String key) {
		YamlConfig yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, getLocale(player));
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		List<String> translations = yml.getConfig().getStringList(key);
		
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
}