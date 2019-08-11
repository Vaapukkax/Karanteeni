package net.karanteeni.foxet.punishment;

import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.foxet.Foxet;

public class BanInventory extends InventoryMenu<Foxet> {

	public BanInventory(KaranteeniPlugin plugin, Player player, InventoryBase<Foxet> main) {
		super(plugin, player, main);
	}

}
