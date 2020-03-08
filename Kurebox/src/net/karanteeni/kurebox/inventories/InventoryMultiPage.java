package net.karanteeni.kurebox.inventories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.ActionItem;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.InventoryMenu;
import net.karanteeni.core.inventory.preset.ActionNoPermission;
import net.karanteeni.core.inventory.preset.PresetActionItems;

public class InventoryMultiPage<T extends KaranteeniPlugin> extends InventoryBase<T> {

	// page X Y
	private ActionItem[][][] selectableItems;
	private int page = 0;
	
	/**
	 * Initializes the list of items to select with given parameters. The title will be the inventory name
	 * and player the one who will receive the items
	 * @param plugin {@inheritDoc}
	 * @param holder {@inheritDoc}
	 * @param emptyItem {@inheritDoc}
	 * @param player {@inheritDoc}
	 * @param allowClose {@inheritDoc}
	 * @param title {@inheritDoc}
	 * @param storeKey the to which the loaded itemstacks will be put to
	 */
	public InventoryMultiPage(T plugin, 
			ItemStack emptyItem, 
			Player player,
			boolean allowClose, 
			String title) {
		super(plugin, emptyItem, player, InventoryType.CHEST, 6, allowClose, title);
	}

	
	/**
	 * Sets the selectable items
	 * @param items items the player is able to select
	 */
	public void setSelectable(Collection<ActionItem> items) {
		this.selectableItems = new ActionItem[1 + items.size() / 44][9][5];
		
		int counter = 0;
		for(ActionItem item : items) {
			this.selectableItems[counter / 45][(counter % 45) % 9][(counter % 45) / 9] = item;
			++counter;
		}
		
		setPage(page);
	}
	
	
	/**
	 * Sets no permission item to these locations
	 * @param indeces no permission to set to these locations
	 */
	public void setNoPermissionIndeces(List<Integer> indeces) {
		//ItemStack item = new ItemStack(Material.GRAY_DYE, 1);
		ActionNoPermission noPerm = PresetActionItems.getNoPermission(player);
		//ActionNoPermission noPerm = new ActionNoPermission();
		for(Integer i : indeces) {
			this.selectableItems[i/45][(i % 45) % 9][(i % 45) / 9] = noPerm;
		}
		// refreshes the active page
		this.refreshPage();
	}

	
	/**
	 * Generates the blocks to the current inventory from indeces 0 to 44
	 */
	private void generatePageItems() {
		// loop each item
		for(int y = 0; y < 5; ++y) // horizontal loop
		for(int x = 0; x < 9; ++x) { // vertical loop
			ActionItem item = this.selectableItems[page][x][y]; // the current item to set to the menu
			if(item == null) { // if the item is null set empty
				this.setEmpty(emptyItem, x, y);
			} else {
				this.setAction(item, x, y); // set the item to the display
			}
		}
	}
	
	
	/**
	 * Generates the selection navbar to the nav list
	 */
	private void generatePageNavbar() {
		// set the empty items
		for(int x = 0; x < 9; ++x) // loop the x-axis, y is ALWAYS 5
			this.setEmpty(emptyItem, x, 5);

		// set the return item
		this.setAction(PresetActionItems.getReturn(player), 0, 5);
		
		// dont generate next or previous page buttons on last and first pages
		if(!isFirstPage())
			this.setAction(PresetActionItems.getPreviousPage(player), 2, 5);
		if(!isLastPage())
			this.setAction(PresetActionItems.getNextPage(player), 6, 5);
		
		// close button
		this.setAction(PresetActionItems.getClose(player), 8, 5);
	}
	
	
	/**
	 * Is the current page the first page
	 * @return true if first page, false otherwise
	 */
	private boolean isFirstPage() {
		return page == 0;
	}
	
	
	/**
	 * Is the current page the last page
	 * @return true if at the last page, false otherwise
	 */
	private boolean isLastPage() {
		return page == selectableItems.length-1;
	}
	
	
	/**
	 * Gets the page number for the given index
	 * @param index index to convert to page
	 * @return the page number
	 */
	public int getAsPage(int index) {
		return index / 45;
	}
	
	
	/**
	 * Subtracts the page count from the index to return the regular one page index
	 * @param index index from which the pages are removed
	 * @return the remaining index
	 */
	public int subtractPages(int index) {
		return index - (((int)index / 45)*45);
	}
	
	
	/**
	 * Sets the page of this list 
	 * @param page page to set to
	 */
	private void setPage(int page) {
		if(page < 0)
			page = 0;
		else if(page > this.selectableItems.length-1)
			page = this.selectableItems.length-1;
		this.page = page;
		
		this.generatePageItems();
		this.generatePageNavbar();
	}
	
	
	/**
	 * Refreshes the current page
	 */
	public void refreshPage() {
		setPage(this.page);
	}
	
	
	@Override
	protected void fillItems() {
		setPage(page);
	}
	
	
	/**
	 * Returns all of the items as a list
	 * @return the list of all actiontoggle items
	 */
	public List<ActionItem> getItems() {
		List<ActionItem> items = new ArrayList<ActionItem>();
		for(int i = 0; i < this.selectableItems.length; ++i)
		for(int c = 0; c < 45; ++c) {
			items.add(this.selectableItems[i][c % 9][c / 9]);
		}
		return items;
	}
	
	
	@Override
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void menuClickEvent(InventoryClickEvent event) {
		// are we interacting with this menu
		if(!inventory.equals(event.getClickedInventory()))
			return;
		
		byte result = InventoryMenu.CLICK;
		event.setCancelled(true);
		
		// player clicked nothing or own inventory, return the normal result
		if(event.getCursor() == null || event.getSlot() >= this.inventory.getSize() || event.getSlot() < 0) {
			if(event.getSlot() >= 0)
				holder.clickResult(result);
			return;
		}
		
		// get the item at the clicked location
		ActionItem item = items[event.getSlot() % 9][event.getSlot() / 9];
		if(item != null && menuPreClick(event, item, event.getSlot() % 9, event.getSlot() / 9)) {
			result = item.onSelect(this, holder, event);
		}
		
		// return peacefully
		holder.clickResult(menuClick(event, result, event.getSlot() % 9, event.getSlot() / 9));
	}
	
	
	/**
	 * Change pages on click events
	 */
	@Override
	public byte menuClick(InventoryClickEvent event, byte result, int x, int y) {
		if(result == InventoryMenu.NEXT_PAGE)
			this.setPage(page+1);
		else if(result == InventoryMenu.PREVIOUS_PAGE)
			this.setPage(page-1);
		return result;
	}


	@Override
	public void onOpen() {
		fillItems();
	}
}
