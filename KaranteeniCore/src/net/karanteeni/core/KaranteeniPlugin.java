package net.karanteeni.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.karanteeni.core.block.BlockManager;
import net.karanteeni.core.config.ConfigManager;
import net.karanteeni.core.config.YamlConfig;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.entity.EntityManager;
import net.karanteeni.core.information.Messager;
import net.karanteeni.core.information.sounds.SoundHandler;
import net.karanteeni.core.information.text.DefaultMessages;
import net.karanteeni.core.information.text.TextUtil;
import net.karanteeni.core.information.translation.Translator;
import net.karanteeni.core.item.ItemManager;
import net.karanteeni.core.players.PlayerHandler;
import net.karanteeni.core.timers.KaranteeniTimerInitiater;

/**
 * The plugin which all plugins used by Karanteeni use and extend.
 * Takes care of the most basic stuff
 * @author Matti
 *
 */
public class KaranteeniPlugin extends JavaPlugin {
	protected static Translator translator;
	protected static SoundHandler soundHandler;
	protected static KaranteeniTimerInitiater timerInitiater;
	protected static Messager messager;
	protected static DatabaseConnector dbConnector;
	protected static HashMap<String, KaranteeniPlugin> kPluginInstances = new HashMap<String, KaranteeniPlugin>();
	protected static EntityManager entityManager;
	protected static BlockManager blockManager;
	protected static DefaultMessages defaultMessages;
	protected static PlayerHandler playerHandler;
	protected static ItemManager itemManager;
	private static ConfigManager cfgm;
	protected static String serverID;
	private static String SERVERID = "ServerID";
	private final boolean usesTranslation;
	/** Settings config in which the settings of core plugin are set */
	private final YamlConfig settings;
	
	
	/**
	 * A new instance of KaranteeniPlugin is created
	 */
	public KaranteeniPlugin(boolean usesTranslator) {
		super();
		this.usesTranslation = usesTranslator;
		
		settings = new YamlConfig(this, "Settings.yml");
		
		//Keep up the plugins
		kPluginInstances.put(this.getName(), this);
		Bukkit.getLogger().log(Level.INFO, "Plugin registered to KaranteeniCore: " + this.getName());
	}
	
	/**
	 * Called when the core plugin is loaded
	 */
	protected void load() {
		/** Loads the server ID */
		if(this.getConfig().isSet(SERVERID))
			serverID = this.getConfig().getString(SERVERID);
		else {
			this.getConfig().set(SERVERID, TextUtil.getRandomString(5, true));
			serverID = this.getConfig().getString(SERVERID);
			this.saveConfig();
		}
	}
	
	
	/**
	 * Called when the core plugin is enabled
	 */
	protected void enable() {
		//MUST COME FIRST! DO NOT CHANGE ORDER
		translator = new Translator();
		cfgm = new ConfigManager();
		
		//Connect to database
		if(dbConnector == null) {
			try {
				dbConnector = createDatabaseConnector();
				createTables();
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE, "COULD NOT CONNECT TO DATABASE! SOME OPERATIONS WILL NOT WORK!", e);
				e.printStackTrace();
			}
		}
		// after connecting to the database create language tables
		Translator.initTable();
		
		//MUST COME FIRST! DO NOT CHANGE ORDER
		
