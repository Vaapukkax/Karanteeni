package net.karanteeni.groups.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.groups.KaranteeniPerms;

/**
 * Contains the connection to the database
 * and manages the caching. Is used to get the group of a player.
 * LocalGroup, GlobalGroup, GlobalLevel
 * @author Nuubles
 *
 */
public class PlayerModel {

	private GroupDatabase db;
	private KaranteeniPerms plugin;
	private final static String GROUP_KEY = "GROUP";
	private final static String GROUP_DATA_KEY = "GROUP_DATA";
	
	/** Permissions of players */
	private HashMap<UUID,PermissionAttachment> playerPermissions = new HashMap<UUID,PermissionAttachment>();
	
	/**
	 * Creates a new playerdata handler
	 * @param pl
	 */
	public PlayerModel(KaranteeniPerms pl) throws SQLException
	{
		this.plugin = pl;
		db = new GroupDatabase(); //May throw SQLException		
		this.plugin = pl;
	}
	
	/**
	 * Returns the permissionplayer according to given UUID
	 * @param uuid
	 * @return
	 */
	public PermissionPlayer getPermissionPlayer(UUID uuid)
	{
		PermissionPlayer pl = new PermissionPlayer(
				uuid, 
				getLocalGroup(uuid), 
				getGroupData(uuid),
				this::savePermissionPlayerData,
				this::savePermissionPlayerData,
				this::savePermissionPlayerData,
				this::savePermissionPlayerData,
				this::savePermissionPlayerData,
				this::savePermissionPlayerData, 
				this::savePermissionPlayerData, 
				this::savePermissionPlayerData);
		
		return pl;
	}
	
