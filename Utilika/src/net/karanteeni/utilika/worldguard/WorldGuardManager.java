package net.karanteeni.utilika.worldguard;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
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
}
