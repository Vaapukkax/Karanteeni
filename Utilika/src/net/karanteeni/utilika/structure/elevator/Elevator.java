package net.karanteeni.utilika.structure.elevator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;
import net.karanteeni.core.event.PlayerJumpEvent;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.utilika.Utilika;

public class Elevator implements Listener {
	private Material elevatorMaterial;
	private int maxDistance;
	private boolean allowOccluding;
	private boolean center;
	private boolean requiresPowered;
	private Sound elevatorSound;
	private float pitch;
	
	public Elevator() {
		Utilika utilika = Utilika.getPlugin(Utilika.class);
		
		// set and load block type from config
		if(!utilika.getConfig().isSet("elevator.blocktype")) {
			utilika.getConfig().set("elevator.blocktype", Material.EMERALD_BLOCK.name());
			utilika.saveConfig();
		}
		elevatorMaterial = Material.valueOf(utilika.getConfig().getString("elevator.blocktype"));
		
		// set and load max distance from config
		if(!utilika.getConfig().isSet("elevator.max-distance")) {
			utilika.getConfig().set("elevator.max-distance", 25);
			utilika.saveConfig();
		}
		maxDistance = utilika.getConfig().getInt("elevator.max-distance");
		
		// set and load if elevator allows occluding blocks
		if(!utilika.getConfig().isSet("elevator.allow-occluding")) {
			utilika.getConfig().set("elevator.allow-occluding", false);
			utilika.saveConfig();
		}
		allowOccluding = utilika.getConfig().getBoolean("elevator.allow-occluding");
		
		// set and load if elevator teleports to the center of a block
		if(!utilika.getConfig().isSet("elevator.center-player")) {
			utilika.getConfig().set("elevator.center-player", false);
			utilika.saveConfig();
		}
		center = utilika.getConfig().getBoolean("elevator.center-player");
		
		// set and load if elevator teleports to the center of a block
		if(!utilika.getConfig().isSet("elevator.sound.sound")) {
			utilika.getConfig().set("elevator.sound.sound", Sound.ENTITY_SHULKER_BULLET_HIT.name());
			utilika.saveConfig();
		}
		elevatorSound = Sound.valueOf(utilika.getConfig().getString("elevator.sound.sound"));
		// set and load if elevator teleports to the center of a block
		if(!utilika.getConfig().isSet("elevator.sound.pitch")) {
			utilika.getConfig().set("elevator.sound.pitch", 1.8);
			utilika.saveConfig();
		}
		pitch = (float)utilika.getConfig().getDouble("elevator.sound.pitch");
		
		// check config if the elevator blocks needs to be powered with redstone
		if(!utilika.getConfig().isSet("elevator.requires-power")) {
			utilika.getConfig().set("elevator.requires-power", true);
			utilika.saveConfig();
		}
		requiresPowered = utilika.getConfig().getBoolean("elevator.requires-power");
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	private void elevatorJump(PlayerJumpEvent event) {
		// check does the player has the required permission
		if(!event.getPlayer().hasPermission("utilika.elevator.use"))
			return;
		
		if(!isElevatorAtLocation(event.getPlayer().getLocation()))
			return;
		
		// get the next elevator block
		Location l = event.getPlayer().getLocation().subtract(0,1,0);
		l.setY(l.getBlockY());
		Teleporter tp = getElevatorTeleporter(l, elevatorMaterial, true);
		
		if(tp == null)
			return;
		
		// teleport player
		Location tpLocation = null;
		tpLocation = tp.teleport(event.getPlayer(), false, center, true, TeleportCause.PLUGIN);
		/*if(center)
			tpLocation = tp.preciseTeleport(event.getPlayer(), false);
		else
			tpLocation = tp.teleport(event.getPlayer(), false);*/
		
		// push player down
		event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, -2, 0)));
		
		// play the teleport sound
		if(tpLocation != null)
			event.getPlayer().getLocation().getWorld().playSound(
				tpLocation, 
				elevatorSound, 
				SoundCategory.PLAYERS, 
				1.5f, 
				pitch);
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	private void elevatorSneak(PlayerToggleSneakEvent event) {
		if(!event.isSneaking())
			return;
		// check does the player has the required permission
		if(!event.getPlayer().hasPermission("utilika.elevator.use"))
			return;
		
		if(!isElevatorAtLocation(event.getPlayer().getLocation()))
			return;
		
		// get the next elevator block
		Location l = event.getPlayer().getLocation().subtract(0,1,0);
		l.setY(l.getBlockY());
		Teleporter tp = getElevatorTeleporter(l, elevatorMaterial, false);
		
		if(tp == null)
			return;
		
		// teleport player
		Location tpLocation = tp.teleport(event.getPlayer(), false, center, true, TeleportCause.PLUGIN);
		/*if(center)
			tpLocation = tp.preciseTeleport(event.getPlayer(), false);
		else
			tpLocation = tp.teleport(event.getPlayer(), false);*/
		
		// push player down
		event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, -2, 0)));
		
		// play the teleport sound
		if(tpLocation != null)
			event.getPlayer().getLocation().getWorld().playSound(
				tpLocation, 
				elevatorSound, 
				SoundCategory.PLAYERS, 
				1.5f, 
				pitch);
	}
	
	
	/**
	 * Returns the elevator compatible block from above or below
	 * @param material
	 * @return
	 */
	private Teleporter getElevatorTeleporter(Location location, Material material, boolean above) {
		Block found = null;
		Teleporter tp = new Teleporter(location);
		int i = 0;
		
		// loop until a good block is found
		while(found == null && ++i < maxDistance) {
			if(above)
				location.add(0, 1, 0);
			else
				location.subtract(0, 1, 0);
			
			// if the found block is elevator type
			if(location.getBlock().getType() != elevatorMaterial || !isBlockPowered(location.getBlock()))
				continue;
			
			Location feetLevel = location.clone().add(0, 1, 0);
			
			// is the feet level safe
			if(!blockIsSafe(feetLevel, allowOccluding))
				continue;
			
			// if block is not occluding and not air then prevent it is a slab
			Block block = feetLevel.getBlock();
			boolean isSlab = block.getType() != Material.AIR && 
					block.getType() != Material.CAVE_AIR &&
					!block.isPassable();
			
			// is the head level safe
			if(!blockIsSafe(feetLevel.add(0, 1, 0), allowOccluding))
				continue;
			
			// block is safe, accept teleport
			tp.setDestination(location.add(0, isSlab?1.5:1.0, 0));
			return tp;
		}
		
		return null;
	}
	
	
	private boolean isBlockPowered(Block block) {
		return (!requiresPowered) || block.getBlockPower() > 0;
	}
	
	
	/**
	 * Checks and returns true if a given block at location is a valid elevator
	 * @param location
	 * @return
	 */
	private boolean isElevatorAtLocation(Location location) {
		Block block = location.clone().subtract(0, 0.6, 0).getBlock();
		return (block.getType() == elevatorMaterial) && isBlockPowered(block); // check if block needs redstone
	}
	
	
	/**
	 * Checks if the block at given location is safe for player
	 * @param location
	 * @return
	 */
	private boolean blockIsSafe(Location l, boolean allowOcclude) {
		return (!l.getBlock().getType().isOccluding() && allowOcclude) || 
				(!l.getBlock().getType().isSolid() && l.getBlock().getType() != Material.LAVA);
	}
}
