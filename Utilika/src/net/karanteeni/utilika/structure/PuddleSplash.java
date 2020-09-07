package net.karanteeni.utilika.structure;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import net.karanteeni.utilika.Utilika;
import net.karanteeni.utilika.external.CoreProtectAccessor;
import net.karanteeni.utilika.worldguard.WorldGuardManager;

public class PuddleSplash implements Listener {
	private float puddleChance = 0.2f;
	private Utilika plugin;
	
	public PuddleSplash(Utilika plugin) {
		this.plugin = plugin;
		if(!plugin.getConfig().isSet("puddle-chance")) {
			plugin.getConfig().set("puddle-chance", puddleChance);
			plugin.saveConfig();
		}
		puddleChance = (float)plugin.getConfig().getDouble("puddle-chance");
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onSplash(ProjectileHitEvent event) {
		ThrownPotion potion = null;
		if(!(event.getEntity() instanceof ThrownPotion))
			return;
		potion = (ThrownPotion)event.getEntity();
		if(!potion.getEffects().isEmpty())
			return;
		if(!(event.getEntity().getShooter() instanceof Player))
			return;
		
		Player thrower = (Player)event.getEntity().getShooter();

		event.getHitEntity();
		Location hitLocation = null;
		if(event.getHitBlock() != null)
			hitLocation = event.getHitBlock().getLocation();
		else
			hitLocation = event.getHitEntity().getLocation();
		if(hitLocation.getWorld().getEnvironment() == Environment.NETHER)
			return;
		
		Random r = new Random();

		for(int x = -2; x < 3; ++x)
		for(int z = -2; z < 3; ++z)
		for(int y = -2; y < 3; ++y) {
			if(r.nextDouble() > puddleChance)
				continue;

			Block relative = hitLocation.getBlock().getRelative(x, y, z);
			// is the block air
			if(relative == null || !relative.getType().isAir())
				continue;

			// is the block solid
			Block blockBelow = relative.getRelative(0, -1, 0);
			if(!blockBelow.getType().isSolid())
				continue;

			// check blocks around, we don't want the water to tower up
			if(relative.getRelative(1, 0, 0).getType() == Material.WATER ||
				relative.getRelative(0, 0, 1).getType() == Material.WATER ||
				relative.getRelative(-1, 0, 0).getType() == Material.WATER ||
				relative.getRelative(0, 0, -1).getType() == Material.WATER)
				continue;
			
			// place water
			if(canPlayerPlace(thrower, relative)) {
				placePuddle(thrower, relative);		
				break;
			}
		}
	}
	
	
	private void placePuddle(Player player, Block block) {
		block.setType(Material.WATER, false);
		Levelled levelled = (Levelled)block.getBlockData();
		levelled.setLevel(7);
		block.setBlockData(levelled, false);
		//block.getState().update(true, false);
		logBlock(player, block.getType(), block.getLocation(), block.getBlockData());
	}
	
	
	/**
	 * Does WG allow the player to place blocks here
	 * @param player
	 * @param blocks
	 */
	private boolean canPlayerPlace(Player player, Block block) {
		WorldGuardManager wgm = plugin.getWorldGuardManager();
		if(wgm != null) {
			return wgm.canBuild(player, block);
		}
		return true;
	}
	
	
	/**
	 * Logs the placed blocks to coreprotect
	 * @param player
	 * @param blocks
	 */
	private void logBlock(Player player, Material material, Location location, BlockData blockData) {
		CoreProtectAccessor accessor = plugin.getCoreProtectAccessor();
		if(accessor != null) {
			accessor.registerBlockPlacement(player, material, location, blockData);
		}
	}
}
