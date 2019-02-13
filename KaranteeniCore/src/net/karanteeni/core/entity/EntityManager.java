package net.karanteeni.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class EntityManager {
	
	/**
	 * Returns the nearby entities in a radius
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Entity> getNearbyEntities(Location location, double radius)
	{
		return new ArrayList<Entity>(location.getWorld().getNearbyEntities(location, radius, radius, radius));
	}

	/**
	 * Returns the nearby entities in a radius
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Entity> getNearbyEntities(Location location, double x, double y, double z)
	{
		return new ArrayList<Entity>(location.getWorld().getNearbyEntities(location, x, y, z));
	}
	
	/**
	 * Returns the nearby players in a radius
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Player> getNearbyPlayers(Location location, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Player> players = new ArrayList<Player>();
		
		for(Entity ent : ents)
			if(ent.getType().equals(EntityType.PLAYER))
				players.add((Player)ent);
		
		return players;
	}
	
	/**
	 * Returns the nearest player to this location
	 * @param location
	 * @return
	 */
	public Player getNearestPlayer(Location location)
	{
		List<Player> players = location.getWorld().getPlayers();
		if(players.isEmpty()) return null;
		
		Player closest = players.get(0);
		
		for(Player player : players)
			if(location.distance(player.getLocation()) < location.distance(closest.getLocation()))
				closest = player;
		return closest;
	}
	
	/**
	 * Returns the nearby players
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Player> getNearbyPlayers(Location location, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Player> players = new ArrayList<Player>();
		
		for(Entity ent : ents)
			if(ent.getType().equals(EntityType.PLAYER))
				players.add((Player)ent);
		
		return players;
	}
	
	/**
	 * Returns the nearby hostile mobs
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Animals> getNearbyAnimals(Location location, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Animals> animals = new ArrayList<Animals>();
		
		for(Entity ent : ents)
			if(ent instanceof Animals)
				animals.add((Animals)ent);
		
		return animals;
	}
	
	/**
	 * Returns the nearby animals
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Animals> getNearbyAnimals(Location location, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Animals> animals = new ArrayList<Animals>();
		
		for(Entity ent : ents)
			if(ent instanceof Animals)
				animals.add((Animals)ent);
		
		return animals;
	}
	
	/**
	 * Returns the nearby hostile entities
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Monster> getNearbyMonster(Location location, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Monster> animals = new ArrayList<Monster>();
		
		for(Entity ent : ents)
			if(ent instanceof Monster)
				animals.add((Monster)ent);
		
		return animals;
	}
	
	/**
	 * Returns the nearby hostile entities
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Monster> getNearbyMonster(Location location, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Monster> animals = new ArrayList<Monster>();
		
		for(Entity ent : ents)
			if(ent instanceof Monster)
				animals.add((Monster)ent);
		
		return animals;
	}
	
	/**
	 * Returns the entities of a certain type in a radius
	 * @param location
	 * @param entity
	 * @param radius
	 * @return
	 */
	public List<Entity> getNearbyEntityTypes(Location location, EntityType entity, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Entity> animals = new ArrayList<Entity>();
		
		for(Entity ent : ents)
			if(ent.getType().equals(entity))
				animals.add(ent);
		
		return animals;
	}
	
	/**
	 * Gets the nearby entity types
	 * @param location
	 * @param entity
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Entity> getNearbyEntityTypes(Location location, EntityType entity, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Entity> animals = new ArrayList<Entity>();
		
		for(Entity ent : ents)
			if(ent.getType().equals(entity))
				animals.add(ent);
		
		return animals;
	}
	
	/**
	 * Gets the nearby arrows
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Arrow> getNearbyArrows(Location location, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Arrow> animals = new ArrayList<Arrow>();
		
		for(Entity ent : ents)
			if(ent instanceof Arrow)
				animals.add((Arrow)ent);
		
		return animals;
	}
	
	/**
	 * Gets the nearby arrows
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Arrow> getNearbyArrows(Location location, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Arrow> animals = new ArrayList<Arrow>();
		
		for(Entity ent : ents)
			if(ent instanceof Arrow)
				animals.add((Arrow)ent);
		
		return animals;
	}
	
	/**
	 * Gets the nearby creatures
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Creature> getNearbyCreatures(Location location, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Creature> animals = new ArrayList<Creature>();
		
		for(Entity ent : ents)
			if(ent instanceof Creature)
				animals.add((Creature)ent);
		
		return animals;
	}
	
	/**
	 * Gets the nearby creatures
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Creature> getNearbyCreatures(Location location, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Creature> animals = new ArrayList<Creature>();
		
		for(Entity ent : ents)
			if(ent instanceof Creature)
				animals.add((Creature)ent);
		
		return animals;
	}
	
	/**
	 * Gets the nearby hanging entities (painting and such)
	 * @param location
	 * @param radius
	 * @return
	 */
	public List<Hanging> getNearbyHanging(Location location, double radius)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, radius, radius, radius);
		List<Hanging> animals = new ArrayList<Hanging>();
		
		for(Entity ent : ents)
			if(ent instanceof Hanging)
				animals.add((Hanging)ent);
		
		return animals;
	}
	
	/**
	 * Returns the nearby hanging entities
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public List<Hanging> getNearbyHanging(Location location, double x, double y, double z)
	{
		Collection<Entity> ents = location.getWorld().getNearbyEntities(location, x, y, z);
		List<Hanging> animals = new ArrayList<Hanging>();
		
		for(Entity ent : ents)
			if(ent instanceof Hanging)
				animals.add((Hanging)ent);
		
		return animals;
	}
}
