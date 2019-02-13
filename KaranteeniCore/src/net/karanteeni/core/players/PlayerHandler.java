package net.karanteeni.core.players;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.DatabaseConnector;

public class PlayerHandler {

	/**
	 * Data keys for variables in the database
	 * @author Nuubles
	 */
	public static enum PlayerDataKeys
	{
		UUID("UUID"),
		LAST_IP("lastip"),
		LAST_ONLINE("lastonline"),
		DISPLAY_NAME("displayname"),
		NAME("name"),
		LEVEL("level"),
		GLOBAL_RANK("globalrank"),
		PLAYER_TABLE("player");
		
		String key;
		
		PlayerDataKeys(String data)
		{
			key = data;
		}
		
		@Override
		public String toString()
		{
			return key;
		}
	}
	
	public PlayerHandler()
	{
		createTable();
	}
	
	/**
	 * Creates a table of players to database
	 * @throws Exception 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private void createTable()
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return;
		
		String query = 
				"CREATE TABLE IF NOT EXISTS "+PlayerDataKeys.PLAYER_TABLE+" ("
				+ PlayerDataKeys.UUID+ " VARCHAR(60) NOT NULL, \n"
				+ PlayerDataKeys.LAST_IP + " VARCHAR(45) NOT NULL, \n"
				+ PlayerDataKeys.LAST_ONLINE  + " BIGINT, \n"
				+ PlayerDataKeys.DISPLAY_NAME + " VARCHAR(128), \n"
				+ PlayerDataKeys.NAME + " VARCHAR(16) NOT NULL, \n"
				+ PlayerDataKeys.LEVEL + " INT(8), \n"
				+ PlayerDataKeys.GLOBAL_RANK + " VARCHAR(25), \n"
				+ "PRIMARY KEY("+PlayerDataKeys.UUID+"), \n"
				+ "UNIQUE(" + PlayerDataKeys.DISPLAY_NAME + "), \n"
				+ "UNIQUE(" + PlayerDataKeys.NAME + ")\n"
				+ ");";
		
		
		try {
			Statement st = db.getStatement();
			st.execute(query);
			st.close();
			//db.runQuery(query);
		/*} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the offline uuid of a player with this name
	 * @param name
	 */
	private UUID getOfflineUUID(String name)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		UUID uuid = null;
		
		try {
			PreparedStatement st = db.prepareStatement(
					"SELECT "+PlayerDataKeys.UUID+" FROM " + PlayerDataKeys.PLAYER_TABLE + " WHERE " + PlayerDataKeys.NAME+"=?;");
			
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			if(rs.first())
				uuid = UUID.fromString(rs.getString(1));
			st.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
		}
		
		return uuid;
	}
	
	/**
	 * Searches for UUID by given player name
	 * @param name Name of player to return
	 * @return UUID of given player or null
	 */
	public UUID getUUID(String name)
	{
		Player player = Bukkit.getPlayer(name);
		//Is player here
		if(player != null)
			return player.getUniqueId();
	
		//Player is not here, try to search database
		return getOfflineUUID(name);
	}
	
	/**
	 * Gets players from the server with arguments
	 * @param location
	 * @param name
	 * @return
	 */
	public List<Player> getOnlinePlayers(Location location, String name)
	{
		Set<Player> foundPlayers = new HashSet<Player>();
		String[] playerNames = name.split(",");
		
		for(int i = 0; i < playerNames.length; ++i)
		{
			Player player = Bukkit.getPlayer(playerNames[i]);
			
			//Player was found by name
			if(player != null)
			{
				foundPlayers.add(player);
				continue;
			}
			//Get all players on server
			if(playerNames[i].equalsIgnoreCase("@a") || playerNames[i].equalsIgnoreCase("all"))
			{
				foundPlayers.addAll(Bukkit.getOnlinePlayers());
				continue;
			}
			//Gets the closes player
			else if(playerNames[i].equalsIgnoreCase("@p")) 
			{
				Player p = KaranteeniCore.getEntityManager().getNearestPlayer(location);
				if(p != null)
					foundPlayers.add(p);
				continue;
			}
			else if(playerNames[i].equalsIgnoreCase("@r"))
			{
				List<Player> pr = new ArrayList<Player>(Bukkit.getOnlinePlayers());
				foundPlayers.add(pr.get((new Random()).nextInt(pr.size())));
				continue;
			}
		}
		
		return new ArrayList<Player>(foundPlayers);
	}
	
	/**
	 * Get the name of a player who is not online
	 * @param uuid
	 * @return
	 */
	public String getOfflineName(UUID uuid)
	{
		if(Bukkit.getPlayer(uuid) != null)
			return Bukkit.getPlayer(uuid).getName();
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		try {
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.NAME+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			st.close();
			if(rs.first())
				return rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Get the displayname of a player who is not online
	 * @param uuid
	 * @return
	 */
	public String getOfflineDisplayName(UUID uuid)
	{
		if(Bukkit.getPlayer(uuid) != null)
			return Bukkit.getPlayer(uuid).getDisplayName();
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return null;
		
		try {
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT "+PlayerDataKeys.DISPLAY_NAME+" FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';");
			if(rs.first())
				return rs.getString(1);
			st.close();
			/*return db.getString("SELECT * FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';", PlayerDataKeys.DISPLAY_NAME.toString());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets the last seen ip from player
	 * @param uuid
	 * @return
	 */
	public String getOfflineIP(UUID uuid)
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
			/*return db.getString("SELECT * FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';", PlayerDataKeys.LAST_IP.toString());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns the last online time from player
	 * @param uuid
	 * @return
	 */
	public Long getLastOnline(UUID uuid)
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
			/*return db.getLong("SELECT * FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';", PlayerDataKeys.LAST_ONLINE.toString());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns the rank level of player
	 * @param uuid
	 * @return
	 */
	public Integer getLevel(UUID uuid)
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
			/*return db.getInteger("SELECT * FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';", PlayerDataKeys.LEVEL.toString());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns the global rank of the player
	 * @param uuid
	 * @return
	 */
	public String getGlobalRank(UUID uuid)
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
			/*return db.getString("SELECT * FROM " + PlayerDataKeys.PLAYER_TABLE + 
					" WHERE UUID='"+uuid+"';", PlayerDataKeys.GLOBAL_RANK.toString());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
