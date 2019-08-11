package net.karanteeni.tester;



import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public final class RegionStates {
	private static RegionContainer rg = WorldGuard.getInstance().getPlatform().getRegionContainer();
	
	/**
	 * Testaa alueella flageja
	 * @param location
	 * @param flag
	 * @return
	 */
	public static boolean testFlag(Location location, StateFlag flag)
	{
		//RegionContainer rg = WorldGuard.getInstance().getPlatform().getRegionContainer();
		ApplicableRegionSet regions = rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
		return regions.testState(null, flag);
	}
	
	/**
	 * Palauta regionit alueelta
	 * @param loc
	 * @return
	 */
	public static Set<ProtectedRegion> getRegions(Location loc)
	{
		ApplicableRegionSet regions = rg.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
		return regions.getRegions();
	}
	
	public static ProtectedRegion getRegionByName(Location loc, String regionName)
	{
		return rg.get(BukkitAdapter.adapt(loc.getWorld())).getRegion(regionName);
	}
	
	/**
	 * Onko pelaaja regionin omistaja
	 * @param region
	 * @param player
	 * @return
	 */
	public static boolean isRegionOwner(ProtectedRegion region, Player player)
	{
		return region.isOwner(WorldGuardPlugin.inst().wrapPlayer(player));
	}
	
	/**
	 * Poista regionista member
	 * @param region
	 * @param playerName
	 * @return oliko pelaajaa alunperin regionissa
	 */
	public static boolean removeRegionMember(ProtectedRegion region, UUID playerName)
	{
		DefaultDomain members = region.getMembers();
		if(members.contains(playerName))
		{
			members.removePlayer(playerName);
			region.setMembers(members);
			return true;
		}
		return false;
	}
	
	/**
	 * Lis�� pelaaja regioniin
	 * @param region
	 * @param player
	 * @return true jos pelaajaa ei aiemmin ollut regionissa
	 */
	public static boolean addRegionMember(ProtectedRegion region, UUID uuid)
	{
		DefaultDomain members = region.getMembers();
		
		if(!members.contains(uuid))
		{
			members.addPlayer(uuid);
			region.setMembers(members);
			return true;
		}
		else
			return false;
	}
}
