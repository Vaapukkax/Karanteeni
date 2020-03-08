package net.karanteeni.karanteeniperms.bungee.groups;

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
import net.karanteeni.core.data.ObjectPair;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;

public class BungeeGroupDatabase {
	
	/**
	 * Initializes the database tables
	 */
	public QueryState initialize() {
		Connection conn = null;
		QueryState result = QueryState.TABLE_CREATION_SUCCESSFUL;
		
		try {
			conn = KaranteeniPermsBungee.getDatabaseConnector().openConnection();
			// create the database table
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS bungeegroups ("+
					"UUID VARCHAR(60) NOT NULL,"+
					"ID VARCHAR(64) NOT NULL,"+
					"FOREIGN KEY (UUID) REFERENCES player(UUID),"+
					"PRIMARY KEY (UUID));");
			
			//Create table for private permissions
			stmt.execute("CREATE TABLE IF NOT EXISTS bungeepermissions ("
					+ "UUID VARCHAR(60) NOT NULL," //Players UUID
					+ "permission VARCHAR(128) NOT NULL," //permission of player
					+ "spigot BOOLEAN NOT NULL,"
					+ "FOREIGN KEY (UUID) REFERENCES player(UUID),"
					+ "PRIMARY KEY (UUID,permission));");
			
			//Create table for custom group data
			stmt.execute("CREATE TABLE IF NOT EXISTS custom_bungeegroup_data( "+
					"UUID VARCHAR(60) NOT NULL, "+
					"groupname VARCHAR(64) DEFAULT NULL, "+
					"groupnameshort VARCHAR(64) DEFAULT NULL, "+
					"prefix VARCHAR(64) DEFAULT NULL, "+
					"suffix VARCHAR(64) DEFAULT NULL, "+
					"FOREIGN KEY (UUID) REFERENCES player(UUID), "+
					"PRIMARY KEY (UUID));");
		} catch(SQLException e) {
			e.printStackTrace();
			result = QueryState.TABLE_CREATION_FAIL;
		} finally {
			try {
				// close the database connection
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				result = QueryState.TABLE_CREATION_FAIL;
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Saves the group of a given uuid to the database
	 * @param name name if the player whose group is being set
	 * @param group group to set to
	 * @return INSERTION_FAIL_ALREADY_EXISTS if no update, INSERTION_FAIL_NO_KEY if no player found, INSERTION_FAIL_OTHER
	 * on other errors
	 */
	public QueryState saveGroup(String name, Group group) {
		UUID uuid = KaranteeniPermsBungee.getPlayerHandler().getUUID(name);
		if(uuid == null)
			return QueryState.INSERTION_FAIL_NO_KEY;
		return saveGroup(uuid, group);
	}
	
	
	/**
	 * Saves the group of a given uuid to the database
	 * @param name name if the player whose group is being set
	 * @param group group to set to
	 * @return INSERTION_FAIL_ALREADY_EXISTS if no update, INSERTION_FAIL_OTHER on other errors
	 */
	public QueryState saveGroup(UUID uuid, Group group) {
		Connection conn = null;
		QueryState result = QueryState.INSERTION_SUCCESSFUL;
		
		try {
			conn = KaranteeniPermsBungee.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement( 
				"INSERT INTO bungeegroups (UUID, ID) "+
				"VALUES (?,?) "+
				"ON DUPLICATE KEY UPDATE "+
				"ID = ?;");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, group.getID());
			stmt.setString(3, group.getID());
			
			int updateCount = stmt.executeUpdate();
			
			if(updateCount == 0) {
				result = QueryState.INSERTION_FAIL_ALREADY_EXISTS;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			result = QueryState.INSERTION_FAIL_OTHER;
		} finally {
			try {
				if(conn != null) conn.close();
			} catch(SQLException e) {
				result = QueryState.INSERTION_FAIL_OTHER;
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Inserts a given permission to players data into database
	 * @param uuid UUID of player to add the permission to
	 * @param permission Permission to add to player
	 * @return was the insertion successful
	 */
	protected QueryState addPlayerPermission(UUID uuid, String permission, boolean isSpigot) {
		Connection conn = null;
		
		try {
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			String statement = "INSERT INTO bungeepermissions (UUID,permission,spigot) VALUES (?,?,?);";
			PreparedStatement st = conn.prepareStatement(statement);
			st.setString(1, uuid.toString());
			st.setString(2, permission.toLowerCase());
			st.setBoolean(3, isSpigot);
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
	protected QueryState removePlayerPermission(UUID uuid, String permission, boolean isSpigot) {
		Connection conn = null;
		
		try{
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			String statement = "DELETE FROM permissions WHERE UUID = ? AND permission = ? AND spigot = ?;";
			PreparedStatement st = conn.prepareStatement(statement);
			st.setString(1, uuid.toString());
			st.setString(2, permission.toLowerCase());
			st.setBoolean(3, isSpigot);
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
	
	
	/**
	 * Loads the bungeegroup for the player with the given name. If no group is found, returns null
	 * @param uuid UUID of the player
	 * @return group found or null if none found
	 */
	public Group loadGroup(UUID uuid) {
		Connection conn = null;
		String groupName = null;
		
		try {
			conn = KaranteeniPermsBungee.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM bungeegroups WHERE UUID = ?;");
			stmt.setString(1, uuid.toString());
			ResultSet set = stmt.executeQuery();
			
			if(set.next()) {
				groupName = set.getString(1); 
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		// check if anything were found
		if(groupName == null) return null;
		
		// get the group with the same ID as the group stored in the database
		KaranteeniPermsBungee plugin = KaranteeniPermsBungee.getInstance();
		Group group = plugin.getGroupList().getGroup(groupName);
		
		return group;
	}
	
	
	/**
	 * Returns the custom groupdata related to a players UUID
	 * @param uuid
	 * @return
	 */
	protected GroupData getPrivateGroupData(UUID uuid) {
		String statement = "SELECT groupname, groupnameshort, prefix, suffix " +
				"FROM custom_bungeegroup_data "+
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
				gd = new GroupData(prefix, suffix, groupname, groupnameShort, getPrivatePermissions(uuid));
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
	 * Returns the permissions a certain player has 
	 * @param uuid UUID of the player
	 * @return private permissions this player has
	 */
	protected Collection<ObjectPair<String,Boolean>> getPrivatePermissions(UUID uuid) {
		String statement = 
				"SELECT permission, spigot " +
				"FROM bungeepermissions " +
				"WHERE UUID = ?;";
		
		Connection conn = null;
		List<ObjectPair<String,Boolean>> perms = new ArrayList<ObjectPair<String,Boolean>>(); //Players loaded group
		
		try {
			conn = KaranteeniPerms.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(statement);
			//Prepare the statements
			st.setString(1, uuid.toString());
			
			ResultSet set = st.executeQuery();
			
			//Get all the permissions from the database query
			while(set.next()) {
				perms.add(new ObjectPair<String,Boolean>(set.getString(1), set.getBoolean(2)));
			}
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
	 * Sets the players groupdata to given custom groupdata values
	 * @param uuid UUID of the player
	 * @param groupData groupdata for the player
	 * @return was the modification successful
	 */
	protected boolean setPrivateGroupData(UUID uuid, GroupData groupData) {
		String statement = 
				"INSERT INTO custom_bungeegroup_data (UUID, groupname, groupnameshort, prefix, suffix)"+ 
				"VALUES (?,?,?,?,?) "+
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
	 * Loads the bungeegroup for the player with the given name. If no group is found, returns null
	 * @param name name for the player searched
	 * @return group found or null if none found
	 */
	public Group loadGroup(String name) {
		UUID uuid = KaranteeniPermsBungee.getPlayerHandler().getUUID(name);
		if(uuid == null) return null;
		return loadGroup(uuid);
	}
}
