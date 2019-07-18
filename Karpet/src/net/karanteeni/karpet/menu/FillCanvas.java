package net.karanteeni.karpet.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;

public class FillCanvas<Karpet> extends ActionItem {

	/**
	 * {@inheritDoc}
	 */
	public FillCanvas(ItemStack item) {
		super(item);
	}

	@Override
	public <T extends KaranteeniPlugin> byte onSelect(InventoryBase<T> inv, InventoryMenu<T> arg1,
			InventoryClickEvent event) {
		InventoryEditor editor = (InventoryEditor)inv;
		
		editor.fillCanvas(editor.getColor());
		net.karanteeni.karpet.Karpet.getSoundHandler().playSound((Player)event.getWhoClicked(), 
				new SoundType(Sound.ITEM_BUCKET_EMPTY, 1f, 0.5f));
		
		return InventoryMenu.SILENT;
	}
	
	
}