	/**
	 * Returns the private GroupData from database or cache.
	 * For example, custom prefix and suffix only for this player
	 * @param uuid
	 * @return
	 */
	private GroupData getGroupData(UUID uuid)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player == null || !player.isOnline())
			return db.getPrivateGroupData(uuid, null);
		return this.getGroupData(KPlayer.getKPlayer(player));
	}
	
	/**
	 * Returns the private GroupData from database or cache
	 * @param kplayer
	 * @return
	 */
	private GroupData getGroupData(KPlayer kplayer)
	{
		//Player group is in cache, load from cache
		if(kplayer.dataExists(plugin, GROUP_DATA_KEY))
			return (GroupData)kplayer.getData(plugin, GROUP_DATA_KEY);
		else
		{
			List<String> perms = new ArrayList<String>();
			for(Entry<String,Boolean> perm : this.playerPermissions.get(kplayer.getPlayer().getUniqueId()).getPermissions().entrySet())
			{
				if(perm.getValue())
					perms.add(perm.getKey());
				else
					perms.add("-"+perm.getKey()); //Load the negation perms
			}
			
			if(perms.isEmpty())
				perms = null;
			
			//Load the group from database
			GroupData groupdata = db.getPrivateGroupData(kplayer.getPlayer().getUniqueId(), perms);
			
			if(groupdata == null)
				return null;
			
			//Update the group to the cache if not loaded already
			kplayer.setData(plugin, GROUP_DATA_KEY, groupdata);
			return groupdata;
		}
	}
	
	/**
	 * Save the custom data of player
	 * @param player Player data pack to save
	 * @return was the save successful
	 */
	private boolean savePermissionPlayerData(PermissionPlayer player)
	{ return db.setPrivateGroupData(player.getUUID(), player.getGroupData()); }
	
	/*private boolean savePermissionPlayerPrefix(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean savePermissionPlayerSuffix(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean savePermissionPlayerRankName(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean savePermissionPlayerRankShortName(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean resetPermissionPlayerPrefix(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean resetPermissionPlayerSuffix(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean resetPermissionPlayerRankName(PermissionPlayer player)
	{
		return false;
	}
	
	private boolean resetPermissionPlayerRankShortName(PermissionPlayer player)
	{
		return false;
	}*/
	
	/**
	 * Load and set players permissions
	 * @param uuid
	 */
	protected void loadOnlinePlayerGroupPermissions(Player player, Group group)
	{
		clearOnlinePlayerPermissions(player); //Clear all permissions from player
		
		PermissionAttachment attachment = player.addAttachment(plugin);
		this.playerPermissions.put(player.getUniqueId(), attachment);
		
		//Get players group and load the permissions
		for(String perm : group.getPermissions())
			setPermissionToAttachment(attachment, new Permission(perm));
	}
	
	/**
	 * Adds the given permission to player
	 * @param uuid
	 * @param perm
	 */
	public void addPermissionToPlayer(UUID uuid, String perm)
	{
		PermissionAttachment attch = this.playerPermissions.get(uuid);
		if(attch != null)
			setPermissionToAttachment(attch, new Permission(perm));
	}
	
	/**
	 * Removes a given permission from player
	 * @param uuid
	 * @param perm
	 */
	public void removePermissionFromPlayer(UUID uuid, String perm)
	{
		PermissionAttachment attch = this.playerPermissions.get(uuid);
		if(attch != null)
			removePermissionFromAttachment(attch, new Permission(perm));
	}
	
	/**
	 * Handles negation permission etc. and adds permissions to
	 * PermissionAttachments accordingly
	 * @param attch
	 * @param perm
	 */
	private void setPermissionToAttachment(PermissionAttachment attch, Permission perm)
	{
		String realPerm = perm.getName();
		boolean isNegation = realPerm.charAt(0) == '-';

		if(isNegation)
			realPerm = realPerm.substring(1, realPerm.length());
		
		//If there's no text, just return
		if(realPerm.length() <= 0)
			return;
		
		perm = new Permission(realPerm);
		
		//Handle negation perms
		if(!attch.getPermissions().containsKey(perm.getName()))
			attch.setPermission(perm, !isNegation);
		else if(isNegation && attch.getPermissions().get(perm.getName()))
			attch.setPermission(perm, false);
	}
	
	/**
	 * Removes a given permission from permissionattachment.
	 * BUG: When removing negation perm it just sets it to true!
	 * @param attch
	 * @param perm
	 * @return Was the permission removed
	 */
	private boolean removePermissionFromAttachment(PermissionAttachment attch, Permission perm)
	{
		String realPerm = perm.getName();
		boolean isNegation = realPerm.charAt(0) == '-';
		
		if(isNegation)
			realPerm = realPerm.substring(1, realPerm.length());
		
		//If there's no text, just return
		if(realPerm.length() <= 0)
			return false;
		
		perm = new Permission(realPerm);
		
		//Handle negation perms
		if(!attch.getPermissions().containsKey(perm.getName()))
			return false;
		else if(attch.getPermissions().get(perm.getName()) && !isNegation)
		{
			attch.unsetPermission(perm);
			return true;
		}
		else if(!attch.getPermissions().get(perm.getName()) && isNegation)
		{
			attch.setPermission(perm, true); //Negation perm was removed
			return true;
		}
		else
			return false;
			
	}
	
	/**
	 * Adds a permission to player and saves it to the database
	 * @param uuid UUID of player
	 * @param permission Permission to give to player
	 * @param save Do we save this to the database
	 * @return null if there is no save, otherwise returned value from database save
	 */
	protected QueryState addAndSavePlayerPermission(UUID uuid, String permission, boolean save)
	{
		PermissionAttachment attch = this.playerPermissions.get(uuid);

		//Set the permission to cache
		if(attch != null)
			this.setPermissionToAttachment(attch, new Permission(permission));
		//Save new permission to the database
		if(attch != null && save)
			return this.db.addPlayerPermission(uuid, permission);
		return null;
	}
	
	/**
	 * Removes a permission from player and saves the removed value to database if
	 * so set.
	 * @param uuid UUID of player
	 * @param permission permission to remove
	 * @param save Do we save this modification to database?
	 * @return The querystate from database or null if no save needed
	 */
	protected QueryState removePlayerPermission(UUID uuid, String permission, boolean save)
	{
		PermissionAttachment attch = this.playerPermissions.get(uuid);

		//Set the permission to cache
		if(attch != null)
			this.removePermissionFromAttachment(attch, new Permission(permission));
		//Save new permission to the database
		if(attch != null && save)
			return this.db.removePlayerPermission(uuid, permission);
		return null;
	}
	
	/**
	 * Clears all permissions of an online player. Does not save
	 * @param player
	 */
	protected void clearOnlinePlayerPermissions(Player player)
	{
		this.playerPermissions.remove(player.getUniqueId());
	}
	
	/**
	 * Loads players own permissions. Does not clear permissions
	 * @param player
	 * @return Was the addition successful
	 */
	protected boolean loadOnlinePlayerPrivatePermissions(Player player)
	{
		//If player is not already in map, put it there
		if(!this.playerPermissions.containsKey(player.getUniqueId()))
			this.playerPermissions.put(player.getUniqueId(), player.addAttachment(plugin));
		
		//Load private permissions from database
		Collection<String> perms = db.getPrivatePermissions(player.getUniqueId());
		
		if(perms == null)
			return false;
		
		PermissionAttachment attch = this.playerPermissions.get(player.getUniqueId());
		for(String perm : perms)
			setPermissionToAttachment(attch, new Permission(perm));
		return true;
	}
	
	/**
	 * Clears all permissions players have
	 */
	public void clearAllPlayersPermissions()
	{ this.playerPermissions.clear(); }
	
	/**
	 * Return the group of an uuid
	 * @param uuid
	 * @return
	 */
	public Group getLocalGroup(UUID uuid)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player == null) //Return the group for offline player
			return db.getLocalGroup(plugin.getGroupModel().getLocalGroupList(), uuid);
		
		//Return the group for online player
		return this.getLocalGroup(player);
	}
	
	/**
	 * Return the group of a player
	 * @param player
	 * @return
	 */
	public Group getLocalGroup(Player player)
	{
		if(player.isOnline()) //Return the group of online player
			return getLocalGroup(KPlayer.getKPlayer(player));
		else //Return the group of offline player
			return getLocalGroup(player.getUniqueId());
	}
	
	/**
	 * Return the group of a player
	 * @param kplayer
	 * @return
	 */
	public Group getLocalGroup(KPlayer kplayer)
	{
		//Player group is in cache, load from cache
		if(kplayer.dataExists(plugin, GROUP_KEY))
			return (Group)kplayer.getData(plugin, GROUP_KEY);
		else
		{
			//Load the group from database
			Group group = db.getLocalGroup(
					plugin.getGroupModel().getLocalGroupList(), 
					kplayer.getPlayer().getUniqueId());
			
			if(group == null)
				return null;
			
			//Update the group to the cache if not loaded already
			kplayer.setData(plugin, GROUP_KEY, group);
			return group;
		}
	}
}

