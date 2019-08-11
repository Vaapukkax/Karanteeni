package net.karanteeni.bungee.configuration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.karanteeni.bungee.KaranteeniBungee;
import net.karanteeni.bungee.KaranteeniBungeeCore;

public class ConfigManager {
	private static String dataFolder; 
	private static HashMap<KaranteeniBungee, String> subDataFolders = new HashMap<KaranteeniBungee, String>();
	// <Plugin, <Name of locale, file of said locale translations>>
	private static HashMap<KaranteeniBungee, HashMap<Locale, YamlConfig> > translations = 
			new HashMap<KaranteeniBungee, HashMap<Locale, YamlConfig>>();
	private static String TRANSLATIONS = "Translations";
	
	public ConfigManager() {
		KaranteeniBungee plugin = KaranteeniBungeeCore.getInstance();
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
		Collection<KaranteeniBungee> plugins = KaranteeniBungeeCore.getPluginInstances();
		
		//Creates necessary folders and files for each plugin on load
		for(KaranteeniBungee plugin : plugins) {
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
	private void addTranslationConfigs(KaranteeniBungee plugin) {
		String datafolder = plugin.getDataFolder().getPath();
		
		File dir = new File(datafolder + File.separator + TRANSLATIONS);
		
		//Create the plugin translation folder if not exists
		if(!dir.exists())
			dir.mkdirs();
		
		for(Locale locale : KaranteeniBungeeCore.getTranslator().getLocales()) {
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
	public YamlConfig getPluginTranslationYML(KaranteeniBungee plugin, Locale locale) {
		return translations.get(plugin).get(locale);
	}
	
	
	/**
	 * Returns the file which has the translations to messages
	 * @param plugin
	 * @return
	 */
	public Map<Locale, YamlConfig> getPluginTranslationYMLs(KaranteeniBungee plugin) {
		return translations.get(plugin);
	}
	
	
	/**
	 * Saves the translation file of plugin (for example when new translations are added)
	 * @param plugin
	 * @return
	 */
	public boolean savePluginTranslationYML(KaranteeniBungee plugin, Locale locale) {
		//try {
			//Save the plugin translation file
			return translations.get(plugin).get(locale).saveConfig();
			/*translations.get(plugin).get(locale).save(new File(subDataFolders.get(plugin) + 
					File.separator + TRANSLATIONS + File.separator + locale.toLanguageTag() + ".yml"));*/
		/*	return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;*/
	}
}