		defaultMessages = new DefaultMessages();
		defaultMessages.registerTranslations();
		soundHandler = new SoundHandler();
		timerInitiater = new KaranteeniTimerInitiater();
		messager = new Messager();
		entityManager = new EntityManager();
		blockManager = new BlockManager();
		playerHandler = new PlayerHandler();
		itemManager = new ItemManager();
	}
	
	
	/**
	 * Create the server table to database
	 */
	private void createTables()
	{
		Statement st = dbConnector.getStatement();
		try {
			st.execute(
				"CREATE TABLE IF NOT EXISTS server("
				+ "ID VARCHAR(64) NOT NULL,"
				+ "PRIMARY KEY (ID));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// insert this server to the database server table
		try {
			PreparedStatement ps = dbConnector.prepareStatement("INSERT IGNORE INTO server (ID) VALUES (?);");
			ps.setString(1, serverID);
			ps.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		try{
			st.execute("CREATE TABLE IF NOT EXISTS location("+
				"id VARCHAR(128) NOT NULL, "+
				"serverID VARCHAR(64) NOT NULL, "+
				"world VARCHAR(64) NOT NULL, "+
				"x NUMERIC(20,10) NOT NULL, "+
				"y NUMERIC(20,10) NOT NULL, "+
				"z NUMERIC(20,10) NOT NULL, "+
				"pitch NUMERIC(8,5) NOT NULL, "+
				"yaw NUMERIC(8,5) NOT NULL, "+
				"FOREIGN KEY (serverID) REFERENCES server(ID), "+
				"PRIMARY KEY (id,serverID));");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Connects to mysql database according to config
	 * @return
	 */
	private DatabaseConnector createDatabaseConnector() throws SQLException {
		//Load host, database, username, password and port from config
		if(!this.getConfig().isSet("Database.host")) {
			this.getConfig().set("Database.host", "localhost");
			this.saveConfig();
		}
		String host = this.getConfig().getString("Database.host");
		
		if(!this.getConfig().isSet("Database.database")) {
			this.getConfig().set("Database.database", "Karanteeni");
			this.saveConfig();
		}
		String database = this.getConfig().getString("Database.database");
		
		if(!this.getConfig().isSet("Database.user")) {
			this.getConfig().set("Database.user", "root");
			this.saveConfig();
		}
		String user = this.getConfig().getString("Database.user");
		
		if(!this.getConfig().isSet("Database.password")) {
			this.getConfig().set("Database.password", "");
			this.saveConfig();
		}
		String password = this.getConfig().getString("Database.password");
		
		if(!this.getConfig().isSet("Database.port")) {
			this.getConfig().set("Database.port", 3306);
			this.saveConfig();
		}
		int port = this.getConfig().getInt("Database.port");
		
		return new DatabaseConnector(host, database, user, password, port);
	}
	
	
	/**
	 * Called when the core plugin is disabled
	 */
	protected void disable() {
		// close database connection
		/*if(dbConnector != null && dbConnector.isConnected())
			dbConnector.closeConnection();*/
		// stop all the timers
		timerInitiater.closeTimers();
	}
	
	/**
	 * Returns an object to which you can register all
	 * tick based timers
	 * @return Registrer of timers
	 */
	public final static KaranteeniTimerInitiater getTimerHandler() {
		return timerInitiater;
	}
	
	/**
	 * Return the soundhandler which will play sound to a player
	 * @return the plugins soundhandler
	 */
	public final static SoundHandler getSoundHandler() {
		return soundHandler;
	}
	
	/**
	 * Returns the messagehandler for this plugin
	 * @return
	 */
	public final static Messager getMessager() {
		return messager;
	}
	
	public final static DatabaseConnector getDatabaseConnector() {
		return dbConnector;
	}
	
	/**
	 * Returns the translator for plugin messages
	 * It is recommended to register translations in a place where they will be registered on startup
	 * for example
	 * 
	 * public Pay()
	 * {
	 * 	getTranslator().registerTranslation()
	 * }
	 * 
	 * transferMoney()
	 * {
	 * 	sendmessage(getTranslation())
	 * }
	 * @return
	 */
	public final static Translator getTranslator() {
		return translator;
	}
	
	/**
	 * Returns the entitymanager of this plugin
	 * @return
	 */
	public final static EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * Returns all the plugins using KaranteeniCores KaranteeniPlugin interface
	 * Useful for translation unit usage
	 * @return
	 */
	public final static List<KaranteeniPlugin> getPluginInstances() {
		return new ArrayList<KaranteeniPlugin>(kPluginInstances.values());
	}
	
	/**
	 * Returns an instance of a plugin
	 * @param string
	 * @return
	 */
	public final KaranteeniPlugin getPluginInstance(String string) {
		return (KaranteeniPlugin) kPluginInstances.get(string);
	}
	
	/**
	 * Returns whether this plugin uses the Core Translator service
	 * @return
	 */
	public final boolean pluginUsesTranslator() {
		return this.usesTranslation;
	}
	
	/**
	 * Returns the blockmanager of the coreplugin
	 * @return
	 */
	public final static BlockManager getBlockManager() {
		return blockManager;
	}
	
	/**
	 * Returns the ID of this server
	 * @return
	 */
	public final static String getServerIdentificator() {
		return serverID;
	}
	
	/**
	 * Returns the DefaultMessages class which contains some default messages for children
	 * @return
	 */
	public final static DefaultMessages getDefaultMsgs() {
		return defaultMessages;
	}
	
	/**
	 * Returns the configmanager of the core plugin
	 * @return
	 */
	public static ConfigManager getConfigManager() {
		return cfgm;
	}
	
	/**
	 * Returns the PlayerHandler of the coreplugin
	 * @return
	 */
	public final static PlayerHandler getPlayerHandler() {
		return playerHandler;
	}
	
	/**
	 * Returns the itemmanager of the coreplugin
	 * @return
	 */
	public final static ItemManager getItemManager() {
		return itemManager;
	}
	
	
	/**
	 * Returns the Core plugin data settings configuration file
	 * @return core settings
	 */
	public FileConfiguration getSettings() {
		return settings.getConfig();
	}
	
	
	/**
	 * Saves the core plugin settings configuration file
	 * @return true if save was successful, false otherwise
	 */
	public boolean saveSettings() {
		return settings.save();
	}
}
