package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;


/**
 * This class is responsible for updating and giving out
 * @author Nuubles
 *
 */
public class PermissionPlayer {
	//private final static String GROUP_DATA_KEY = "GROUP_DATA"; // TODO Cacheable data
	//private final static String GROUP_KEY = "GROUP";
	private final KaranteeniPermsBungee plugin = KaranteeniPermsBungee.getInstance();
	private GroupData groupData;
	private UUID uuid;
	private Group group;
	private boolean changedGroup = false;
	private boolean changedPlayerInformation = false;
	private HashMap<UUID, PermissionAttachment> attachments = new HashMap<UUID, PermissionAttachment>();
	private PermissionAttachment mainAttachment;
	
	
	private PermissionPlayer(UUID uuid) {
		this.uuid = uuid;
	}
	
	
	/**
	 * Sets the player of this PermissionPlayer. Valid only after load. PermissionAttachment generated and permissions
	 * for the player set
	 * @return true if player was loaded, false otherwise
	 */
	protected boolean activatePlayer() {
		ProxiedPlayer player = KaranteeniPermsBungee.getInstance().getProxy().getPlayer(uuid);
		
		if(player == null) return false;
		
		this.mainAttachment = this.addAttachment();
		// generate and set the permission attachment of the player
		for(ExtendedPermission ep : this.groupData.getBungeePermissions())
			this.mainAttachment.addPermission(ep);
		
		// register player to use this group
		this.group.registerUser(player);
		// store data to KPlayer
		
		return true;
	}
	
	
	/**
	 * Adds a permission attachment to this player
	 * @param attachment
	 */
	public PermissionAttachment addAttachment() {
		PermissionAttachment attachment = new PermissionAttachment();
		this.attachments.put(attachment.getUniqueId(), attachment);
		return attachment;
	}
	
	
	/**
	 * Removes a permission attachment from this player
	 * @param attachment attachment to remove
	 * @return attachment removed
	 */
	public PermissionAttachment removeAttachment(PermissionAttachment attachment) {
		return this.attachments.remove(attachment.getUniqueId());
	}
	
	
	/**
	 * Deactivates the player permissions. For example in quit event
	 */
	protected boolean deActivatePlayer() {
		// unregister player from the given groups
		ProxiedPlayer player = this.plugin.getProxy().getPlayer(uuid);
		if(player != null && this.group != null) 
			return this.group.unregisterUser(player);
		return false;
	}
	
	
	/**
	 * Loads the private groupdata of this permissionplayer to memory
	 * @return true if load was successful, false otherwise
	 */
	private boolean loadGroupData() {
		// load the private group data and permissions from database
		GroupData gd = plugin.getPlayerModel().getGroupDatabase().getPrivateGroupData(uuid);
		if(gd == null) return false;
		
		this.groupData = gd;
			
		return true;
	}
	
	
	/**
	 * Loads and sets the group of this PermissionPlayer
	 * @return true if load was successful, false otherwise
	 */
	private boolean loadGroup() {
		Group group = plugin.getPlayerModel().getGroupDatabase().loadGroup(uuid);
		if(group == null) return false;
		this.group = group;
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
		//TODO send data to spigot
		
		return pp;
	}
	
	
	/**
	 * Checks if this player has a given permission
	 * @param permission permission to check for
	 * @param exact whether the checked permission should match exactly
	 * @return
	 */
	public boolean hasPermission(String permission, boolean exact) {
		boolean hasPermission = false;
		
		hasPermission = this.group.hasPermission(permission, exact);
		
		for(PermissionAttachment attch : attachments.values()) {
			if(!attch.isPermissionNegated(permission)) {
				if(!hasPermission) {
					hasPermission = attch.hasPermission(permission, exact);
				}
			} else {
				hasPermission = false;
			}
		}
		
		return hasPermission;
	}
	
	
	/**
	 * Checks if this player has the given permission
	 * @param permission permission to have
	 * @return true if player has this permission, false otherwise
	 */
	public boolean hasPrivatePermission(String permission, boolean isSpigot) {
		return this.groupData.hasPermission(permission, isSpigot);
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
		return prefix;
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
		return prefix;
	}
	
	
	/**
	 * Returns the raw prefix of this player
	 * @return
	 */
	public String getRawPrefix() {
		if(groupData.getPrefix() != null) return groupData.getPrefix();
		if(group.getRawPrefix() != null) return group.getRawPrefix();
		return null;
	}
	
	
	/**
	 * Returns the suffix of this player or if not set then groups suffix
	 * @return
	 */
	public String getSuffix() {
		if(this.groupData.getSuffix() != null) return this.groupData.getSuffix();
		if(this.group.getSuffix() != null) return this.group.getSuffix();
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
		} else {
			if(this.groupData.getGroupName() != null) return this.groupData.getGroupName();
			if(this.group.getName(sender) != null) return this.group.getName(sender);
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
		} else {
			if(this.groupData.getGroupName() != null) return this.groupData.getGroupName();
			if(this.group.getName(locale) != null) return this.group.getName(locale);
		}
		
		return null;
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
		ProxiedPlayer player = this.plugin.getProxy().getPlayer(uuid);
		
		if(player != null)
			this.group.unregisterUser(player);
		this.group = group;
		
		if(player != null)
			this.group.registerUser(player);
		this.changedGroup = true;
		
		return g;
	}
	
	
	/**
	 * Saves the player's data
	 * @return
	 */
	public boolean save() {
		BungeeGroupDatabase gdb = new BungeeGroupDatabase();
		boolean success = true;
		
		if(this.changedPlayerInformation)
		if(!gdb.setPrivateGroupData(getUUID(), groupData))
			success = false;
		if(this.changedGroup)
		if(gdb.saveGroup(getUUID(), group) == QueryState.INSERTION_SUCCESSFUL)
			success = false;
		
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
	public QueryState addPermission(String permission, boolean isSpigot) { 
		// add the permission to the player
		if(this.mainAttachment != null) {
			if(!isSpigot)
				this.mainAttachment.addPermission(permission);
			this.groupData.addPermission(permission, isSpigot);
		}
		
		// add and save a new permission for player
		return (new BungeeGroupDatabase()).addPlayerPermission(getUUID(), permission, isSpigot);
	}
	
	
	/**
	 * Removes and saves a given permission to player
	 * @param permission
	 * @return
	 */
	public QueryState removePermission(String permission, boolean isSpigot) {
		// remove this permission from player
		if(this.mainAttachment != null) {
			if(!isSpigot)
				this.mainAttachment.removePermission(permission);
			this.groupData.removePermission(permission, isSpigot);
		}
		
		// save the removal
		return (new BungeeGroupDatabase()).removePlayerPermission(getUUID(), permission, isSpigot);
	}
	
	
	/**
	 * Returns the uuid of the given player
	 * @return
	 */
	public UUID getUUID() { 
		return this.uuid; 
	}
}
