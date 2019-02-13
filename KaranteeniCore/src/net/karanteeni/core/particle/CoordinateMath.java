package net.karanteeni.core.particle;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

public class CoordinateMath {
    
	/**
	 * Returns coordinates for an empty hollow cubes
	 * @param corner1
	 * @param corner2
	 * @param particleDistance
	 * @return
	 */
	public static List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
		List<Location> result = new ArrayList<Location>();
		World world = corner1.getWorld();
		double minX = Math.min(corner1.getX(), corner2.getX());
		double minY = Math.min(corner1.getY(), corner2.getY());
		double minZ = Math.min(corner1.getZ(), corner2.getZ());
		double maxX = Math.max(corner1.getX(), corner2.getX());
		double maxY = Math.max(corner1.getY(), corner2.getY());
		double maxZ = Math.max(corner1.getZ(), corner2.getZ());
		
		for (double x = minX; x <= maxX; x = roundTo4(x + particleDistance)) {
			for (double y = minY; y <= maxY; y = roundTo4(y + particleDistance)) {
				for (double z = minZ; z <= maxZ; z = roundTo4(z + particleDistance)) {
					int components = 0;
					if (x == minX || maxX == x) components++;
					if (y == minY || maxY == y) components++;
					if (z == minZ || maxZ == z) components++;
					if (components >= 2) {
						result.add(new Location(world, x, y, z));
					}
				}
			}
		}
	  
		return result;
	}
	
	/**
	 * Rounds the double value to precision of 0.01
	 * @param d
	 * @return
	 */
	private static double roundTo4(double d)
	{
		return Math.round(d*1000.0)/1000.0;
	}
}