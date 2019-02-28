package net.karanteeni.core.data.math;

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
}
