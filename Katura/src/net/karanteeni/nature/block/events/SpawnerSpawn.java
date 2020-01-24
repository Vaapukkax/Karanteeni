package net.karanteeni.nature.block.events;

import java.util.Arrays;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import net.karanteeni.core.block.BlockEffects;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.item.ItemType;
import net.karanteeni.nature.Katura;

public class SpawnerSpawn implements Listener,TranslationContainer {
	private static Katura pl = Katura.getPlugin(Katura.class);
	private static String ratePrefix = "entity.%s.spawner.rate";
	private static String allowSpawnPrefix = "entity.%s.spawner.allow.spawn";
	private static String allowSetPrefix = "entity.%s.spawner.allow.commandeggset";
	private static String entityNotAllowed = "entity-not-allowed";
	
	public SpawnerSpawn() {
		registerTranslations();
		
		for(EntityType type : EntityType.values())
			if(type.isSpawnable()) {
				if(!pl.getConfig().isSet(String.format(ratePrefix, type.toString()))) 
					pl.getConfig().set(String.format(ratePrefix, type.toString()), 1.0);
				
				if(!pl.getConfig().isSet(String.format(allowSpawnPrefix, type.toString()))) 
					pl.getConfig().set(String.format(allowSpawnPrefix, type.toString()), true);
				
				if(!pl.getConfig().isSet(String.format(allowSetPrefix, type.toString()))) 
					pl.getConfig().set(String.format(allowSetPrefix, type.toString()), true);
			}
		pl.saveConfig();
	}
	

	/**
	 * Allows or disallows entity spawn from spawner
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void onCreatureSpawn(CreatureSpawnEvent event) {
		if(!event.getSpawnReason().equals(SpawnReason.SPAWNER))
			return;
		
		//Check whether config allows this entity to spawn from spawner
		if(pl.getConfig().getBoolean(
				String.format(allowSpawnPrefix, event.getEntityType().toString())))
			return;
		
		event.setCancelled(true);
	}
	
	
	/**
	 * Handles spawner breaks
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onSpawnerBreak(BlockBreakEvent event) {
		if(!event.getBlock().getType().equals(Material.SPAWNER))
			return;
		if(!ItemType.PICKAXE.contains(event.getPlayer().getInventory().getItemInMainHand().getType()))
			return;
		
		if(!event.getPlayer().hasPermission("katura.dropspawner.always")) { //Continue if player can always pick spawner
			if(event.getPlayer().hasPermission("katura.dropspawner.silk")) { //Check if player can pick with silk
				if(!event.getPlayer().getInventory().getItemInMainHand() //If player does not have silk return
						.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
					return;
				}
			} else {
				return;
			}
		}
		// register to coreprotect
		/*if(pl.isCoreProtectEnabled()) {
			pl.getCoreProtectAccess().getCoreProtect().logRemoval(
					event.getPlayer().getName(), 
					event.getBlock().getLocation(), 
					event.getBlock().getType(), 
					event.getBlock().getBlockData());
		}*/
		
		//Drop the spawner etc. only with picaxe
		if(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || 
				event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
			//Drop the broken spawner
			CreatureSpawner cs = (CreatureSpawner) event.getBlock().getState();
			ItemStack spawner = new ItemStack(event.getBlock().getType(), 1);
			BlockStateMeta blockMeta = (BlockStateMeta) spawner.getItemMeta();
			blockMeta.setBlockState(cs);
			//Add the spawned type to lore
			blockMeta.setLore(Arrays.asList("§7"+cs.getSpawnedType().toString()));
			spawner.setItemMeta(blockMeta);
			event.setExpToDrop(0); // don't drop any xp on silk touch drop
		
			event.getBlock().getLocation().getWorld().dropItemNaturally(
				event.getBlock().getLocation(), spawner);
		
			//Create breaking effects
			Katura.getBlockManager().getBlockEffects().createEffect(
					event.getBlock(), 
					BlockEffects.Effect.ENCHANT, 
					Particle.ENCHANTMENT_TABLE, 
					new SoundType(Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1.8f));
		}
	}
	
	
	/**
	 * Sets the spawners spawn rate
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSpawnerPlace(BlockPlaceEvent event) {
		//Only for spawners
		if(!event.getBlock().getType().equals(Material.SPAWNER))
			return;
		
		Block block = event.getBlock();
		BlockState state = block.getState();
		CreatureSpawner spawner = (CreatureSpawner)state;
		EntityType entity = EntityType.PIG;
		
		//Get the entityType from lore
		if(event.getItemInHand().getItemMeta().getLore() != null) {
			EntityType ent = null;
			try{
				ent = EntityType.valueOf(
					event.getItemInHand().getItemMeta().getLore().get(0).substring(2));
			}catch(Exception e){}
			if(ent != null)
				entity = ent;
		}
		
		//Set the max spawner deilay from config
		spawner.setMaxSpawnDelay((int)Math.ceil(spawner.getMaxSpawnDelay() * 
				pl.getConfig().getDouble(String.format(ratePrefix, entity.toString()))));
		//Set the min spawner deilay from config
		spawner.setMinSpawnDelay((int)(spawner.getMinSpawnDelay() * 
				pl.getConfig().getDouble(String.format(ratePrefix, entity.toString()))));
		
		//Check whether this entitytype is allowed in spawners
		if(!pl.getConfig().getBoolean(String.format(allowSpawnPrefix, entity.toString()))) {
			Katura.getMessager().sendMessage(event.getPlayer(), Sounds.NO.get(), 
					Katura.getTranslator().getTranslation(
							Katura.getPlugin(Katura.class), event.getPlayer(), entityNotAllowed).replace("%type%", entity.toString()));
			entity = EntityType.PIG;
		}
		
		//Set the spawner entityType
		spawner.setSpawnedType(entity);
		spawner.update();
	}
	

	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerTranslation(pl, entityNotAllowed, 
				"Entity type %type% is not allowed in spawners!");
	}
}
