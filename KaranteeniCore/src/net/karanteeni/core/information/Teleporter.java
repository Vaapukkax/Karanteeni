package net.karanteeni.core.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Teleporter {
	private Location destination;
	private Location origination;
	
	/**
	 * Initializes the teleporter with starting destination
	 * @param location Destination of this teleporter
	 */
	public Teleporter(Location location)
	{ this.destination = location; }
	
	/**
	 * Sets the destination of this teleported
	 * @param location destination of this teleporter
	 * @return this this
	 */
	public Teleporter setDestination(Location location)
	{ this.destination = location; return this; }
	
	/**
	 * Returns the point to which the entity will be teleported
	 * @return destination of this teleporter
	 */
	public Location getDestination()
	{ return this.destination; }
	
	/**
	 * Returns the point from which the last entity was teleported
	 * @return origination point of this teleporter
	 */
	public Location getOrigination()
	{ return this.origination; }
	
	
	/**
	 * Returns a safe point in vertical axis to the given point
	 * @param location
	 * @param allowWater
	 * @return
	 */
	public Location getSafePoint(Location location, boolean allowWater, int eyeHeight) {
		Chunk chunk = location.getChunk();
		ChunkSnapshot chunkShot = chunk.getChunkSnapshot();
		int x = Math.abs(location.getBlockX() % 16);
		int y = Math.max(location.getBlockY(), 0);
		int z = Math.abs(location.getBlockZ() % 16);
		if(location.getZ() < 0)
			z = 16 - z;
		if(location.getX() < 0)
			x = 16 - x;
		if(z == 16)
			z = 0;
		if(x == 16)
			x = 0;
		
		// check if the location in the chunk is safe
		SAFENESS safeness = null;
		
		if(y+eyeHeight <= 255)
			safeness = isSafe(allowWater, chunkShot.getBlockType(x, y-1, z), 
					chunkShot.getBlockType(x, y, z), 
					chunkShot.getBlockType(x, (y+eyeHeight), z));
		else if(y+eyeHeight > 255 && y <= 255)
			safeness = isSafe(allowWater, chunkShot.getBlockType(x, y-1, z), 
					chunkShot.getBlockType(x, y, z));
		else
			safeness = isSafe(allowWater, chunkShot.getBlockType(x, y-1, z));
			
		
		// if safe, return the given location
		if(safeness == SAFENESS.SAFE)
			return location;
		
		// get the highest location to return if previous was unsafe
		int highestY = chunkShot.getHighestBlockYAt(x, z)+1;
		// no blocks at the axis
		if(highestY < 1) {
			return null;
		} else if(highestY + eyeHeight < 256) {
			// normal location in world
			safeness = isSafe(allowWater, chunkShot.getBlockType(x, highestY-1, z), 
					chunkShot.getBlockType(x, highestY, z), 
					chunkShot.getBlockType(x, (highestY+eyeHeight), z));
		} else if(highestY+eyeHeight > 255 && highestY <= 255){
			// eye height above build limit, don't check eyeheight
			safeness = isSafe(allowWater, chunkShot.getBlockType(x, highestY-1, z), 
					chunkShot.getBlockType(x, highestY, z));
		} else {
			// highest block at build limit
			safeness = isSafe(allowWater, chunkShot.getBlockType(x, highestY-1, z));
		}
		
		// a safe location found, return it
		if(safeness == SAFENESS.SAFE)
			return new Location(location.getWorld(), location.getBlockX()+0.5, highestY, location.getBlockZ()+0.5, 
					location.getYaw(), location.getPitch());
		
		// scan all the blocks below until a safe location is found
		while(highestY > 1) {
			--highestY;
			
			Material blockBelowFeet = chunkShot.getBlockType(x, highestY-1, z);
			Material blockAtFeet = chunkShot.getBlockType(x, highestY, z);
			
			if((highestY + eyeHeight) <= 255) {
				Material blockAtHead = chunkShot.getBlockType(x, (highestY+eyeHeight), z);
				if(SAFENESS.SAFE == isSafe(allowWater, blockBelowFeet, blockAtFeet, blockAtHead))
					return new Location(location.getWorld(), location.getBlockX()+0.5, highestY, location.getBlockZ()+0.5, location.getYaw(), location.getPitch());
			} else {
				if(SAFENESS.SAFE == isSafe(allowWater, blockBelowFeet, blockAtFeet))
					return new Location(location.getWorld(), location.getBlockX()+0.5, highestY, location.getBlockZ()+0.5, location.getYaw(), location.getPitch());
			}
			
		}
		
		// no safe location found
		return null;
	}
	
	
	/**
	 * Teleports an entity to this destination and sets
	 * the origination point.
	 * @param entity Entity to teleport
	 * @return The location to which the entity was teleported
	 */
	public Location teleport(LivingEntity entity, boolean safe, boolean centerEntity, boolean allowWater, TeleportCause tpCause) {
		// get the safe tp location
		Location tpLocation = destination.clone();
		
		if(centerEntity)
			tpLocation = new Location(
					tpLocation.getWorld(), 
					tpLocation.getBlockX() + 0.5, 
					tpLocation.getBlockY(), 
					tpLocation.getBlockZ() + 0.5, 
					tpLocation.getYaw(), 
					tpLocation.getPitch());
		
		// if we want the tp to be safe, get the safe location
		if(safe)
			tpLocation = getSafePoint(tpLocation, allowWater, (int)entity.getEyeHeight());
		
		// if null, no safe location found, return null
		if(tpLocation == null)
			return null;
		HashMap<Entity, List<Entity>> ridingEntities = new HashMap<Entity, List<Entity>>();
		
		// loop all the passengers
		LinkedList<Entity> passengers = new LinkedList<Entity>();
		passengers.add(entity);
		
		// add all the entities into the hashmap
		Entity passenger_ = null;
		while(!passengers.isEmpty()) {
			passenger_ = passengers.pop();
			passengers.addAll(passenger_.getPassengers());
			
			ridingEntities.put(passenger_, passenger_.getPassengers());
			// unseat the riding entites
			entity.eject();
		}
		
		// entities to remove from the riding thing
		List<Entity> toRemove = new ArrayList<Entity>();
		
		// teleport all players to the new location
		for(Entity entity_ : ridingEntities.keySet()) {
			if(!entity_.teleport(tpLocation, tpCause))
				toRemove.add(entity_);
		}
		
		// remove any mounts that may have failed to teleport
		for(Entity ent : toRemove)
			ridingEntities.remove(ent);
		
		// mount the entities
		for(Entry<Entity, List<Entity>> entry : ridingEntities.entrySet()) {
			// seat all the passengers except those which failed to teleport
			for(Entity ent : entry.getValue()) {
				if(toRemove.contains(ent))
					continue;
				entry.getKey().addPassenger(ent);
			}
		}
		
		return tpLocation;
	}
	
	
	/**
	 * Swaps destination and origination locations
	 * @return true if swapped, false otherwise
	 */
	public boolean swap() {
		if(origination == null || destination == null)
			return false;
		Location l = origination;
		origination = destination;
		destination = l;
		return true;
	}
	
	
	/**
	 * Is the destination point safe for
	 * teleport. Does not count adjustments
	 * @return true if safe, false otherwise
	 */
	public SAFENESS isSafe(boolean allowWater, Material blockBelowFeet, Material blockAtFeet, Material blockAtHead) {
		// check below feet
		if((!(blockBelowFeet.isSolid() || blockBelowFeet.isOccluding()) && 
				(blockBelowFeet != Material.WATER || !allowWater)) ||
				blockBelowFeet == Material.LAVA)
			return SAFENESS.UNSAFE_FLOOR;
		
		// check block at feet
		if((blockAtFeet.isOccluding() || 
				(blockAtFeet == Material.WATER && !allowWater)) ||
				blockAtFeet == Material.LAVA)
			return SAFENESS.UNSAFE_FLOOR;
		
		// check at head
		if((blockAtHead.isOccluding() ||
				(blockAtFeet == Material.WATER && !allowWater)) || 
				blockAtHead == Material.LAVA) //Check eye level
			return SAFENESS.UNSAFE_CEILING;

		return SAFENESS.SAFE;
	}
	
	
	/**
	 * Is the destination point safe for
	 * teleport. Does not count adjustments
	 * @return true if safe, false otherwise
	 */
	public SAFENESS isSafe(boolean allowWater, Material blockBelowFeet, Material blockAtFeet) {
		// check below feet
		if((!(blockBelowFeet.isSolid() || blockBelowFeet.isOccluding()) && 
				(blockBelowFeet != Material.WATER || !allowWater)) ||
				blockBelowFeet == Material.LAVA)
			return SAFENESS.UNSAFE_FLOOR;
		
		// check block at feet
		if((blockAtFeet.isOccluding() || 
				(blockAtFeet == Material.WATER && !allowWater)) || 
				blockAtFeet == Material.LAVA)
			return SAFENESS.UNSAFE_FLOOR;

		return SAFENESS.SAFE;
	}
	
	
	/**
	 * Is the destination point safe for
	 * teleport. Does not count adjustments
	 * @return true if safe, false otherwise
	 */
	public SAFENESS isSafe(boolean allowWater, Material blockBelowFeet) {
		// check below feet
		if((!(blockBelowFeet.isSolid() || blockBelowFeet.isOccluding()) && 
				(blockBelowFeet != Material.WATER || !allowWater)) ||
				blockBelowFeet == Material.LAVA)
			return SAFENESS.UNSAFE_FLOOR;

		return SAFENESS.SAFE;
	}
	
	
	/**
	 * Is the destination point safe for
	 * teleport. Does not count adjustments
	 * @return true if safe, false otherwise
	 */
	@Deprecated
	public SAFENESS isSafe(EyeLevel eye, boolean allowWater) {
		Location atHead = destination.clone().add(0, eye.getHeight(), 0);
		Location belowFeet = destination.clone().subtract(0, 1, 0); //Check below feet
		
		if(((!belowFeet.getBlock().getType().isSolid() && !belowFeet.getBlock().getType().isOccluding()) &&
				(belowFeet.getBlock().getType() != Material.WATER || !allowWater))/*||
			((!belowFeet.clone().add(0, 1, 0).getBlock().getType().isSolid() && !belowFeet.clone().add(0, 1, 0).getBlock().getType().isOccluding()) &&
					belowFeet.clone().add(0, 1, 0).getBlock().getType() != Material.WATER)*/)
			return SAFENESS.UNSAFE_FLOOR;
		
		
		if((atHead.getBlock().getType().isOccluding() &&
				atHead.getBlock().getType().isSolid()) || 
				atHead.getBlock().getType() == Material.LAVA) //Check eye level
			return SAFENESS.UNSAFE_CEILING;

		return SAFENESS.SAFE;
	}
	
	
	public static enum SAFENESS {
		UNSAFE_FLOOR,
		UNSAFE_CEILING,		
		SAFE
	}
	
	@Deprecated
	public static enum EyeLevel {
		Player(1.8),
		Pig(0.9),
		Squid(0.95),
		Chicken(0.4),
		Sheep(1.3),
		Enderman(2.9);
		
		private double height;
		
		public double getHeight()
		{
			return this.height;
		}
		
		EyeLevel(double height)
		{
			this.height = height;
		}
	}
}
