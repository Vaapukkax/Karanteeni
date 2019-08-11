package net.karanteeni.foxet.punishment;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class ActionTimeModify extends ActionItem {
	long time;
	
	public ActionTimeModify(long time, ItemStack item) {
		super(item);
		this.time = time;
	}

	
	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> base, InventoryMenu<T> menu,
			InventoryClickEvent event) {
		
		((TimeSelector)base).addTime(this.time);
		return InventoryMenu.CHANGE;
	}

}
