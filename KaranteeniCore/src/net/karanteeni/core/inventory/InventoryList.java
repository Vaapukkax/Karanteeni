package net.karanteeni.core.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.inventory.preset.ActionAccept;
import net.karanteeni.core.inventory.preset.ActionNoPermission;
import net.karanteeni.core.inventory.preset.ActionToggle;
import net.karanteeni.core.inventory.preset.PresetActionItems;

public class InventoryList<T extends KaranteeniPlugin> extends InventoryBase<T> {

	// page X Y
	private ActionItem[][][] selectableItems;
	private int page = 0;
	private boolean singleItem;
	private boolean requiresItem;
	public final String ITEM_STORE;
	
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
	public InventoryList(T plugin, 
			ItemStack emptyItem, 
			Player player,
			boolean allowClose, 
			String title, 
			boolean singleItem, 
			String storeKey, 
			boolean requiresItem) {
		super(plugin, emptyItem, player, InventoryType.CHEST, 6, allowClose, title);
		this.ITEM_STORE = storeKey;
		this.requiresItem = requiresItem;
		this.singleItem = singleItem;
	}

	
	/**
	 * Sets the selectable items
	 * @param items items the player is able to select
	 */
	public void setSelectable(Collection<ItemStack> items) {
		this.selectableItems = new ActionItem[1 + items.size() / 44][9][5];
		
		int counter = 0;
		for(ItemStack item : items) {
			this.selectableItems[counter / 45][(counter % 45) % 9][(counter % 45) / 9] = new ActionToggle(item, false);
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
	 * Sets the selectable items
	 * @param items items the player is able to select
	 */
	public void setSelectableFormatted(Collection<ActionToggle> items) {
		this.selectableItems = new ActionItem[1 + items.size() / 44][9][5];
		
		int counter = 0;
		for(ActionToggle item : items) {
			this.selectableItems[counter / 45][(counter % 45) % 9][(counter % 45) / 9] = item;
			++counter;
		}
		
		setPage(page);
	}
	
	
	/**
	 * Sets the selected state of a given item
	 * @param page page the item is on
	 * @param x x index of item
	 * @param y y index of item
	 * @param selected true to make selected, false deselect
	 */
	public void setSelected(int page, int x, int y, boolean selected) {
		if(!(this.selectableItems[page][x][y] instanceof ActionToggle))
			return;
		
		// sets the given item as selected
		if(singleItem && selected)
			clearSelected();
		((ActionToggle)this.selectableItems[page][x][y]).setEnabled(this, holder, y*9+x, selected);
		this.generatePageNavbar();
	}
	
	
	/**
	 * Sets the item as selected
	 * @param page page the item is on
	 * @param index index of item
	 * @param selected new selected stage for item
	 */
	public void setSelected(int page, int index, boolean selected) {
		if(!(this.selectableItems[page][index % 9][index / 9] instanceof ActionToggle))
			return;
		// sets the given item as selected
		if(singleItem && selected)
			clearSelected();
		((ActionToggle)this.selectableItems[page][index % 9][index / 9]).setEnabled(this, holder, index, selected);
		this.generatePageNavbar();
	}
	
	
	/**
	 * Sets the map with already selected items
	 * @param items items the player is able to select
	 */
	public void setSelectable(Map<ItemStack, Boolean> items) {
		this.selectableItems = new ActionItem[1 + items.size() / 44][9][5];
		
		int counter = 0;
		for(Entry<ItemStack, Boolean> entry : items.entrySet()) {
			this.selectableItems[counter / 45][(counter % 45) % 9][(counter % 45) / 9] =
					new ActionToggle(entry.getKey(), entry.getValue());
			++counter;
		}
		
		setPage(page);
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
		
		// if using multiple items add the item to determine when this will be accepted
		if(requiresItem)
			this.setAction(PresetActionItems.getAccept(player, true, isAnySelected()), 8, 5);
		else
			this.setAction(PresetActionItems.getAccept(player, true, true), 8, 5);
		
	}
	
	
	/**
	 * Changes the acceptability of the accept item
	 * @param accept true to allow accept, false to deny accept
	 */
	private void setAcceptState(boolean accept) {
		ActionAccept item = (ActionAccept)this.items[8][5];
		item.setEnabled(accept);
	}
	
	
	/**
	 * Checks if any items are selected or not
	 * @return true if at least one item is selected, otherwise false
	 */
	private boolean isAnySelected() {
		for(int i = 0; i < this.selectableItems.length; ++i)
		for(int l = 0; l < this.selectableItems[i].length; ++l)
		for(int j = 0; j < this.selectableItems[i][l].length; ++j) {
			ActionItem item = this.selectableItems[i][l][j];
			if(!(item instanceof ActionToggle)) continue;
			
			// if the item is selected add it to the list
			if(((ActionToggle)item).isEnabled())
				return true;
		}
		
		return false;
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
	 * Clears selection from all of the items
	 */
	public void clearSelected() {
		// loop all items and add those which are selected to the list
		for(int i = 0; i < this.selectableItems.length; ++i)
		for(int l = 0; l < this.selectableItems[i].length; ++l)
		for(int j = 0; j < this.selectableItems[i][l].length; ++j) {
			ActionItem item = this.selectableItems[i][l][j];
			if(!(item instanceof ActionToggle)) continue;
			
			// if the item is selected add it to the list
			if(((ActionToggle)item).isEnabled()) {
				if(page != i)
					((ActionToggle)item).setEnabled(false);
				else
					((ActionToggle)item).setEnabled(this, this.holder, j*9 + l, false);
			}
		}
	}
	
	
	/**
	 * Returns all the items which were selected in the list
	 * @return list of all selected items
	 */
	public List<ItemStack> getSelectedItems() {
		// list of all items which were selected
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		// loop all items and add those which are selected to the list
		for(int i = 0; i < this.selectableItems.length; ++i)
		for(int l = 0; l < this.selectableItems[i].length; ++l)
		for(int j = 0; j < this.selectableItems[i][l].length; ++j) {
			ActionItem item = this.selectableItems[i][l][j];
			if(!(item instanceof ActionToggle)) continue;
			
			// if the item is selected add it to the list
			if(((ActionToggle)item).isEnabled())
				items.add(item.getItem());
		}
		
		return items;
	}
	
	
	/**
	 * Sets all of the items with this material as selected
	 * @param material material to use as selected
	 * @return the selected items. if material was already selected it won't show in the list
	 */
	public List<ActionToggle> setSelected(Material material) {
		List<ActionToggle> items = new ArrayList<ActionToggle>();
		
		for(int i = 0; i < this.selectableItems.length; ++i)
		for(int l = 0; l < this.selectableItems[i].length; ++l)
		for(int j = 0; j < this.selectableItems[i][l].length; ++j) {
			ActionItem item = this.selectableItems[i][l][j];
			if(!(item instanceof ActionToggle)) continue;
			
			// if the item is selected add it to the list
			if(((ActionToggle)item).getItem().getType() == material) {
				if(singleItem) {
					this.clearSelected();
					items.clear();
				}
				
				// if on existing page update, if not don't update inventory
				if(i*44 + l + j*9 <= 44)
					((ActionToggle)item).setEnabled(this, holder, i*44 + l + j*9, true);
				else
					((ActionToggle)item).setEnabled(true);
				items.add((ActionToggle)item);
			}
		}
		
		return items;
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
	
	
	/**
	 * Returns all the items which were selected in the list
	 * @return list of all selected items
	 */
	public List<ActionToggle> getSelectedActionItems() {
		// list of all items which were selected
		List<ActionToggle> items = new ArrayList<ActionToggle>();
		
		// loop all items and add those which are selected to the list
		for(int i = 0; i < this.selectableItems.length; ++i)
		for(int l = 0; l < this.selectableItems[i].length; ++l)
		for(int j = 0; j < this.selectableItems[i][l].length; ++j) {
			ActionItem item = this.selectableItems[i][l][j];
			if(!(item instanceof ActionToggle)) continue;
			
			// if the item is selected add it to the list
			if(((ActionToggle)item).isEnabled())
				items.add((ActionToggle)item);
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
		
		if(item instanceof ActionToggle) {
			ActionToggle toggler = (ActionToggle)item;
			boolean itemEnabled = toggler.isEnabled();
			
			// if only 1 items needs to be selected and the selected item was the one selected
			if(singleItem) {
				this.clearSelected();
				// call the click event of the item itself. Don't call if item WAS active AND no items required to leave it disabled
				// if no items required, allow the items to be empty
				if(menuPreClick(event, item, event.getSlot() % 9, event.getSlot() / 9) && (requiresItem || !itemEnabled))
					result = item.onSelect(this, holder, event);
			} else {
				// multiple items can be selected
				if(menuPreClick(event, item, event.getSlot() % 9, event.getSlot() / 9)) {
					result = item.onSelect(this, holder, event); // toggle the item
					// if no items are selected and items are required, disable the accept button
					if(requiresItem && !isAnySelected())
						this.setAcceptState(false);
					else
						this.setAcceptState(true);
				}
			}
		} else if(item != null && menuPreClick(event, item, event.getSlot() % 9, event.getSlot() / 9)) {
			result = item.onSelect(this, holder, event);
		}
		
		// if we're returning to the previous window, store the selected blocks
		if(result == InventoryMenu.SUCCESS_RETURN) {
			holder.setObject(ITEM_STORE, this.getSelectedItems());
			holder.setObject("selection-modified", true);
		} else {
			holder.setObject("selection-modified", false);
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
