package net.karanteeni.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.karanteeni.bungee.configuration.ConfigManager;
import net.karanteeni.bungee.configuration.YamlConfig;
import net.karanteeni.bungee.data.player.PlayerHandler;
import net.karanteeni.bungee.database.DatabaseConnector;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public abstract class KaranteeniBungee extends Plugin implements Listener {
	private YamlConfig config;
	private static Translator translator;
	private static DatabaseConnector dbc;
	private static ConfigManager configManager;
	private static PlayerHandler playerHandler;
	protected static HashMap<String, KaranteeniBungee> kPluginInstances = new HashMap<String, KaranteeniBungee>();
	private ChannelKey mainChannel;
	
	public KaranteeniBungee(String name) {
		super();
		
		//Keep up the plugins
		kPluginInstances.put(name, this);
		System.out.println("Plugin registered to KaranteeniBungee: " + name);
		
		// create config
		config = new YamlConfig(File.separator + name, "config");
		mainChannel = new ChannelKey(name);
	}
	
	
	/**
	 * Called only when the core plugin starts up
	 */
	protected void enable() {
		//MUST COME FIRST! DO NOT CHANGE ORDER
		translator = new Translator();
		configManager = new ConfigManager();
		
		// initialize the database connector
		dbc = createDatabaseConnector();
		
		playerHandler = new PlayerHandler();
	}
	
	
	/**
	 * Returns a list of all plugins which use this class as super
	 * @return list of all inheriting classes
	 */
	public static Collection<KaranteeniBungee> getPluginInstances() {
		return kPluginInstances.values();
	}
	
	
	/**
	 * Returns the main channel this plugin uses
	 * @return the main channel used by this plugin
	 */
	public ChannelKey getMainChannel() {
		return this.mainChannel;
	}
	
	
	/**
	 * Returns the player handler of the plugin
	 * @return plugins player handler
	 */
	public static PlayerHandler getPlayerHandler() {
		return playerHandler;
	}
	
	
	/**
	 * Returns the config manager of this plugin
	 * @return config manager of this plugin
	 */
	public static ConfigManager getConfigManager() {
		return configManager;
	}
	
	
	/**
	 * Returns the module which allows you to connect to the database easily
	 * @return connector to the database
	 */
	public static DatabaseConnector getDatabaseConnector() {
		return dbc;
	}
	
	
	/**
	 * Connects to mysql database according to config
	 * @return
	 */
	private DatabaseConnector createDatabaseConnector() {
		//Load host, database, username, password and port from config
		if(!this.getConfig().contains("Database.host")) {
			this.getConfig().set("Database.host", "localhost");
			this.saveConfig();
		}
		String host = this.getConfig().getString("Database.host");
		
		if(!this.getConfig().contains("Database.database")) {
			this.getConfig().set("Database.database", "Karanteeni");
			this.saveConfig();
		}
		String database = this.getConfig().getString("Database.database");
		
		if(!this.getConfig().contains("Database.user")) {
			this.getConfig().set("Database.user", "root");
			this.saveConfig();
		}
		String user = this.getConfig().getString("Database.user");
		
		if(!this.getConfig().contains("Database.password")) {
			this.getConfig().set("Database.password", "");
			this.saveConfig();
		}
		String password = this.getConfig().getString("Database.password");
		
		if(!this.getConfig().contains("Database.port")) {
			this.getConfig().set("Database.port", 3306);
			this.saveConfig();
		}
		int port = this.getConfig().getInt("Database.port");
		
		return new DatabaseConnector(host, database, user, password, port);
	}
	
	
	/**
	 * Get the config to this plugin
	 * @return configuration of this plugin
	 */
	public Configuration getConfig() {
		return config.getConfig();
	}
	
	
	/**
	 * Save the config to this plugin
	 * @return true if config saved, false otherwise
	 */
	public boolean saveConfig() {
		return config.saveConfig();
	}
	
	
	/**
	 * Reloads the configuration file
	 * @return true if file reloaded, false otherwise
	 */
	public boolean reloadConfig() {
		return config.reloadConfig();
	}
	
	
	
	/**
	 * Returns the translator of this plugin
	 * @return the tranlator registered
	 */
	public static Translator getTranslator() {
		return translator;
	}
	
	
	@EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
            	String channel = in.readUTF();
            	if(channel.equals("get")) {
            		String message = in.readUTF();
            		System.out.println(message);
            		
            		Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();
                    for (Map.Entry<String, ServerInfo> en : servers.entrySet()) {
                        String name = en.getKey();
                        ServerInfo all = BungeeCord.getInstance().getServerInfo(name);
                        sendToBukkit("TellMods", message, all);
                    }
            	}
            } catch(Exception e1) {
            	
            }
     
        }
    }
	
	private void sendToBukkit(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("Return", stream.toByteArray());
    }
}
