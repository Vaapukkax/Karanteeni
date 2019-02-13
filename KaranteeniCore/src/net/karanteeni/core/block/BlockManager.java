package net.karanteeni.core.block;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class BlockManager {

	private static String PROCESSING = "PRC";
	private final BlockType blockTypes = new BlockType();
	private final BlockThrower blockThrower = new BlockThrower();
	private final BlockEffects blockEffects = new BlockEffects();
	
	/**
	 * Returns the blockTypes from the main plugin
	 * @return
	 */
	public BlockType getBlockTypes()
	{
		return blockTypes;
	}
	
	/**
	 * Returns the blockthrower
	 * @return
	 */
	public BlockThrower getBlockThrower()
	{
		return blockThrower;
	}
	
	/**
	 * Returns the block effects manager
	 * @return
	 */
	public BlockEffects getBlockEffects()
	{
		return blockEffects;
	}
	
	/**
	 * Add metadata to block to indicate that a plugin is processing this block
	 * @param plugin
	 * @param block
	 * @return
	 */
	public Block setBlockToProcess(Plugin plugin, Block block)
	{
		block.setMetadata(PROCESSING, new FixedMetadataValue(plugin, true));
		
		return block;
	}
	
	/**
	 * Add metadata to block to indicate that a plugin is processing this block
	 * @param plugin
	 * @param blocks
	 * @return the blocks which were indicated to being processed
	 */
	public List<Block> setBlockToProcess(Plugin plugin, List<Block> blocks)
	{
		FixedMetadataValue meta = new FixedMetadataValue(plugin, true);
		
		for(Block block : blocks)
			block.setMetadata(PROCESSING, meta);
		
		return blocks;
	}
	
	/**
	 * Removes the metadata from a block which indicates that it is being processed
	 * @param plugin
	 * @param block
	 */
	public void removeProcessingStatus(Plugin plugin, Block block)
	{
		block.removeMetadata(PROCESSING, plugin);
	}
	
	/**
	 * Removes the metadata from blocks which indicates that it is being processed
	 * @param plugin
	 * @param blocks
	 */
	public void removeProcessingStatus(Plugin plugin, List<Block> blocks)
	{
		for(Block block : blocks)
			block.removeMetadata(PROCESSING, plugin);
	}
	
	/**
	 * Returns whether this block is being processed
	 * @param block
	 * @return
	 */
	public boolean isBeingProcessed(Block block)
	{
		return block.hasMetadata(PROCESSING);
	}
	
	/**
	 * Removes the blocks from a list which are already being processed
	 * @param blocks
	 * @return
	 */
	public List<Block> removeBlocksAlreadyProcessing(List<Block> blocks)
	{
		for(Block block : blocks)
			if(block.hasMetadata(PROCESSING))
				blocks.remove(block);
		
		return blocks;
	}
}
