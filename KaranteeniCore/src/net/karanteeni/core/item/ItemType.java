package net.karanteeni.core.item;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;

public class ItemType {

	/**
	 * All picaxe type materials
	 */
	public static final Set<Material> PICKAXE = EnumSet.of(
			Material.DIAMOND_PICKAXE,
			Material.GOLDEN_PICKAXE,
			Material.IRON_PICKAXE,
			Material.STONE_PICKAXE,
			Material.WOODEN_PICKAXE
	);
	
	/**
	 * All shovel type materials
	 */
	public static final Set<Material> SHOVEL = EnumSet.of(
			Material.DIAMOND_SHOVEL,
			Material.GOLDEN_SHOVEL,
			Material.IRON_SHOVEL,
			Material.STONE_SHOVEL,
			Material.WOODEN_SHOVEL
	);
	
	/**
	 * All sword type materials
	 */
	public static final Set<Material> SWORD = EnumSet.of(
			Material.DIAMOND_SWORD,
			Material.GOLDEN_SWORD,
			Material.IRON_SWORD,
			Material.STONE_SWORD,
			Material.WOODEN_SWORD
	);
	
	/**
	 * All axe type materials
	 */
	public static final Set<Material> AXE = EnumSet.of(
			Material.DIAMOND_AXE,
			Material.GOLDEN_AXE,
			Material.IRON_AXE,
			Material.STONE_AXE,
			Material.WOODEN_AXE
	);
	
	/**
	 * All hoe type materials
	 */
	public static final Set<Material> HOE = EnumSet.of(
			Material.DIAMOND_HOE,
			Material.GOLDEN_HOE,
			Material.IRON_HOE,
			Material.STONE_HOE,
			Material.WOODEN_HOE
	);
}
