package net.karanteeni.core.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.InventoryHolder;

public class BlockType {
	
	/**
	 * All dirt type materials
	 */
	public static final Set<Material> GROWABLE = EnumSet.of(
			Material.DIRT, Material.GRASS_BLOCK, Material.PODZOL,
			Material.MYCELIUM, Material.COARSE_DIRT
	);
	
	/**
	 * All concrete type materials
	 */
	public static final Set<Material> CONCRETE = EnumSet.of(
			Material.BLACK_CONCRETE,
			Material.BLUE_CONCRETE,
			Material.BROWN_CONCRETE,
			Material.CYAN_CONCRETE,
			Material.GRAY_CONCRETE,
			Material.GREEN_CONCRETE,
			Material.LIGHT_BLUE_CONCRETE,
			Material.LIGHT_GRAY_CONCRETE,
			Material.LIME_CONCRETE,
			Material.MAGENTA_CONCRETE,
			Material.ORANGE_CONCRETE,
			Material.PINK_CONCRETE,
			Material.PURPLE_CONCRETE,
			Material.RED_CONCRETE,
			Material.WHITE_CONCRETE,
			Material.YELLOW_CONCRETE
	);
	
	/**
	 * All concrete powder type materials
	 */
	public static final Set<Material> CONCRETE_POWDER = EnumSet.of(
			Material.BLACK_CONCRETE_POWDER,
			Material.BLUE_CONCRETE_POWDER,
			Material.BROWN_CONCRETE_POWDER,
			Material.CYAN_CONCRETE_POWDER,
			Material.GRAY_CONCRETE_POWDER,
			Material.GREEN_CONCRETE_POWDER,
			Material.LIGHT_BLUE_CONCRETE_POWDER,
			Material.LIGHT_GRAY_CONCRETE_POWDER,
			Material.LIME_CONCRETE_POWDER,
			Material.MAGENTA_CONCRETE_POWDER,
			Material.ORANGE_CONCRETE_POWDER,
			Material.PINK_CONCRETE_POWDER,
			Material.PURPLE_CONCRETE_POWDER,
			Material.RED_CONCRETE_POWDER,
			Material.WHITE_CONCRETE_POWDER,
			Material.YELLOW_CONCRETE_POWDER
	);
	
	/**
	 * All wool type materials
	 */
	public static final Set<Material> WOOL = EnumSet.of(
			Material.BLACK_WOOL,
			Material.BLUE_WOOL,
			Material.BROWN_WOOL,
			Material.CYAN_WOOL,
			Material.GRAY_WOOL,
			Material.GREEN_WOOL,
			Material.LIGHT_BLUE_WOOL,
			Material.LIGHT_GRAY_WOOL,
			Material.LIME_WOOL,
			Material.MAGENTA_WOOL,
			Material.ORANGE_WOOL,
			Material.PINK_WOOL,
			Material.PURPLE_WOOL,
			Material.RED_WOOL,
			Material.WHITE_WOOL,
			Material.YELLOW_WOOL
	);
	
	/**
	 * All wool type materials
	 */
	public static final Set<Material> BED = EnumSet.of(
			Material.BLACK_BED,
			Material.BLUE_BED,
			Material.BROWN_BED,
			Material.CYAN_BED,
			Material.GRAY_BED,
			Material.GREEN_BED,
			Material.LIGHT_BLUE_BED,
			Material.LIGHT_GRAY_BED,
			Material.LIME_BED,
			Material.MAGENTA_BED,
			Material.ORANGE_BED,
			Material.PINK_BED,
			Material.PURPLE_BED,
			Material.RED_BED,
			Material.WHITE_BED,
			Material.YELLOW_BED
	);
	
	/**
	 * All log type materials
	 */
	public static final Set<Material> LOG = EnumSet.of(
			Material.ACACIA_LOG,
			Material.BIRCH_LOG,
			Material.DARK_OAK_LOG,
			Material.JUNGLE_LOG,
			Material.OAK_LOG,
			Material.SPRUCE_LOG
	);
	
	/**
	 * All stripped log type materials
	 */
	public static final Set<Material> STRIPPED_LOG = EnumSet.of(
			Material.STRIPPED_ACACIA_LOG,
			Material.STRIPPED_BIRCH_LOG,
			Material.STRIPPED_DARK_OAK_LOG,
			Material.STRIPPED_JUNGLE_LOG,
			Material.STRIPPED_OAK_LOG,
			Material.STRIPPED_SPRUCE_LOG
	);
	
