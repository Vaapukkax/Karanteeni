package net.karanteeni.tester;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.block.BlockType;

public class Protectables {
	
	/**
	 * Onko block suojattava blocktyyppi?
	 * @param type
	 * @return
	 */
	public static boolean isProtectable(Material type)
	{
		KaranteeniCore.getBlockManager().getBlockTypes();
		if(type == Material.CHEST ||
			type == Material.TRAPPED_CHEST ||
			type == Material.FURNACE ||
			type == Material.DISPENSER ||
			type == Material.DROPPER ||
			type == Material.HOPPER ||
			type == Material.WHITE_SHULKER_BOX ||
			type == Material.SHULKER_BOX ||
			type == Material.ORANGE_SHULKER_BOX ||
			type == Material.MAGENTA_SHULKER_BOX ||
			type == Material.LIGHT_BLUE_SHULKER_BOX ||
			type == Material.YELLOW_SHULKER_BOX ||
			type == Material.LIME_SHULKER_BOX ||
			type == Material.PINK_SHULKER_BOX ||
			type == Material.GRAY_SHULKER_BOX ||
			type == Material.LIGHT_GRAY_SHULKER_BOX ||
			type == Material.CYAN_SHULKER_BOX ||
			type == Material.PURPLE_SHULKER_BOX ||
			type == Material.BLUE_SHULKER_BOX ||
			type == Material.BROWN_SHULKER_BOX ||
			type == Material.GREEN_SHULKER_BOX ||
			type == Material.RED_SHULKER_BOX ||
			type == Material.BLACK_SHULKER_BOX ||
			BlockType.SIGNS.contains(type) ||
			BlockType.TRAPDOOR.contains(type) ||
			BlockType.WALL_SIGNS.contains(type) ||
			BlockType.FENCE_GATE.contains(type) ||
			type == Material.ACACIA_FENCE_GATE ||
			type == Material.BIRCH_FENCE_GATE ||
			type == Material.DARK_OAK_FENCE_GATE ||
			type == Material.JUNGLE_FENCE_GATE ||
			type == Material.SPRUCE_FENCE_GATE)
			return true;
		return false;
	}
	
	/**
	 * Voiko pelaaja avata ja sulkea blockin, muttei omista sit�
	 * @param location
	 * @param player
	 * @return
	 */
	public static boolean canInteractBlock(Location location, Player player)
	{
		if((TesterMain.getDb().isMember(player.getUniqueId().toString(),
				location.getBlockX(), 
				location.getBlockY(), 
				location.getBlockZ(),
				player.getWorld().getName()) || 
				TesterMain.getDb().isOpenForAll(
					location.getBlockX(), 
					location.getBlockY(), 
					location.getBlockZ(),
					player.getWorld().getName())))
			return true;
		else
			return canAlterBlock(location, player);
	}
	
	/**
	 * Omistaako pelaaja blockin
	 * @param location
	 * @param player
	 * @return
	 */
	public static boolean canAlterBlock(Location location, Player player)
	{
		return (TesterMain.getDb().isOwner(player.getUniqueId().toString(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()));
	}
	
	/**
	 * Lis�� suojaus sijainnin blockiin
	 * @param location
	 * @param player
	 * @return
	 */
	public static boolean addProtection(Location location, Player player)
	{
		return TesterMain.getDb().addProtection(player.getUniqueId().toString(), 
				player.getName().toString(),
				location.getBlockX(), 
				location.getBlockY(), 
				location.getBlockZ(),
				location.getWorld().getName());
	}
	
	/**
	 * Lis�� pelaajalle oikeudet suojaukseen
	 * @param location
	 * @param player
	 * @return true jos lis�ys onnistui
	 */
	public static boolean addMember(Location location, Player player)
	{
		return (TesterMain.getDb().addMember(player.getUniqueId().toString(), 
				player.getUniqueId().toString(), 
				player.getName().toString(), 
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ(),
				location.getWorld().getName()));
	}
	
	/**
	 * Poista member palikan suojauksesta
	 * @param loc palikan sijainti
	 * @param owner palikan omistaja
	 * @param member palikan poistettava member
	 * @return onnistuiko poisto
	 */
	public static boolean removeMember(Location loc, Player owner, Player member)
	{
		return TesterMain.getDb().deleteMember(owner.getUniqueId().toString(), 
				member.getUniqueId().toString(), 
				/*member.getName(),*/
				loc.getBlockX(), 
				loc.getBlockY(), 
				loc.getBlockZ(), 
				loc.getWorld().getName());
	}
	
	/**
	 * Ota blockin owner
	 * @param block
	 * @return
	 */
	public static String getBlockOwner(Block block)
	{
		return TesterMain.getDb().getOwner(
				block.getLocation().getBlockX(), 
				block.getLocation().getBlockY(), 
				block.getLocation().getBlockZ(),
				block.getWorld().getName());
	}
	
	/**
	 * Poista blockin suojaus
	 * @param b
	 * @return
	 */
	public static boolean removeProtection(Block b)
	{
		if (TesterMain.getDb().removeProtection(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(),b.getLocation().getWorld().getName()))
		{
			if(!(TesterMain.getDb().deleteAllMembers(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(),b.getLocation().getWorld().getName())))
			{
				Bukkit.broadcastMessage("Virhe suojauksen poistossa!");
				return false;
			}
		}
		else
		{
			Bukkit.broadcastMessage("Virhe suojauksen poistossa!");
			return false;
		}
		
		return true;
	}
}
