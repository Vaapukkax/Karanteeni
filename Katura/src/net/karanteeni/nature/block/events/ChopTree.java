package net.karanteeni.nature.block.events;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.karanteeni.core.block.BlockType;
import net.karanteeni.nature.Katura;
import net.minecraft.server.v1_13_R2.BlockPosition;

public class ChopTree implements Listener{
	
	private static String NATURAL = "nat";
	private static String configKey = "chop-trees";
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	public ChopTree()
	{
		pl = Katura.getPlugin(Katura.class);
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey))
		{
			pl.getConfig().set(configKey, true);
			pl.saveConfig();
		}
	}
	
	/**
	 * Set a metadata to placed block
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	private void blockPlace(BlockPlaceEvent event)
	{
		if(BlockType.LOG.contains(event.getBlock().getType()))
			event.getBlock().setMetadata(NATURAL, new FixedMetadataValue(Katura.getPlugin(Katura.class), true));
	}
	
	/**
	 * Break all the blocks in a tree
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	private void blockBreak(BlockBreakEvent event)
	{
		//The block is log and player is not sneaking
		if(!event.getPlayer().isSneaking() &&
			BlockType.LOG.contains(event.getBlock().getType()) &&
			event.getBlock().getMetadata(NATURAL).isEmpty() &&
			!Katura.getBlockManager().isBeingProcessed(event.getBlock()))
		{
			if(!pl.getConfig().getBoolean(configKey))
				return;
			
			
			Player player = event.getPlayer();
			
			if(player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_AXE ||
				player.getInventory().getItemInMainHand().getType() == Material.IRON_AXE ||
				player.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE ||
				player.getInventory().getItemInMainHand().getType() == Material.STONE_AXE ||
				player.getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE)
			{
				event.setCancelled(true);
				
				//Break the tree
				if(!processTree(event.getBlock(), event.getPlayer()))
				{
					event.setCancelled(false);
				}
			}
		}
		else if(BlockType.LOG.contains(event.getBlock().getType()))
		{
			event.getBlock().removeMetadata(NATURAL, Katura.getPlugin(Katura.class));
		}
	}
	
	
	/**
	 * Breaks the tree with this block
	 * @param block
	 * @param player
	 * @return
	 */
	public boolean processTree(Block block, Player player)
	{
		ArrayList<Block> blocks = getTreeBlocks(block, null);
		Material leafType = getLeafType(block.getType());
		
		//Check if this tree contains leaves
		if(!containsLeaves(blocks, leafType))
			return false;
		
		//What is the type of the sapling to be placed
		Material saplingType = getSaplingType(block.getType());
		ArrayList<Block> futureSaplings = getSaplingBlocks(blocks);
		
		//Timer to break all the blocks
		BukkitRunnable blockBreaker = new BukkitRunnable() {
			@Override
			public void run() {
				if(!blocks.isEmpty())
				{
					//Make player break the blocks
					for(Block b_ : blocks)
					{
						Katura.getBlockManager().setBlockToProcess(Katura.getPlugin(Katura.class), b_);
						
						((CraftPlayer) player).getHandle().playerInteractManager.breakBlock(
								new BlockPosition(b_.getLocation().getBlockX(), b_.getLocation().getBlockY(), b_.getLocation().getBlockZ()));
						Katura.getBlockManager().removeProcessingStatus(Katura.getPlugin(Katura.class), b_);
						b_.removeMetadata(NATURAL, Katura.getPlugin(Katura.class));
					}
					cancel();
				}
				else
					cancel();
			}
		};
		
		//Break the blocks of the tree
		blockBreaker.runTask(Katura.getPlugin(Katura.class));
		//blockBreaker.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
		
		//Plant all saplings
		BukkitRunnable saplingPlanter = new BukkitRunnable() {
			@Override
			public void run() {
				//plant the saplings
				for(int i = 0; i < futureSaplings.size(); i++)
				{
					if(futureSaplings.get(i).getType().equals(Material.AIR) || futureSaplings.get(i).getType().equals(Material.CAVE_AIR))
					{
						futureSaplings.get(i).setType(saplingType);
						futureSaplings.get(i).getState().update();
					}
				}
				
				this.cancel();
			}
		};
		
		//Plant the saplings
		saplingPlanter.runTaskLater(Katura.getPlugin(Katura.class), 5);
		
		return true;
	}
	
	/**
	 * Tarkistaa onko puussa lehti� kiinni jotta sen voi hakata
	 * @param blocks
	 * @return
	 */
	private boolean containsLeaves(ArrayList<Block> blocks, Material leafType)
	{
		for(int i = blocks.size()-1; i > 0; i--)
		{
			if(blocks.get(i).getLocation().add(1,0,0).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(-1,0,0).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(0,1,0).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(0,0,1).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(0,0,-1).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(1,1,0).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(-1,1,0).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(0,1,1).getBlock().getType().equals(leafType) ||
					blocks.get(i).getLocation().add(0,1,-1).getBlock().getType().equals(leafType))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Hakee saplingiksi sopivat blockit jonosta
	 * @param blocks
	 * @return
	 */
	private ArrayList<Block> getSaplingBlocks(ArrayList<Block> blocks)
	{
		int lowestLoc = 255;
		ArrayList<Block> saplings = new ArrayList<Block>();
		
		for(int i = 0; i < blocks.size(); i++)
		{
			if(blocks.get(i).getLocation().getBlockY() < lowestLoc)
			{
				//L�ydettiin uusi pohjataso, tyhjenent��n lista ja asetetaan matalin taso uudelleen
				lowestLoc = blocks.get(i).getLocation().getBlockY();
				saplings.clear();
				
				//Lis�� tarkastettu block saplinglistaan
				if(BlockType.GROWABLE.contains(blocks.get(i).getLocation().add(0,-1,0).getBlock().getType()))
					saplings.add(blocks.get(i));
			}
			else if(blocks.get(i).getLocation().getBlockY() == lowestLoc &&
					BlockType.GROWABLE.contains(blocks.get(i).getLocation().add(0,-1,0).getBlock().getType()))
			{
				//T�m� block on sopiva saplingiksi
				saplings.add(blocks.get(i));
			}
		}
		
		return saplings;
	}
	
	
	//ArrayList<Block> blocks = new ArrayList<Block>();
	/**
	 * Hakee kaikki annetun blockin kaltaiset blockit
	 * @param b
	 * @param blocks t�ytyy olla NULL
	 * @return
	 */
	private ArrayList<Block> getTreeBlocks(Block b, ArrayList<Block> blocks)
	{
		ArrayList<Location> locs = new ArrayList<Location>();
		//ArrayList<Block> blocks = new ArrayList<Block>();
		//Jos blocksia ei ole niin luodaan uusi
		if(blocks == null)
			blocks = new ArrayList<Block>();
		
		//Lis�t��n nykyinen block listaan jo valmiiksi
		if(!blocks.contains(b))
			blocks.add(b);
		
		//Looppaa sivut
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()+1, b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()-1, b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()+1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()-1));
		
		//Looppaa ymp�rilt� kulmat
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()+1, b.getLocation().getBlockY(), b.getLocation().getBlockZ()+1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()-1, b.getLocation().getBlockY(), b.getLocation().getBlockZ()-1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()+1, b.getLocation().getBlockY(), b.getLocation().getBlockZ()-1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()-1, b.getLocation().getBlockY(), b.getLocation().getBlockZ()+1));
		
		//Looppaa ylh��lt�
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()));
		
		//Looppaa ylh��lt� rinki
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()+1, b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()-1, b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()+1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()-1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()+1, b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()+1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()-1, b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()-1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()+1, b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()-1));
		locs.add(new Location(b.getWorld(), b.getLocation().getBlockX()-1, b.getLocation().getBlockY()+1, b.getLocation().getBlockZ()+1));
		
		if(blocks.size() > 1024)
			return blocks;
		
		//K�yd��n kaikki sijainnit l�pi
		for(Location loc : locs)
		{
			//Lis�t��n sijainnissa oleva block jos on oikeaa tyyppi�, sek� 
			if(loc.getBlock().getType().equals(b.getType()) && !blocks.contains(loc.getBlock()))
			{
				ArrayList<Block> bls = getTreeBlocks(loc.getBlock(), blocks);
				
				//K�yd��n l�pi ja tarkistetaan ettei t�t� ole viel� t�ss�
				for(int i = 0; i < bls.size(); i++)
				{
					if(!blocks.contains(bls.get(0)))
						blocks.add(bls.get(i));
					else
						break;
				}
			}
		}
		
		return blocks;
	}
	
	/**
	 * Ottaa saplingin tyypin hakatusta logista
	 * @param material
	 * @return
	 */
	private Material getSaplingType(Material material)
	{
		if(material.equals(Material.OAK_LOG))
			return Material.OAK_SAPLING;
		else if(material.equals(Material.SPRUCE_LOG))
			return Material.SPRUCE_SAPLING;
		else if(material.equals(Material.DARK_OAK_LOG))
			return Material.DARK_OAK_SAPLING;
		else if(material.equals(Material.BIRCH_LOG))
			return Material.BIRCH_SAPLING;
		else if(material.equals(Material.ACACIA_LOG))
			return Material.ACACIA_SAPLING;
		else
			return Material.JUNGLE_SAPLING;
	}
	
	/**
	 * Palauttaa logista lehtityypin joka pit�� olla hakattavan puun l�hell�
	 * @param material
	 * @return
	 */
	private Material getLeafType(Material material)
	{
		if(material.equals(Material.OAK_LOG))
			return Material.OAK_LEAVES;
		else if(material.equals(Material.SPRUCE_LOG))
			return Material.SPRUCE_LEAVES;
		else if(material.equals(Material.DARK_OAK_LOG))
			return Material.DARK_OAK_LEAVES;
		else if(material.equals(Material.BIRCH_LOG))
			return Material.BIRCH_LEAVES;
		else if(material.equals(Material.ACACIA_LOG))
			return Material.ACACIA_LEAVES;
		else
			return Material.JUNGLE_LEAVES;
	}
}