	/**
	 * All planks type materials
	 */
	public static final Set<Material> PLANKS = EnumSet.of(
			Material.ACACIA_PLANKS,
			Material.BIRCH_PLANKS,
			Material.DARK_OAK_PLANKS,
			Material.JUNGLE_PLANKS,
			Material.OAK_PLANKS,
			Material.SPRUCE_PLANKS
	);
	
	/**
	 * All trapdoor type materials
	 */
	public static final Set<Material> TRAPDOOR = EnumSet.of(
			Material.ACACIA_TRAPDOOR,
			Material.BIRCH_TRAPDOOR,
			Material.DARK_OAK_TRAPDOOR,
			Material.JUNGLE_TRAPDOOR,
			Material.OAK_TRAPDOOR,
			Material.SPRUCE_TRAPDOOR
	);
	
	/**
	 * All button type materials
	 */
	public static final Set<Material> BUTTON = EnumSet.of(
			Material.ACACIA_BUTTON,
			Material.BIRCH_BUTTON,
			Material.DARK_OAK_BUTTON,
			Material.JUNGLE_BUTTON,
			Material.OAK_BUTTON,
			Material.SPRUCE_BUTTON,
			Material.STONE_BUTTON
	);
	
	/**
	 * All door type materials
	 */
	public static final Set<Material> DOOR = EnumSet.of(
			Material.ACACIA_DOOR,
			Material.BIRCH_DOOR,
			Material.DARK_OAK_DOOR,
			Material.JUNGLE_DOOR,
			Material.OAK_DOOR,
			Material.SPRUCE_DOOR
	);
	
	/**
	 * All fence gate type materials
	 */
	public static final Set<Material> FENCE_GATE = EnumSet.of(
			Material.ACACIA_FENCE_GATE,
			Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,
			Material.JUNGLE_FENCE_GATE,
			Material.OAK_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE
	);
	
	/**
	 * All fence type materials
	 */
	public static final Set<Material> FENCE = EnumSet.of(
			Material.ACACIA_FENCE,
			Material.BIRCH_FENCE,
			Material.DARK_OAK_FENCE,
			Material.JUNGLE_FENCE,
			Material.OAK_FENCE,
			Material.SPRUCE_FENCE
	);
	
	/**
	 * All stone type materials
	 */
	public static final Set<Material> STONE = EnumSet.of(
			Material.STONE,
			Material.INFESTED_STONE,
			Material.COBBLESTONE,
			Material.INFESTED_COBBLESTONE,
			Material.MOSSY_COBBLESTONE,
			Material.GRANITE,
			Material.ANDESITE,
			Material.DIORITE
	);

	/**
	 * All leave type materials
	 */
	public static final Set<Material> LEAVES = EnumSet.of(
			Material.ACACIA_LEAVES,
			Material.BIRCH_LEAVES,
			Material.DARK_OAK_LEAVES,
			Material.JUNGLE_LEAVES,
			Material.OAK_LEAVES,
			Material.SPRUCE_LEAVES
	);
	
	/**
	 * Check whether this material is either dirt, grass, podzol,coarse dirt or mycelium
	 * @param material
	 * @return
	 */
	/*public static boolean isGrowable(Material material)
	{
		return (material.equals(Material.DIRT) ||
				material.equals(Material.GRASS_BLOCK) ||
				material.equals(Material.PODZOL) ||
				material.equals(Material.MYCELIUM) ||
				material.equals(Material.COARSE_DIRT));
	}*/
	
	/**
	 * Check whether this material is concrete
	 * @param material
	 * @return
	 */
	/*public static boolean isConcrete(Material material)
	{
		return (material.equals(Material.BLACK_CONCRETE) ||
				material.equals(Material.BLUE_CONCRETE) ||
				material.equals(Material.BROWN_CONCRETE) ||
				material.equals(Material.CYAN_CONCRETE) ||
				material.equals(Material.GRAY_CONCRETE) ||
				material.equals(Material.GREEN_CONCRETE) ||
				material.equals(Material.LIGHT_BLUE_CONCRETE) ||
				material.equals(Material.LIGHT_GRAY_CONCRETE) ||
				material.equals(Material.LIME_CONCRETE) ||
				material.equals(Material.MAGENTA_CONCRETE) ||
				material.equals(Material.ORANGE_CONCRETE) ||
				material.equals(Material.PINK_CONCRETE) ||
				material.equals(Material.PURPLE_CONCRETE) ||
				material.equals(Material.RED_CONCRETE) ||
				material.equals(Material.WHITE_CONCRETE) ||
				material.equals(Material.YELLOW_CONCRETE));
	}*/
	
