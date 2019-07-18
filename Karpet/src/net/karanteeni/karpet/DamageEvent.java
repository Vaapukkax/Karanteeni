package net.karanteeni.karpet;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerQuitEvent;

public class DamageEvent implements Listener {
	private Karpet plugin;
	
	public DamageEvent(Karpet karpet) {
		this.plugin = karpet;
	}
	
	/**
	 * Prevent players from breaking carpet blocks
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void blockBreak(BlockBreakEvent event) {
		if(Carpet.partOfCarpet(event.getBlock()))
			event.setCancelled(true);
		else if(event.getBlock().getBlockData() instanceof Bisected) {
			// prevent half blocks breaking
			Bisected bi = (Bisected)(event.getBlock().getBlockData());
			if(bi.getHalf() == Half.BOTTOM && Carpet.partOfCarpet(event.getBlock().getLocation().add(0, 1, 0).getBlock())) {
				event.setCancelled(true);
			} else if(Carpet.partOfCarpet(event.getBlock().getLocation().subtract(0, 1, 0).getBlock())) {
				event.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerLeave(PlayerQuitEvent event) {
		plugin.getCarpetHandler().removeCarpet(event.getPlayer());
	}
	
	
	/*@EventHandler(priority = EventPriority.LOW)
	private void blockIgnite(BlockIgniteEvent event) {
		//Bukkit.broadcastMessage(event.getIgnitingBlock());
		if(Carpet.partOfCarpet(event.getIgnitingBlock()) || Carpet.partOfCarpet(event.getBlock()))
			event.setCancelled(true);
	}*/
	
	
	/*@EventHandler(priority = EventPriority.LOW)
	private void playerInteract(PlayerInteractEvent event) {
		if(Carpet.partOfCarpet(event.getClickedBlock()) && event.getMaterial() == Material.FLINT_AND_STEEL) {
			Bukkit.broadcastMessage(event.getAction().name());
			event.setCancelled(true);
		}
	}*/
	
	
	@EventHandler(priority = EventPriority.LOW)
	private void blockSpreadEvent(BlockSpreadEvent event) {
		if(Carpet.partOfCarpet(event.getBlock()) || Carpet.partOfCarpet(event.getSource()))
			event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	private void onLeavesDecay(LeavesDecayEvent event) {
		if(Carpet.partOfCarpet(event.getBlock()))
			event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	private void blockBurn(BlockBurnEvent event) {
		if(Carpet.partOfCarpet(event.getBlock()))
			event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	private void blockFadce(BlockFadeEvent event) {
		if(Carpet.partOfCarpet(event.getBlock()))
			event.setCancelled(true);
	}
	
	
	/*@EventHandler(priority = EventPriority.LOW)
	private void explosionPrime(EntityBlockFormEvent event) {
		if(Carpet.partOfCarpet(event.getBlock()))
			event.setCancelled(true);
	}*/
	
	/**
	 * Prevent carpet from breaking from physics events
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockPhysicsEvent(BlockPhysicsEvent event) {
		if(Carpet.partOfCarpet(event.getBlock())) {
			event.setCancelled(true);
		}
	}
	
	
	/**
	 * Prevent piston from moving carpet blocks
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	private void onPistonRetract(BlockPistonExtendEvent event) {
		for(Block b : event.getBlocks())
		if(Carpet.partOfCarpet(b))
		event.setCancelled(true);
	}
	
	
	/**
	 * Prevent piston from moving carpet blocks
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	private void onPistonExtend(BlockPistonRetractEvent event) {
		for(Block b : event.getBlocks())
		if(Carpet.partOfCarpet(b))
		event.setCancelled(true);
	}
	
	
	/**
	 * Don't allow carpet blocks to explode
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	private void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> blocks = event.blockList().iterator();
		
		// loop all the blocks in the explosion and remove those part of a carpet
		while(blocks.hasNext()) {
			if(Carpet.partOfCarpet(blocks.next()))
				blocks.remove();
		}
	}
	
	
	/**
	 * Don't allow carpet blocks to damage entities
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if(event.getCause() == DamageCause.SUFFOCATION) {
			// prevent entities from suffocating
			if(event.getEntity() instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity)event.getEntity();
				
				if(Carpet.partOfCarpet(entity.getEyeLocation().getBlock()))
					event.setCancelled(true);
			}
		} else if(event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player) {
			// preven player fall damage
			if(plugin.getCarpetHandler().hasCarpet((Player)event.getEntity()))
				event.setCancelled(true);
		}
	}
	
	
	/**
	 * Prevent paintings and itemframes from destrying when carpet is on top of them
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onHangingBreak(HangingBreakEvent event) {
		if(event.getCause() == RemoveCause.OBSTRUCTION || event.getCause() == RemoveCause.PHYSICS)
		for(double x = event.getEntity().getBoundingBox().getMinX(); x < event.getEntity().getBoundingBox().getMaxX(); ++x)
		for(double y = event.getEntity().getBoundingBox().getMinY(); y < event.getEntity().getBoundingBox().getMaxY(); ++y)
		for(double z = event.getEntity().getBoundingBox().getMinZ(); z < event.getEntity().getBoundingBox().getMaxZ(); ++z)
		if(Carpet.partOfCarpet((new Location(event.getEntity().getWorld(), x, y, z)).getBlock())) {
			event.setCancelled(true);
			return;
		}
	}
}
