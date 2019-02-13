package net.karanteeni.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.karanteeni.core.block.BlockManager;
import net.karanteeni.core.config.ConfigManager;
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
public abstract class KaranteeniPlugin extends JavaPlugin{
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
	
	/**
	 * A new instance of KaranteeniPlugin is created
	 */
	public KaranteeniPlugin(boolean usesTranslator)
	{
		super();
		this.usesTranslation = usesTranslator;
		
		//Keep up the plugins
		kPluginInstances.put(this.getName(), this);
		Bukkit.getLogger().log(Level.INFO, "Plugin registered to KaranteeniCore: " + this.getName());
	}
	
	/**
	 * Called when the core plugin is loaded
	 */
	protected void load()
	{
		/** Loads the server ID */
		if(this.getConfig().isSet(SERVERID))
			serverID = this.getConfig().getString(SERVERID);
		else
		{
			this.getConfig().set(SERVERID, TextUtil.getRandomString(5, true));
			serverID = this.getConfig().getString(SERVERID);
			this.saveConfig();
		}
	}
	
	/**
	 * Called when the core plugin is enabled
	 */
	protected void enable()
	{
		//MUST COME FIRST! DO NOT CHANGE ORDER
		translator = new Translator();
		cfgm = new ConfigManager();
		
		//Connect to database
		if(dbConnector == null)
			dbConnector = createDatabaseConnector();
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
	 * Connects to mysql database according to config
	 * @return
	 */
	private DatabaseConnector createDatabaseConnector()
	{
		//Load host, database, username, password and port from config
		if(!this.getConfig().isSet("Database.host"))
		{
			this.getConfig().set("Database.host", "localhost");
			this.saveConfig();
		}
		String host = this.getConfig().getString("Database.host");
		
		if(!this.getConfig().isSet("Database.database"))
		{
			this.getConfig().set("Database.database", "Karanteeni");
			this.saveConfig();
		}
		String database = this.getConfig().getString("Database.database");
		
		if(!this.getConfig().isSet("Database.user"))
		{
			this.getConfig().set("Database.user", "root");
			this.saveConfig();
		}
		String user = this.getConfig().getString("Database.user");
		
		if(!this.getConfig().isSet("Database.password"))
		{
			this.getConfig().set("Database.password", "");
			this.saveConfig();
		}
		String password = this.getConfig().getString("Database.password");
		
		if(!this.getConfig().isSet("Database.port"))
		{
			this.getConfig().set("Database.port", 3306);
			this.saveConfig();
		}
		int port = this.getConfig().getInt("Database.port");
		
		return new DatabaseConnector(host, database, user, password, port);
	}
	
	/**
	 * Called when the core plugin is disabled
	 */
	public void disable()
	{
		if(dbConnector != null && dbConnector.isConnected())
			dbConnector.closeConnection();
	}
	
	/**
	 * Returns an object to which you can register all
	 * tick based timers
	 * @return Registrer of timers
	 */
	public final static KaranteeniTimerInitiater getTimerHandler()
	{
		return timerInitiater;
	}
	
	/**
	 * Return the soundhandler which will play sound to a player
	 * @return the plugins soundhandler
	 */
	public final static SoundHandler getSoundHandler()
	{
		return soundHandler;
	}
	
	/**
	 * Returns the messagehandler for this plugin
	 * @return
	 */
	public final static Messager getMessager()
	{
		return messager;
	}
	
	public final static DatabaseConnector getDatabaseConnector()
	{
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
	public final static Translator getTranslator()
	{
		return translator;
	}
	
	/**
	 * Returns the entitymanager of this plugin
	 * @return
	 */
	public final static EntityManager getEntityManager()
	{
		return entityManager;
	}
	
	/**
	 * Returns all the plugins using KaranteeniCores KaranteeniPlugin interface
	 * Useful for translation unit usage
	 * @return
	 */
	public final static List<KaranteeniPlugin> getPluginInstances()
	{
		return new ArrayList<KaranteeniPlugin>(kPluginInstances.values());
	}
	
	/**
	 * Returns an instance of a plugin
	 * @param string
	 * @return
	 */
	public final static KaranteeniPlugin getPluginInstance(String string)
	{
		return (KaranteeniPlugin) kPluginInstances.get(string);
	}
	
	/**
	 * Returns whether this plugin uses the Core Translator service
	 * @return
	 */
	public final boolean pluginUsesTranslator()
	{
		return this.usesTranslation;
	}
	
	/**
	 * Returns the blockmanager of the coreplugin
	 * @return
	 */
	public final static BlockManager getBlockManager()
	{
		return blockManager;
	}
	
	/**
	 * Returns the ID of this server
	 * @return
	 */
	public static final String getServerIdentificator()
	{
		return serverID;
	}
	
	/**
	 * Returns the DefaultMessages class which contains some default messages for children
	 * @return
	 */
	public static final DefaultMessages getDefaultMsgs()
	{
		return defaultMessages;
	}
	
	/**
	 * Returns the configmanager of the core plugin
	 * @return
	 */
	public static ConfigManager getConfigManager()
	{
		return cfgm;
	}
	
	/**
	 * Returns the PlayerHandler of the coreplugin
	 * @return
	 */
	public static final PlayerHandler getPlayerHandler()
	{
		return playerHandler;
	}
	
	/**
	 * Returns the itemmanager of the coreplugin
	 * @return
	 */
	public static final ItemManager getItemManager()
	{
		return itemManager;
	}
}
