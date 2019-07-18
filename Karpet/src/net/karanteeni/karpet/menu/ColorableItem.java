package net.karanteeni.karpet.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ColorableItem extends ActionItem {
	private int x;
	private int y;
	
	public ColorableItem(ItemStack item, int x, int y) {
		super(item);
		this.x = x;
		this.y = y;
	}
	

	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inv, InventoryMenu<T> arg1,
			InventoryClickEvent event) {
		if(event.getClick().isRightClick()) {
			InventoryEditor editor = (InventoryEditor)inv;
			
			// sets the itemstack to this
			editor.setColor(event.getCurrentItem().getType());
			
			return InventoryMenu.SUCCESS;
		} else {
			InventoryEditor editor = (InventoryEditor)inv;
			
			// sets the itemstack to this
			editor.changeColor(editor.getColor(), x, y);
			editor.refreshCanvas();
			
			return InventoryMenu.CHANGE;
		}
	}

}
