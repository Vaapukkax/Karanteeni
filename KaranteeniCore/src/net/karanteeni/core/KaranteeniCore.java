package net.karanteeni.core;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import net.karanteeni.core.block.BlockManager;
import net.karanteeni.core.block.executable.ActionBlockManager;
import net.karanteeni.core.command.ShortcutCommand;
import net.karanteeni.core.commands.LanguageCommand;
import net.karanteeni.core.commands.LanguageComponent;
import net.karanteeni.core.config.ConfigManager;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.entity.EntityManager;
import net.karanteeni.core.event.NoActionEvent;
import net.karanteeni.core.event.PlayerHasJoinedEvent;
import net.karanteeni.core.event.PlayerJumpEvent;
import net.karanteeni.core.information.Messager;
import net.karanteeni.core.information.sounds.SoundHandler;
import net.karanteeni.core.information.text.DefaultMessages;
import net.karanteeni.core.information.text.TextUtil;
import net.karanteeni.core.information.translation.CoreTranslations;
import net.karanteeni.core.information.translation.PlayerJoinLoadTranslation;
import net.karanteeni.core.information.translation.Translator;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.core.item.ItemManager;
import net.karanteeni.core.players.KPlayerJoin;
import net.karanteeni.core.players.PlayerHandler;
import net.karanteeni.core.players.events.Invincibility;
import net.karanteeni.core.timers.KaranteeniTimerInitiater;

public class KaranteeniCore extends KaranteeniPlugin {
	
	public KaranteeniCore() {
		//Does use translator service
		super(true);
		actionBlockManager = new ActionBlockManager(this);
	}
	
	@Override
	public void onLoad() {
		getLogger().log(Level.INFO, "KaranteeniCore started loading...!");
		initializeClasses();

		/** Loads the server ID */
		if(this.getConfig().isSet(SERVERID))
			serverID = this.getConfig().getString(SERVERID);
		else {
			this.getConfig().set(SERVERID, TextUtil.getRandomString(5, true));
			serverID = this.getConfig().getString(SERVERID);
			this.saveConfig();
		}
		
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
		
		// load action blocks from database
		KaranteeniCore.getActionBlockManager().readBlocksFromDatabase();
		
		getLogger().log(Level.INFO, "KaranteeniCore has loaded!");
	}
	
	
	/**
	 * Initializes classes that may need initialization
	 */
	private void initializeClasses() {
		ArrayFormat.initialize();
	}
	
	
	@Override
	public void onEnable() {
		//MUST COME FIRST! DO NOT CHANGE ORDER
		translator = new Translator();
		cfgm = new ConfigManager();
		
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
		KaranteeniCore.getActionBlockManager().loadReadBlockData();
		
		//Register all core translations
		(new CoreTranslations()).registerCoreTranslations();
		
		// initialize the inventory items
		PresetActionItems.initialize(this);
		
		enableCommands();
		enableEvents();
		//Initialize the Time class for usage
		// Time.initialize(); // DELETED
		getLogger().log(Level.INFO, "KaranteeniCore has been enabled!");
		reloadPlayers(Bukkit.getOnlinePlayers());
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				reloadPlayers(Bukkit.getOnlinePlayers());
			}
		}, 0);
	}
	
	
	/**
	 * Create the server table to database
	 */
	private void createTables() {
		Connection conn = null;
		try {
			conn = getDatabaseConnector().openConnection();
			Statement st = conn.createStatement();
			st.execute(
				"CREATE TABLE IF NOT EXISTS server("
				+ "ID VARCHAR(64) NOT NULL,"
				+ "PRIMARY KEY (ID));");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// insert this server to the database server table
		try {
			conn = getDatabaseConnector().openConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO server (ID) VALUES (?);");
			ps.setString(1, serverID);
			ps.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try{
			conn = getDatabaseConnector().openConnection();
			Statement st = conn.createStatement();
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
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// initialize database tables for execblock manager
		ActionBlockManager.initializeAndLoadDatabase();
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

	
	@Override
	public void onDisable() {
		timerInitiater.closeTimers();
		
		getLogger().log(Level.INFO, "KaranteeniCore has been disabled!");
	}
	
	
	/**
	 * Enables coreplugin commands
	 */
	private void enableCommands() {
		// initialize shortcuts
		ShortcutCommand.initializeShortcuts(this);
		
		LanguageCommand lc = new LanguageCommand();
		LanguageComponent lcomp = new LanguageComponent();
		
		lc.setLoader(lcomp);
		lc.register();
	}
	
	
	/**
	 * Gets online players and sends them each a join event
	 * in case of reload
	 */
	private void reloadPlayers(Collection<? extends Player> collection) {
		for(Player player : collection) {
			PlayerJoinEvent joinEvent = new PlayerJoinEvent(player, "§e" + player.getName() + " experienced reload!");
			Bukkit.broadcastMessage("§e" + player.getName() + " experienced reload!");
			Bukkit.getPluginManager().callEvent(joinEvent);
		}
	}
	
	
	/**
	 * Enables coreplugin events
	 */
	private void enableEvents() {
		// register custom events
		NoActionEvent.register(this);
		PlayerJumpEvent.register(this);
		PlayerHasJoinedEvent.register(this);
		
		// register listenable events
		getServer().getPluginManager().registerEvents(new KPlayerJoin(), this);
		getServer().getPluginManager().registerEvents(new Invincibility(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinLoadTranslation(), this);
		//getServer().getPluginManager().registerEvents(actionBlockManager, this);
		getServer().getPluginManager().registerEvents(actionBlockManager.getActionBlockEventManager(), this);
		//getServer().getPluginManager().registerEvents(new PreLoginEvent(System.currentTimeMillis()), this);
	}
}
