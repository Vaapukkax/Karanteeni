package net.karanteeni.statmanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import net.karanteeni.core.database.DatabaseObject;
import net.karanteeni.core.information.time.TimeData;

public class Time implements DatabaseObject {
	private UUID uuid;
	private long originalTime;
	private long countStartTime;
	private Long afk = null;
	
	private Time(UUID uuid, long originalTime, long countStartTime) {
		this.uuid = uuid;
		this.originalTime = originalTime;
		this.countStartTime = countStartTime;
	}
	
	
	/**
	 * Loads a time from the database if one with given UUID exists
	 * @param uuid uuid of the player whose time is being loaded
	 * @return time loaded
	 */
	public static Time loadTime(UUID uuid) {
		Connection conn = null;
		
		try {
			conn = StatManager.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			ResultSet set = stmt.executeQuery("SELECT time FROM global_playtime WHERE player = '" + uuid.toString() + "';");
			// if player does not have time already create new
			long time = 0;
			if(set.next())
				time = set.getLong(1);
			return new Time(uuid, time, System.currentTimeMillis());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /* ignored */ }
		}
		
		return null;
	}
	
	
	/**
	 * Sets the time to start ignoring current time until release
	 */
	public void setAfk() {
		if(isAfk()) return;
		this.afk = System.currentTimeMillis();
	}
	
	
	/**
	 * Releases the time from afk
	 */
	public void releaseAfk() {
		if(!isAfk()) return;
		this.originalTime -= System.currentTimeMillis() - this.afk.longValue();
		this.afk = null;
	}
	
	
	/**
	 * Checks if this time has been set to count only afk time
	 * @return is the player afk
	 */
	public boolean isAfk() {
		return this.afk != null;
	}
	
	
	/**
	 * Adds a given amount of time to the current play time
	 * @param time time to add
	 * @return this
	 */
	public Time add(long time) {
		this.originalTime += time;
		if(this.originalTime < 0)
			this.originalTime = 0;
		return this;
	}
	
	
	/**
	 * Sets the new play time to this class
	 * @param time time to set
	 * @return this
	 */
	public Time setTime(long time) {
		this.originalTime = time;
		this.countStartTime = System.currentTimeMillis();
		return this;
	}
	
	
	/**
	 * Subtracts the given time from the players play time
	 * @param time time to subtract
	 * @return this
	 */
	public Time subtract(long time) {
		this.originalTime -= time;
		if(this.originalTime < 0)
			this.originalTime = 0;
		return this;
	}
	

	@Override
	public boolean delete() {
		Connection conn = null;

		try {
			conn = StatManager.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			int rows = stmt.executeUpdate("DELETE FROM global_playtime WHERE uuid = '" + uuid.toString() + "';");
			return rows == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /* ignored */ }
		}
		
		return false;
	}
	
	
	/**
	 * Returns the current total time of player
	 * @return the current playtime of a given player
	 */
	public long getTime() {
		if(isAfk())
			return this.originalTime + (System.currentTimeMillis() - countStartTime) - afk;
		else
			return this.originalTime + (System.currentTimeMillis() - countStartTime);
	}
	
	
	/**
	 * Returns the time in this Time as TimeData object to ease time handing and formatting
	 * @return timedata to ease time formatting
	 */
	public TimeData asTimeData() {
		return new TimeData(getTime());
	}


	/**
	 * Initializes the database for this object
	 * @return true if initialization was a success, false otherwise
	 */
	public static boolean initialize() {
		Connection conn = null;
		
		try {
			conn = StatManager.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			
			stmt.execute("CREATE TABLE IF NOT EXISTS global_playtime( " +
					"player VARCHAR(128) PRIMARY KEY, " +
					"time BIGINT(19) UNSIGNED,"+
					"FOREIGN KEY (player) REFERENCES player(UUID));");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(conn != null)
				try { conn.close(); } catch(Exception e) { /* ignored */ }
		}
	}


	@Override
	public boolean save() {
		Connection conn = null;
		
		try {
			conn = StatManager.getDatabaseConnector().openConnection();
			Statement stmt = conn.createStatement();
			long millis = System.currentTimeMillis();
			long time = getTime();
			stmt.executeUpdate("INSERT INTO global_playtime (player, time) VALUES ('"+
			uuid.toString() + "', " + getTime() + ") ON DUPLICATE KEY UPDATE time = " + time + ";");
			this.originalTime = time;
			this.countStartTime = millis;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try {conn.close();} catch(Exception e) { /* ignored */ }
		}
		
		return false;
	}
}
