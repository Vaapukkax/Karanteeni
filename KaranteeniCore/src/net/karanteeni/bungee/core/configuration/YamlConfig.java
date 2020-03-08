package net.karanteeni.bungee.core.configuration;

import java.io.File;
import java.io.IOException;
import net.karanteeni.bungee.core.KaranteeniPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class YamlConfig {
	private final String fileName;
	//private final String path;
	private File file;
	private Configuration configuration;
	private KaranteeniPlugin plugin;
	
	public YamlConfig(KaranteeniPlugin plugin, String fileName) {
		this.fileName = fileName + ".yml";
		//this.path = ProxyServer.getInstance().getPluginsFolder().getPath();
		
		file = new File(ProxyServer.getInstance().getPluginsFolder(), File.separator + plugin.getPluginName() + File.separator + this.fileName);
		
		try {
			if(!file.exists())
				file.createNewFile();
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public YamlConfig(String path, String fileName) {
		this.fileName = fileName + ".yml";
		File path_ = ProxyServer.getInstance().getPluginsFolder();
		path_ = new File(path_.getPath() + path);
		file = new File(path_, File.separator + this.fileName);
		//this.path = path_.getPath();
		
		// create the path if one has not been made yet
		if(!path_.exists())
			path_.mkdirs();
		
		try {
			if(!file.exists())
				file.createNewFile();
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Returns the configuration modified
	 * @return configuration
	 */
	public Configuration getConfig() {
		return this.configuration;
	}
	
	
	/**
	 * Returns the plugin this config belongs to
	 * @return plugin this config belongs to
	 */
	public KaranteeniPlugin getPlugin() {
		return this.plugin;
	}
	
	
	/**
	 * Saves this configuration
	 * @return true if saved, false otherwise
	 */
	public boolean saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Reloads this configuration
	 * @return true if reloaded, false otherwise
	 */
	public boolean reloadConfig() {
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
