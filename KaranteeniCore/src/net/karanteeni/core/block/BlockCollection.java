package net.karanteeni.core.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * A collection that holds a collection of blocks.
 * Can be used to scan connected blocks in 3D space and 
 * @author Nuubles
 *
 */
public class BlockCollection implements Iterable<Block>{
	public static enum Axis {
		X,
		Y,
		Z;
		
		public static Axis fromBlockFace(BlockFace blockFace) {
			if(blockFace == BlockFace.EAST || blockFace == BlockFace.WEST)
				return X;
			else if(blockFace == BlockFace.SOUTH || blockFace == BlockFace.NORTH)
				return Z;
			else
				return Y;
		}
	}
	
	private HashSet<Block> blocks = new HashSet<Block>();
	private Location initialLocation;
	
	/**
	 * Create a new BlockCollection from blocks
	 * @param blocks
	 */
	public BlockCollection(Collection<Block> blocks)
	{ this.blocks = new HashSet<Block>(blocks); }
	
	public BlockCollection(Collection<Block> blocks, Location initialLocation) {
		this.blocks = new HashSet<Block>(blocks);
		this.initialLocation = initialLocation;
	}
	
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
				collection.add(block);
			}
		}
		
		return collection;
	}
	
	public BlockCollection removeBlockNotOfType(Material material)
	{
		//Store the removed blocks
		BlockCollection collection = new BlockCollection();
		
		//If material is not block don't even try to remove
		if(!material.isBlock())
			return collection;
		
		for(Block block : blocks)
		{
			if(!block.getType().equals(material))
			{
				collection.add(block);
			}
		}
		
		return collection;
	}
	
	public Collection<Block> getBlocks() {
		return this.blocks;
	}
	
	/**
	 * Reselects the blocks in the related to current selection
	 * @param x
	 * @param y
	 * @param z
	 */
	public void shiftSelection(int x, int y, int z) {
		HashSet<Block> newBlocks = new HashSet<Block>();
		this.initialLocation.add(x, y, z);

		for(Block block : blocks) {
			Block b = block.getRelative(x, y, z);
			if(b != null)
				newBlocks.add(b);
		}
		this.blocks = newBlocks;
	}
	
	public static BlockCollection scanBlockTypesFlat(Block block, Axis direction, int limit) {
		BlockCollection coll = new BlockCollection();
		coll.initialLocation = block.getLocation();
		
		Queue<Block> q = new LinkedList<Block>();
		q.add(block);
		coll.add(block);
		
		while(!q.isEmpty() && coll.size() < limit)
		{
			Block b = q.remove();
			
			Block[] blocks = new Block[4];
			//Check all blocks around the current block
			switch(direction) {
			case X:
				blocks[0] = b.getRelative(0, 1, 0);
				blocks[1] = b.getRelative(0, 0, 1);
				blocks[2] = b.getRelative(0, -1, 0);
				blocks[3] = b.getRelative(0, 0, -1);
				break;
			case Y:
				blocks[0] = b.getRelative(1, 0, 0);
				blocks[1] = b.getRelative(0, 0, 1);
				blocks[2] = b.getRelative(-1, 0, 0);
				blocks[3] = b.getRelative(0, 0, -1);
				break;
			case Z:
				blocks[0] = b.getRelative(0, 1, 0);
				blocks[1] = b.getRelative(1, 0, 0);
				blocks[2] = b.getRelative(0, -1, 0);
				blocks[3] = b.getRelative(-1, 0, 0);
				break;
			}
			
			for(Block foundBlock : blocks)
			if(foundBlock.getType() == block.getType() && !q.contains(foundBlock) && !coll.contains(foundBlock)) {
				if(coll.size() >= limit) break;

				coll.add(foundBlock);
				q.add(foundBlock);
			}
		}
		
		return coll;
	}
	
	
	/**
	 * If a simple bucket fill cannot reach a block in the collection, the
	 * block will be removed.
	 * @param direction
	 * @param limit
	 */
	public void scanAndFilterByUnplaceableFlatByDistance(Axis direction, int limit) {
		Block block = initialLocation.getBlock();
		
		HashSet<Block> coll = new HashSet<Block>();
		Queue<Block> q = new LinkedList<Block>();
		q.add(block);
		coll.add(block);
		
		while(!q.isEmpty())
		{
			Block b = q.remove();
			
			Block[] blocks = new Block[4];
			//Check all blocks around the current block
			switch(direction) {
			case X:
				blocks[0] = b.getRelative(0, 1, 0);
				blocks[1] = b.getRelative(0, 0, 1);
				blocks[2] = b.getRelative(0, -1, 0);
				blocks[3] = b.getRelative(0, 0, -1);
				break;
			case Y:
				blocks[0] = b.getRelative(1, 0, 0);
				blocks[1] = b.getRelative(0, 0, 1);
				blocks[2] = b.getRelative(-1, 0, 0);
				blocks[3] = b.getRelative(0, 0, -1);
				break;
			case Z:
				blocks[0] = b.getRelative(0, 1, 0);
				blocks[1] = b.getRelative(1, 0, 0);
				blocks[2] = b.getRelative(0, -1, 0);
				blocks[3] = b.getRelative(-1, 0, 0);
				break;
			}
			
			for(Block foundBlock : blocks)
			if(BlockType.REPLACEABLE.contains(foundBlock.getType()) && !q.contains(foundBlock) && !coll.contains(foundBlock)) {
				if(foundBlock.getLocation().distance(block.getLocation()) > limit) continue;

				coll.add(foundBlock);
				q.add(foundBlock);
			}
		}
		
		this.blocks.retainAll(coll);
	}
	
	
	public static BlockCollection scanBlockTypesFlatByDistance(Block block, Axis direction, int limit) {
		BlockCollection coll = new BlockCollection();
		coll.initialLocation = block.getLocation();
		Queue<Block> q = new LinkedList<Block>();
		q.add(block);
		coll.add(block);
		
		while(!q.isEmpty())
		{
			Block b = q.remove();
			
			Block[] blocks = new Block[4];
			//Check all blocks around the current block
			switch(direction) {
			case X:
				blocks[0] = b.getRelative(0, 1, 0);
				blocks[1] = b.getRelative(0, 0, 1);
				blocks[2] = b.getRelative(0, -1, 0);
				blocks[3] = b.getRelative(0, 0, -1);
				break;
			case Y:
				blocks[0] = b.getRelative(1, 0, 0);
				blocks[1] = b.getRelative(0, 0, 1);
				blocks[2] = b.getRelative(-1, 0, 0);
				blocks[3] = b.getRelative(0, 0, -1);
				break;
			case Z:
				blocks[0] = b.getRelative(0, 1, 0);
				blocks[1] = b.getRelative(1, 0, 0);
				blocks[2] = b.getRelative(0, -1, 0);
				blocks[3] = b.getRelative(-1, 0, 0);
				break;
			}
			
			for(Block foundBlock : blocks)
			if(foundBlock.getType() == block.getType() && !q.contains(foundBlock) && !coll.contains(foundBlock)) {
				// if the block is too far away don't add it
				if(foundBlock.getLocation().distance(block.getLocation()) > limit) continue;
				
				coll.add(foundBlock);
				q.add(foundBlock);
			}
		}
		
		return coll;
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
	public static BlockCollection scanBlockTypes(Block block, boolean corners, boolean goDown, ArrayList<Block> connectedBlocks)
	{
		BlockCollection coll = new BlockCollection();
		coll.initialLocation = block.getLocation();
		Queue<Block> q = new LinkedList<Block>();
		q.add(block);
		
		while(!q.isEmpty())
		{
			Block b = q.remove();
			
			coll.add(b);
			
			//Check all blocks around the current block
			for(int x = -1; x <= 1; ++x)
			for(int y = goDown?-1:0; y <= 1; ++y)
			for(int z = -1; z <= 1; ++z)
			{
				if(x == 0 && y == 0 && z == 0)
					continue;
				
				Block cBlock = b.getLocation().add(x, y, z).getBlock();
				
				//If no corners are allowed, skip them
				if(!corners && x != 0 && y != 0 && z != 0)
					continue;
				
				//Blocks are same type, continue
				if(block.getType() == cBlock.getType())
				{
					if(!(coll.contains(cBlock) || q.contains(cBlock))) //Don't add if already in collection
						q.add(cBlock);
				}
				//Blocks are different type, add to connectedBlocks
				else if(connectedBlocks != null)
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
	/*public Block get(int index) 
	{ return blocks.get(index); }*/
	
	/**
	 * Remove a block from list
	 * @param index
	 * @return
	 */
	/*public Block remove(int index) 
	{ return blocks.remove(index); }*/

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
		int x = -1;
		
		for(Block block : blocks)
		{
			if(x == -1)
				x = block.getLocation().getBlockX();
			if(x != block.getLocation().getBlockX())
				return false;
		}
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
		int y = -1;
		
		for(Block block : blocks)
		{
			if(y == -1)
				y = block.getLocation().getBlockY();
			if(y != block.getLocation().getBlockY())
				return false;
		}
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
		int z = -1;
		
		for(Block block : blocks)
		{
			if(z == -1)
				z = block.getLocation().getBlockZ();
			if(z != block.getLocation().getBlockZ())
				return false;
		}
		return true;
	}
}
