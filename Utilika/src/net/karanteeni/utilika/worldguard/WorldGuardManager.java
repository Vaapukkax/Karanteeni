package net.karanteeni.utilika.worldguard;

import java.util.Collection;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardManager {
	public boolean canBuild(Player player, Location loc) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		
		// test bypass
		if(WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(loc.getWorld())))
			return true;
		
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BUILD);
	}
	
	
	public boolean canBuild(Player player, Block block) {
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(block.getLocation());
		World world = BukkitAdapter.adapt(block.getWorld());
		
		if (!WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(WorldGuardPlugin.inst().wrapPlayer(player), world)) {
            return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
        }
		return true;
	}
	
	
	public void filterUnbuildable(Player player, Collection<Block> blocks) {
		Iterator<Block> iter = blocks.iterator();
		
		while(iter.hasNext()) {
			Block block = iter.next();
			if(!canBuild(player, block)) {
				iter.remove();
			}
		}
	}
}
