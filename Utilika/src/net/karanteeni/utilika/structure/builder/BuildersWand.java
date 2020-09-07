package net.karanteeni.utilika.structure.builder;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import net.karanteeni.utilika.Utilika;

public class BuildersWand {
	private static final String TOWER_WAND_NAME = "§6Builders wand";
	private static final String TOWER_WAND_LORE = "§6Wand of tower";
	private static final String EXTEND_WAND_NAME = "§6Builders wand";
	private static final String EXTEND_WAND_LORE = "§6Wand of extend";
	private static final String LEVITATION_WAND_NAME = "§6Builders wand";
	private static final String LEVITATION_WAND_LORE = "§6Wand of levitation";
	private static final String NOTHINGNESS_WAND_NAME = "§6Builders wand";
	private static final String NOTHINGNESS_WAND_LORE = "§6Wand of nothingness";
	
	
	public static enum WandType {
		NOTHINGNESS,
		LEVITATION,
		TOWER,
		EXTEND
	}
	
	
	public static ShapedRecipe getRecipe() {
		ItemStack wand = new BuildersWand().createWand(WandType.TOWER);
		NamespacedKey wandKey = new NamespacedKey(Utilika.getPlugin(Utilika.class), "builders_wand");
		ShapedRecipe wandRecipe = new ShapedRecipe(wandKey, wand);
		wandRecipe.shape("LBL", "LEL", "LEL");
		wandRecipe.setIngredient('L', Material.LEATHER);
		wandRecipe.setIngredient('B', Material.BLAZE_ROD);
		wandRecipe.setIngredient('E', Material.END_ROD);
		return wandRecipe;
	}
	
	private ItemStack createWand(String name, String lore, Material material, Color color) {
		ItemStack wand = new ItemStack(material, 1);
		ItemMeta meta = wand.getItemMeta();
		if(color != null && meta instanceof PotionMeta) {
			((PotionMeta)meta).setColor(color);
		}
		meta.setLore(Arrays.asList(lore));
		meta.setDisplayName(name);
		wand.setItemMeta(meta);
		meta.setUnbreakable(true);
		
		return Utilika.getItemManager().addGlow(wand);
	}
	
	
	public ItemStack createWand(WandType wandType) {
		switch(wandType) {
		case EXTEND:
			return createWand(EXTEND_WAND_NAME, EXTEND_WAND_LORE, Material.TIPPED_ARROW, Color.YELLOW);
		case TOWER:
			return createWand(TOWER_WAND_NAME, TOWER_WAND_LORE, Material.TIPPED_ARROW, Color.BLACK);
		case NOTHINGNESS:
			return createWand(NOTHINGNESS_WAND_NAME, NOTHINGNESS_WAND_LORE, Material.SPECTRAL_ARROW, null);
		case LEVITATION:
			return createWand(LEVITATION_WAND_NAME, LEVITATION_WAND_LORE, Material.TIPPED_ARROW, Color.RED);
		default:
			return null;
		}
	}
	
	
	public WandType getWandType(final ItemStack itemStack) {
		if(isLevitationWand(itemStack)) {
			return WandType.LEVITATION;
		} else if(isTowerWand(itemStack)){
			return WandType.TOWER;
		} else if(isExtendWand(itemStack)) {
			return WandType.EXTEND;
		} else if(isNothingnessWand(itemStack)){
			return WandType.NOTHINGNESS;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Toggles the wand to another type of wand
	 * @param itemStack
	 * @return
	 */
	public ItemStack toggleWand(ItemStack itemStack) {
		WandType type = getWandType(itemStack);
		if(type == null)
			return null;
		int itemCount = itemStack.getAmount();
		ItemStack wand;
				
		if(type == WandType.LEVITATION) {
			wand = createWand(WandType.TOWER);
		} else if(type == WandType.TOWER){
			wand = createWand(WandType.EXTEND);
		} else if(type == WandType.EXTEND) {
			wand = createWand(WandType.NOTHINGNESS);
		} else {
			wand = createWand(WandType.LEVITATION);
		}
		
		ItemMeta meta = wand.getItemMeta();
		meta.setDisplayName(itemStack.getItemMeta().getDisplayName());
		wand.setItemMeta(meta);
		wand.setAmount(itemCount);
		
		return wand;
	}
	
	
	private boolean isWand(final ItemStack itemStack, String loreText, Material material) {
		if(itemStack.getType().equals(material)) {
			List<String> lore = itemStack.getItemMeta().getLore();
			return !lore.isEmpty() && lore.get(0).equals(loreText);
		}
		return false;
	}
	
	
	public boolean isLevitationWand(final ItemStack itemStack) {		
		return isWand(itemStack, LEVITATION_WAND_LORE, Material.TIPPED_ARROW);
	}
	
	
	public boolean isNothingnessWand(final ItemStack itemStack) {		
		return isWand(itemStack, NOTHINGNESS_WAND_LORE, Material.SPECTRAL_ARROW);
	}
	
	
	public boolean isTowerWand(final ItemStack itemStack) {		
		return isWand(itemStack, TOWER_WAND_LORE, Material.TIPPED_ARROW);
	}
	
	
	public boolean isExtendWand(final ItemStack itemStack) {
		return isWand(itemStack, EXTEND_WAND_LORE, Material.TIPPED_ARROW);
	}
}