	/**
	 * Check whether this material is concrete powder
	 * @param material
	 * @return
	 */
	/*public static boolean isConcretePowder(Material material)
	{
		return (material.equals(Material.BLACK_CONCRETE_POWDER) ||
				material.equals(Material.BLUE_CONCRETE_POWDER) ||
				material.equals(Material.BROWN_CONCRETE_POWDER) ||
				material.equals(Material.CYAN_CONCRETE_POWDER) ||
				material.equals(Material.GRAY_CONCRETE_POWDER) ||
				material.equals(Material.GREEN_CONCRETE_POWDER) ||
				material.equals(Material.LIGHT_BLUE_CONCRETE_POWDER) ||
				material.equals(Material.LIGHT_GRAY_CONCRETE_POWDER) ||
				material.equals(Material.LIME_CONCRETE_POWDER) ||
				material.equals(Material.MAGENTA_CONCRETE_POWDER) ||
				material.equals(Material.ORANGE_CONCRETE_POWDER) ||
				material.equals(Material.PINK_CONCRETE_POWDER) ||
				material.equals(Material.PURPLE_CONCRETE_POWDER) ||
				material.equals(Material.RED_CONCRETE_POWDER) ||
				material.equals(Material.WHITE_CONCRETE_POWDER) ||
				material.equals(Material.YELLOW_CONCRETE_POWDER));
	}*/
	
	/**
	 * Check whether this material is wool
	 * @param material
	 * @return
	 */
	/*public static boolean isWool(Material material)
	{
		return (material.equals(Material.BLACK_WOOL) ||
				material.equals(Material.BLUE_WOOL) ||
				material.equals(Material.BROWN_WOOL) ||
				material.equals(Material.CYAN_WOOL) ||
				material.equals(Material.GRAY_WOOL) ||
				material.equals(Material.GREEN_WOOL) ||
				material.equals(Material.LIGHT_BLUE_WOOL) ||
				material.equals(Material.LIGHT_GRAY_WOOL) ||
				material.equals(Material.LIME_WOOL) ||
				material.equals(Material.MAGENTA_WOOL) ||
				material.equals(Material.ORANGE_WOOL) ||
				material.equals(Material.PINK_WOOL) ||
				material.equals(Material.PURPLE_WOOL) ||
				material.equals(Material.RED_WOOL) ||
				material.equals(Material.WHITE_WOOL) ||
				material.equals(Material.YELLOW_WOOL));
	}*/
	
	/**
	 * Returns all chests connected to the current chest
	 * @param block
	 * @return
	 */
	public static List<Block> getConnectedChestBlock(Block block)
	{
		if(block.getState() instanceof Chest)
		{
			Chest c = (Chest)block.getState();
			InventoryHolder holder = c.getInventory().getHolder();
			
			if(holder instanceof DoubleChest)
			{
				DoubleChest dc = (DoubleChest)holder;
				Chest c1 = (Chest)dc.getLeftSide();
				Chest c2 = (Chest)dc.getRightSide();
				//Palauta chestin molemmat blockit
				return new ArrayList<Block>(Arrays.asList(c1.getBlock(), c2.getBlock()));
			}
			
		}
		
		//No other chest, return current block
		return new ArrayList<Block>(Arrays.asList(block));
	}
	
	/**
	 * Ovatko annetut materiaalit samantyyppisi�
	 * @param material
	 * @param m
	 * @return
	 */
	/*public static boolean isSimiliar(Material material, Material m)
	{
		if(material.equals(m))
			return true;
		if(isLog(material) && isLog(m))
			return true;
		else if(isStoneType(material) && isStoneType(m))
			return true;
		else if(isGrowable(material) && isGrowable(m))
			return true;
		else
			return false;
	}*/
	
