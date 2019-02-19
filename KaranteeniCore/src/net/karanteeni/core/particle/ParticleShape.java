package net.karanteeni.core.particle;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.data.math.MineMath;
import net.karanteeni.core.data.math.Point3D;
import net.karanteeni.core.data.structures.Edge;
import net.karanteeni.core.data.structures.UndirectedAdjacencyListGraph;
import net.karanteeni.core.data.structures.Vertex;
import net.karanteeni.core.timers.KaranteeniTimer;

public class ParticleShape implements Iterable<Point3D>{
	/** List of points which creates shapes */
	private UndirectedAdjacencyListGraph<Point3D> points;
	private Vector startRotation;
	private Location startLocation;
	private Animator animator = null;
	private Animatable shapeDrawer;

	/** Current rotation of the shape */
	private Vector rotation = new Vector(0,0,0);
	
	/**
	 * Creates a particle shape with given points
	 */
	public ParticleShape(Collection<Point3D> points, Location currentLocation)
	{ 
		this.startLocation = currentLocation;
		this.points = new UndirectedAdjacencyListGraph<Point3D>();
		for(Point3D p : points)
			this.points.insertVertex(p);
	}
	
	/**
	 * Creates a particle shape with given points
	 */
	public ParticleShape(Collection<Point3D> points, Vector rotation, Location currentLocation)
	{ 
		this.startLocation = currentLocation;
		this.points = new UndirectedAdjacencyListGraph<Point3D>();
		for(Point3D p : points)
			this.points.insertVertex(p);
		this.rotate(rotation.getX(), rotation.getY(), rotation.getZ());
	}
	
	/**
	 * Creates a particle spahe with given graph
	 * @param points
	 */
	public ParticleShape(UndirectedAdjacencyListGraph<Point3D> points, Location currentLocation)
	{ 
		this.startLocation = currentLocation;
		this.points = points; 
	}
	
	/**
	 * Adds a new point to points
	 * @param point
	 */
	public final void addPoint(Point3D point)
	{ this.points.insertVertex(point); }
	
	/**
	 * Add edge to particle shape from point to another point
	 * @param from
	 * @param to
	 */
	public final void addEdge(Point3D from, Point3D to)
	{ points.insertUndirectedEdge(from, to); }
	
	/**
	 * Adds points to pointlist
	 * @param points
	 */
	public final void addPoints(Collection<Point3D> points)
	{
		for(Point3D point : points)
			this.points.insertVertex(point);
	}
	
	/**
	 * Rotates point the shape by values
	 * @param x rotation on x
	 * @param y rotation on y
	 * @param z rotation on z
	 */
	public final void rotate(double x, double y, double z)
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
	public final void setRotation(double x, double y, double z)
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
		
