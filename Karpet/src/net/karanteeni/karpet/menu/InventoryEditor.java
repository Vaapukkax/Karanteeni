package net.karanteeni.karpet.menu;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryList;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.core.inventory.preset.ActionAccept;
import net.karanteeni.core.inventory.preset.ActionInventoryOpener;
import net.karanteeni.core.inventory.preset.ActionReturn;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.karpet.Karpet;

public class InventoryEditor extends InventoryBase<Karpet> {
	private Material[][] carpetMaterials;
	private Material[][] defaultLayout;
	private Material paint;
	private int lastClickedSlot = -1;
	
	public InventoryEditor(Karpet plugin, 
			Player player,
			Material[][] carpetMaterials/*,
			Material[][] defaultLayout*/) {
		super(plugin, 
				PresetActionItems.getEmpty(player), 
				player, 
				InventoryType.CHEST, 
				6, 
				false, 
				Karpet.getTranslator().getTranslation(plugin, player, "carpet.create-custom"));
		
		// create copies of array prevent duplication problems
		Material[][] mat1 = new Material[5][5];
		for(int i = 0; i < 5; ++i)
		for(int l = 0; l < 5; ++l)
			mat1[i][l] = carpetMaterials[i][l]; // use the old layout as default
			//mat1[i][l] = defaultLayout[i][l];
		this.defaultLayout = mat1;
		
		Material[][] mat2 = new Material[5][5];
		for(int i = 0; i < 5; ++i)
		for(int l = 0; l < 5; ++l)
			mat2[i][l] = carpetMaterials[i][l];
		this.carpetMaterials = mat2;
		
		// take the center block of materials as the paint
		paint = carpetMaterials[2][2];
	}

	
	@Override
	protected void fillItems() {
		// fill with empty items
		this.setEmpty(PresetActionItems.getEmpty(player));
		// get the possible material from the material picker and set it to the carpet
		if(lastClickedSlot != -1 && holder.hasObject("selected") && holder.hasObject("selection-modified")) {
			// on left click change color
			ArrayList<ItemStack> items = holder.getObject("selected");
			if(holder.<Boolean>getObject("selection-modified")) {
				/*if(items.size() == 0)
					this.carpetMaterials[(lastClickedSlot % 9)-2][lastClickedSlot / 9] = Material.BARRIER;
				else
					this.carpetMaterials[(lastClickedSlot % 9)-2][lastClickedSlot / 9] = items.get(0).getType();*/
				this.setColor(items.get(0).getType());
			}
		}
		
		refreshCanvas();
		
		// layer 1 item
		
		// layer 2 item
		// accept item
		ActionAccept accept = PresetActionItems.getAccept(player, true, true);
		this.setAction(accept, 8, 5);
		// cancel item
		ActionReturn returnButton = PresetActionItems.getReturn(player);
		this.setAction(returnButton, 0, 5);
		// clear canvas item
		ItemStack clear = new ItemStack(Material.TNT, 1);
		Karpet.getItemManager().setDisplayName(clear, Karpet.getTranslator().getTranslation(plugin, player, "carpet.clear-canvas"));
		ClearCanvas<Karpet> cc = new ClearCanvas<Karpet>(clear);
		this.setAction(cc, 2, 5);
		// fill canvas item
		ItemStack fill = new ItemStack(Material.WATER_BUCKET, 1);
		Karpet.getItemManager().setDisplayName(fill, Karpet.getTranslator().getTranslation(plugin, player, "carpet.fill"));
		FillCanvas<Karpet> fillCanvas = new FillCanvas<Karpet>(fill);
		this.setAction(fillCanvas, 6, 5);
		// color picker and defined color
		this.setColor(paint);
	}
	
	
	/**
	 * Refreshes the canvas layout
	 */
	public void refreshCanvas() {
		// set the inventory opener items
		for(int y = 0; y < 5; ++y)
		for(int x = 0; x < 5; ++x) {
			ItemStack item = new ItemStack(carpetMaterials[x][y], 1);
			
			// loop each item and generate inventory opener from them
			//ActionInventoryOpener ao = new ActionInventoryOpener(item, true, "picker");
			ColorableItem ci = new ColorableItem(item, x, y);
			
			// set the item to the inventory
			this.setAction(ci, x + 2, y);
		}
	}
	
	
	/**
	 * Returns the color of the paint
	 * @return paint color
	 */
	protected Material getColor() {
		return this.paint;
	}
	
	
	/**
	 * Changes the color of the canvas at a given index
	 * @param material material to change to
	 * @param x horizontal index
	 * @param y vertical index
	 */
	protected void changeColor(Material material, int x, int y) {
		this.carpetMaterials[x][y] = material;
		
		ItemStack item = new ItemStack(carpetMaterials[x][y], 1);
		ColorableItem ci = new ColorableItem(item, x, y);
		this.setAction(ci, x + 2, y);
	}
	
	
	/**
	 * Sets the paint color
	 * @param material
	 */
	protected void setColor(Material material) {
		this.paint = material;
		// color selector
		ActionInventoryOpener ao = new ActionInventoryOpener(
				new ItemStack(material, 1), 
				false, 
				"picker");
		this.setAction(ao, 4, 5);
	}
	
	
	/**
	 * Fills the canvas with a given color
	 * @param material material to fill the canvas with
	 */
	public void fillCanvas(Material material) {
		// set the inventory opener items
		for(int y = 0; y < 5; ++y)
		for(int x = 0; x < 5; ++x) {
			changeColor(material, x, y);
		}
	}
	
	
	/**
	 * Clears the canvas
	 */
	public void clearCanvas() {
		for(int y = 0; y < 5; ++y)
		for(int x = 0; x < 5; ++x) {
			carpetMaterials[x][y] = defaultLayout[x][y];
		}
		
		refreshCanvas();
		Karpet.getSoundHandler().playSound(player, new SoundType(Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 1.8f));
	}
	
	
	/**
	 * Make the color selector have the correct predefined color
	 */
	@Override
	public boolean menuPreClick(InventoryClickEvent event, ActionItem item, int x, int y) {
		if(item instanceof ActionInventoryOpener) {
			// if we're opening the paint selector inventory
			InventoryList<Karpet> list = ((InventoryList<Karpet>)holder.getInventory("picker"));
			// clear the selected items if going to open new inventory
			list.clearSelected();
			list.setSelected(paint);
		}
		return true;
	}
	
	
	@Override
	public byte menuClick(InventoryClickEvent event, byte actionResult, int x, int y) {
		// player changes the block
		this.lastClickedSlot = event.getSlot();
		
		// on success safe the carpet design
		if(actionResult == InventoryMenu.SUCCESS || actionResult == InventoryMenu.SUCCESS_RETURN) {
			plugin.getCarpetHandler().setLayout(player, carpetMaterials);
		}
		
		return actionResult;
	}


	@Override
	public void onOpen() {
		fillItems();
	}
}