	/**
	 * Tarkistaa onko materiaali puuta
	 * @param material tarkistettava materiaali
	 * @return onko ei stripattu puu
	 */
	/*public static boolean isLog(Material material)
	{
		if(material.equals(Material.ACACIA_LOG) ||
				material.equals(Material.BIRCH_LOG) ||
				material.equals(Material.DARK_OAK_LOG) ||
				material.equals(Material.JUNGLE_LOG) ||
				material.equals(Material.OAK_LOG) ||
				material.equals(Material.SPRUCE_LOG))
			return true;
		return false;
	}*/
	
	/*public static boolean isStrippedLog(Material material)
	{
		if(material.equals(Material.STRIPPED_ACACIA_LOG) ||
				material.equals(Material.STRIPPED_BIRCH_LOG) ||
				material.equals(Material.STRIPPED_DARK_OAK_LOG) ||
				material.equals(Material.STRIPPED_JUNGLE_LOG) ||
				material.equals(Material.STRIPPED_OAK_LOG) ||
				material.equals(Material.STRIPPED_SPRUCE_LOG))
			return true;
		return false;
	}*/
	
	/*public static boolean isPlanks(Material material)
	{
		if(material.equals(Material.ACACIA_PLANKS) ||
				material.equals(Material.BIRCH_PLANKS) ||
				material.equals(Material.DARK_OAK_PLANKS) ||
				material.equals(Material.JUNGLE_PLANKS) ||
				material.equals(Material.OAK_PLANKS) ||
				material.equals(Material.SPRUCE_PLANKS))
			return true;
		return false;
	}*/
	
	/**
	 * Tarkistaa onko materiaali kive�
	 * @param material tarkistettava materiaali
	 * @return onko kive�
	 */
	/*public static boolean isStoneType(Material material)
	{
		if(material.equals(Material.STONE) ||
				material.equals(Material.INFESTED_STONE) ||
				material.equals(Material.COBBLESTONE) ||
				material.equals(Material.INFESTED_COBBLESTONE) ||
				material.equals(Material.MOSSY_COBBLESTONE) ||
				material.equals(Material.GRANITE) ||
				material.equals(Material.ANDESITE) ||
				material.equals(Material.DIORITE))
			return true;
		return false;
	}*/

	/*public static boolean isLeaves(Material material)
	{
		if(material.equals(Material.ACACIA_LEAVES) ||
				material.equals(Material.BIRCH_LEAVES) ||
				material.equals(Material.DARK_OAK_LEAVES) ||
				material.equals(Material.JUNGLE_LEAVES) ||
				material.equals(Material.OAK_LEAVES) ||
				material.equals(Material.SPRUCE_LEAVES))
			return true;
		return false;
	}*/
	
	/*public static boolean isTrapdoor(Material material)
	{
		if(material.equals(Material.ACACIA_TRAPDOOR) ||
				material.equals(Material.BIRCH_TRAPDOOR) ||
				material.equals(Material.DARK_OAK_TRAPDOOR) ||
				material.equals(Material.JUNGLE_TRAPDOOR) ||
				material.equals(Material.OAK_TRAPDOOR) ||
				material.equals(Material.SPRUCE_TRAPDOOR))
			return true;
		return false;
	}*/
	
	/*public static boolean isFenceGate(Material material)
	{
		if(material.equals(Material.ACACIA_FENCE_GATE) ||
				material.equals(Material.BIRCH_FENCE_GATE) ||
				material.equals(Material.DARK_OAK_FENCE_GATE) ||
				material.equals(Material.JUNGLE_FENCE_GATE) ||
				material.equals(Material.OAK_FENCE_GATE) ||
				material.equals(Material.SPRUCE_FENCE_GATE))
			return true;
		return false;
	}*/
	
	/*public static boolean isDoor(Material material)
	{
		if(material.equals(Material.ACACIA_DOOR) ||
				material.equals(Material.BIRCH_DOOR) ||
				material.equals(Material.DARK_OAK_DOOR) ||
				material.equals(Material.JUNGLE_DOOR) ||
				material.equals(Material.OAK_DOOR) ||
				material.equals(Material.SPRUCE_DOOR))
			return true;
		return false;
	}*/
	
	/*public static boolean isButton(Material material)
	{
		if(material.equals(Material.ACACIA_BUTTON) ||
				material.equals(Material.BIRCH_BUTTON) ||
				material.equals(Material.DARK_OAK_BUTTON) ||
				material.equals(Material.JUNGLE_BUTTON) ||
				material.equals(Material.OAK_BUTTON) ||
				material.equals(Material.SPRUCE_BUTTON))
			return true;
		return false;
	}*/
}
