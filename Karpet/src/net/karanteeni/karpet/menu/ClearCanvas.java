package net.karanteeni.karpet.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ClearCanvas<Karpet> extends ActionItem {

	public ClearCanvas(ItemStack item) {
		super(item);
	}
	

	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inve, InventoryMenu<T> menu,
			InventoryClickEvent event) {
		
		// clear the canvas
		((InventoryEditor)inve).clearCanvas();
		
		return InventoryMenu.SILENT;
	}

}
