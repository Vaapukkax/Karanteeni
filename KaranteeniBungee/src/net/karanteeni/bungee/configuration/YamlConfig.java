package net.karanteeni.bungee.configuration;

import java.io.File;
import java.io.IOException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class YamlConfig {
	private final String fileName;
	private File file;
	private Configuration configuration;
	
	public YamlConfig(String fileName) {
		this.fileName = fileName + ".yml";
		
		file = new File(ProxyServer.getInstance().getPluginsFolder(), "/" + this.fileName);
		
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
