package net.karanteeni.teleportal.warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import net.karanteeni.core.config.YamlConfig;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.karanteenials.functionality.Back;
import net.karanteeni.teleportal.Teleportal;

public class Warp {
	private static YamlConfig config;
	private static final String WARP_CONFIG = "warps.yml";
	private String name;
	private String displayname;
	private String description;
	private String permission = null;
	private Location location;
	
	
	/**
	 * Initializes a new warp with name, displayname and location
	 * @param name name which is used to search warps
	 * @param displayname displayname this warp has when shown on inventories and chat
	 * @param location location of this warp
	 */
	public Warp(String name, String displayname, Location location) {
		this.name = name.toLowerCase();
		this.displayname = displayname;
		this.location = location;
		generateDescriptionTranslation();
		loadConfig();
	}
	
	
	/**
	 * Initializes a new warp with name, displayname and location
	 * @param name name which is used to search warps
	 * @param displayname displayname this warp has when shown on inventories and chat
	 * @param location location of this warp
	 */
	public Warp(String name, String displayname, Location location, String permission) {
		this.name = name.toLowerCase();
		this.displayname = displayname;
		this.location = location;
		this.permission = permission;
		generateDescriptionTranslation();
		loadConfig();
	}
	
	
	/**
	 * Initializes a new warp with name, displayname and location
	 * @param name name which is used to search warps
	 * @param displayname displayname this warp has when shown on inventories and chat
	 * @param location location of this warp
	 */
	private Warp(String name, String displayname, Location location, String permission, String description) {
		this.name = name.toLowerCase();
		this.displayname = displayname;
		this.location = location;
		this.permission = permission;
		this.description = description;
		generateDescriptionTranslation();
		loadConfig();
	}
	
	
	/**
	 * Generates translatable description for this warp in the translation config of Teleportal.
	 */
	private void generateDescriptionTranslation() {
		Teleportal.getTranslator().registerTranslation(Teleportal.getPlugin(Teleportal.class), 
				"warp.description."+this.name, 
				"");
	}
	
	
	/**
	 * Loads the config to warp class
	 */
	private static void loadConfig() {
		if(config == null) {
			config = new YamlConfig(Teleportal.getPlugin(Teleportal.class), WARP_CONFIG);
		}
	}
	
	
	/**
	 * Returns all warp names from config
	 * @return list of all warp names
	 */
	public static List<String> getWarpNames() {
		loadConfig();
		return new ArrayList<String>(config.getConfig().getKeys(false));
	}
	
	
	/**
	 * Loads and returns all warps from config
	 * @return all warps in config file
	 */
	public static Warp[] getWarps() {
		loadConfig();
		Set<String> keys = config.getConfig().getKeys(false);
		Warp[] warps = new Warp[keys.size()];
		
		int i = 0;
		for(String key : keys) {
			String perm = config.getConfig().getString(key+".permission");
			
			// check if warp requires permission
			if(perm == null)
				warps[i] = new Warp(
					key, 
					config.getConfig().getString(key+".dname"), 
					(Location)config.getConfig().get(key+".loc"));
			else
				warps[i] = new Warp(
					key, 
					config.getConfig().getString(key+".dname"), 
					(Location)config.getConfig().get(key+".loc"),
					perm);
			++i;
		}
		
		return warps;
	}
	
	
	/**
	 * Returns the description of this warp
	 * @return description of this warp
	 */
	public String getDescription() {
		return this.description;
	}
	
	
	/**
	 * Loads and returns all warps from config
	 * @return all warps in config file
	 */
	public static List<Warp> getWarps(CommandSender sender) {
		loadConfig();
		Set<String> keys = config.getConfig().getKeys(false);
		List<Warp> warps = new ArrayList<Warp>();
		
		for(String key : keys) {
			String perm = config.getConfig().getString(key+".permission");
			String description = Teleportal.getTranslator().getTranslation(
					Teleportal.getPlugin(Teleportal.class), sender, "warp.description."+key);
			
			// check if warp requires permission
			if(perm == null) {
				Warp warp = new Warp(
						key, 
						config.getConfig().getString(key+".dname"), 
						(Location)config.getConfig().get(key+".loc"));
				warp.description = description;
				warps.add(warp);
			} else if(sender.hasPermission(perm))
				warps.add(new Warp(
					key, 
					config.getConfig().getString(key+".dname"), 
					(Location)config.getConfig().get(key+".loc"),
					perm,
					description));
		}
		
		return warps;
	}
	
	
	/**
	 * Teleports the given entity to this location
	 * @param player player to be teleported to the warp
	 * @return location to which the player was warped to or null if no permission or 
	 */
	public Location teleport(Player player) {
		if(this.permission != null && !player.hasPermission(this.permission))
			return null;
		Teleportal teleportal = Teleportal.getPlugin(Teleportal.class);
		
		if(!teleportal.getConfig().isSet("warp.back")) {
			teleportal.getConfig().set("warp.back", true);
			teleportal.saveConfig();
		}
		
		Teleporter tp = new Teleporter(location);
		Location loc = player.getLocation();
		
		// enable /back to work
		if(teleportal.getConfig().getBoolean("warp.back")) {
			Back back = new Back(player);
			back.setBackLocation(loc);
		}
		
		if(!teleportal.getConfig().isSet("warp.safe")) {
			teleportal.getConfig().set("warp.safe", true);
			teleportal.saveConfig();
		}
		
		Location newLoc = tp.teleport(player, teleportal.getConfig().getBoolean("warp.safe"), 
				false, 
				true, 
				TeleportCause.PLUGIN);
				//tp.teleport(player, teleportal.getConfig().getBoolean("warp.safe"));
		if(newLoc == null)
			return null;
		
		return newLoc;
	}
	
	
	/**
	 * Checks if the given player has the permission to use this warp
	 * @param player player to use the warp
	 * @return true if player has permission to this warp, false otherwise
	 */
	public boolean hasPermission(Player player) {
		return permission == null || player.hasPermission(this.permission);
	}
	
	
	/**
	 * Loads a warp from config file to memory
	 * @param name search the warp with this name
	 * @return loaded warp or null if not found
	 */
	public static Warp loadWarp(String name, CommandSender sender) {
		loadConfig();
		name = name.toLowerCase();
		if(!config.getConfig().isSet(name))
			return null;
		
		String dname = config.getConfig().getString(name+".dname");
		Location loc = (Location)config.getConfig().get(name+".loc");
		String perm = config.getConfig().getString(name+".permission");
		String description = Teleportal.getTranslator().getTranslation(
				Teleportal.getPlugin(Teleportal.class), sender, "warp.description."+name);
		
		if(perm == null) {
			Warp warp = new Warp(name, dname, loc);
			warp.description = description;
			return warp;
		} else {
			return new Warp(name, dname, loc, perm, description);
		}
	}
	
	
	/**
	 * Check if a warp with this name is already saved in config
	 * @return true if this name already exists, false otherwise
	 */
	public boolean exists() {
		loadConfig();
		return config.getConfig().isSet(name);
	}
	
	
	/**
	 * Save the warp to config. Does NOT save description!
	 * @return true if save was successful, false otherwise
	 */
	public boolean save() {
		loadConfig();
		config.getConfig().set(name+".loc", location);
		config.getConfig().set(name+".dname", displayname);
		config.getConfig().set(name+".permission", this.permission);
		return config.save();
	}
	
	
	/**
	 * Deletes this warp from the config
	 * @return true if deletion successful, false otherwise
	 */
	public boolean delete() {
		loadConfig();
		config.getConfig().set(name, null);
		return config.save();
	}
	
	
	/**
	 * Changes the name of this warp
	 * @param name new name for warp
	 * @return true if change was successful, false if name already in use
	 */
	public boolean setName(String name) {
		name = name.toLowerCase();
		if(config.getConfig().contains(name, false))
			return false;
		
		this.name = name;
		return true;
	}
	
	
	/**
	 * Returns the name of this warp
	 * @return name of this warp
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Changes the display name of this warp
	 * @param displayname the new display name for this warp
	 */
	public void setDisplayName(String displayname) {
		this.displayname = displayname;
	}
	
	
	/**
	 * Returns the displayname of this warp
	 * @return displayname of this warp
	 */
	public String getDisplayName() {
		return this.displayname;
	}
	
	
	/**
	 * Change the location of this warp
	 * @param location new location for this warp
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	
	/**
	 * Gets the location of this warp
	 * @return location of this warp
	 */
	public Location getLocation() {
		return this.location;
	}
	
	
	/**
	 * Sets the permission required to use this warp
	 * @param permission permission this warp has
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	
	/**
	 * Returns the permission this warp requires to use
	 * @return permission if one is there or null if open for all
	 */
	public String getPermission() {
		return this.permission;
	}
}
