package net.karanteeni.groups.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

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
	
	/** Permissions of players */
	private HashMap<UUID,PermissionAttachment> playerPermissions = new HashMap<UUID,PermissionAttachment>();
	
	/**
	 * Creates a new playerdata handler
	 * @param pl
	 */
	public PlayerModel(KaranteeniPerms pl) throws SQLException
	{
		this.plugin = pl;
		db = new GroupDatabase(pl); //May throw SQLException		
	}
	
	/**
	 * Load and set players permissions
	 * @param uuid
	 */
	protected void loadOnlinePlayerPermissions(Player player, Group group)
	{
		clearOnlinePlayerPermissions(player); //Clear all permissions from player
		
		PermissionAttachment attachment = player.addAttachment(plugin);
		this.playerPermissions.put(player.getUniqueId(), attachment);
		
		//Get players group and load the permissions
		for(Permission perm : group.getPermissions())
			setPermissionToAttachment(attachment, perm);
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
		if(!attch.getPermissions().containsKey(perm))
			attch.setPermission(perm, !isNegation);
		else if(attch.getPermissions().containsKey(perm) && attch.getPermissions().get(perm))
			attch.setPermission(perm, !isNegation);
		
		Bukkit.broadcastMessage(perm.getName());
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
	 */
	protected void loadOnlinePlayerPrivatePermissions(Player player)
	{
		//If player is not already in map, put it there
		if(!this.playerPermissions.containsKey(player.getUniqueId()))
			this.playerPermissions.put(player.getUniqueId(), player.addAttachment(plugin));
		
		//Load private permissions from database
		Collection<Permission> perms = db.getPrivatePermissions(player.getUniqueId());
		PermissionAttachment attch = this.playerPermissions.get(player.getUniqueId());
		for(Permission perm : perms)
			setPermissionToAttachment(attch, perm);
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
	protected GroupDatabase(KaranteeniPerms permPlugin) throws SQLException
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
		
		stmt.execute("CREATE TABLE IF NOT EXISTS permissions ("
				+ "UUID VARCHAR(60) NOT NULL," //Players UUID
				+ "permission VARCHAR(128) NOT NULL," //ID of the group
				+ "serverID VARCHAR(64) NOT NULL," //ID of the server
				+ "FOREIGN KEY (UUID) REFERENCES player(UUID),"
				+ "FOREIGN KEY (serverID) REFERENCES server(ID),"
				+ "UNIQUE (permission),"
				+ "PRIMARY KEY (UUID,serverID));");
	}
	
	/**
	 * Returns the permissions a certain player has 
	 * @param uuid UUID of the player
	 * @return private permissions this player has
	 */
	protected Collection<Permission> getPrivatePermissions(UUID uuid)
	{
		String statement = 
				"SELECT permission " +
				"FROM permissions " +
				"WHERE UUID = ? " +
				"AND serverID = ?;";
		
		PreparedStatement st = KaranteeniPerms.getDatabaseConnector().prepareStatement(statement);
		List<Permission> perms = new ArrayList<Permission>(); //Players loaded group
		
		try {
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			//Get all the permissions from the database query
			while(set.next())
				perms.add(new Permission(set.getString(1)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Return the group with ID from database
		return perms;
	}
	
	/**
	 * Adds a new private permission to player
	 * @param uuid
	 * @param permission
	 * @return
	 */
	@Deprecated
	protected boolean addPrivatePermission(UUID uuid, String permission)
	{
		return false;
	}
	
	/**
	 * Removes a private permission from player
	 * @param uuid
	 * @param permission
	 * @return
	 */
	@Deprecated
	protected boolean removePrivatePermission(UUID uuid, String permission)
	{
		return false;
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
	 * Sets the local group of player
	 * @param uuid UUID of the player being updated
	 * @param group Group which is to be the future group of player
	 * @return true if insert/update was successful, false if no update happened
	 */
	public boolean setLocalGroup(UUID uuid, Group group)
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
}






/**
 * Manages the connections to the database,
 * no access to this class from other classes than PlayerData to
 * prevent any breakage
 * @author Nuubles
 *
 */
/*class Database {
	KaranteeniPerms plugin;
	
	public Database(KaranteeniPerms plugin) throws SQLException
	{
		this.plugin = plugin;
		Statement st = KaranteeniPerms.getDatabaseConnector().getStatement();
		String query = 
				"CREATE TABLE IF NOT EXISTS groups"
				+ "(UUID VARCHAR(64),"
				+ "id VARCHAR(36),"
				+ "server VARCHAR(32),"
				+ "PRIMARY KEY (UUID,server),"
				+ "FOREIGN KEY (UUID) REFERENCES player.UUID;";
		
		st.executeQuery(query);
		
		query = 
				"CREATE TABLE IF NOT EXISTS globalgroups"
				+ "(UUID VARCHAR(64),"
				+ "id VARCHAR(36),"
				+ "PRIMARY KEY (UUID),"
				+ "FOREIGN KEY (UUID) REFERENCES player.UUID;";
		st.executeQuery(query);
		
		query = 
				"CREATE TABLE IF NOT EXISTS globallevel"
				+ "(UUID VARCHAR(64),"
				+ "level INT NOT NULL DEFAULT 0,"
				+ "PRIMARY KEY (UUID),"
				+ "FOREIGN KEY (UUID) REFERENCES player.UUID;";
		st.executeQuery(query);
		
		st.close();
	}*/

	/**
	 * Sets the group for a player with UUID
	 * @param group
	 * @param uuid
	 * @return was the group set successful
	 */
	/*public boolean updateGroup(Group group, UUID uuid)
	{
		String query = 
				"UPDATE groups "
				+ "SET id = ?,"
				+ "WHERE UUID = ?;";
		PreparedStatement stmt = KaranteeniPerms.getDatabaseConnector().prepareStatement(query);
		try {
			stmt.setString(0, group.getID());
			stmt.setString(1, uuid.toString());
			return stmt.executeUpdate() == 1; // Returns true if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/
	
	/**
	 * Sets the group for a player with UUID
	 * @param group
	 * @param uuid
	 * @return was the group set successful
	 */
	/*public boolean updateGlobalGroup(Group group, UUID uuid)
	{
		String query = 
				"UPDATE globalgroups "
				+ "SET id = ?,"
				+ "WHERE UUID = ?;";
		PreparedStatement stmt = KaranteeniPerms.getDatabaseConnector().prepareStatement(query);
		try {
			stmt.setString(0, group.getID());
			stmt.setString(1, uuid.toString());
			return stmt.executeUpdate() == 1; // Returns true if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/
	
	/**
	 * Sets the group for a player with UUID
	 * @param group
	 * @param uuid
	 * @return was the group set successful
	 */
	/*public boolean updateLevel(int level, UUID uuid)
	{
		String query = 
				"UPDATE globallevel "
				+ "SET level = ?,"
				+ "WHERE UUID = ?;";
		PreparedStatement stmt = KaranteeniPerms.getDatabaseConnector().prepareStatement(query);
		try {
			stmt.setInt(0, level);
			stmt.setString(1, uuid.toString());
			return stmt.executeUpdate() == 1; // Returns true if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/
	
	/**
	 * Inserts a new row to the database
	 * @param group
	 * @param uuid
	 * @return was the group set successful
	 */
	/*public boolean insertGroup(Group group, UUID uuid)
	{
		String query =  "INSERT INTO groups (UUID, id, server) VALUES (?,?,?);";
		PreparedStatement stmt = KaranteeniPerms.getDatabaseConnector().prepareStatement(query);
		try {
			stmt.setString(0, uuid.toString());
			stmt.setString(1, group.getID());
			stmt.setString(2, KaranteeniPerms.getServerIdentificator());
			return stmt.executeUpdate() == 1; // Returns true if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/
	
	/**
	 * Inserts a new row to the database
	 * @param group
	 * @param uuid
	 * @return was the group set successful
	 */
	/*public boolean insertGlobalGroup(Group group, UUID uuid)
	{
		String query = "INSERT INTO globalgroups (UUID, id) VALUES (?,?);";
		PreparedStatement stmt = KaranteeniPerms.getDatabaseConnector().prepareStatement(query);
		try {
			stmt.setString(0, uuid.toString());
			stmt.setString(1, group.getID());
			return stmt.executeUpdate() == 1; // Returns true if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/
	
	/**
	 * Inserts a new row to the database
	 * @param group
	 * @param uuid
	 * @return was the group set successful
	 */
	/*public boolean insertLevel(int level, UUID uuid)
	{
		String query = "INSERT INTO globallevel (UUID, level) VALUES (?,?);";
		PreparedStatement stmt = KaranteeniPerms.getDatabaseConnector().prepareStatement(query);
		try {
			stmt.setString(0, uuid.toString());
			stmt.setInt(1, level);
			return stmt.executeUpdate() == 1; // Returns true if successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/
	
	/**
	 * Get the group of player
	 * @param player
	 * @return
	 */
	/*public int getLevel(Player player)
	{
		return getLevel(player.getUniqueId());
	}*/
	
	/**
	 * Get the group by players UUID
	 * @param uuid UUID of the player whos globalgroup will be returned
	 * @return Group of the player or null
	 */
	/*public int getLevel(UUID uuid)
	{
		Statement st = KaranteeniPerms.getDatabaseConnector().getStatement();
		String query =
				"SELECT level "
				+ "FROM player, globallevel "
				+ "WHERE player.UUID = " + uuid.toString() +
				  " AND player.UUID = globallevel.UUID;";
		try {
			ResultSet set = st.executeQuery(query);
			if(set.next())
				return set.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		return -1;
	}*/
	
	/**
	 * Get the group of player
	 * @param player
	 * @return
	 */
	/*public Group getGlobalGroup(Player player)
	{
		return getGroup(player.getUniqueId());
	}*/
	
	/**
	 * Get the group by players UUID
	 * @param uuid UUID of the player whos globalgroup will be returned
	 * @return Group of the player or null
	 */
	/*public Group getGlobalGroup(UUID uuid)
	{
		Statement st = KaranteeniPerms.getDatabaseConnector().getStatement();
		String query =
				"SELECT id "
				+ "FROM player, globalgroups "
				+ "WHERE player.UUID = " + uuid.toString() +
				  " AND player.UUID = groups.UUID;";
		try {
			ResultSet set = st.executeQuery(query);
			if(set.next())
				return plugin.getGroupList().getGroup(set.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}*/
	
	/**
	 * Get the group of player
	 * @param player
	 * @return
	 */
	/*public Group getGroup(Player player)
	{
		return getGroup(player.getUniqueId());
	}*/
	
	/**
	 * Get the group by players UUID
	 * @param uuid
	 * @return Group of the player or null
	 */
/*	public Group getGroup(UUID uuid)
	{
		Statement st = KaranteeniPerms.getDatabaseConnector().getStatement();
		String query =
				"SELECT name "
				+ "FROM player, groups "
				+ "WHERE player.UUID = " + uuid.toString() +
				  " AND player.UUID = groups.UUID AND groups.server = " + 
				KaranteeniPerms.getServerIdentificator() + ";";
		try {
			ResultSet set = st.executeQuery(query);
			if(set.next())
				return plugin.getGroupList().getGroup(set.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
}*/
