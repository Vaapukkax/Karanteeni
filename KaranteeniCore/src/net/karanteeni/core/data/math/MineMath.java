package net.karanteeni.core.data.math;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MineMath {
	
	/**
	 * Gets the location on a line between two points
	 * @param p1 First point
	 * @param p2 Second point
	 * @param progress 0..1
	 * @return Location between two points
	 */
	public static Point3D getLocationBetweenPoints(Point3D p1, Point3D p2, double progress)
	{
		double x = (1-progress) * p2.getX() + progress * p1.getX();
		double y = (1-progress) * p2.getY() + progress * p1.getY();
		double z = (1-progress) * p2.getZ() + progress * p1.getZ();
		return new Point3D(x,y,z);
	}
	
	/**
	 * Gets the location on a line between two points
	 * @param p1 First point
	 * @param p2 Second point
	 * @param progress 0..1
	 * @return Location between two points
	 */
	public static Vector getLocationBetweenPoints(Vector p1, Vector p2, double progress)
	{
		double x = (1-progress) * p2.getX() + progress * p1.getX();
		double y = (1-progress) * p2.getY() + progress * p1.getY();
		double z = (1-progress) * p2.getZ() + progress * p1.getZ();
		return new Vector(x,y,z);
	}
	
	/**
	 * Calculate the standard deviation between given locations in X and Z coordinates
	 * @param locs
	 * @return
	 */
	public static double calculateStandardDeviationXZ(Collection<Location> locs)
	{
		double sum = 0.0, sd = 0.0;
		int length = locs.size();
		
		for(Location loc : locs)
		{
			sum += loc.getX() + loc.getZ();
		}
		
		double mean = sum/length;
		
		for(Location loc : locs) 
		{
			sd += Math.pow(loc.getX()+loc.getZ()-mean, 2);
		}
		
		return Math.sqrt(sd/length);
	}
}