/**
 * This class handles the connections to the database from ModelCache.
 * No access should be given to other classes other than ModelCache.
 * @author Nuubles
 *
 */
class GroupDatabase {
	
	/**
	 * Permplugin, allow only this plugin to access this
	 * @param permPlugin
	 */
	protected GroupDatabase() throws SQLException
	{
		Statement stmt = KaranteeniPerms.getDatabaseConnector().getStatement();
		
		//Create table for local groups
		stmt.execute("CREATE TABLE IF NOT EXISTS groups ("
			+ "UUID VARCHAR(60) NOT NULL," //Players UUID
			+ "ID VARCHAR(64) NOT NULL," //ID of the group
			+ "serverID VARCHAR(64) NOT NULL," //ID of the server
			+ "FOREIGN KEY (UUID) REFERENCES player(UUID),"
			+ "FOREIGN KEY (serverID) REFERENCES server(ID),"
			+ "PRIMARY KEY (UUID,serverID));");
		
		//Create table for private permissions
		stmt.execute("CREATE TABLE IF NOT EXISTS permissions ("
				+ "UUID VARCHAR(60) NOT NULL," //Players UUID
				+ "permission VARCHAR(128) NOT NULL," //permission of player
				+ "serverID VARCHAR(64) NOT NULL," //ID of the server
				+ "FOREIGN KEY (UUID) REFERENCES player(UUID),"
				+ "FOREIGN KEY (serverID) REFERENCES server(ID),"
				+ "UNIQUE (permission),"
				+ "PRIMARY KEY (UUID,permission,serverID));");
		
		//Create table for custom group data
		stmt.execute("CREATE TABLE IF NOT EXISTS custom_group_data( "+
				"UUID VARCHAR(60) NOT NULL, "+
				"groupname VARCHAR(64) DEFAULT NULL, "+
				"groupnameshort VARCHAR(64) DEFAULT NULL, "+
				"prefix VARCHAR(64) DEFAULT NULL, "+
				"suffix VARCHAR(64) DEFAULT NULL, "+
				"serverID VARCHAR(64) NOT NULL, "+
				"FOREIGN KEY (UUID) REFERENCES player(UUID), "+
				"FOREIGN KEY (serverID) REFERENCES server(ID), "+
				"PRIMARY KEY (UUID,serverID));");
	}
	
