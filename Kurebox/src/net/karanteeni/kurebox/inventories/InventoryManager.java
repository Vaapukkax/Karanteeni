package net.karanteeni.kurebox.inventories;

import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.kurebox.Kurebox;

public class InventoryManager extends InventoryMenu<Kurebox> {

	public InventoryManager(KaranteeniPlugin plugin, 
			Player player, 
			InventoryBase<Kurebox> main) {
		super(plugin, player, main);
	}

}
