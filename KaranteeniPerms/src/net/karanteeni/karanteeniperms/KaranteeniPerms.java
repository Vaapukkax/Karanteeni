package net.karanteeni.karanteeniperms;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.karanteeniperms.groups.player.CommandPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;
import net.karanteeni.karanteeniperms.groups.player.GroupModel;
import net.karanteeni.karanteeniperms.groups.player.JoinEvent;
import net.karanteeni.karanteeniperms.groups.player.PlayerModel;

public class KaranteeniPerms extends KaranteeniPlugin {
	
	/** Contains all the groups player has */
	private GroupModel groupModel;
	/** Contains cache of all players groups and gets them from database */
	private PlayerModel playerModel;
	
	public KaranteeniPerms() {
		super(true);
	}
	
	
	@Override
	public void onEnable() {
		try {
			this.playerModel = new PlayerModel(this);
			this.groupModel = new GroupModel(
					playerModel::addPermissionToPlayer, //Generate groupmodel
					playerModel::removePermissionFromPlayer); //Allow access to addPermission and removePermission in real time
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, 
					"Error happened trying to launch KaranteeniPerms, closing plugin...:", e);
			
			this.getPluginLoader().disablePlugin(this); //Disable this plugin to prevent further damage!
			return;
		}
		
		registerEvents();
		registerCommands();
		registerChatTags();
	}
	
	
	@Override
	public void onDisable() {
		//Clear all permissions for incase of reload etc.
		playerModel.clearAllPlayersPermissions();
	}
	
	
	/**
	 * Registers the events this plugin uses
	 */
	private final void registerEvents() {
		//Low level joinevent! Loads cache.
		getServer().getPluginManager().registerEvents(new JoinEvent(), this);
	}
	
	
	/**
	 * Registers the commands this plugin uses
	 */
	private final void registerCommands() {
		(new CommandPerms(this,
			"permissions", "/permissions", "Manages the permissions of groups and players",
			getDefaultMsgs().defaultNoPermission())).register();
	}
	
	
	/**
	 * Registers the chat tags if Chatar has been enabled
	 */
	private final void registerChatTags() {
		if(getServer().getPluginManager().getPlugin("Chatar") != null) {
			// get the chatar plugin
			net.karanteeni.chatar.Chatar chatar = 
					(net.karanteeni.chatar.Chatar)getServer().getPluginManager().getPlugin("Chatar");
			
			// register LocalGroupComponent
			net.karanteeni.karanteeniperms.chat.LocalGroupPrefix lgc = new
					net.karanteeni.karanteeniperms.chat.LocalGroupPrefix(this, "group-local-prefix");
			chatar.registerComponent(lgc, "%group-local-prefix%", null);
			chatar.getHoverTexts().registerHover("group-local-prefix", lgc);
			
			net.karanteeni.karanteeniperms.chat.LocalGroupSuffix lgs = new
					net.karanteeni.karanteeniperms.chat.LocalGroupSuffix(this, "group-local-suffix");
			chatar.registerComponent(lgs, "%group-local-suffix%", null);
			chatar.getHoverTexts().registerHover("group-local-suffix", lgs);
			
			net.karanteeni.karanteeniperms.chat.LocalGroupName lgn = new
					net.karanteeni.karanteeniperms.chat.LocalGroupName(this, "group-local-name");
			chatar.registerComponent(lgn, "%group-local-name%", null);
			chatar.getHoverTexts().registerHover("group-local-name", lgn);
			
			net.karanteeni.karanteeniperms.chat.LocalGroupShortName lgsn = new
					net.karanteeni.karanteeniperms.chat.LocalGroupShortName(this, "group-local-sname");
			chatar.registerComponent(lgsn, "%group-local-sname%", null);
			chatar.getHoverTexts().registerHover("group-local-sname", lgsn);
		}
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