package net.karanteeni.nature.block.events;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.block.BlockCollection;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.item.ItemType;
import net.karanteeni.nature.Katura;
import net.minecraft.server.v1_14_R1.BlockPosition;

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
			
			//Check that player has the permission to use choptree
			if(!event.getPlayer().hasPermission("katura.choptree"))
				return;
			
			Player player = event.getPlayer();
			
			if(ItemType.AXE.contains(player.getInventory().getItemInMainHand().getType()))
			{
				event.setCancelled(true);
				
				//Break the tree
				if(!processTree(event.getBlock(), event.getPlayer()))
					event.setCancelled(false);
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
		ArrayList<Block> connected = new ArrayList<Block>();
		//Get all of the same logtypes from the tree
		BlockCollection blocks = BlockCollection.scanBlockTypes(block, true, false, connected);
		Material leafType = getLeafType(block.getType()); //Get tree leaf type
		boolean containsLeaves = false; //Does the tree contain leaves
		
		for(Block b : connected)
			if(b.getType() == leafType)
				containsLeaves = true;
		if(!containsLeaves) 
			return false; //Tree does not contain leaves, don't chop

		//What is the type of the sapling to be placed
		Material saplingType = getSaplingType(block.getType());		
		
		//What is the type of the sapling to be placed
		//Material saplingType = getSaplingType(block.getType());
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
	 * Gets the blocks suitable for sapling from collection
	 * @param blocks
	 * @return
	 */
	private ArrayList<Block> getSaplingBlocks(BlockCollection blocks)
	{
		int lowestLoc = 255;
		ArrayList<Block> saplings = new ArrayList<Block>();
		
		for(Block block : blocks)
		{
			if(block.getLocation().getBlockY() < lowestLoc)
			{
				//Found a new lowest level, reset the list
				lowestLoc = block.getLocation().getBlockY();
				saplings.clear();
				
				//Add checked block to blocklist
				if(BlockType.GROWABLE.contains(block.getLocation().add(0,-1,0).getBlock().getType()))
					saplings.add(block);
			}
			else if(block.getLocation().getBlockY() == lowestLoc &&
					BlockType.GROWABLE.contains(block.getLocation().add(0,-1,0).getBlock().getType()))
			{
				//This block is suitable for sapling
				saplings.add(block);
			}
		}
		
		return saplings;
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
