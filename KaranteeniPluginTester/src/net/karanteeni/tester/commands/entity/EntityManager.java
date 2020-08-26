package net.karanteeni.tester.commands.entity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.data.Entry;
import net.karanteeni.tester.sound.SoundLibrary;

public class EntityManager {
	/**
	 * Effects displayed when an entity is healed through the entitymanager
	 * @param location location of the effect
	 */
	private void healEffect(Entity entity) {
		entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation().add(0, entity.getHeight()/2, 0), 10, 0.5, 0.5, 0.5, 0.05);
	}
	
	
	/**
	 * Effects displayed when an entity is killed through the entitymanager
	 * @param location location of the effect
	 */
	private void killEffect(Entity entity) {
		entity.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, entity.getLocation().add(0, entity.getHeight()/2, 0), 3, 0.5, 0.5, 0.5, 0.05);
	}
	
	
	/**
	 * Plays kill sound at the location of the given entity
	 * @param entity
	 */
	private void killSound(Entity entity) {
		KaranteeniCore.getSoundHandler().playSound(entity.getWorld(), SoundLibrary.DEATH.get(), entity.getLocation());
	}
	
	
	/**
	 * Plays heal sound at the location of the given entity
	 * @param entity
	 */
	private void healSound(Entity entity) {
		KaranteeniCore.getSoundHandler().playSound(entity.getWorld(), SoundLibrary.HEAL.get(), entity.getLocation());
	}
	
	
	/**
	 * Kills the entity with the given UUID
	 * @param uuid uuid of the entity to kill
	 * @return 
	 */
	public Entity killEntity(UUID uuid) {
		if(uuid == null)
			return null;
		
		Entity entity = Bukkit.getEntity(uuid);
		
		if(entity == null || !(entity instanceof Damageable))
			return null;
		
		Damageable damageable = (Damageable)entity;
		damageable.setHealth(0);
		killEffect(entity);
		killSound(entity);
		return entity;
	}
	
	
	/**
	 * Kills count nearest entities excluding pivot 
	 * @param pivot entity from which the entities are searched
	 * @param count amount of entities found
	 * @return entities killed
	 */
	public List<Entity> killNearestEntities(Entity pivot, int count) {
		if(pivot == null) 
			return null;
		List<Entity> entities = getNearestEntities(pivot, count);
		
		for(Entity entity : entities) {
			Damageable damageable = (Damageable)entity;
			damageable.setHealth(0);
			killEffect(damageable);
		}
		
		if(entities.size() > 0)
			killSound(pivot);
		
		return entities;
	}
	
	
	/**
	 * Kills count nearest entities of type type excluding pivot
	 * @param pivot nearest entities from this
	 * @param count amount of nearest entities to heal
	 * @param type types of entities to search for
	 * @return list of entities killed
	 */
	public List<Entity> killNearestEntities(Entity pivot, int count, EntityType type) {
		if(pivot == null || type == null || !(Damageable.class.isAssignableFrom(type.getEntityClass()))) 
			return null;
		List<Entity> entities = getNearestEntities(pivot, count, type);
		
		for(Entity entity : entities) {
			Damageable damageable = (Damageable)entity;
			damageable.setHealth(0);
			killEffect(damageable);
		}
		
		if(entities.size() > 0)
			killSound(pivot);
		
		return entities;
	}
	
	
	/**
	 * Heals the entity with the given UUID
	 * @param uuid uuid of the entity to heal
	 * @return 
	 */
	public Entity healEntity(UUID uuid) {
		if(uuid == null)
			return null;
		
		Entity entity = Bukkit.getEntity(uuid);
		
		if(entity == null || !(entity instanceof Damageable))
			return null;
		
		Damageable damageable = (Damageable)entity;
		damageable.setHealth(((Attributable)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		healEffect(entity);
		healSound(entity);
		return entity;
	}
	
	
	/**
	 * Heals count nearest entities excluding pivot 
	 * @param pivot entity from which the entities are searched
	 * @param count amount of entities found
	 * @return entities healed
	 */
	public List<Entity> healNearestEntities(Entity pivot, int count) {
		if(pivot == null) 
			return null;
		List<Entity> entities = getNearestEntities(pivot, count);
		
		for(Entity entity : entities) {
			Damageable damageable = (Damageable)entity;
			damageable.setHealth(((Attributable)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			healEffect(damageable);
		}
		
		if(entities.size() > 0)
			healSound(pivot);
		
		return entities;
	}
	
	
	/**
	 * Heals count nearest entities of type type excluding pivot
	 * @param pivot nearest entities from this
	 * @param count amount of nearest entities to heal
	 * @param type types of entities to search for
	 * @return list of entities healed
	 */
	public List<Entity> healNearestEntities(Entity pivot, int count, EntityType type) {
		if(pivot == null || type == null || !(Damageable.class.isAssignableFrom(type.getEntityClass()))) 
			return null;
		List<Entity> entities = getNearestEntities(pivot, count, type);
		
		for(Entity entity : entities) {
			Damageable damageable = (Damageable)entity;
			damageable.setHealth(((Attributable)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			healEffect(damageable);
		}
		
		if(entities.size() > 0)
			healSound(pivot);
		
		return entities;
	}
	
	
	/**
	 * Lists all nearby entities related to the player
	 * @param pivot nearest entities from this entity, excluding this
	 * @param count amount of nearest entities to collect
	 * @return set of all the nearest entities in the order of their distance
	 */
	public List<Entity> getNearestEntities(Entity pivot, int count) {
		if(pivot == null) 
			return null;
		
		TreeSet<Entry<Double, Entity>> nearestEntities = new TreeSet<Entry<Double, Entity>>();
		List<Entity> entities = pivot.getWorld().getEntities();
		
		for(Entity entity : entities) {
			if(!(entity instanceof Damageable) || entity.getUniqueId().equals(pivot.getUniqueId()))
				continue;
			
			nearestEntities.add(new Entry<Double, Entity>(pivot.getLocation().distance(entity.getLocation()), entity));
			if(nearestEntities.size() > count)
				nearestEntities.pollLast();
		}
		
		List<Entity> results = new LinkedList<Entity>();
		for(Entry<Double, Entity> entry : nearestEntities) {
			results.add(entry.getValue());
		}
		
		return results;
	}
	
	
	/**
	 * Lists all nearby entities related to the player
	 * @param pivot nearest entities from this entity, excluding this
	 * @param count amount of nearest entities to collect
	 * @param type types of entities to collect
	 * @return set of all the nearest entities in the order of their distance
	 */
	public List<Entity> getNearestEntities(Entity pivot, int count, EntityType type) {
		if(pivot == null || type == null) 
			return null;
		
		TreeSet<Entry<Double, Entity>> nearestEntities = new TreeSet<Entry<Double, Entity>>();
		Collection<? extends Entity> entities = pivot.getWorld().getEntitiesByClass(type.getEntityClass());
		
		for(Entity entity : entities) {
			if(!(entity instanceof Damageable) || entity.getUniqueId().equals(pivot.getUniqueId()))
				continue;
			
			nearestEntities.add(new Entry<Double, Entity>(pivot.getLocation().distance(entity.getLocation()), entity));
			if(nearestEntities.size() > count)
				nearestEntities.pollLast();
		}
		
		List<Entity> results = new LinkedList<Entity>();
		for(Entry<Double, Entity> entry : nearestEntities) {
			results.add(entry.getValue());
		}
		
		return results;
	}
}
