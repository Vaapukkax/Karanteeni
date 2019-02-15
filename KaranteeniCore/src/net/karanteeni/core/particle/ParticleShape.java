package net.karanteeni.core.particle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.util.Vector;

import net.karanteeni.core.math.Point3D;

public class ParticleShape implements Iterable<Point3D>{
	/** List of points which creates shapes */
	private List<Point3D> points;
	/** Current rotation of the shape */
	private Vector rotation = new Vector(0,0,0);
	
	/**
	 * Creates a particle shape with given points
	 */
	public ParticleShape(Collection<Point3D> points)
	{ this.points = new ArrayList<Point3D>(points); }
	
	/**
	 * Creates a particle shape with given points
	 */
	public ParticleShape(Collection<Point3D> points, Vector rotation)
	{ 
		this.points = new ArrayList<Point3D>(points);
		this.rotate(rotation.getX(), rotation.getY(), rotation.getZ());
	}
	
	/**
	 * Adds a new point to points
	 * @param point
	 */
	public void addPoint(Point3D point)
	{
		this.points.add(point);
	}
	
	/**
	 * Adds points to pointlist
	 * @param points
	 */
	public void addPoints(Collection<Point3D> points)
	{
		this.points.addAll(points);
	}
	
	/**
	 * Rotates point the shape by values
	 * @param x rotation on x
	 * @param y rotation on y
	 * @param z rotation on z
	 */
	public void rotate(double x, double y, double z)
	{
		rotation.add(new Vector(x,y,z));
		setRotation(rotation.getX(),rotation.getY(),rotation.getZ());
	}
	
	/**
	 * Sets the rotation for points
	 * @param x rotation on x
	 * @param y rotation on y
	 * @param z rotation on z
	 */
	public void setRotation(double x, double y, double z)
	{
		// the numbers are the angles on which you want to rotate your animation.
		double xangle = x; // note that here we do have to convert to radians.
		double xAxisCos = Math.cos(xangle); // getting the cos value for the pitch.
		double xAxisSin = Math.sin(xangle); // getting the sin value for the pitch.

		double yangle = y; // note that here we do have to convert to radians.
		double yAxisCos = Math.cos(-yangle); // getting the cos value for the yaw.
		double yAxisSin = Math.sin(-yangle); // getting the sin value for the yaw.

		double zangle = z; // note that here we do have to convert to radians.
		double zAxisCos = Math.cos(zangle); // getting the cos value for the roll.
		double zAxisSin = Math.sin(zangle); // getting the sin value for the roll.
		
		for(Point3D pts : points)
		for (double a = 0; a < Math.PI * 2; a += Math.PI / 20) {
			Vector vec = new Vector(x, 0, z);
			rotateAroundAxisX(vec, xAxisCos, xAxisSin);
			rotateAroundAxisY(vec, yAxisCos, yAxisSin);
			rotateAroundAxisZ(vec, zAxisCos, zAxisSin);
			// add vec to center, display particle and subtract vec from center.
			pts.setX(vec.getX());
			pts.setY(vec.getY());
			pts.setZ(vec.getZ());
		}
	}
	
	private Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    private Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    /**
     * Returns the iterator to all points in this shape
     */
	@Override
	public Iterator<Point3D> iterator() {
		return points.iterator();
	}
}
