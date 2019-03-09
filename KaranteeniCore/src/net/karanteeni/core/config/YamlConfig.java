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
	private static String FILE_FAILURE = "Failed to create necessary destination files!";
	
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
			{
				dir.mkdirs();
				file.createNewFile();
			}
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, FILE_FAILURE, e);
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
	 * Creates a new config file to plugin defaultdatafolder
	 * @param plugin
	 * @param fileName
	 */
	public YamlConfig(Plugin plugin, String fileName)
	{
		//Create the files necessary
		this.file = new File(plugin.getDataFolder().toString(), fileName);
		File dir = new File(plugin.getDataFolder().toString());
		
		try{
			//Check that groupfile exists
			if(!file.exists())
			{
				dir.mkdirs();
				file.createNewFile();
			}
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, FILE_FAILURE, e);
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
	 * Checks that the yml is created, loaded and file exists
	 * @return
	 */
	public boolean isValid()
	{ return (cconfig != null && file.exists()); }
	
	/**
	 * Save the fileconfiguration
	 */
	public boolean save()
	{
		try{
			cconfig.save(file);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
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
