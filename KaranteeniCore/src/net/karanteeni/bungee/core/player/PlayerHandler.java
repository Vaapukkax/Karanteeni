package net.karanteeni.bungee.core.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import net.karanteeni.bungee.core.KaranteeniCore;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class PlayerHandler {

	/**
	 * Data keys for variables in the database
	 * @author Nuubles
	 */
	public static enum PlayerDataKeys {
		UUID("UUID"),
		DISPLAY_NAME("displayname"),
		NAME("name"),
		PLAYER_TABLE("player");
		
		String key;
		
		PlayerDataKeys(String data) {
			key = data;
		}
		
		@Override
		public String toString() {
			return key;
		}
	}
	
	
	public PlayerHandler() {
		createTable();
	}
	
	
	/**
	 * Creates a table of players to database
	 * @throws Exception 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private void createTable() {
		Connection conn = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			String query = 
					"CREATE TABLE IF NOT EXISTS "+PlayerDataKeys.PLAYER_TABLE+" ("
							+ PlayerDataKeys.UUID+ " VARCHAR(60) NOT NULL, \n"
							+ PlayerDataKeys.DISPLAY_NAME + " VARCHAR(128), \n"
							+ PlayerDataKeys.NAME + " VARCHAR(16) NOT NULL, \n"
							+ "PRIMARY KEY("+PlayerDataKeys.UUID+"), \n"
							+ "UNIQUE(" + PlayerDataKeys.DISPLAY_NAME + "), \n"
							+ "UNIQUE(" + PlayerDataKeys.NAME + ")\n"
							+ ");";
			Statement st = conn.createStatement();
			st.execute(query);
			st.close();			
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
	}
	
	
	/**
	 * Gets the offline uuid of a player with this name
	 * @param name
	 */
	private UUID getOfflineUUID(String name) {
		Connection conn = null;
		UUID uuid = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(
					"SELECT "+PlayerDataKeys.UUID+" FROM " + PlayerDataKeys.PLAYER_TABLE + " WHERE " + PlayerDataKeys.NAME+"=?;");
			
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			if(rs.first())
				uuid = UUID.fromString(rs.getString(1));
			st.close();
		} catch (SQLException | IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return uuid;
	}
	
	
	/**
	 * Searches for UUID by given player name
	 * @param name Name of player to return
	 * @return UUID of given player or null
	 */
	public UUID getUUID(String name) {
		//Player player = Bukkit.getPlayer(name);
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(name);
		//Is player here
		if(player != null)
			return player.getUniqueId();
	
		//Player is not here, try to search database
		return getOfflineUUID(name);
	}
	
	
	/**
	 * Get the name of a player who is not online
	 * @param uuid
	 * @return
	 */
	public String getName(UUID uuid) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(uuid);
		if(player != null)
			return player.getName();
		
		return getOfflineName(uuid);
	}
	
	
	/**
	 * Get the name of a player who is not online
	 * @param uuid
	 * @return
	 */
	public String getDisplayName(UUID uuid) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(uuid);
		if(player != null)
			return player.getDisplayName();
		
		return getOfflineDisplayName(uuid);
	}
	
	
	/**
	 * Get the name of a player who is not online
	 * @param uuid
	 * @return
	 */
	public String getOfflineName(UUID uuid) {
		Connection conn = null;
		String result = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.NAME+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				result =  rs.getString(1);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * Get the displayname of a player who is not online
	 * @param uuid
	 * @return
	 */
	public String getOfflineDisplayName(UUID uuid) {
		Connection conn = null;
		String result = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.DISPLAY_NAME+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				result = rs.getString(1);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Sets the displayname for player both online and offline
	 * @param uuid uuid to whom the displayname will be set
	 * @return was the save successful
	 */
	public boolean setDisplayName(UUID uuid, String displayname) {
		// set online player displayname
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(uuid);
		if(player != null)
			player.setDisplayName(displayname);
		
		Connection conn = null;
		boolean result = false;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET displayname = ? WHERE UUID = ?;");
			stmt.setString(1, displayname);
			stmt.setString(2, uuid.toString());
			result = stmt.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Gets the last seen ip from player
	 * @param uuid
	 * @return
	 */
	/*public String getOfflineIP(UUID uuid)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		try {
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.LAST_IP+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				return rs.getString(1);
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	/**
	 * Returns the last online time from player
	 * @param uuid
	 * @return
	 */
	/*public Long getLastOnline(UUID uuid)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		try {
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.LAST_ONLINE+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				return rs.getLong(1);
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	/**
	 * Returns the rank level of player
	 * @param uuid
	 * @return
	 */
	/*public Integer getLevel(UUID uuid)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		try {
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.LEVEL+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				return rs.getInt(1);
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	/**
	 * Returns the global rank of the player
	 * @param uuid
	 * @return
	 */
	/*public String getGlobalRank(UUID uuid)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		try {
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.GLOBAL_RANK+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				return rs.getString(1);
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
}
