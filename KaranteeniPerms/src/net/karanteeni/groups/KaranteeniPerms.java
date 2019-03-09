package net.karanteeni.groups;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.groups.player.Group;
import net.karanteeni.groups.player.GroupModel;
import net.karanteeni.groups.player.JoinEvent;
import net.karanteeni.groups.player.PlayerModel;

public class KaranteeniPerms extends KaranteeniPlugin {
	
	/** Contains all the groups player has */
	private GroupModel groupModel;
	/** Contains cache of all players groups and gets them from database */
	private PlayerModel playerModel;
	
	public KaranteeniPerms() {
		super(true);
	}
	
	@Override
	public void onEnable()
	{
		try {
			this.groupModel = new GroupModel();		//Generate groupmodel
			this.playerModel = new PlayerModel(this);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, 
					"Error happened trying to launch KaranteeniPerms, closing plugin...:", e);
			
			this.getPluginLoader().disablePlugin(this); //Disable this plugin to prevent further damage!
			return;
		}
		
		registerEvents();
		registerCommands();
	}
	
	@Override
	public void onDisable()
	{
		//Clear all permissions for incase of reload etc.
		playerModel.clearAllPlayersPermissions();
	}
	
	/**
	 * Registers the events this plugin uses
	 */
	private final void registerEvents()
	{
		//Low level joinevent! Loads cache.
		getServer().getPluginManager().registerEvents(new JoinEvent(), this);
	}
	
	/**
	 * Registers the commands this plugin uses
	 */
	private final void registerCommands()
	{
		
	}
	
	/**
	 * Gets a local group using its ID value
	 * @param id ID of the group searched
	 * @return Local group (server)
	 */
	public Group getLocalGroup(String id)
	{ return groupModel.getLocalGroupList().getGroup(id); }
	
	/**
	 * Gets the local default group this server has
	 * @return Local default group for players
	 */
	public Group getDefaultGroup()
	{ return groupModel.getLocalGroupList().getDefaultGroup(); }
	
	/**
	 * Returns the current groupmodel
	 * @return
	 */
	public GroupModel getGroupModel()
	{ return this.groupModel; }
	
	/**
	 * Returns the playermodel of this plugin
	 * playermodel is used to access players group (database/cache)
	 * @return Class to access the group of player
	 */
	public PlayerModel getPlayerModel()
	{ return this.playerModel; }
}