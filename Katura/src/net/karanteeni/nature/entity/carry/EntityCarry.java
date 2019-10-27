package net.karanteeni.nature.entity.carry;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;
import net.karanteeni.nature.worldguard.WorldGuardManager;

public class EntityCarry implements Listener, TranslationContainer {
	private Katura plugin;
	private Set<UUID> carriers = new TreeSet<UUID>();
	private TreeMap<EntityType, Boolean> carriableEntities = new TreeMap<EntityType, Boolean>();
	private CarryEffects timer = null;
	
	public EntityCarry(Katura katura) {
		this.plugin = katura;
		
		registerTranslations();
		
		// init config
		loadConfig();
	}
	
	
	/**
	 * Loads the carriable entities config
	 */
	private void loadConfig() {
		for(EntityType entityType : EntityType.values())
		if(entityType.isAlive())
		if(!plugin.getConfig().isSet("carry." + entityType.name()+ ".carry")) {
			// by default don't allow non monster carry
			plugin.getConfig().set("carry." + entityType.name()+ ".carry", !Monster.class.isAssignableFrom(entityType.getEntityClass()));

			// add age limiter to ageable entities
			if(Ageable.class.isAssignableFrom(entityType.getEntityClass())) {
				plugin.getConfig().set("carry." + entityType.name()+ ".allowAdult", false);
			}
			plugin.saveConfig();
		}
		
		// loop all config entities and add them to allowed carriables
		for(EntityType entityType : EntityType.values())
		if(entityType.isAlive())
		if(plugin.getConfig().isSet("carry." + entityType.name()+ ".carry") && plugin.getConfig().getBoolean("carry." + entityType.name()+ ".carry"))
		carriableEntities.put(entityType, Ageable.class.isAssignableFrom(entityType.getEntityClass()) && 
											plugin.getConfig().getBoolean("carry." + entityType.name() + ".allowAdult"));
	}
	
	
	/**
	 * Player picks up an entity
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	protected void pickupEntity(PlayerInteractEntityEvent event) {
		// verify the hand is empty and the entity is not aggressive towards anything
		if((event.getPlayer().getInventory().getItemInMainHand() == null || 
				event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) &&
				(!(event.getRightClicked() instanceof Mob) || ((Mob)event.getRightClicked()).getTarget() == null)) {
			EntityType type = event.getRightClicked().getType();
			Entity entity = event.getRightClicked();
			
			// don't add new riders if player is already carrying an entity
			if(event.getPlayer().getPassengers().size() != 0)
				return;
			
			// check if this entity can be carried as an adult and whether the animal is adult
			boolean allowAdult = carriableEntities.getOrDefault(type, false);
			if((entity instanceof Ageable) && !(allowAdult || !((Ageable)entity).isAdult()))
				return;
			
			// verify this entity can be carried
			if(!carriableEntities.containsKey(type))
				return;
			
			// allow pickup only if player is allowed to build here
			WorldGuardManager wgm = plugin.getWorldGuardManager();
			if(wgm == null || wgm.canBuild(event.getPlayer(), event.getRightClicked().getLocation())) {
				event.getPlayer().addPassenger(event.getRightClicked());
				carriers.add(event.getPlayer().getUniqueId());
				// weigh the player down due to entity weight
				event.getPlayer().setWalkSpeed(getEntityWeight(event.getRightClicked()) / 5); // range goes from 1-0, default walking speed is 0.2
				event.getPlayer().setFlySpeed(getEntityWeight(event.getRightClicked()) / 10); // range goes from 1-0, default flying speed is 0.1
				beginEntityCarry(event.getPlayer(), event.getRightClicked());				
			}
		}
	}
	
	
	/**
	 * Player throws the entity away
	 * @param event
	 */
	/*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	protected void throwEntity(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		
		throwEntity(event.getPlayer());
	}*/
	
	
	/**
	 * Player damages the carried entity
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	protected void damageEntity(EntityDamageByEntityEvent event) {
		// verify the damager is player
		if(!(event.getDamager() instanceof Player))
			return;
		
		// check if the player is carrying the damaged entity
		if(carriers.contains(((Player)event.getDamager()).getUniqueId())) {
			event.setCancelled(true);

			// allow drop only if player can build here
			WorldGuardManager wgm = plugin.getWorldGuardManager();
			if(wgm == null || wgm.canBuild((Player)event.getDamager(), event.getDamager().getLocation())) {
				throwEntity((Player)event.getDamager());							
			} else {
				// tell player that they can't throw the entity here
				Katura.getMessager().sendActionBar((Player)event.getDamager(), Sounds.NO.get(), 
						Katura.getTranslator().getTranslation(plugin, (Player)event.getDamager(), "cannot-throw"));
			}
		}
	}
	
	
	/**
	 * Returns weight multiplier
	 * @param entity
	 * @return
	 */
	public float getEntityWeight(Entity entity) {
		return (float)(0.8f/(entity.getBoundingBox().getVolume()+0.8f));
	}
	
	
	/**
	 * Throws the entity away from the given player
	 * @param player player to throw the entity from
	 * @param entity entity to throw
	 */
	private void throwEntity(Player player) {
		if(carriers.remove(player.getUniqueId()))
		for(Entity ent : player.getPassengers()) {
			Vector direction = (player.getEyeLocation().getDirection().multiply(0.7));
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 1F, 1.2F);
			player.eject();
			
			// get the velocity multiplier using entity size (mass)
			double weight = getEntityWeight(ent);
			
			// throw the entity and push player back
			ent.setVelocity(player.getVelocity().multiply(1).add(direction).multiply(weight));
			player.setVelocity(player.getVelocity().add(direction.multiply(-0.3f)));
		}
		
		// remove potion effect from player if player is online
		timer.clearEffect(player.getUniqueId());
		
		// reset player walk speed
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		
		// remove unused empty timer
		if(carriers.isEmpty()) {
			Katura.getTimerHandler().unregisterTimer(timer);
			timer = null;
		}
	}
	
	
	/**
	 * Player begins to carry an entity, add possible special effects to carrying
	 * @param player
	 * @param entity
	 */
	private void beginEntityCarry(Player player, Entity entity) {
		// add custom effects for specific entities
		if(timer == null) {
			timer = new CarryEffects(this.carriers);
			Katura.getTimerHandler().registerTimer(timer, 10);
		}
	}
	
	
	/**
	 * Player quits, remove the carried entity
	 * @param event
	 */
	@EventHandler
	protected void playerQuit(PlayerQuitEvent event) {
		if(carriers.contains(event.getPlayer().getUniqueId())) {
			throwEntity(event.getPlayer());
		}
	}

	
	@EventHandler
	public void entityDeath(EntityDeathEvent event) {
		Entity vehicle = event.getEntity().getVehicle();
		if(vehicle == null)
			return;
		
		// remove the carried entity
		if(carriers.contains(vehicle.getUniqueId())) {
			throwEntity((Player)vehicle);
		}
	}
	

	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerTranslation(plugin, "cannot-throw", "You can't leave entities to protected areas");
	}
}
