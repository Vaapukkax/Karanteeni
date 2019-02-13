package net.karanteeni.core.information.translation;

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
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;

public class Translator {
	
	//Locales used by this plugin
	private List<Locale> locales = new ArrayList<Locale>();
	private Locale defaultLocale = null;
	private Plugin plugin = null;
	private String configLocalesTag = "Locales";
	
	/**
	 * Initializes the default languages which are always used
	 */
	public Translator()
	{
		plugin = KaranteeniCore.getPlugin(KaranteeniCore.class);
		initConfig();
		loadLocales();
	}
	
	/**
	 * Load the locales from config
	 */
	private void loadLocales()
	{
		List<String> lcs = plugin.getConfig().getStringList(configLocalesTag);
		boolean first = true;
		for(String locale : lcs)
		{
			String[] l = locale.split("-");
			if(l.length == 2)
			{
				locales.add(new Locale(l[0], l[1]));
				if(first)
				{
					defaultLocale = locales.get(0);
					first = false;
				}
			}
		}
	}
	
	/**
	 * Creates the locales used on server to config
	 */
	private void initConfig()
	{
		//Get the locales used on server
		if(!plugin.getConfig().isSet(configLocalesTag))
		{
			plugin.getConfig().set(configLocalesTag, 
					new ArrayList<String>(Arrays.asList("en-US")));
			plugin.saveConfig();
		}
	}
	
	/**
	 * Returns the default locale used on this server
	 * @return
	 */
	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}
	
	public Locale getLocale(Player player)
	{
		return new Locale(player.getLocale().substring(0, 2), player.getLocale().substring(3));
	}
	
	/**
	 * Registers a new plugin translation to a key
	 * @param plugin
	 * @param key
	 * @param defaultText
	 */
	public void registerTranslation(KaranteeniPlugin plugin, String key, String defaultText)
	{
		try{
			Map<Locale, FileConfiguration> configs = KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin);
			
			for(Entry<Locale, FileConfiguration> entry : configs.entrySet())
			{
				if(!entry.getValue().isSet(key))
					entry.getValue().set(key, defaultText);
				
				KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, entry.getKey());
			}
		}
		catch(Exception e)
		{
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
	public void registerRandomTranslation(KaranteeniPlugin plugin, String key, String defaultText)
	{
		try{
			Map<Locale, FileConfiguration> configs = KaranteeniCore.getConfigManager().getPluginTranslationYMLs(plugin);
			
			for(Entry<Locale, FileConfiguration> entry : configs.entrySet())
			{
				if(!entry.getValue().isSet(key))
					entry.getValue().set(key, new ArrayList<String>(Arrays.asList(defaultText)));
				
				KaranteeniCore.getConfigManager().savePluginTranslationYML(plugin, entry.getKey());
			}
		}
		catch(Exception e)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Tried to register translation before enabling plugin!");
		}
	}
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getTranslation(KaranteeniPlugin plugin, Locale locale, String key)
	{
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, locale);
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		return yml.getString(key);
	}
	
	/**
	 * Returns a translation for this plugin associated to the given key
	 * @param plugin
	 * @param player
	 * @param key
	 * @return
	 */
	public String getTranslation(KaranteeniPlugin plugin, CommandSender sender, String key)
	{
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
	public String getTranslation(KaranteeniPlugin plugin, Player player, String key)
	{
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
	public String getRandomTranslation(KaranteeniPlugin plugin, Locale locale, String key)
	{
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
	public String getRandomTranslation(KaranteeniPlugin plugin, CommandSender sender, String key)
	{
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
	public String getRandomTranslation(KaranteeniPlugin plugin, Player player, String key)
	{
		FileConfiguration yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, getLocale(player));
		
		//Use default locale if not recognized
		if(yml == null)
			yml = KaranteeniCore.getConfigManager().getPluginTranslationYML(plugin, defaultLocale);
		
		List<String> translations = yml.getStringList(key);
		
		Random r = new Random();
		
		return translations.get((int)r.nextInt(translations.size()));
	}
	
	/**
	 * Returns all the locales used by this plugin
	 * @return
	 */
	public final List<Locale> getLocales()
	{
		return locales;
	}
}
