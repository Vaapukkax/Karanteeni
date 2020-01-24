package net.karanteeni.bungee.core.configuration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.karanteeni.bungee.core.KaranteeniCore;
import net.karanteeni.bungee.core.KaranteeniPlugin;

public class ConfigManager {
	private static String dataFolder; 
	private static HashMap<KaranteeniPlugin, String> subDataFolders = new HashMap<KaranteeniPlugin, String>();
	// <Plugin, <Name of locale, file of said locale translations>>
	private static HashMap<KaranteeniPlugin, HashMap<Locale, YamlConfig> > translations = 
			new HashMap<KaranteeniPlugin, HashMap<Locale, YamlConfig>>();
	private static String TRANSLATIONS = "Translations";
	
	public ConfigManager() {
		KaranteeniCore plugin = KaranteeniCore.getInstance();
		dataFolder = plugin.getDataFolder().getPath();
		File dir = new File(dataFolder);
		if(!dir.exists())
			dir.mkdirs();
		
		
		createDataFolders();
	}
	
	
	/**
	 * Creates a data folder for each registered plugin
	 */
	private void createDataFolders() {
		Collection<KaranteeniPlugin> plugins = KaranteeniCore.getPluginInstances();
		
		//Creates necessary folders and files for each plugin on load
		for(KaranteeniPlugin plugin : plugins) {
			//Saves all of the data folders of each plugin
			String plDataFolder = plugin.getDataFolder().getPath();
			subDataFolders.put(plugin, plDataFolder);
			
			//Creates the possible translation files
			addTranslationConfigs(plugin);
		}
	}
	
	
	/**
	 * Creates the plugin translation files into the plugins directory
	 * @param plugin
	 */
	private void addTranslationConfigs(KaranteeniPlugin plugin) {
		String datafolder = plugin.getDataFolder().getPath();
		
		File dir = new File(datafolder + File.separator + TRANSLATIONS);
		
		//Create the plugin translation folder if not exists
		if(!dir.exists())
			dir.mkdirs();
		
		for(Locale locale : KaranteeniCore.getTranslator().getLocales()) {
			YamlConfig config = new YamlConfig(File.separator + plugin.getDescription().getName() + File.separator + TRANSLATIONS, locale.toLanguageTag());
			
			
			//If the hashmap does not have this key already then add it
			if(!translations.containsKey(plugin))
				translations.put(plugin, new HashMap<Locale, YamlConfig>());
			
			//Add the current translation to plugins translation list
			translations.get(plugin).put(locale, config);
		}
	}
	
	
	/**
	 * Returns the file which has the translations to messages
	 * @param plugin
	 * @return
	 */
	public YamlConfig getPluginTranslationYML(KaranteeniPlugin plugin, Locale locale) {
		return translations.get(plugin).get(locale);
	}
	
	
	/**
	 * Returns the file which has the translations to messages
	 * @param plugin
	 * @return
	 */
	public Map<Locale, YamlConfig> getPluginTranslationYMLs(KaranteeniPlugin plugin) {
		return translations.get(plugin);
	}
	
	
	/**
	 * Saves the translation file of plugin (for example when new translations are added)
	 * @param plugin
	 * @return
	 */
	public boolean savePluginTranslationYML(KaranteeniPlugin plugin, Locale locale) {
		return translations.get(plugin).get(locale).saveConfig();
	}
}
