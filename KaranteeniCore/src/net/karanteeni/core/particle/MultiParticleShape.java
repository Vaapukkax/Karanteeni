package net.karanteeni.core.particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.util.Vector;

import net.karanteeni.core.math.Point3D;

/**
 * A particle shape structure which consists of multiple particle
 * shapes
 * @author Nuubles
 *
 */
public abstract class MultiParticleShape implements Iterable<List<Point3D>>{
	/** List of list of points which creates shapes */
	private List<List<Point3D>> points = new ArrayList<List<Point3D>>();
	/** Current rotation of the shape */
	private List<Vector> rotations = new ArrayList<Vector>();
	
	/**
	 * Creates a particle shape with given points
	 */
	public MultiParticleShape(List<Point3D> points)
	{
		rotations.add(new Vector(0,0,0));
		this.points.add(points);
	}
	
	/**
	 * Creates a particle shape with given points
	 */
	public MultiParticleShape(List<Point3D> points, Vector rotation)
	{
		rotations.add(new Vector(0,0,0));
		this.points.add(points);
		this.setRotation(1, rotation.getX(), rotation.getY(), rotation.getZ());
	}
	
	/**
	 * Rotates point the shape by values
	 * @param x rotation on x
	 * @param y rotation on y
	 * @param z rotation on z
	 */
	public void rotate(int index, double x, double y, double z)
	{
		rotations.get(index).add(new Vector(x,y,z));
		setRotation(
				1,
				rotations.get(index).getX(),
				rotations.get(index).getY(),
				rotations.get(index).getZ());
	}
	
	/**
	 * Sets the rotation for points
	 * @param index index of the shape to be rotated
	 * @param x rotation on x
	 * @param y rotation on y
	 * @param z rotation on z
	 */
	public void setRotation(int index, double x, double y, double z)
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
		
		for(Point3D pts : points.get(index))
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
	public Iterator<List<Point3D>> iterator() {
		return points.iterator();
	}
}
