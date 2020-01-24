package net.karanteeni.core.information;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;

/**
 * Location that holds the means to be saved to database etc
 * @author Nuubles
 *
 */
public class PermanentLocation {
	private UUID uuid;
	private Location location;
	
	/**
	 * Initializes a new permanent location with id for location and
	 * location held
	 * @param uuid id of location
	 * @param location location this object holds
	 */
	public PermanentLocation(UUID uuid, Location location)  {
		this.uuid = uuid;
		this.location = location;
	}
	
	
	/**
	 * Returns the uuid of this location
	 * @return uuid of this location
	 */
	public UUID getID() { 
		return this.uuid; 
	}
	
	
	/**
	 * Returns the location of this location
	 * @return Location
	 */
	public Location getLocation() { 
		return this.location; 
	}
	
	
	/**
	 * Changes the location of this location
	 * @param location
	 */
	public void changeLocation(Location location) { 
		this.location = location; 
	}
	
	
	/**
	 * Saves this location to the database
	 * @return was the save successful
	 */
	public boolean saveLocation() {
		Connection connection = null;
		boolean saved = false;
		try {
			connection = KaranteeniPlugin.getDatabaseConnector().openConnection();
			PreparedStatement st = connection.prepareStatement(
				"INSERT INTO location (id, serverID, world, x, y, z, pitch, yaw) "
						+ "VALUES (?,?,?,?,?,?,?,?) "
						+ "ON DUPLICATE KEY UPDATE "
						+ "world = VALUES(world), "
						+ "x = VALUES(x), "
						+ "y = VALUES(y), "
						+ "z = VALUES(z), "
						+ "pitch = VALUES(pitch), "
						+ "yaw = VALUES(yaw);");
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPlugin.getServerIdentificator());
			st.setString(3, location.getWorld().getName());
			st.setDouble(4, location.getX());
			st.setDouble(5, location.getY());
			st.setDouble(6, location.getZ());
			st.setFloat(7, location.getPitch());
			st.setFloat(8, location.getYaw());
			
			saved = st.executeUpdate() != 0;
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(connection != null)
					connection.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return saved;
	}
	
	
	/**
	 * Loads a permanent location from database using the given uuid
	 * @param uuid uuid of the location
	 * @return location loaded or null if none found
	 */
	public static PermanentLocation loadLocation(UUID uuid) {
		Connection conn = null;
		PermanentLocation location = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			location = loadLocation(conn, uuid);
		} catch(SQLException e) {
			location = null;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) { /* ignore */ }
		}
		
		return location;
	}
	
	
	/**
	 * Loads a permanent location from database using the given uuid
	 * @param conn Connection to the database
	 * @param uuid uuid of the location
	 * @return location loaded or null if none found
	 */
	public static PermanentLocation loadLocation(final Connection conn, final UUID uuid) {
		PermanentLocation location = null;
		try {
			PreparedStatement st = conn.prepareStatement("SELECT world, x, y, z, pitch, yaw FROM location WHERE id = ? AND serverID = ?;");
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniCore.getServerIdentificator());
			ResultSet set = st.executeQuery();
			if(set.next()) {
				String w = set.getString(1);
				double x = set.getDouble(2);
				double y = set.getDouble(3);
				double z = set.getDouble(4);
				float pitch = set.getFloat(5);
				float yaw = set.getFloat(6);
				
				World world = Bukkit.getWorld(w);
				if(world == null)
					return null;
				else
					Bukkit.getLogger().log(Level.SEVERE, "Could not find world " + w);
				
				Location loc = new Location(world, x,y,z,pitch,yaw);
				
				location = new PermanentLocation(uuid, loc);
			}
			else
				location = null;
		} catch(SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
		}
		
		return location;
	}
	
	
	/**
	 * Deletes a location with given uuid from database
	 * @return true if something was removed
	 */
	public boolean deleteLocation() {
		Connection conn = null;
		boolean deleted = false;
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(
				"DELETE FROM location WHERE id = ? AND serverID = ?;");
			
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniCore.getServerIdentificator());
			
			deleted = st.executeUpdate() != 0;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleted;
	}
	
	/**
	 * Deletes a location with given uuid from database
	 * @param uuid id of location to be deleted
	 * @return true if something was removed
	 */
	public static boolean deleteLocation(UUID uuid) {
		boolean deleted = false;
		Connection conn = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement(
				"DELETE FROM location WHERE id = ? AND serverID = ?;");
			
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniCore.getServerIdentificator());
			int count = st.executeUpdate();
			
			deleted = count != 0;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleted;
	}
}
