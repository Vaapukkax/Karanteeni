package net.karanteeni.core.config;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class YamlConfig {
	private File file;
	private FileConfiguration cconfig;
	
	
	/**
	 * Creates a new config file
	 * @param plugin
	 * @param directory
	 * @param fileName
	 */
	public YamlConfig(Plugin plugin, String directory, String fileName)
	{
		//Create the files necessary
		this.file = new File(plugin.getDataFolder().toString()+File.separator+directory, fileName);
		File dir = new File(plugin.getDataFolder().toString()+File.separator+directory);
		
		try{
			//Check that groupfile exists
			if(!file.exists())
				if(!dir.mkdirs() && !file.createNewFile())
					Bukkit.getLogger().log(Level.SEVERE, "FAILED TO CREATE NECESSARY GROUP FILES!");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		cconfig = new YamlConfiguration();
		
		try{
			//Load the file from computer
			cconfig.load(file);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the fileconfiguration
	 */
	public void save()
	{
		try{
			cconfig.save(file);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load the configuration file to the memory
	 */
	public void load()
	{
		try{
			cconfig.load(file);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the fileconfiguration
	 * @return
	 */
	public FileConfiguration getConfig()
	{
		return cconfig;
	}
}
