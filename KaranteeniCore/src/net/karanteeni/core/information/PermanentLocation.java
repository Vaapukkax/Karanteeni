package net.karanteeni.core.information;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import net.karanteeni.core.KaranteeniPlugin;

/**
 * Location that holds the means to be saved to database etc
 * @author Nuubles
 *
 */
public class PermanentLocation 
{
	private UUID uuid;
	private Location location;
	
	/**
	 * Initializes a new permanent location with id for location and
	 * location held
	 * @param uuid id of location
	 * @param location location this object holds
	 */
	public PermanentLocation(UUID uuid, Location location) 
	{
		this.uuid = uuid;
		this.location = location;
	}
	
	/**
	 * Returns the uuid of this location
	 * @return uuid of this location
	 */
	public UUID getID()
	{ return this.uuid; }
	
	/**
	 * Returns the location of this location
	 * @return Location
	 */
	public Location getLocation()
	{ return this.location; }
	
	/**
	 * Changes the location of this location
	 * @param location
	 */
	public void changeLocation(Location location)
	{ this.location = location; }
	
	/**
	 * Saves this location to the database
	 * @return was the save successful
	 */
	public boolean saveLocation()
	{
		try {
			PreparedStatement st = KaranteeniPlugin.getDatabaseConnector().prepareStatement(
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
			
			return st.executeUpdate() != 0;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Loads a permanent location from database using the given uuid
	 * @param uuid uuid of the location
	 * @return location loaded or null if none found
	 */
	public static PermanentLocation loadLocation(UUID uuid)
	{
		try {
			PreparedStatement st = KaranteeniPlugin.getDatabaseConnector().prepareStatement(
					"SELECT world, x, y, z, pitch, yaw FROM location WHERE id = ? AND serverID = ?;");
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPlugin.getServerIdentificator());
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
				
				Location loc = new Location(world, x,y,z,pitch,yaw);
				
				return new PermanentLocation(uuid, loc);
			}
			else
				return null;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Deletes a location with given uuid from database
	 * @return true if something was removed
	 */
	public boolean deleteLocation()
	{
		try {
			PreparedStatement st = KaranteeniPlugin.getDatabaseConnector().prepareStatement(
				"DELETE FROM location WHERE id = ? AND serverID = ?;");
			
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPlugin.getServerIdentificator());
			
			return st.executeUpdate() != 0;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes a location with given uuid from database
	 * @param uuid id of location to be deleted
	 * @return true if something was removed
	 */
	public static boolean deleteLocation(UUID uuid)
	{
		try {
			PreparedStatement st = KaranteeniPlugin.getDatabaseConnector().prepareStatement(
				"DELETE FROM location WHERE id = ? AND serverID = ?;");
			
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPlugin.getServerIdentificator());
			
			return st.executeUpdate() != 0;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
