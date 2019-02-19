package net.karanteeni.core.data.math;

import org.bukkit.util.Vector;

/**
 * A class for a 3D point in space with
 * x,y,z coordinates.
 * @author Nuubles
 *
 */
public class Point3D {
	/** The coordinates of the point */
	private double x,y,z;
	
	public Point3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Initializes the coordinates to have values 0,0,0
	 */
	public Point3D() 
	{}
	
	public Point3D(Vector v)
	{
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();
	}
	
	/**
	 * Adds to the x coordinate
	 * @param x addition to coordinate
	 */
	public void addX(double x)
	{ this.x += x; }
	
	/**
	 * Adds to the y coordinate
	 * @param y addition to coordinate
	 */
	public void addY(double y)
	{ this.y += y; }
	
	/**
	 * Adds to the z coordinate
	 * @param z addition to coordinate
	 */
	public void addZ(double z)
	{ this.z += z; }
	
	/**
	 * Adds to the x coordinate
	 * @param x subtraction to coordinate
	 */
	public void subtractX(double x)
	{ this.x -= x; }
	
	/**
	 * Adds to the y coordinate
	 * @param y subtraction to coordinate
	 */
	public void subtractY(double y)
	{ this.y -= y; }
	
	/**
	 * Adds these two points together
	 * @param p points coordinates to be added
	 */
	public void add(final Point3D p)
	{
		this.x += p.getX();
		this.y += p.getY();
		this.z += p.getZ();
	}
	
	/**
	 * Subtracts these two points together
	 * @param p points coordinates to be subtracted
	 */
	public void subtract(final Point3D p)
	{
		this.x -= p.getX();
		this.y -= p.getY();
		this.z -= p.getZ();
	}
	
	/**
	 * Returns the distance of two points
	 * @param p another point
	 * @return the distance between these points
	 */
	public double distance(final Point3D p)
	{
		return Math.sqrt(Math.pow(x - p.getX(), 2) + 
				Math.pow(y - p.getY(), 2) + Math.pow(z - p.getZ(), 2));
	}
	
	/**
	 * Adds to the z coordinate
	 * @param z subtraction to coordinate
	 */
	public void subtractZ(double z)
	{ this.z -= z; }
	
	/**
	 * Sets the X coordinate of the point
	 * @param x new x coordinate
	 */
	public void setX(double x)
	{ this.x = x; }
	
	/**
	 * Sets the Z coordinate of the point
	 * @param z new z coordinate
	 */
	public void setY(double y)
	{ this.y = y; }
	
	/**
	 * Sets the Y coordinate of the point
	 * @param y new y coordinate
	 */
	public void setZ(double z)
	{ this.z = z; }
	
	/**
	 * Gets the x coordinate of the point
	 * @return x coordinate
	 */
	public double getX()
	{ return x; }
	
	/**
	 * Gets the y coordinate of the point
	 * @return y coordinate
	 */
	public double getY()
	{ return y; }
	
	/**
	 * Gets the z coordinate of the point
	 * @return z coordinate
	 */
	public double getZ()
	{ return z; }
	
	/**
	 * Returns true if the other object is at the same coordinates as this one
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Point3D)
		{
			Point3D p = (Point3D)o;
			return (x==p.getX() && y==p.getY() && z==p.getZ());
		}
		
		return false;
	}
	
	/**
	 * Returns the point as a string
	 */
	@Override
	public String toString()
	{
		return String.format("{%s,%s,%s}",x,y,z);
	}
}
