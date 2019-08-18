package net.karanteeni.karanteenials.player.home;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.PermanentLocation;
import net.karanteeni.core.information.Teleporter;

/**
 * A saveable location holder with owner and lowercase name
 * @author Nuubles
 *
 */
public class Home {
	private UUID owner;
	private String name;
	private PermanentLocation location;
	
	/**
	 * Initializes a new home 
	 * @param owner owner of the home
	 * @param name name of the home in lowercase
	 * @param location location of the home
	 */
	public Home(UUID owner, String name, Location location)
	{
		this.owner = owner;
		this.name = name.toLowerCase();
		this.location = new PermanentLocation(UUID.randomUUID(), location);
	}
	
	/**
	 * Initializes a new home with existing permanent location
	 * @param owner Owner of this home
	 * @param name Name of this home
	 * @param location saveable location of this home
	 */
	public Home(UUID owner, String name, PermanentLocation location)
	{
		this.owner = owner;
		this.name = name.toLowerCase();
		this.location = location;
	}
	
	/**
	 * Returns the owners uuid of this home
	 * @return uuid of the owner of this home
	 */
	public UUID getOwner()
	{ return this.owner; }
	
	/**
	 * Returns the name of this home
	 * @return lowercase name of this home
	 */
	public String getName()
	{ return this.name; }
	
	/**
	 * Returns the location of this home
	 * @return location of this home
	 */
	public Location getLocation()
	{ return this.location.getLocation(); }
	
	/**
	 * Changes the location of this home
	 * @return
	 */
	public void setLocation(Location location)
	{ this.location.changeLocation(location); }
	
	/**
	 * Teleports an entity to this home
	 * @param entity Entity to teleport
	 * @return the teleporter used to teleport the player.
	 * destination of teleporter is the home location in safe position
	 * and origination is the original location of player
	 * before teleport.
	 */
	public Teleporter teleport(Player player)
	{
		Teleporter teleporter = new Teleporter(location.getLocation());
		//teleporter.teleport(player, true); //Teleport player safely
		teleporter.teleport(player, true, false, true, TeleportCause.PLUGIN);
		return teleporter;
	}
	
	/**
	 * Deletes a players home from database
	 * @param uuid uuid of the player whose home will be deleted
	 * @param home home to delete
	 * @return true if deletion was successful
	 */
	public boolean delete() 
	{
		if(owner == null || location == null) return false;
		
		try {
			//Delete the location of this home
			if(!location.deleteLocation())
				return false;
			
			PreparedStatement st = KaranteeniPlugin.getDatabaseConnector().prepareStatement(
					"DELETE FROM home WHERE UUID = '"+owner.toString()+"' AND serverID = ? AND name = ?;");
			
			st.setString(1, KaranteeniPlugin.getServerIdentificator());
			st.setString(2, name);
			if(st.executeUpdate() == 0)
				return false;
			
			return true; //Delete location of this home
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Saves a home for uuid
	 * @param uuid uuid of player to whom this will be saved
	 * @param home home to save
	 * @return true if save was successful, false otherwise
	 */
	public boolean save() 
	{
		if(owner == null || location == null) return false;
		
		try {
			Home old = getHome(owner, name);
			
			if(old == null) //There's no old home, create new one
			{
				if(!location.saveLocation()) //Save the location
					return false;
				
				PreparedStatement st = KaranteeniPlugin.getDatabaseConnector().prepareStatement(
						"INSERT INTO home "
						+ "(UUID, name, location, serverID) "
						+ "VALUES "
						+ "(?,?,?,?);");
				st.setString(1, owner.toString());
				st.setString(2, name);
				st.setString(3, location.getID().toString());
				st.setString(4, KaranteeniPlugin.getServerIdentificator());
				return st.executeUpdate() != 0;
			}
			else
			{
				//Update only the location as there's no need to update the home data
				old.location.changeLocation(this.location.getLocation());
				if(!old.location.saveLocation())
					return false;
			}
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns all homes of this player on this server
	 * @param uuid UUID of player
	 * @return
	 */
	protected static List<Home> getHomes(UUID uuid)
	{
		try {
			PreparedStatement st = 
				KaranteeniPlugin.getDatabaseConnector().prepareStatement(
					"SELECT name, world, x, y, z, pitch, yaw, location.id "+
					"FROM home, location "+
					"WHERE home.UUID = ? AND "+
					"home.location = location.id AND "+ 
					"home.serverID = ? AND "+
					"home.serverID = location.serverID;");
			st.setString(1, uuid.toString());
			st.setString(2, KaranteeniPlugin.getServerIdentificator());
			ResultSet set = st.executeQuery();
			
			List<Home> homes = new ArrayList<Home>();
			
			while(set.next())
			{
				String name = set.getString(1);
				String world = set.getString(2);
				double x = set.getDouble(3);
				double y = set.getDouble(4);
				double z = set.getDouble(5);
				float pitch = set.getFloat(6);
				float yaw = set.getFloat(7);
				UUID locID = UUID.fromString(set.getString(8));
				World w = Bukkit.getWorld(world);
				if(w == null)
					continue;
				Location loc = new Location(w, x, y, z, yaw, pitch);
				homes.add(new Home(uuid, name.toLowerCase(), new PermanentLocation(locID, loc)));
			}
			
			return homes;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Returns a home of name from specific player
	 * @param uuid UUID of player
	 * @param name name of home
	 * @return home found or null if no home found
	 */
	protected static Home getHome(UUID uuid, String name)
	{
		try {
			PreparedStatement st = 
				KaranteeniPlugin.getDatabaseConnector().prepareStatement(
					"SELECT world, x, y, z, pitch, yaw, location.id "+
					"FROM home, location "+
					"WHERE home.UUID = ? AND "+
					"home.name = ? AND "+
					"home.location = location.id AND "+ 
					"home.serverID = ? AND "+
					"home.serverID = location.serverID;");
			st.setString(1, uuid.toString());
			st.setString(2, name.toLowerCase());
			st.setString(3, KaranteeniPlugin.getServerIdentificator());
			ResultSet set = st.executeQuery();
			
			if(set.next())
			{
				String world = set.getString(1);
				double x = set.getDouble(2);
				double y = set.getDouble(3);
				double z = set.getDouble(4);
				float pitch = set.getFloat(5);
				float yaw = set.getFloat(6);
				UUID locID = UUID.fromString(set.getString(7));
				
				World w = Bukkit.getWorld(world);
				if(w == null)
					return null;
				Location loc = new Location(w, x, y, z, yaw, pitch);
				return new Home(uuid, name.toLowerCase(), new PermanentLocation(locID, loc));
			}
			
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
