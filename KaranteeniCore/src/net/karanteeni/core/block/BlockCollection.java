package net.karanteeni.core.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * A collection that holds a collection of blocks.
 * Can be used to scan connected blocks in 3D space and 
 * @author Nuubles
 *
 */
public class BlockCollection implements Iterable<Block>{
	private ArrayList<Block> blocks = new ArrayList<Block>();
	
	/**
	 * Create a new BlockCollection from blocks
	 * @param blocks
	 */
	public BlockCollection(Collection<Block> blocks)
	{ this.blocks = new ArrayList<Block>(blocks); }
	
	/**
	 * Creates a new empty block collection
	 */
	public BlockCollection() { }
	
	/**
	 * Removes all blocks of type from collection
	 * @param material material of blocks to remove
	 * @return removed blocks
	 */
	public BlockCollection removeBlockOfType(Material material)
	{
		//Store the removed blocks
		BlockCollection collection = new BlockCollection();
		
		//If material is not block don't even try to remove
		if(!material.isBlock())
			return collection;
		
		for(Block block : blocks)
		{
			if(block.getType().equals(material))
			{
				blocks.remove(block);
				collection.add(block);
			}
		}
		
		return collection;
	}
	
	/**
	 * Scans all the blocks of same type in 3D space
	 * @param block
	 * @param corners
	 * @param goDown Scan blocks downwards
	 * @return
	 */
	public static BlockCollection scanBlockTypes(Block block, boolean corners, boolean goDown)
	{ return scanBlockTypes(block,corners,goDown, null); }
	
	/**
	 * Scans all the blocks of same type in 3D space
	 * @param block
	 * @param corners
	 * @param goDown Scan blocks downwards
	 * @param connectedBlocks blocks that are not the same type but are connected to the structure anyways
	 * @return
	 */
	public static BlockCollection scanBlockTypes(Block block, boolean corners, boolean goDown,ArrayList<Block> connectedBlocks)
	{
		BlockCollection coll = new BlockCollection();
		
		Queue<Block> q = new LinkedList<Block>();
		q.add(block);
		
		while(!q.isEmpty())
		{
			Block b = q.remove();
			coll.add(b);
			
			//Check all blocks around the current block
			for(int x = -1; x <= 1; ++x)
			for(int y = -1; y <= 1; ++y)
			for(int z = -1; z <= 1; ++z)
			{
				if(x == 0 && y == 0 && z == 0)
					continue;
				
				Block cBlock = b.getLocation().add(x, y, z).getBlock();
				
				//If no corners are allowed, skip them
				if(!(corners || (x == 0 && y == 0 )||( x == 0 && z == 0 )||(z == 0 && y == 0)))
					continue;
				
				//Check if we can scan downwards
				if(!goDown && cBlock.getLocation().add(x, y, z).getBlockY() < block.getLocation().getBlockY())
					continue;
				
				//Blocks are same type, continue
				if(block.getType() == cBlock.getType())
					if(!coll.contains(cBlock)) //Don't add if already in collection
						q.add(cBlock);
				//Blocks are different type, add to connectedBlocks
				if(connectedBlocks != null)
					connectedBlocks.add(cBlock);
			}
		}
		
		//Return the collection of blocks
		return coll;
	}
	
	/**
	 * Check whether this collection contains a block
	 * @param block
	 * @return
	 */
	public boolean contains(Block block)
	{ return blocks.contains(block); }
	
	@Override
	public Iterator<Block> iterator() 
	{ return blocks.iterator(); }

	/**
	 * Add a block to the collection
	 * @param block
	 */
	public boolean add(Block block) 
	{ return blocks.add(block); }
	
	/**
	 * The amount of blocks in collection
	 * @return blocks in collection
	 */
	public int size() 
	{ return blocks.size(); }

	/**
	 * Block at index in collection
	 * @param index index at which the block will be taken
	 * @return Block at index
	 */
	public Block get(int index) 
	{ return blocks.get(index); }
	
	/**
	 * Remove a block from list
	 * @param index
	 * @return
	 */
	public Block remove(int index) 
	{ return blocks.remove(index); }

	/**
	 * Removes a specific block from the collection
	 * @param block
	 * @return
	 */
	public boolean remove(Block block)
	{ return blocks.remove(block); }
	
	/**
	 * Checks if this collection is empty
	 * @return Is this collection empty
	 */
	public boolean isEmpty()
	{ return blocks.isEmpty(); }
	
	/**
	 * Are the blocks on a flat plane in X axis
	 * @return
	 */
	public boolean isFlatX()
	{
		if(blocks.isEmpty())
			return true;
		int x = blocks.get(0).getLocation().getBlockX();
		
		for(Block block : blocks)
			if(x != block.getLocation().getBlockX())
				return false;
		return true;
	}
	
	/**
	 * Are the blocks on a flat plane in Y axis
	 * @return
	 */
	public boolean isFlatY()
	{
		if(blocks.isEmpty())
			return true;
		int y = blocks.get(0).getLocation().getBlockY();
		
		for(Block block : blocks)
			if(y != block.getLocation().getBlockY())
				return false;
		return true;
	}
	
	/**
	 * Are the blocks on a flat plane in Z axis
	 * @return
	 */
	public boolean isFlatZ()
	{
		if(blocks.isEmpty())
			return true;
		int z = blocks.get(0).getLocation().getBlockZ();
		
		for(Block block : blocks)
			if(z != block.getLocation().getBlockZ())
				return false;
		return true;
	}
}