	/**
	 * Returns the permissions a certain player has 
	 * @param uuid UUID of the player
	 * @return private permissions this player has
	 */
	protected Collection<String> getPrivatePermissions(UUID uuid)
	{
		String statement = 
				"SELECT permission " +
				"FROM permissions " +
				"WHERE UUID = ? " +
				"AND serverID = ?;";
		
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		List<String> perms = new ArrayList<String>(); //Players loaded group
		
		try {
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			//Get all the permissions from the database query
			while(set.next())
				perms.add(set.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Return the group with ID from database
		return perms;
	}
	
	/**
	 * Returns the group of a player using players UUID
	 * @param list list of groups from which the target group will be searched
	 * @param uuid
	 * @return Players group or default group if no access to database or player does not have a group
	 */
	protected Group getLocalGroup(GroupList list, UUID uuid)
	{
		String statement = 
				"SELECT UUID " +
				"FROM groups " +
				"WHERE UUID = ? " +
				"AND serverID = ?;";
		
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		Group playerGroup = null; //Players loaded group
		
		try {
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			if(!set.next()) //Empty, player does not have a group. Return default group
			{
				this.setLocalGroup(uuid, list.getDefaultGroup()); // Set the players group to default group
				return list.getDefaultGroup();
			}
			
			playerGroup = list.getGroup(set.getString(1)); //Get the first group player has in the database
			
			if(playerGroup == null) //If group no longer exists, set to default group
				playerGroup = list.getDefaultGroup();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		//Return the group with ID from database
		return playerGroup;
	}
	
	/**
	 * Returns the custom groupdata related to a players UUID
	 * @param uuid
	 * @return
	 */
	protected GroupData getPrivateGroupData(UUID uuid, List<String> perms)
	{
		String statement = "SELECT groupname, groupnameshort, prefix, suffix " +
				"FROM custom_group_data "+
				"WHERE UUID = ? AND serverID = ?;";
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		
		try{
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			if(set.next())
			{
				String groupname = set.getString(1);
				String groupnameShort = set.getString(2);
				String prefix = set.getString(3);
				String suffix = set.getString(4);
				if(perms == null)
					return new GroupData(prefix, suffix, groupname, groupnameShort, (List<String>)getPrivatePermissions(uuid));
				return new GroupData(prefix, suffix, groupname, groupnameShort, perms);
			}
			if(perms == null)
				return new GroupData(null,null,null,null,(List<String>)getPrivatePermissions(uuid));
			return new GroupData(null,null,null,null,perms);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Sets the players groupdata to given custom groupdata values
	 * @param uuid UUID of the player
	 * @param groupData groupdata for the player
	 * @return
	 */
	protected boolean setPrivateGroupData(UUID uuid, GroupData groupData)
	{
		String statement = 
				"INSERT INTO custom_group_data (UUID, groupname, groupnameshort, prefix, suffix, serverID)"+ 
				"VALUES (?,?,?,?,?,?) "+
				"ON DUPLICATE KEY UPDATE "+
				"groupname = VALUES(groupname), "+
				"groupnameshort = VALUES(groupnameshort),"+ 
				"prefix = VALUES(prefix)," +
				"suffix = VALUES(suffix);";
		
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		
		try{
			st.setString(1, uuid.toString());
			st.setString(2, groupData.getGroupName());
			st.setString(3, groupData.getGroupShortName());
			st.setString(4, groupData.getPrefix());
			st.setString(5, groupData.getSuffix());
			st.setString(6, KaranteeniPerms.getServerIdentificator());
			return st.executeUpdate() == 1;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Sets the local group of player
	 * @param uuid UUID of the player being updated
	 * @param group Group which is to be the future group of player
	 * @return true if insert/update was successful, false if no update happened
	 */
	protected boolean setLocalGroup(UUID uuid, Group group)
	{
		String statement = 
				"INSERT INTO groups (UUID, ID, serverID) "+
				"VALUES (?,?,?) "+
				"ON DUPLICATE KEY UPDATE "+
				"ID = ?;";
		
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		
		try {
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, group.getID());
			st.setString(3, KaranteeniPerms.getServerIdentificator());
			st.setString(4, group.getID());
			
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Return the group with ID from database
		return false;
	}
	
	/**
	 * Inserts a given permission to players data into database
	 * @param uuid UUID of player to add the permission to
	 * @param permission Permission to add to player
	 * @return was the insertion successful
	 */
	protected QueryState addPlayerPermission(UUID uuid, String permission)
	{
		String statement = "INSERT INTO permissions (UUID,permission,serverID) VALUES (?,?,?);";
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		
		try{
			st.setString(1, uuid.toString());
			st.setString(2, permission.toLowerCase());
			st.setString(3, KaranteeniPerms.getServerIdentificator());
			int updatedRows = st.executeUpdate();
			if(updatedRows > 0)
				return QueryState.INSERTION_SUCCESSFUL;
			else
				return QueryState.INSERTION_FAIL_ALREADY_EXISTS;
			
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Error on insertion to database!", e);
			return QueryState.INSERTION_FAIL_OTHER;
		}
	}
	
	/**
	 * Removes a permission from player from server database
	 * @param uuid UUID of player to remove the permission from
	 * @param permission Permission to remove
	 * @return was the deletion successful
	 */
	protected QueryState removePlayerPermission(UUID uuid, String permission)
	{
		String statement = "DELETE FROM permissions WHERE UUID = ? AND permission = ? AND serverID = ?;";
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		
		try{
			st.setString(1, uuid.toString());
			st.setString(2, permission.toLowerCase());
			st.setString(3, KaranteeniPerms.getServerIdentificator());
			int updatedRows = st.executeUpdate();
			if(updatedRows > 0)
				return QueryState.REMOVAL_SUCCESSFUL;
			else
				return QueryState.REMOVAL_FAIL_NO_DATA;
			
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Error on deletion from database!", e);
			return QueryState.REMOVAL_FAIL_OTHER;
		}	
	}
}