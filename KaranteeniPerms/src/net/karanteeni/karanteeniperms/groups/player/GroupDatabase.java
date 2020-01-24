package net.karanteeni.karanteeniperms.groups.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

/**
 * This class handles the connections to the database from ModelCache.
 * No access should be given to other classes other than ModelCache.
 * @author Nuubles
 *
 */
public class GroupDatabase {
	/**
	 * Initializes the database tables
	 * @throws SQLException
	 */
	public static void initialize() throws SQLException {
		Connection conn = null;
		conn = KaranteeniPerms.getDatabaseConnector().openConnection();
		Statement stmt = conn.createStatement();
		
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
		
		stmt.close();
		conn.close();
	}
	
	
	/**
	 * Returns the permissions a certain player has 
	 * @param uuid UUID of the player
	 * @return private permissions this player has
	 */
	protected Collection<String> getPrivatePermissions(UUID uuid) {
		String statement = 
				"SELECT permission " +
				"FROM permissions " +
				"WHERE UUID = ? " +
				"AND serverID = ?;";
		
		Connection conn = null;
		List<String> perms = new ArrayList<String>(); //Players loaded group
		
		try {
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(statement);
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			//Get all the permissions from the database query
			while(set.next())
				perms.add(set.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /*ignored*/ }
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
	protected Group getLocalGroup(GroupList list, UUID uuid) {
		String statement = 
				"SELECT UUID, ID " +
				"FROM groups " +
				"WHERE UUID = ? " +
				"AND serverID = ?;";
		
		Connection conn = null;
		
		Group playerGroup = null; //Players loaded group
		
		try {
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(statement);
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			if(!set.next()) //Empty, player does not have a group. Return default group
			{
				this.setLocalGroup(uuid, list.getDefaultGroup()); // Set the players group to default group
				return list.getDefaultGroup();
			}
			
			playerGroup = list.getGroup(set.getString(2)); //Get the first group player has in the database
			
			if(playerGroup == null) //If group no longer exists, set to default group
				playerGroup = list.getDefaultGroup();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) {}
		}
		
		//Return the group with ID from database
		return playerGroup;
	}
	
	
	/**
	 * Returns the custom groupdata related to a players UUID
	 * @param uuid
	 * @return
	 */
	protected GroupData getPrivateGroupData(UUID uuid, List<String> perms) {
		String statement = "SELECT groupname, groupnameshort, prefix, suffix " +
				"FROM custom_group_data "+
				"WHERE UUID = ? AND serverID = ?;";
		GroupData gd = null;
		Connection conn = null;
		try{
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(statement);
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPerms.getServerIdentificator());
			
			ResultSet set = st.executeQuery();
			
			if(set.next()) {
				String groupname = set.getString(1);
				String groupnameShort = set.getString(2);
				String prefix = set.getString(3);
				String suffix = set.getString(4);
				if(perms == null)
					gd = new GroupData(prefix, suffix, groupname, groupnameShort, (List<String>)getPrivatePermissions(uuid));
				else
					gd = new GroupData(prefix, suffix, groupname, groupnameShort, perms);
			} else if(perms == null) {
				gd = new GroupData(null,null,null,null,(List<String>)getPrivatePermissions(uuid));
			} else {
				gd = new GroupData(null,null,null,null,perms);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /*ignore*/ }
		}
		
		return gd;
	}
	
	
	/**
	 * Sets the players groupdata to given custom groupdata values
	 * @param uuid UUID of the player
	 * @param groupData groupdata for the player
	 * @return was the modification successful
	 */
	protected boolean setPrivateGroupData(UUID uuid, GroupData groupData) {
		String statement = 
				"INSERT INTO custom_group_data (UUID, groupname, groupnameshort, prefix, suffix, serverID)"+ 
				"VALUES (?,?,?,?,?,?) "+
				"ON DUPLICATE KEY UPDATE "+
				"groupname = VALUES(groupname), "+
				"groupnameshort = VALUES(groupnameshort),"+ 
				"prefix = VALUES(prefix)," +
				"suffix = VALUES(suffix);";
		Connection conn = null;
		boolean result = false;
		
		try{
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(statement);
			st.setString(1, uuid.toString());
			st.setString(2, groupData.getGroupName());
			st.setString(3, groupData.getGroupShortName());
			st.setString(4, groupData.getPrefix());
			st.setString(5, groupData.getSuffix());
			st.setString(6, KaranteeniPerms.getServerIdentificator());
			result = st.executeUpdate() > 0;
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
		
		return result;
	}
	
	
	/**
	 * Sets the local group of player
	 * @param uuid UUID of the player being updated
	 * @param group Group which is to be the future group of player
	 * @return true if insert/update was successful, false if no update happened
	 */
	protected boolean setLocalGroup(UUID uuid, Group group) {
		String statement = 
				"INSERT INTO groups (UUID, ID, serverID) "+
				"VALUES (?,?,?) "+
				"ON DUPLICATE KEY UPDATE "+
				"ID = ?;";
		
		Connection conn = null; 
		
		try {
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(statement);
			//Prepare the statements
			st.setString(1, uuid.toString());
			st.setString(2, group.getID());
			st.setString(3, KaranteeniPerms.getServerIdentificator());
			st.setString(4, group.getID());
			
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try { conn.close(); } catch (Exception e) { /* ignore */ }
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
	protected QueryState addPlayerPermission(UUID uuid, String permission) {
		Connection conn = null;
		
		try {
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			String statement = "INSERT INTO permissions (UUID,permission,serverID) VALUES (?,?,?);";
			PreparedStatement st = conn.prepareStatement(statement);
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
		} finally {
			if(conn != null)
				try { conn.close(); } catch (Exception e) { /* ignore */ }
		}
	}
	
	
	/**
	 * Removes a permission from player from server database
	 * @param uuid UUID of player to remove the permission from
	 * @param permission Permission to remove
	 * @return was the deletion successful
	 */
	protected QueryState removePlayerPermission(UUID uuid, String permission) {
		Connection conn = null;
		
		try{
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			String statement = "DELETE FROM permissions WHERE UUID = ? AND permission = ? AND serverID = ?;";
			PreparedStatement st = conn.prepareStatement(statement);
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
		} finally {
			if(conn != null)
				try { conn.close(); } catch (Exception e) { /* ignore */ }
		}
	}
}
