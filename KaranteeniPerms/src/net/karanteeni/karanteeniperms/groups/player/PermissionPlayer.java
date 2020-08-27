package net.karanteeni.karanteeniperms.groups.player;

import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;


/**
 * This class is responsible for updating and giving out
 * @author Nuubles
 *
 */
public class PermissionPlayer {
	private final static String GROUP_DATA_KEY = "GROUP_DATA";
	public final static String GROUP_KEY = "GROUP";
	private final KaranteeniPerms plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
	private GroupData groupData;
	private PermissionAttachment pAttachment = null;
	private UUID uuid;
	private Group group;
	private BungeeGroup globalGroup = null;
	private boolean changedGroup = false;
	private boolean changedPlayerInformation = false;
	
	
	private PermissionPlayer(UUID uuid) {
		this.uuid = uuid;
	}
	
	
	/**
	 * Sets the player of this PermissionPlayer. Valid only after load. PermissionAttachment generated and permissions
	 * for the player set
	 * @return true if player was loaded, false otherwise
	 */
	protected boolean activatePlayer() {
		Player player = Bukkit.getPlayer(uuid);
		if(player == null) return false;
		KPlayer kp = KPlayer.getKPlayer(player);
		if(kp == null) return false;
		
		this.pAttachment = player.addAttachment(plugin);
		// generate and set the permission attachment of the player
		for(ExtendedPermission ep : this.groupData.getPermissions())
			this.pAttachment.setPermission(ep.getPermission(), ep.isPositive());
		
		kp.setCacheData(plugin, GROUP_DATA_KEY, groupData);
		
		// register player to use this group
		this.group.registerUser(player);
		// store data to KPlayer
		kp.setCacheData(plugin, GROUP_KEY, group);
		
		// register player to use this global group
		// TODO
		// store data to KPlayer
		// TODO
		
		return true;
	}
	
	
	/**
	 * Deactivates the player permissions. For example in quit event
	 */
	protected boolean deActivatePlayer() {
		// unregister player from the given groups
		Player player = Bukkit.getPlayer(getUUID());
		if(player != null && this.group != null) 
			return this.group.unregisterUser(player);
		return false;
	}
	
	
	/**
	 * Loads the private groupdata of this permissionplayer to memory
	 * @return true if load was successful, false otherwise
	 */
	private boolean loadGroupData() {
		KPlayer kp = KPlayer.getKPlayer(uuid);
		
		// use cache if it has been set
		if(kp != null && kp.dataExists(plugin, GROUP_DATA_KEY)) {
			this.groupData = (GroupData)kp.getData(plugin, GROUP_DATA_KEY);
			return true;
		} else {
			// load the private group data and permissions from database
			GroupData gd = plugin.getPlayerModel().getGroupDatabase().getPrivateGroupData(uuid, null);
			if(gd == null) return false;
			
			this.groupData = gd;
			
			// store to cache if not null. Uses same object to allow modification from multiple places
			if(kp != null)
				kp.setCacheData(plugin, GROUP_DATA_KEY, gd);
				
			return true;
		}
	}
	
	
	/**
	 * Loads and sets the group of this PermissionPlayer
	 * @return true if load was successful, false otherwise
	 */
	private boolean loadGroup() {
		KPlayer kp = KPlayer.getKPlayer(uuid);
		
		// load from cache if available
		if(kp != null && kp.dataExists(plugin, GROUP_KEY)) {
			this.group = (Group)kp.getData(plugin, GROUP_KEY);
			return true;
		}
		
		Group group = plugin.getPlayerModel().getGroupDatabase().getLocalGroup(plugin.getGroupList(), uuid);
		if(group == null) return false;
		this.group = group;
		return true;
	}
	
	
	/**
	 * Loads and sets the bungeegroup built from data offered by BungeeCord
	 * @return true if load was successful, false otherwise
	 */
	private boolean loadBungeeGroup() {
		Bukkit.getLogger().log(Level.SEVERE, "Bungee group load and activation not implemented");
		return true;
	}
	
	
	/**
	 * Loads the data of empty permissionplayer but does not generate player specific information.
	 * To generate player information, run activatePlayer() in order to access player specific information
	 * @param uuid uuid of the player whose permissionplayer will be loaded
	 * @return loaded permissionplayer or null if not found
	 */
	protected static PermissionPlayer getPermissionPlayer(UUID uuid) {
		PermissionPlayer pp = new PermissionPlayer(uuid);
		// load private player data
		if(!pp.loadGroupData()) return null;
		// load players local group
		if(!pp.loadGroup()) return null;
		// load players global group 
		if(!pp.loadBungeeGroup()) return null;
		//TODO
		
		return pp;
	}
	
	
	/**
	 * Checks if this player has a given permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(String permission) {
		return this.groupData.hasPermission(permission) || this.group.hasPermission(permission); // TODO add globalgroup
	}
	
	
	/**
	 * Checks if this player has the given permission
	 * @param permission permission to have
	 * @return true if player has this permission, false otherwise
	 */
	public boolean hasPrivatePermission(String permission) {
		return this.groupData.hasPermission(permission);
	}
	
	
	/**
	 * Returns the prefix of this player
	 * @return
	 */
	public String getPrefix(CommandSender sender, boolean shortened) {
		String prefix = null;
		// player prefix
		if(shortened)
			prefix = groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupShortName());
		else
			prefix = groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupName());
		if(prefix != null) return prefix;
		
		// group prefix
		prefix = group.getPrefix(sender, shortened);
		if(prefix != null) return prefix;
		
		// bungee group prefix
		prefix = "TODO: BUNGEEGROUP prefix PermissionPlayer"; // TODO
		if(prefix != null) return prefix;
		
		return null;
	}
	
	
	/**
	 * Returns the prefix of this player
	 * @return
	 */
	public String getPrefix(Locale locale, boolean shortened) {
		String prefix = null;
		// player prefix
		if(shortened)
			prefix = groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupShortName());
		else
			prefix = groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupName());
		if(prefix != null) return prefix;
		
		// group prefix
		prefix = group.getPrefix(locale, shortened);
		if(prefix != null) return prefix;
		
		// bungee group prefix
		prefix = "TODO: BUNGEEGROUP prefix PermissionPlayer"; // TODO
		if(prefix != null) return prefix;
		
		return null;
	}
	
	
	/**
	 * Returns the raw prefix of this player
	 * @return
	 */
	public String getRawPrefix() {
		if(groupData.getPrefix() != null) return groupData.getPrefix();
		if(group.getRawPrefix() != null) return group.getRawPrefix();
		if(true) return "TODO: BUNGEEGROUP getRawPrefix PermissionPlayer"; // TODO
		return null;
	}
	
	
	/**
	 * Returns the suffix of this player or
	 * null if using groups suffix
	 * @return
	 */
	public String getSuffix() {
		if(this.groupData.getSuffix() != null) return this.groupData.getSuffix();
		if(this.group.getSuffix() != null) return this.group.getSuffix();
		if(true) return "TODO: BungeeGroup getSuffix"; // TODO
		return null;
	}
	
	
	/**
	 * Returns the players groupname, or null
	 * if using actual groups group name
	 * @return
	 */
	public String getGroupName(CommandSender sender, boolean shortened) {
		if(shortened) {
			if(this.groupData.getGroupShortName() != null) return this.groupData.getGroupShortName();
			if(this.group.getShortName(sender) != null) return this.group.getShortName(sender);
			if(true) return "TODO: getGroupName BungeeGroup PermissionPlayer"; //TODO
		} else {
			if(this.groupData.getGroupName() != null) return this.groupData.getGroupName();
			if(this.group.getName(sender) != null) return this.group.getName(sender);
			if(true) return "TODO: getGroupName BungeeGroup PermissionPlayer"; //TODO
		}
		
		return null;
	}
	
	
	/**
	 * Returns the players groupname, or null
	 * if using actual groups group name
	 * @return
	 */
	public String getGroupName(Locale locale, boolean shortened) {
		if(shortened) {
			if(this.groupData.getGroupShortName() != null) return this.groupData.getGroupShortName();
			if(this.group.getShortName(locale) != null) return this.group.getShortName(locale);
			if(true) return "TODO: getGroupName BungeeGroup PermissionPlayer"; //TODO
		} else {
			if(this.groupData.getGroupName() != null) return this.groupData.getGroupName();
			if(this.group.getName(locale) != null) return this.group.getName(locale);
			if(true) return "TODO: getGroupName BungeeGroup PermissionPlayer"; //TODO
		}
		
		return null;
	}
	
	
	/**
	 * Gets the global group player belongs to
	 * @return global group player belongs to
	 */
	public BungeeGroup getBungeeGroup() {
		return this.globalGroup;
	}
	
	
	/**
	 * Sets the player global group. To keep the change, call save()
	 * @param group the global group for player
	 */
	public BungeeGroup setBungeeGroup(BungeeGroup group) {
		// TODO call bungee to update the group
		
		if(this.globalGroup.equals(group)) return this.globalGroup;
		BungeeGroup g = this.globalGroup;
		
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline())
			this.globalGroup.unregisterUser(player);
		this.globalGroup = group;
		
		if(player != null && player.isOnline())
			this.globalGroup.registerUser(player);

		// global group changes should be handled from bungee, not spigot
		
		return g;
	}
	
	
	/**
	 * Returns the group of this player
	 * @return
	 */
	public Group getGroup() { 
		return this.group; 
	}
	
	
	/**
	 * Sets the players group. To save the change call save()
	 * @param group the new group for player
	 * @return the previous group of the player
	 */
	public Group setGroup(Group group) {
		// if the group is the same don't change
		if(this.group.equals(group)) return this.group;
		
		Group g = this.group;
		// if player is online remember to modify existing player data directly
		Player player = Bukkit.getPlayer(uuid);
		
		if(player != null && player.isOnline())
			this.group.unregisterUser(player);
		this.group = group;
		
		if(player != null && player.isOnline())
			this.group.registerUser(player);
		this.changedGroup = true;
		
		return g;
	}
	
	
	/**
	 * Saves the player's data
	 * @return
	 */
	public boolean save() {
		GroupDatabase gdb = new GroupDatabase();
		boolean success = true;
		
		if(this.changedPlayerInformation)
		if(!gdb.setPrivateGroupData(getUUID(), groupData))
			success = false;
		if(this.changedGroup)
		if(!gdb.setLocalGroup(getUUID(), group))
			success = false;
		
		
		// TODO call to bungee to save global group change
		return success;
	}
	
	
	/**
	 * Sets and saves the suffix of this player
	 * @param suffix
	 */
	public void setSuffix(String suffix) {
		this.changedPlayerInformation = true;
		this.groupData.setSuffix(suffix);
	}
	
	
	/**
	 * Returns the groupdata of this class
	 * @return
	 */
	protected GroupData getGroupData() { 
		return this.groupData; 
	}
	
	
	/**
	 * Reset the suffix of this player
	 * @return
	 */
	public void resetSuffix() {
		this.changedPlayerInformation = true;
		this.groupData.setSuffix(null);
	}
	
	
	/**
	 * Sets and saves the prefix of this player
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		this.changedPlayerInformation = true;
		this.groupData.setPrefix(prefix);
	}
	
	
	/**
	 * Resets the prefix of this player
	 * @return
	 */
	public void resetPrefix() {
		this.changedPlayerInformation = true;
		this.groupData.setPrefix(null);
	}
	
	
	/**
	 * Sets and saves the groupname for given player.
	 * Notice: no translation available for this!
	 * If translation is needed, please modify the actual group 
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.changedPlayerInformation = true;
		this.groupData.setGroupName(groupName);
	}
	
	
	/**
	 * Resets the local groupname of the player
	 */
	public void resetGroupName() {
		this.changedPlayerInformation = true;
		groupData.setGroupName(null);
	}
	
	
	/**
	 * Reset the players group name to use players groups group name
	 * @param groupShortName
	 * @return
	 */
	public void setGroupShortName(String groupShortName) {
		this.changedPlayerInformation = true;
		this.groupData.setGroupShortName(groupShortName);
	}
	
	
	/**
	 * Reset the players shortened groupname to use
	 * the normal groups name
	 * @return
	 */
	public void resetGroupShortName() {
		this.groupData.setGroupShortName(null);
		this.changedPlayerInformation = true;
	}
	
	
	/**
	 * Checks whether this player has a custom groupname
	 * @return
	 */
	public boolean hasCustomGroupName() { 
		return this.groupData.getGroupName() != null; 
	}
	
	
	/**
	 * Checks whether this player has a custom prefix
	 * @return
	 */
	public boolean hasCustomPrefix() { 
		return this.groupData.getPrefix() != null; 
	}
	
	
	/**
	 * Checks if this player has a custom suffix
	 * @return
	 */
	public boolean hasCustomSuffix() { 
		return this.groupData.getSuffix() != null; 
	}
	
	
	/**
	 * Checks if this player has custom shortened group name
	 * @return
	 */
	public boolean hasCustomShortGroupName() { 
		return this.groupData.getGroupShortName() != null; 
	}
	
	
	/**
	 * Adds and saves a given permission to player
	 * @param permission
	 * @return
	 */
	public QueryState addPermission(String permission) { 
		// add the permission to the player
		if(this.pAttachment != null) {
			ExtendedPermission ePermission = new ExtendedPermission(permission);
			this.pAttachment.setPermission(ePermission.getPermission(), ePermission.isPositive());
			this.groupData.addPermission(permission);
		}
		
		// add and save a new permission for player
		return (new GroupDatabase()).addPlayerPermission(getUUID(), permission);
	}
	
	
	/**
	 * Removes and saves a given permission to player
	 * @param permission
	 * @return
	 */
	public QueryState removePermission(String permission) {
		// remove this permission from player
		if(this.pAttachment != null) {
			ExtendedPermission ePermission = new ExtendedPermission(permission);
			this.pAttachment.unsetPermission(ePermission.getPermission());
			this.groupData.removePermission(permission);
		}
		
		// save the removal
		return (new GroupDatabase()).removePlayerPermission(getUUID(), permission);
	}
	
	
	/**
	 * Returns the uuid of the given player
	 * @return
	 */
	public UUID getUUID() { 
		return this.uuid; 
	}
}
