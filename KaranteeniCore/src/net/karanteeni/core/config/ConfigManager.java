package net.karanteeni.core.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;

public class ConfigManager {
	private static String dataFolder; 
	private static HashMap<Plugin, String> subDataFolders = new HashMap<Plugin, String>();
	// <Plugin, <Name of locale, file of said locale translations>>
	private static HashMap<Plugin, HashMap<Locale, FileConfiguration> > translations = new HashMap<Plugin, HashMap<Locale, FileConfiguration>>();
	private static String TRANSLATIONS = "Translations";
	
	public ConfigManager()
	{
		Plugin plugin = KaranteeniCore.getPlugin(KaranteeniCore.class);
		dataFolder = plugin.getDataFolder().getPath();
		File dir = new File(dataFolder);
		if(!dir.exists())
			dir.mkdir();
		
		
		createDataFolders();
	}
	
	/**
	 * Creates a data folder for each registered plugin
	 */
	private void createDataFolders()
	{
		List<KaranteeniPlugin> plugins = KaranteeniCore.getPluginInstances();
		
		//Creates necessary folders and files for each plugin on load
		for(KaranteeniPlugin plugin : plugins)
		{
			//Saves all of the data folders of each plugin
			String plDataFolder = plugin.getDataFolder().getPath();
			subDataFolders.put(plugin, plDataFolder);
			
			//Creates the possible translation files
			if(plugin.pluginUsesTranslator())
				addTranslationConfigs(plugin);
		}
	}
	
	/**
	 * Creates the plugin translation files into the plugins directory
	 * @param plugin
	 */
	private void addTranslationConfigs(KaranteeniPlugin plugin)
	{
		String datafolder = plugin.getDataFolder().getPath();
		
		File dir = new File(datafolder + File.separator + TRANSLATIONS);
		
		//Create the plugin translation folder if not exists
		if(!dir.exists())
			dir.mkdirs();
		
		for(Locale locale : KaranteeniCore.getTranslator().getLocales())
		{
			File configFile = new File(dir + File.separator + locale.toString() + ".yml");
			
			try {
				//If the config file does not exist create it
				if(!configFile.exists())
					configFile.createNewFile();
				
				FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
				
				//If the hashmap does not have this key already then add it
				if(!translations.containsKey(plugin))
					translations.put(plugin, new HashMap<Locale, FileConfiguration>());
				
				//Add the current translation to plugins translation list
				translations.get(plugin).put(locale, config);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the file which has the translations to messages
	 * @param plugin
	 * @return
	 */
	public FileConfiguration getPluginTranslationYML(KaranteeniPlugin plugin, Locale locale)
	{
		return translations.get(plugin).get(locale);
	}
	
	/**
	 * Returns the file which has the translations to messages
	 * @param plugin
	 * @return
	 */
	public Map<Locale, FileConfiguration> getPluginTranslationYMLs(KaranteeniPlugin plugin)
	{
		return translations.get(plugin);
	}
	
	/**
	 * Saves the translation file of plugin (for example when new translations are added)
	 * @param plugin
	 * @return
	 */
	public boolean savePluginTranslationYML(KaranteeniPlugin plugin, Locale locale)
	{
		try {
			//Save the plugin translation file
			translations.get(plugin).get(locale).save(new File(subDataFolders.get(plugin) + 
					File.separator + TRANSLATIONS + File.separator + locale.toString() + ".yml"));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
