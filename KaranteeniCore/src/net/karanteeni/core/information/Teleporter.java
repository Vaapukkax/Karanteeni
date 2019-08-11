package net.karanteeni.core.information;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

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
	 * Teleports an entity to this destination in the middle of the block
	 * @param entity Entity to teleport
	 * @return The location to which the entity was teleported
	 */
	public Location preciseTeleport(LivingEntity entity, boolean safe)
	{
		if(safe)
			destination.setY(destination.getBlockY() + 1);
		
		Location l = destination;
		// center the coordinates to the middle of the block
		l.setX((double)l.getBlockX()+0.5);
		l.setZ((double)l.getBlockZ()+0.5);
		
		if(safe) {
			SAFENESS safestat = isSafe(EyeLevel.Player);
			boolean goingDown = safestat == SAFENESS.UNSAFE_FLOOR;
			
			while(safestat != SAFENESS.SAFE) {
				if(safestat == SAFENESS.UNSAFE_FLOOR) {
					if(!goingDown) //Prevent infinite loops 
						break;
					goingDown = true;
					
					l.subtract(0, 1, 0);
					if(l.getBlockY() == 1) //Going under bedrock
						break;
					
					safestat = isSafe(EyeLevel.Player);
				} else if(safestat == SAFENESS.UNSAFE_CEILING) {
					if(goingDown) //Prevent infinite loops 
						break;
					goingDown = false;
					
					l.add(0, 1, 0);
					if(l.getBlockY() == 255){ //Going over build limit
						safestat = SAFENESS.SAFE;
						break;
					}
					
					safestat = isSafe(EyeLevel.Player);
				}
			}
			
			if(safestat != SAFENESS.SAFE) //Unsafe and unable to adjust
				return null;
			
			//if(goingDown) //Fix 1 block error
			//l.add(0, 1, 0);
		}
		
		Location orig = entity.getLocation();
		if(!entity.teleport(l)) //Was the teleport successful
			return null;
		this.origination = orig;
		this.destination = l;
		return l;
	}
	
	
	/**
	 * Teleports an entity to this destination and sets
	 * the origination point.
	 * @param entity Entity to teleport
	 * @return The location to which the entity was teleported
	 */
	public Location teleport(LivingEntity entity, boolean safe) {
		if(safe)
			destination.setY(destination.getBlockY() + 1);
		
		Location l = destination;
		
		if(safe) {
			SAFENESS safestat = isSafe(EyeLevel.Player);
			boolean goingDown = safestat == SAFENESS.UNSAFE_FLOOR;
			
			while(safestat != SAFENESS.SAFE) {
				if(safestat == SAFENESS.UNSAFE_FLOOR) {
					if(!goingDown) //Prevent infinite loops 
						break;
					goingDown = true;
					
					l.subtract(0, 1.0, 0);
					if(l.getBlockY() == 1) //Going under bedrock
						break;
					
					safestat = isSafe(EyeLevel.Player);
				} else if(safestat == SAFENESS.UNSAFE_CEILING) {
					if(goingDown) //Prevent infinite loops 
						break;
					goingDown = false;
					
					l.add(0, 1.0, 0);
					if(l.getBlockY() == 255){ //Going over build limit
						safestat = SAFENESS.SAFE;
						break;
					}
					
					safestat = isSafe(EyeLevel.Player);
				}
			}
			
			if(safestat != SAFENESS.SAFE) //Unsafe and unable to adjust
				return null;
			
			//if(goingDown) //Fix 1 block error
			//l.add(0, 1, 0);
		}
		
		Location orig = entity.getLocation();
		if(!entity.teleport(l)) //Was the teleport successful
			return null;
		this.origination = orig;
		this.destination = l;
		return l;
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
	public SAFENESS isSafe(EyeLevel eye) {
		Location atHead = destination.clone().add(0, eye.getHeight(), 0);
		Location belowFeet = destination.clone().subtract(0, 1, 0); //Check below feet
		
		if(((!belowFeet.getBlock().getType().isSolid() && !belowFeet.getBlock().getType().isOccluding()) &&
				belowFeet.getBlock().getType() != Material.WATER)/*||
			((!belowFeet.clone().add(0, 1, 0).getBlock().getType().isSolid() && !belowFeet.clone().add(0, 1, 0).getBlock().getType().isOccluding()) &&
					belowFeet.clone().add(0, 1, 0).getBlock().getType() != Material.WATER)*/)
			return SAFENESS.UNSAFE_FLOOR;
		
		
		if((atHead.add(0, 1+eye.height, 0).getBlock().getType().isOccluding() &&
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
