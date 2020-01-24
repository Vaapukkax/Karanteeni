package net.karanteeni.chatar.command.ignore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.database.QueryState;

public class IgnoreData {
	// stored in the direction of blocked by whom <Blocked, Set<Players blocking>
	private HashMap<UUID, HashSet<UUID>> ignoreMap = new HashMap<UUID, HashSet<UUID>>();
	
	
	/**
	 * Creates the needed table to database
	 */
	public void initializeTable() {
		Connection conn = null;
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			
			stmt.execute("CREATE TABLE IF NOT EXISTS ignores ( "+
				"uuid VARCHAR(60) NOT NULL, "+
			    "ignored VARCHAR(60) NOT NULL, "+
			    "PRIMARY KEY (uuid, ignored), "+
			    "FOREIGN KEY (uuid) "+
			    	"REFERENCES player(UUID) "+
			    	"ON DELETE CASCADE, "+
			    "FOREIGN KEY (ignored) "+
			    	"REFERENCES player(UUID) "+
			    	"ON DELETE CASCADE);");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {}
		}
	}
	
	
	/**
	 * Get the map of players who is ignored by whom
	 * @return map where <Ignored, Set<Ignorers>>
	 */
	public HashMap<UUID, HashSet<UUID>> getIgnoreMap() {
		return this.ignoreMap;
	}
	
	
	/**
	 * Returns a list of all player uuids who this given player has ignored
	 * @param ignorer
	 * @return
	 */
	public List<UUID> getPlayersIgnoredByPlayer(UUID ignorer) {
		Connection conn = null;
		List<UUID> ignored = new LinkedList<UUID>();
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT ignored FROM ignores WHERE uuid = ?;");
			stmt.setString(1, ignorer.toString());
			ResultSet set = stmt.executeQuery();
			
			while(set.next()) {
				String uidstr = set.getString(1);
				try {
					UUID ignored_ = UUID.fromString(uidstr);
					// create edge from player to blocked to show who blocks who
					ignored.add(ignored_);					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
			ignored = null;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {}
		}
		
		return ignored;
	}
	
	
	/**
	 * Adds new ignore related
	 * @param uuid who ignores
	 * @param ignored who is getting ignored
	 * @return
	 */
	synchronized public QueryState addIgnore(UUID uuid, UUID ignored) {
		// immediately add to the ignore map
		if(ignoreMap.containsKey(ignored)) {
			// check that the ignore does not already exist
			HashSet<UUID> ignoreSet = ignoreMap.get(ignored);
			if(!ignoreSet.contains(uuid))
				ignoreSet.add(uuid);
		} else {
			HashSet<UUID> ignorers = new HashSet<UUID>();
			ignorers.add(uuid);
			ignoreMap.put(ignored, ignorers);
		}
		
		Connection conn = null;
		QueryState result = QueryState.INSERTION_SUCCESSFUL;
		
		// add the ignore to the database
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO ignores (uuid, ignored) VALUES (?,?);");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, ignored.toString());
			stmt.executeUpdate();
		} catch(SQLException e) {
			if(e.getSQLState().equals("23000"))
				result = QueryState.INSERTION_FAIL_ALREADY_EXISTS;
			else {
				result = QueryState.INSERTION_FAIL_OTHER;
				e.printStackTrace();
			}
		} finally {			
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {}
		}
		
		return result;
	}
	
	
	/**
	 * Unload the given player from the maps
	 * @param uuid
	 */
	public void unload(UUID uuid) {
		Iterator<Entry<UUID, HashSet<UUID>>> iter = ignoreMap.entrySet().iterator();
		
		while(iter.hasNext()) {
			Entry<UUID, HashSet<UUID>> entry = iter.next();
			
			// if removed from the map and the map is now empty remove the key from the iterator
			if(entry.getValue().remove(uuid) && entry.getValue().isEmpty())
				iter.remove();
		}
	}
	
	
	/**
	 * Removes the ignore between two players
	 * @param uuid
	 * @param ignored
	 * @return
	 */
	synchronized public QueryState removeIgnore(UUID uuid, UUID ignored) {
		// immediately remove to the ignore map
		if(ignoreMap.containsKey(ignored)) {
			// check that the ignore does not already exist
			HashSet<UUID> ignoreSet = ignoreMap.get(ignored);
			ignoreSet.remove(uuid);

			// if the set is empty remove the main value from map
			if(ignoreSet.isEmpty())
				ignoreMap.remove(ignored);
		}
		
		Connection conn = null;
		QueryState result = QueryState.REMOVAL_SUCCESSFUL;
		
		// remove the ignore from the database
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM ignores WHERE uuid = ? AND ignored = ?;");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, ignored.toString());
			if(stmt.executeUpdate() != 1)
				result = QueryState.REMOVAL_FAIL_NO_DATA;
		} catch(SQLException e) {
			e.printStackTrace();
			result = QueryState.REMOVAL_FAIL_OTHER;
		} finally {			
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {}
		}
		
		return result;
	}
	
	
	/**
	 * Checks if the given player has ignored the other player
	 * @param uuid
	 * @param ignored
	 * @return
	 */
	synchronized public boolean isIgnored(UUID uuid, UUID ignored) {
		Connection conn = null;
		boolean result = false;
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ignores WHERE uuid = ? AND ignored = ?;");
			stmt.setString(1, uuid.toString());
			stmt.setString(2, ignored.toString());
			
			ResultSet set = stmt.executeQuery();
			if(set.next())
				result = true;
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();				
			} catch (SQLException e) {}
		}
		
		return result;
	}
	
	
	/**
	 * Check if the other player is ignoring the other one online
	 * @param uuid
	 * @param ignored
	 * @return
	 */
	synchronized public boolean isIgnoredOnline(UUID uuid, UUID ignored) {
		HashSet<UUID> ignored_ = ignoreMap.get(ignored);
		if(ignored_ == null || !ignored_.contains(uuid))
			return false;
		return true;
	}
	
	
	/**
	 * Load the ignore data for a given player
	 * @param uuid
	 */
	synchronized public boolean loadData(UUID uuid) {
		Connection conn = null;
		boolean result = true;
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT uuid FROM ignores WHERE ignored = ?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet set = stmt.executeQuery();
			
			// insert player to the graph
			HashSet<UUID> ignoreSet = new HashSet<UUID>();
			
			while(set.next()) {
				String uidstr = set.getString(1);
				UUID ignored = UUID.fromString(uidstr);
				// create edge from player to blocked to show who blocks who
				ignoreSet.add(ignored);
			}
			
			// store to memory only if not empty
			if(!ignoreSet.isEmpty())
				ignoreMap.put(uuid, ignoreSet);
		} catch(SQLException e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {}
		}
		
		return result;
	}
}
