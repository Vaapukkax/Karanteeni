package net.karanteeni.core;
import java.util.Collection;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import net.karanteeni.core.command.ShortcutCommand;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.event.NoActionEvent;
import net.karanteeni.core.event.PlayerHasJoinedEvent;
import net.karanteeni.core.event.PlayerJumpEvent;
import net.karanteeni.core.information.translation.CoreTranslations;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.core.players.KPlayerJoin;
import net.karanteeni.core.players.events.Invincibility;

public class KaranteeniCore extends KaranteeniPlugin {
	
	public KaranteeniCore() {
		//Does use translator service
		super(true);
	}
	
	@Override
	public void onLoad() {
		getLogger().log(Level.INFO, "KaranteeniCore started loading...!");
		initializeClasses();
		super.load();
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
		super.enable();
		
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
	
	
	@Override
	public void onDisable() {
		super.disable();
		
		getLogger().log(Level.INFO, "KaranteeniCore has been disabled!");
	}
	
	/**
	 * Enables coreplugin commands
	 */
	private void enableCommands() {
		// initialize shortcuts
		ShortcutCommand.initializeShortcuts(this);
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
	}
}
