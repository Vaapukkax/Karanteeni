package net.karanteeni.karpet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.metadata.FixedMetadataValue;

public class Carpet {
	private LinkedHashMap<Location, BlockState> replacedBlocks = new LinkedHashMap<Location, BlockState>();
	private boolean tools = false;
	private boolean lights = false;
	private Material[][] carpetBlocks = new Material[5][5];
	private Material[][] toolBlocks = new Material[5][5];
	private static String CARPET_DATA = "Karpet";
	private static Karpet plugin;
	private static ArrayList<Material> allowClip;
	private double ID = Math.random();
	
	/**
	 * Initialize this class with blocks which can be used to clip through blocks
	 * @param clippable block the carpet can clip through. null or empty array to allow all
	 */
	public static void initialize(Karpet plugin, Collection<Material> clippable) {
		if(clippable == null || clippable.isEmpty())
			Carpet.allowClip = null;
		else
			Carpet.allowClip = new ArrayList<Material>(clippable);
		Carpet.plugin = plugin;
	}
	
	
	/**
	 * Creates a new carpet with given material type
	 * @param type type of carpet to use
	 */
	public Carpet(Material type) {
		for(int i = 0; i < 5; ++i)
			for(int l = 0; l < 5; ++l)
				carpetBlocks[i][l] = type;
	}
	
	
	public Carpet(Material[][] type) throws IllegalArgumentException {
		if(type.length != 5) {
			throw new IllegalArgumentException("Carpet width must be 5 blocks!");
		} else {
			for(int i = 0; i < carpetBlocks.length; ++i)
				if(type[i].length != 5)
					throw new IllegalArgumentException("Carpet width must be 5 blocks!");
		}
		
		carpetBlocks = type;
	}
	
	
	/**
	 * Checks if the given block is a part of the magic carpet used
	 * @param b block to check if it is a part of the carpet
	 * @return true if part of carpet, false if not
	 */
	public static boolean partOfCarpet(Block b) {
		if(b == null)
			return false;
		return b.hasMetadata(CARPET_DATA);
	}
	
	
	/**
	 * Checks if the block is a part of this given carpet
	 * @param b block to check
	 * @return true if part of this carpet, false otherwise
	 */
	public boolean partOfThisCarpet(Block b) {
		if(b == null)
			return false;
		return replacedBlocks.containsKey(b.getLocation()) && replacedBlocks.get(b.getLocation()) != null;
	}
	
	
	/**
	 * Returns the blocktype of the carpet
	 * @return material which the carpet uses
	 */
	public Material[][] getBlockTypes() {
		return this.carpetBlocks;
	}
	
	
	/**
	 * Returns the blocktype of the carpet at given location
	 * @param i index x
	 * @param l index z
	 * @return material which the carpet uses here
	 */
	public Material getBlockType(int i, int l) {
		return this.carpetBlocks[i][l];
	}
	
	
	/**
	 * Set the tools off or on
	 * @param state true to set the tools on, false to turn them off
	 */
	public void setTools(boolean state) {
		this.tools = state;
	}
	
	
	/**
	 * Get if the tools are on or off
	 * @return true if tools are on, false if off
	 */
	public boolean getToolState() {
		return this.tools;
	}
	
	
	/**
	 * Set lights either on or off
	 * @param state true to enable lights, false to disable
	 */
	public void setLights(boolean state) {
		this.lights = state;
	}
	
	
	/**
	 * Get if lights are on or off
	 * @return true if lights are on, false if off
	 */
	public boolean getLigts() {
		return this.lights;
	}
	
	
	/**
	 * Remove this carpet
	 */
	public void remove() {
		Iterator<Entry<Location, BlockState>> it = replacedBlocks.entrySet().iterator();
		
		// loop all blocks and remove all carpet blocks 
		while(it.hasNext()) {
			Entry<Location, BlockState> blockState = it.next();
			
			if(blockState.getValue() != null)
				blockState.getValue().update(true, false);
			blockState.getValue().getBlock().removeMetadata(CARPET_DATA, plugin);
		}
		
		// clear the blocks from the carpet
		replacedBlocks.clear();
	}
	
	
	/**
	 * Change the material of the block to something else
	 * @param material material to set to
	 * @param true if given material was of type block, otherwise false
	 */
	public boolean setMaterial(Material material) {
		if(!material.isBlock())
			return false;
		
		for(Location loc : replacedBlocks.keySet()) {
			loc.getBlock().setType(material, false);
			loc.getBlock().getState().update(true, false);
		}
		
		for(int i = 0; i < carpetBlocks.length; ++i)
			for(int l = 0; l < carpetBlocks.length; ++l)
				carpetBlocks[i][l] = material;
		return true;
	}
	
	
	/**
	 * Sets the layout to be used in the carpet
	 * @param layout layout
	 */
	public void setLayout(Material[][] layout) {
		this.carpetBlocks = layout;
	}
	
	
	/**
	 * Sets the layout to be used for the tool blocks
	 * @param layout
	 */
	public void setToolLayout(Material[][] layout) {
		this.toolBlocks = layout;
	}
	
	
	/**
	 * Sets a new style for the carpet
	 * @param material
	 * @return
	 */
	public boolean setMaterial(Material[][] material) throws IllegalArgumentException {
		if(material.length != 5)
			throw new IllegalArgumentException("Carpet width must be 5 blocks!");
		
		for(int i = 0; i < material.length; ++i)
		if(material[i].length != 5)
			throw new IllegalArgumentException("Carpet width must be 5 blocks!");
		
		// check that the carpet is of type block
		for(int i = 0; i < carpetBlocks.length; ++i)
			for(int l = 0; l < carpetBlocks.length; ++l)
				if(!material[i][l].isBlock())
					return false;
		
		
		// replace all carpet blocks
		for(Location loc : replacedBlocks.keySet()) {
			//loc.getBlock().setType(Material.glas, false);
			// set all carpet blocks back to normal
			loc.getBlock().getState().update(true, false);
		}
		
		for(int i = 0; i < carpetBlocks.length; ++i) {
			for(int l = 0; l < carpetBlocks.length; ++l) {
				carpetBlocks[i][l] = material[i][l];
			}
		}
		
		return true;
	}
	
	
	/**
	 * Draws the carpet to given location
	 * @param location location to draw the carpet to
	 */
	public void draw(Location location) {
		ArrayList<Location> drawnBlocks = new ArrayList<Location>();
		
		// loop all blocks in the carpet
		for(int x = -carpetBlocks.length/2; x <= carpetBlocks.length/2; ++x) {
			for(int z = -carpetBlocks.length/2; z <= carpetBlocks.length/2; ++z) {
				// the block to be drawn is not solid, don't draw if
				if(toolBlocks[x+2][z+2] == null && !carpetBlocks[x+2][z+2].isSolid())
					continue;
				
				// location of the block to be drawn
				Location blockLoc = new Location(location.getWorld(), location.getBlockX()+x, location.getBlockY(), location.getBlockZ()+z);
				// get the block at the location
				Block originalBlock = blockLoc.getBlock();
				
				// the block will be drawn here at this location always from here on
				drawnBlocks.add(blockLoc);
				
				// check if this block type can be clipped through
				if(!Carpet.partOfCarpet(originalBlock) && (allowClip != null && !allowClip.contains(originalBlock.getType())))
					continue;
				
				// the given block at location is replaced
				boolean blockIsReplaced = replacedBlocks.containsKey(blockLoc);
				BlockState blockReplaced = replacedBlocks.get(blockLoc);
				
				// check if block is already replaced and is of correct type (either part of carpet layer or tool layer
				if(blockIsReplaced && 
						(blockReplaced.getType() == carpetBlocks[x+2][z+2] || // carpet type 
						(toolBlocks != null && blockReplaced.getType() == toolBlocks[x+2][z+2]))) // tool type
					continue; // block is correct type, don't touch
				
				// here block is wrong type, set the new type of the block and draw it
				// replace only if the block belongs to THIS carpet
				if(!blockIsReplaced && 
						(!Carpet.partOfCarpet(originalBlock) || 
								originalBlock.getMetadata(CARPET_DATA).get(0).asDouble() == ID)) // don't add to the map if it already exists there
					replacedBlocks.put(blockLoc, originalBlock.getState());
				
				BlockState state = originalBlock.getState();
				
				// if block is a container clear to prevent item drops
				if(originalBlock.getState() instanceof Container) {
					Container container = (Container)originalBlock.getState();
					container.getInventory().clear();
				}
				
				// if there is a tool at this location, set the tool and else set regular carpet block
				if(toolBlocks[x+2][z+2] != null) {
					state.setType(toolBlocks[x+2][z+2]);
					//originalBlock.setType(toolBlocks[x+2][z+2], false); // set the block type and don't update neighboring blocks
				} else {
					state.setType(carpetBlocks[x+2][z+2]);
					//originalBlock.setType(carpetBlocks[x+2][z+2], false); // set the block type and don't update neighboring blocks
				}
				// update the block so that the player can see it
				state.update(true, false);
				// set metadata to indicate that this block is a part of this carpet
				originalBlock.setMetadata(CARPET_DATA, new FixedMetadataValue(plugin, ID));
			}
		}
		
		// use iterator to prevent exceptions
		Iterator<Entry<Location, BlockState>> it = replacedBlocks.entrySet().iterator();
		ArrayList<BlockState> blocksRemoved = new ArrayList<BlockState>();
		
		// collect all blocks to restore into an array and remove from the set
		while(it.hasNext()) {
			Entry<Location, BlockState> entry = it.next();
			
			// if the block has been drawn DON'T remove it
			if(!drawnBlocks.contains(entry.getKey())) {
				blocksRemoved.add(entry.getValue());
				// remove from the map
				it.remove();
			}
		}
		
		// restore the old state and remove metadata from the blocks outside the carpet
		for(BlockState state : blocksRemoved) {
			state.update(true, false);
			state.removeMetadata(CARPET_DATA, plugin);
		}
	}
}
























