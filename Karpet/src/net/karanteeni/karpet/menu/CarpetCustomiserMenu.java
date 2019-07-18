package net.karanteeni.karpet.menu;

import org.bukkit.entity.Player;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.karpet.Karpet;

public class CarpetCustomiserMenu extends InventoryMenu<Karpet> {

	public CarpetCustomiserMenu(
			Karpet plugin, 
			Player player, 
			InventoryBase<Karpet> first) {
		super(plugin, player, first);
	}
	
	
	public static void registerTranslations() {
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.create-custom", 
				"§6Create custom carpet");
		/*Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.cancel", 
				"Cancel");
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.save", 
				"Save");*/
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.fill", 
				"§eFill");
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.available-blocks", 
				"§eAvailable blocks");
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.tools", 
				"§eTool layer");
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.layout", 
				"§eBase layout");
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.clear-canvas", 
				"§7Restore layout");
		Karpet.getTranslator().registerTranslation(Karpet.getPlugin(Karpet.class), 
				"carpet.select-color", 
				"§bSelect color");
	}
}
