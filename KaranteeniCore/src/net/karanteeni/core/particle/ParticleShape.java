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
import net.karanteeni.core.timers.KaranteeniTimer;

public class ParticleShape implements Iterable<Point3D>{
	/** List of points which creates shapes */
	private UndirectedAdjacencyListGraph<Point3D> points;
	private Vector startRotation = new Vector(0,0,0);
	private Location startLocation;
	private Animator animator = null;
	private StaticDrawer drawer = null;
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
	 * Returns the start location of the object
	 * @return
	 */
	public final Location getLocation()
	{ return this.startLocation; }
	
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
		//setRotation(rotation.getX(),rotation.getY(),rotation.getZ());
		setRotation(rotation.getX(),rotation.getY(),rotation.getZ(),1);
	}
	
	public final void setRotation(double yaw, double pitch, double roll, double scale) {
        // Convert to radians
		yaw = Math.toRadians(yaw);
		pitch = Math.toRadians(pitch);
		roll = Math.toRadians(roll);
		
		//Precalc the necessary variables
		double cy = Math.cos(yaw);
		double sy = Math.sin(yaw);
		double cp = Math.cos(pitch);
		double sp = Math.sin(pitch);
		double cr = Math.cos(roll);
		double sr = Math.sin(roll);
		
		//Normalize the rotations
		//double d = Math.sqrt(yaw*yaw+pitch*pitch+roll*roll);
		//Vector p = new Vector(yaw/d, pitch/d, roll/d);
		//Vector p = new Vector(yaw, pitch, roll);
		//p.normalize();
		
	    for (Point3D point : points.getValues()) {
	    	double tempx = point.getX(), x = point.getX();
	    	double tempy = point.getY(), y = point.getY();
	    	double tempz = point.getZ(), z = point.getZ();
	    	y = cy * y - sy * z;
		    z = cy * z + sy * tempy;
		    tempz = z;
		    z = cp * z - sp * x;
		    x = cp * x + sp * tempz;
		    tempx = x;
		    x = cr * x - sr * y;
		    y = cr * y + sr * tempx;
		    
	    	point.setX(x*scale);
	    	point.setY(y*scale);
	    	point.setZ(z*scale);
	    }
	}
	
	/**
	 * Sets the rotation for points
	 * @param x rotation on x
	 * @param y rotation on y
	 * @param z rotation on z
	 */
	/*public final void setRotation(double x, double y, double z)
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
		
		for(Vertex<Vector> pts : points.getVertices())
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
	}*/

	
	/*private final Vector rotateAroundAxisX(Vector v, double cos, double sin) {
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
    }*/

    /**
     * Starts to animate the particleshape to a location
     * @param destination
     * @param rotation
     */
    public final void startAnimation(
    		KaranteeniPlugin plugin, 
    		Animatable shapeDrawer, 
    		Vector destination, 
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
    	KaranteeniPlugin.getTimerHandler().registerTimer(animator, 4);
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
    
    public final void drawOnce()
    {
    	//Draw the corners of the shape
		for(Point3D p : ParticleShape.this.points.getValues())
			drawPoint(p);
		
		//Draw the edges of the shape
		for(Edge<Point3D> edge : ParticleShape.this.points.getEdges())
			drawEdge(edge);
    }
    
    /**
     * Draws the point of the shape
     * @param point
     */
    private void drawPoint(Point3D p)
    {
    	this.drawPoint(new Location(
				startLocation.getWorld(), 
				p.getX()+startLocation.getX(), 
				p.getY()+startLocation.getY(), 
				p.getZ()+startLocation.getZ()), true);
    }
    
    /**
     * Draws the edge of a shape
     * @param edge
     */
    private void drawEdge(Edge<Point3D> edge)
    {
    	double distance = edge.getFrom().getValue().distance(edge.getTo().getValue());

    	//Draw multiple points on an edge in smooth intervals
		for(float i = 0.3f; i < distance; i += 0.3f)
		{
			Point3D p = MineMath.getLocationBetweenPoints(
					edge.getFrom().getValue(), edge.getTo().getValue(), i/distance);
    		
			this.drawPoint(new Location(
					startLocation.getWorld(), 
					p.getX()+startLocation.getX(), 
					p.getY()+startLocation.getY(), 
					p.getZ()+startLocation.getZ()), false);
		}
    }
    
    /**
     * Starts to draw the particle shape with given methods
     * @param plugin
     * @param shapeDrawer
     */
    public void show(
    		KaranteeniPlugin plugin, 
    		Animatable shapeDrawer)
    {
    	//This is used to draw the particles in the locations
    	this.shapeDrawer = shapeDrawer;
    	//If timer exists, stop the timer
    	if(animator != null)
    		KaranteeniPlugin.getTimerHandler().unregisterTimer(animator);
    	
    	//Create new class to draw the object
    	drawer = new StaticDrawer();
    	//Register the timer to animate the object
    	KaranteeniPlugin.getTimerHandler().registerTimer(drawer, 4);
    }
    
    /**
     * Stops the static draw of the particle shape
     */
    public void hide()
    {
    	if(drawer == null)
    		return;
    	KaranteeniPlugin.getTimerHandler().unregisterTimer(drawer);
    	drawer = null;
    	
    }
    
    /**
     * Used to draw all the particles using the class given to this class
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
     * Draws the shape to a static location until cancelled
     * @author Nuubles
     *
     */
    private class StaticDrawer implements KaranteeniTimer 
    {
		@Override
		public void runTimer() {
			//Draw the corners of the shape
    		for(Point3D p : ParticleShape.this.points.getValues())
    			ParticleShape.this.drawPoint(p);
    		
    		//Draw the edges of the shape
    		for(Edge<Point3D> edge : ParticleShape.this.points.getEdges())
    			ParticleShape.this.drawEdge(edge);
		}

		@Override
		public void timerStopped() {
		}

		@Override
		public void timerWait() {
		}
    }
    
    /**
     * Animator designated to animating the particleshape to different location
     * @author Nuubles
     *
     */
    private class Animator implements KaranteeniTimer {

    	/** Destination where the animator aims to go */
    	private Vector destination = null;
    	private Location currLocation;
    	private Vector destrot = null;
    	private Vector currentRotation = null;
    	//private boolean ongoing = false;
    	//private ANIMATION style = ANIMATION.LINEAR;
    	private long animationEndTime;
    	private long animationLength;
    	//private long animationStartTime;
    	//private boolean loop = false;
    	
    	public Animator(Vector destination, Vector rotation, ANIMATION style, long length)
    	{
    		//Set the current location to the start location of animation
    		currLocation = ParticleShape.this.startLocation.clone();
    		currentRotation = ParticleShape.this.startRotation.clone();
			this.animationLength = length;
			this.animationEndTime = System.currentTimeMillis()+length;
    		this.destination = destination;
    		this.destrot = rotation;
    		//this.animationStartTime = System.currentTimeMillis();
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
    		float currState = (float)(animationEndTime-System.currentTimeMillis())/animationLength; 
    		
    		//Stop the animation if progress is going over limit
    		if(currState < 0)
    		{
    			ParticleShape.this.stopAnimation();
    			return;
    		}
    		
    		//Calculate the location of the point in 3D line
    		Location l = ParticleShape.this.startLocation;
    		Vector newLoc = MineMath.getLocationBetweenPoints(
    				new Vector(l.getX(),l.getY(),l.getZ()), 
    				new Vector(currLocation.getX(),currLocation.getY(),currLocation.getZ()), 
    				currState);
    		
    		//Update the current location
    		currLocation.setX(newLoc.getX());
    		currLocation.setY(newLoc.getY());
    		currLocation.setZ(newLoc.getZ());
    		
    		//Update the rotation of the point in 3D space
    		Vector v = ParticleShape.this.startRotation;
    		newLoc = MineMath.getLocationBetweenPoints(
    				new Vector(v.getX(),v.getY(),v.getZ()), 
    				new Vector(destrot.getX(),destrot.getY(),destrot.getZ()), 
    				currState);
    		//Update the current rotation
    		currentRotation.setX(newLoc.getX());
    		currentRotation.setY(newLoc.getY());
    		currentRotation.setZ(newLoc.getZ());
    		
    		//Update the rotation of the object
    		ParticleShape.this.setRotation(
    				currentRotation.getX(), 
    				currentRotation.getY(), 
    				currentRotation.getZ(), 1);
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
    					p.getX()+startLocation.getX(), 
    					p.getY()+startLocation.getY(), 
    					p.getZ()+startLocation.getZ()), true);
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
	    					ParticleShape.this.startLocation.getWorld(),
	    					p.getX()+startLocation.getX(),
	    					p.getY()+startLocation.getY(),
	    					p.getZ()+startLocation.getZ()), 
	    					false);
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
    		
    		//Update position and rotation of shape to main class
    		ParticleShape.this.startLocation = currLocation;
    		ParticleShape.this.rotation = destrot;
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