		for(Vertex<Point3D> pts : points.getVertices())
		for (double a = 0; a < Math.PI * 2; a += Math.PI / 20) {
			Vector vec = new Vector(x, 0, z);
			rotateAroundAxisX(vec, xAxisCos, xAxisSin);
			rotateAroundAxisY(vec, yAxisCos, yAxisSin);
			rotateAroundAxisZ(vec, zAxisCos, zAxisSin);
			// add vec to center, display particle and subtract vec from center.
			pts.getValue().setX(vec.getX());
			pts.getValue().setY(vec.getY());
			pts.getValue().setZ(vec.getZ());
		}
	}
	
	private final Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private final Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    private final Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    /**
     * Starts to animate the particleshape to a location
     * @param destination
     * @param rotation
     */
    public final void startAnimation(
    		KaranteeniPlugin plugin, 
    		Animatable shapeDrawer, 
    		Point3D destination, 
    		Vector rotation, 
    		ANIMATION style, 
    		long length)
    {
    	//This is used to draw the particles in the locations
    	this.shapeDrawer = shapeDrawer;
    	//If timer exists, stop the timer
    	if(animator != null)
    		KaranteeniPlugin.getTimerHandler().unregisterTimer(animator);
    	
    	//Create new class to animate the object
    	animator = new Animator(destination, rotation, style, length);
    	//Register the timer to animate the object
    	KaranteeniPlugin.getTimerHandler().registerTimer(animator, 1);
    }
    
    /**
     * Stops the animation
     */
    public final void stopAnimation()
    {
    	if(animator != null)
    	{
    		KaranteeniPlugin.getTimerHandler().unregisterTimer(animator);
    	}
    	animator = null;
    }
    
    public final void draw()
    {
    	
    }
    
    /**
     * Draws the particles
     * @param The location at which the particle will be played
     * @param corner If true, draws the corners, if false, draws the edges
     */
    private final void drawPoint(Location loc, boolean corner)
    {
    	if(corner)
    		shapeDrawer.playCornerParticle(loc);
    	else
    		shapeDrawer.playEdgeParticle(loc);
    }
    
    /**
     * Animator designated to animating the particleshape to different location
     * @author Nuubles
     *
     */
    private class Animator implements KaranteeniTimer{

    	/** Destination where the animator aims to go */
    	private Point3D destination = null;
    	private Location currLocation;
    	private Vector destrot = null;
    	private Vector currentRotation = null;
    	//private boolean ongoing = false;
    	//private ANIMATION style = ANIMATION.LINEAR;
    	private long animationEndTime;
    	private long animationLength;
    	//private boolean loop = false;
    	
    	public Animator(Point3D destination, Vector rotation, ANIMATION style, long length)
    	{
    		//Set the current location to the start location of animation
    		currLocation = ParticleShape.this.startLocation.clone();
    		currentRotation = ParticleShape.this.startRotation.clone();
			this.animationLength = length;
			this.animationEndTime = System.currentTimeMillis()+length;
    		this.destination = destination;
    		this.destrot = rotation;
    		//this.style = style;
    		//ongoing = true;
    	}
    	
    	/**
    	 * Modifies the current location and rotation
    	 * to be correct with animation timestamp
    	 */
    	private void advanceAnimationState()
    	{
    		//Get the current state of animation as a percentage
    		float currState = 1-((animationEndTime-System.currentTimeMillis())/animationLength);
    		
    		//Stop the animation if progress is going over limit
    		if(currState >= 1)
    		{
    			ParticleShape.this.stopAnimation();
    			return;
    		}
    		
    		//Calculate the location of the point in 3D line
    		Location l = ParticleShape.this.startLocation;
    		Point3D newLoc = MineMath.getLocationBetweenPoints(
    				new Point3D(l.getX(),l.getY(),l.getZ()), 
    				new Point3D(currLocation.getX(),currLocation.getY(),currLocation.getZ()), 
    				currState);
    		
    		//Update the current location
    		currLocation.setX(newLoc.getX());
    		currLocation.setY(newLoc.getY());
    		currLocation.setZ(newLoc.getZ());
    		
    		//Update the rotation of the point in 3D space
    		Vector v = ParticleShape.this.startRotation;
    		newLoc = MineMath.getLocationBetweenPoints(
    				new Point3D(v.getX(),v.getY(),v.getZ()), 
    				new Point3D(destrot.getX(),destrot.getY(),destrot.getZ()), 
    				currState);
    		//Update the current rotation
    		currentRotation.setX(newLoc.getX());
    		currentRotation.setY(newLoc.getY());
    		currentRotation.setZ(newLoc.getZ());
    		
    		//Update the rotation of the object
    		ParticleShape.this.setRotation(
    				currentRotation.getX(), 
    				currentRotation.getY(), 
    				currentRotation.getZ());
    	}
    	
    	/**
    	 * Makes the parent class draw the particles
    	 */
    	private void drawAnimation()
    	{
    		//Draw the corners of the shape
    		for(Point3D p : ParticleShape.this.points.getValues())
    		{
    			ParticleShape.this.drawPoint(new Location(
    					currLocation.getWorld(), 
    					p.getX(), 
    					p.getY(), 
    					p.getZ()), true);
    		}
    		
    		//Draw the edges of the shape
    		for(Edge<Point3D> edge : ParticleShape.this.points.getEdges())
    		{
    			//Draw multiple points on an edge
    			for(float prg = 0; prg <= 1; prg += 0.1)
    			{
	    			Point3D p = MineMath.getLocationBetweenPoints(
	    					edge.getFrom().getValue(), edge.getTo().getValue(), prg);
	        		
	    			ParticleShape.this.drawPoint(new Location(
	    					currLocation.getWorld(), 
	    					p.getX(), 
	    					p.getY(), 
	    					p.getZ()), true);
    			}
    		}
    	}
    	
    	/**
    	 * Draw and anime the motion for the particle
    	 */
    	@Override
    	public void runTimer() {
    		advanceAnimationState();
    		drawAnimation();
    	}

    	@Override
    	public void timerStopped() {
    		//Runs the given code when the animation has stopped
    		ParticleShape.this.shapeDrawer.animationStopped(
    				new Location(
    						ParticleShape.this.startLocation.getWorld(), 
    						this.destination.getX(),
    						this.destination.getY(),
    						this.destination.getZ()));
    	}

    	@Override
    	public void timerWait() {
    	}
    }
    
    /**
     * Used to creating animated classes for ParticleShape to play
     * @author Nuubles
     *
     */
    public static interface Animatable {
    	/**
    	 * This function draws the corners of the 
    	 * particle shape
    	 * @param loc Location for one of the corners
    	 */
    	public abstract void playCornerParticle(Location loc);
    	/**
    	 * This function draws the edges of the
    	 * particle shape
    	 * @param loc Location for a point on an edge
    	 */
    	public abstract void playEdgeParticle(Location loc);
    	
    	/**
    	 * Called when the animation has come to an end
    	 * @param loc
    	 */
    	public abstract void animationStopped(Location loc);
    }
    
    /**
     * Returns the iterator to all points in this shape
     */
	@Override
	public Iterator<Point3D> iterator() {
		return points.getValues().iterator();
	}
	
	/**
	 * Animation type for animating the 
	 * @author Nuubles
	 */
	public enum ANIMATION {
		LINEAR, SMOOTH, INCREMENTAL
	}
}
