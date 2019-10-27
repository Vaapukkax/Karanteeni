package net.karanteeni.nature.entity.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.nature.Katura;
import net.karanteeni.nature.external_api.CoreProtectAccess;

public class ExplosionEffects implements Listener{

	private static String configKey = "explosion.%s.throw-blocks";
	private static String configKey2 = "explosion.%s.throw-chance";
	private static String configKey3 = "explosion-damages-falling-blocks";
	private static HashMap<UUID, FallingBlock> thrownBlocks = new HashMap<UUID, FallingBlock>();
	Katura pl;
	
	public ExplosionEffects() {
		pl = Katura.getPlugin(Katura.class);

		//Create a list of exploding entities
		ArrayList<Object> explodingTypes = new ArrayList<Object>();
		explodingTypes.add(EntityType.CREEPER);
		explodingTypes.add(EntityType.FIREBALL);
		explodingTypes.add(EntityType.WITHER);
		explodingTypes.add(EntityType.PRIMED_TNT);
		explodingTypes.add(EntityType.MINECART_TNT);
		explodingTypes.add(EntityType.WITHER_SKULL);
		explodingTypes.add(EntityType.ENDER_CRYSTAL);
		//Create a list of exploding blocks
		explodingTypes.add("BED");
		
		for(Object type : explodingTypes) {
			//Save a config option for the lightning effects
			if(!pl.getConfig().isSet(String.format(configKey, type.toString()))) {
				pl.getConfig().set(String.format(configKey, type.toString()), true);
				pl.saveConfig();
			}
			
			if(!pl.getConfig().isSet(String.format(configKey2, type.toString()))) {
				pl.getConfig().set(String.format(configKey2, type.toString()), 0.9);
				pl.saveConfig();
			}
		}
		
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey3)) {
			pl.getConfig().set(configKey3, false);
			pl.saveConfig();
		}
	}
	
	
	/**
	 * Don't allow damage to falling blocks
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void preventFlyingBlockDamage(EntityDamageEvent event) {
		if(!event.getEntity().getType().equals(EntityType.FALLING_BLOCK))
			return;
		if(!pl.getConfig().getBoolean(configKey3))
			return;
		if(!(event.getCause().equals(DamageCause.BLOCK_EXPLOSION) ||
				event.getCause().equals(DamageCause.ENTITY_EXPLOSION)))
			return;
		
		event.setCancelled(true);
	}
	
	
	/**
	 * When an explosion happens, 
	 * @param event
	 */
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBlowUp(BlockExplodeEvent event) {
		//Do we throw blocks at all?
		if(BlockType.BED.contains(event.getBlock().getType())) {
			if(!pl.getConfig().getBoolean(String.format(configKey, "BED"))) {
				return;
			}
		}
		else
			return;
		
		
		ItemStack breakerStack = new ItemStack(Material.IRON_PICKAXE);
		List<Block> blocks = new ArrayList<Block>();
		List<Block> toRemove = new ArrayList<Block>();
		Random r = new Random();
		
		//Get the chance of blocks getting thrown
		double chance = Katura.getPlugin(Katura.class).getConfig().getDouble(
				String.format(configKey2, "BED"));
		
		//Loop through all the blocks
		for(Block block : event.blockList()) {
			//If the block is a container don't throw
			if(block.getState() instanceof InventoryHolder || 
					block.getType().equals(Material.TNT))
				continue;
			
			//70% chance that the blocks will be thrown
			if(r.nextDouble() < chance) {
				List<ItemStack> drops = new ArrayList<ItemStack>(block.getDrops(breakerStack));
	
				//Is there drops
				if(!drops.isEmpty()) {
					//Check if it is a block
					if(drops.get(0).getType().isBlock()) {
						toRemove.add(block);
						block.setType(drops.get(0).getType());
						block.getState().update();
						blocks.add(block);
					}
				}
			}
		}
		
		//Remove all of these blocks from explosion and let them break
		event.blockList().removeAll(toRemove);
		
		// if coreprotect is enabled log the thrown blocks
		if(pl.isCoreProtectEnabled()) {
			CoreProtectAccess capi = pl.getCoreProtectAccess();
			
			for(Block b : blocks) {
				capi.getCoreProtect().logRemoval("#" + event.getBlock().getType().name().toLowerCase(), 
						b.getLocation(), 
						b.getType(), 
						b.getBlockData());
			}			
		}
		
		HashMap<UUID, FallingBlock> bls = Katura.getBlockManager().getBlockThrower().throwBlocks(blocks, 1);
		
		if(pl.isCoreProtectEnabled())
			thrownBlocks.putAll(bls);
	}
	
	
	/**
	 * When an explosion happens, 
	 * @param event
	 */
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityBlowUp(EntityExplodeEvent event) {
		//Do we throw blocks at all?
		if(!Katura.getPlugin(Katura.class).getConfig().getBoolean(
				String.format(configKey, event.getEntityType().toString())))
			return;
		
		ItemStack breakerStack = new ItemStack(Material.IRON_PICKAXE);
		List<Block> blocks = new ArrayList<Block>();
		List<Block> toRemove = new ArrayList<Block>();
		Random r = new Random();
		
		//Get the chance of blocks getting thrown
		double chance = Katura.getPlugin(Katura.class).getConfig().getDouble(
				String.format(configKey2, event.getEntityType().toString()));
		
		//Loop through all the blocks
		for(Block block : event.blockList()) {
			//If the block is a container don't throw (or tnt)
			if(block.getState() instanceof InventoryHolder || 
					block.getType().equals(Material.TNT))
				continue;
			
			//70% chance that the blocks will be thrown
			if(r.nextDouble() < chance) {
				List<ItemStack> drops = new ArrayList<ItemStack>(block.getDrops(breakerStack));
	
				//Is there drops
				if(!drops.isEmpty()) {
					//Check if it is a block
					if(drops.get(0).getType().isBlock()) {
						toRemove.add(block);
						block.setType(drops.get(0).getType());
						block.getState().update();
						blocks.add(block);
					}
				}
			}
		}
		
		//Remove all of these blocks from explosion and let them break
		event.blockList().removeAll(toRemove);
		
		// if coreprotect is enabled log the thrown blocks
		if(pl.isCoreProtectEnabled()) {
			CoreProtectAccess capi = pl.getCoreProtectAccess();
			
			for(Block b : blocks) {
				capi.getCoreProtect().logRemoval("#" + event.getEntityType().name().toLowerCase(), 
						b.getLocation(), 
						b.getType(), 
						b.getBlockData());
			}
		}
				
		HashMap<UUID, FallingBlock> bls = Katura.getBlockManager().getBlockThrower().throwBlocks(blocks, 1);
		
		if(pl.isCoreProtectEnabled())
			thrownBlocks.putAll(bls);
	}
	
	
	/**
	 * This event checks if a thrown block transforms into an actual block and logs it into coreprotect
	 * @param event
	 */
	@EventHandler
	public void onSolidify(EntityChangeBlockEvent event) {
		if(pl.isCoreProtectEnabled())
		if(event.getEntity() instanceof FallingBlock) {
			FallingBlock block = (FallingBlock)event.getEntity();
			
			// check if the block has fallen because of this plugin
			if(thrownBlocks.containsKey(block.getUniqueId())) {
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						pl.getCoreProtectAccess().getCoreProtect().logPlacement("#falling_block", 
								event.getBlock().getLocation(), 
								event.getBlock().getType(), 
								event.getBlockData());
					}
				};
				runnable.runTaskLater(pl, 2);
				
				thrownBlocks.remove(block.getUniqueId());
			}
		}
	}
	
	
	/**
	 * This event is checking if a falling block transforms into an item, in which case it removes the block from the map
	 * @param event
	 */
	@EventHandler
	public void onItemify(ItemSpawnEvent event) {
		if(!pl.isCoreProtectEnabled())
			return;
		
		List<Entity> ents = event.getEntity().getNearbyEntities(2, 2, 2);
		for(Entity e : ents) {
			if(thrownBlocks.containsKey(e.getUniqueId())) {
				thrownBlocks.remove(e.getUniqueId());
			}
		}
	}
}
