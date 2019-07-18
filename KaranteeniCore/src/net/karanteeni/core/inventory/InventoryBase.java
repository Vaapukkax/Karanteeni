/**
 * 
 */
package net.karanteeni.core.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.players.KPlayer;

/**
 * @author Nuubles
 *
 */
public abstract class InventoryBase<T extends KaranteeniPlugin> implements Listener {

	protected Inventory inventory;
	protected Player player;
	protected ActionItem[][] items;
	protected ItemStack emptyItem;
	protected boolean allowClose;
	protected boolean allowCloseorig;
	protected boolean openingClosed = false;
	protected InventoryMenu<T> holder;
	protected T plugin;
	

	/**
	 * Creates a new translated inventorymenu for player
	 * @param player
	 */
	public InventoryBase(T plugin,
			//InventoryMenu<T> holder,
			ItemStack emptyItem, 
			Player player, 
			InventoryType type, 
			int height, 
			boolean allowClose, 
			String title) 
	{
		//Can player close this inventory
		this.allowClose = allowClose;
		this.allowCloseorig = allowClose;
		this.player = player;
		//this.holder = holder;
		this.plugin = plugin;
		
		// TODO add to holder collection
		
		//Creates the inventory
		if(type.equals(InventoryType.CHEST))
			inventory = Bukkit.createInventory(player, height*9, title);
		else
			inventory = Bukkit.createInventory(player, type, title);
		
		// initialize the items array
		this.items = new ActionItem[9][height];
		
		// fill the inventory with empty items
		setEmpty(emptyItem);
	}
	
	
	/**
	 * Sets the inventory holder. Must NOT be used by anything else except the inventorymenu class
	 * @param holder holder to use as the data store and access
	 */
	protected void setHolder(InventoryMenu<T> holder) {
		this.holder = holder;
	}
	
	
	/**
	 * Opens this inventory to player
	 */
	public void openInventory() {
		InventoryBase<T> self = this;
		
		if(player != null && player.isOnline()) {
			/*Bukkit.getScheduler().scheduleSyncDelayedTask(KaranteeniCore.getPlugin(KaranteeniCore.class), new Runnable() {
				@Override
				public void run() {*/
					// closing another inventorybase
					if(holder.getActiveInventory() != self.inventory) {
						InventoryBase<T> inv = holder.getActiveInventory();
						if(inv != null) inv.close();
					} else {
						// close a reqular inventory
						player.closeInventory();
					}
					
					//Register inventory events
					Bukkit.getPluginManager().registerEvents(self, plugin);
					//Set this inventory to be the current inventory of the player
					KPlayer kp = KPlayer.getKPlayer(player);
					kp.setData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory", inventory);
					player.openInventory(inventory);
					onOpen();
					
					self.allowClose = self.allowCloseorig;
				/*}
			}, 1);*/
		}
		else
			this.close();
	}
	
	
	/**
	 * Reopens illegally closed inventory
	 */
	private void reopenClosed() {
		//InventoryBase<T> self = this;
		if(player != null && player.isOnline()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(KaranteeniCore.getPlugin(KaranteeniCore.class), new Runnable() {
				@Override
				public void run() {
					//Set this inventory to be the current inventory of the player
					KPlayer kp = KPlayer.getKPlayer(player);
					kp.setData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory", inventory);
					player.openInventory(inventory);
					
					//self.allowClose = self.allowCloseorig;
				}
			}, 0);
		}
		else
			this.close();
	}
	
	
	/**
	 * Called when the inventory is being opened
	 */
	public abstract void onOpen();
	
	
	/**
	 * Closes this inventory which has been opened by the player
	 * @param p
	 */
	public void close() {
		//Get the current holding inventory of the player
		KPlayer kp = KPlayer.getKPlayer(player);
		
		//Player was found
		if(kp != null) {
			Object data = kp.getData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory");
			
			//Remove the inventory only if it is the current one
			if(data != null && ((Inventory)data).equals(inventory))
				KPlayer.getKPlayer(player).removeData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory");
		}
		
		InventoryClickEvent.getHandlerList().unregister(this);
		InventoryCloseEvent.getHandlerList().unregister(this);
		InventoryDragEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
		this.allowClose = true;
		if(player.isOnline())
			player.closeInventory();
	}
	
	
	/**
	 * Adds a new action item to the inventory
	 * @param item item to add
	 * @param x horizontal location of the item
	 * @param y vertical location of the item
	 */
	public void setAction(ActionItem item, int x, int y) {
		this.items[x][y] = item;
		this.inventory.setItem(y*9 + x, item.getItem());
	}
	
	
	/**
	 * Sets the empty item to fill the empty slots in the inventory
	 * @param item item to set to the empty slots
	 */
	public void setEmpty(ItemStack item) {
		this.emptyItem = item;
		for(int x = 0; x < items.length; ++x)
		for(int y = 0; y < items[x].length; ++y)
		if(items[x][y] == null) {
			this.inventory.setItem(y*9 + x, item);
		}
	}

	
	/**
	 * Sets the empty item to the given slot
	 * @param item item to set to the given slot
	 */
	public void setEmpty(ItemStack item, int x, int y) {
		items[x][y] = null;
		this.inventory.setItem(y*9 + x, item);
	}
	
	
	/**
	 * Returns the inventory of this base. NOTICE: THIS INVENTORY SHOULD NOT BE MODIFIED!
	 * @return the inventory in this inventory
	 */
	public Inventory getInventory() {
		return this.inventory;
	}
	
	
	/**
	 * Returns the actual size of this inventory
	 * @return the size of this inventory
	 */
	public int getSize() {
		return this.inventory.getSize();
	}
	
	
	/**
	 * Returns the actual width of this inventory
	 * @return the width of this inventory
	 */
	public int getWidth() {
		return this.items.length;
	}
	
	
	/**
	 * Returns the actual height of this inventory
	 * @return the height of this inventory
	 */
	public int getHeight() {
		return this.items[0].length;
	}
	
	
	/**
	 * Returns the action items of this inventory.
	 * @return the items in this inventory
	 */
	public ActionItem[][] getActions() {
		return this.items;
	}
	
	
	/**
	 * Removes the action item from the given location in the inventory
	 * @param x horizontal coordinate of the item to remove
	 * @param y vertical coordinate of the item to remove
	 * @return the item removed
	 */
	public ActionItem removeAction(int x, int y) {
		ActionItem item = this.items[x][y];
		this.items[x][y] = null;
		this.inventory.setItem(y*9 + x, this.emptyItem);
		return item;
	}
	
	
	/**
	 * Removes the action at the given index removing the empty or previous item
	 * @param index index to set the item to
	 * @return the item removed or null
	 */
	public ActionItem removeAction(int index) {
		ActionItem item = this.items[index % 9][index / 9];
		this.items[index % 9][index / 9] = null;
		this.inventory.setItem(index, this.emptyItem);
		return item;
	}
	
	
	/**
	 * Player clicks this inventory. By default the click validity is checked and the click forwarded to the action event
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void menuClickEvent(InventoryClickEvent event) {
		// check if the clicked inventory is this inventory and not some else inventory
		if(!inventory.equals(event.getClickedInventory()))
			return;
		
		byte result = InventoryMenu.CLICK;
		event.setCancelled(true);
		
		// check whether the clicked item is valid
		if(event.getCursor() != null) {
			// player clicked own inventory
			if(event.getSlot() < this.inventory.getSize() && event.getSlot() >= 0) {
				// get the action item
				ActionItem item = items[event.getSlot() % 9][event.getSlot() / 9];
				
				
				// if the item has no action, just play the click sound 
				if(item != null && menuPreClick(event, item, event.getSlot() % 9, event.getSlot() / 9))
					result = item.onSelect(this, holder, event);
			}
		}
		
		// play the possible action of this base and play the sound and navigation
		holder.clickResult(menuClick(event, result, event.getSlot() % 9, event.getSlot() / 9));
	}
	
	
	/**
	 * This event will be called when the item is valid and the actionevent has been executed
	 * @param event
	 * @param actionResult the actionresult given by the action item. You may change this to change the output
	 * @return result (InventoryMenu.CLICK for example) which tells InventoryMenu in which way should it react to this
	 */
	public byte menuClick(InventoryClickEvent event, byte actionResult, int x, int y) { 
		return actionResult;
	}
	
	
	/**
	 * This event will be fired before executing the given action item
	 * @param event event fired
	 * @param item item which WILL be fired
	 * @return true if we continue on to executing the actionitem or false to deny
	 */
	public boolean menuPreClick(InventoryClickEvent event, ActionItem item, int x, int y) {
		return true;
	}
	
	
	/**
	 * Returns the action item at given index
	 * @param x x coordinate of the item
	 * @param y y coordinate of the item
	 * @return The required action item or null
	 */
	public ActionItem getActionItem(int x, int y) {
		if(x >= this.items.length || x < 0 || y >= this.items[0].length || y < 0) return null;
		return this.items[x][y];
	}
	
	
	/**
	 * Returns the action item at given index
	 * @param index index of item
	 * @return the required action item or null
	 */
	public ActionItem getActionItem(int index) {
		return getActionItem(index % 9, index / 9);
	}

	
	/**
	 * Fills the inventoryItems to this inventory
	 */
	protected abstract void fillItems();
	
	
	/**
	 * Cancel player inventory menu item drag
	 * @param event
	 */
	@EventHandler
	public void menuDrag(InventoryDragEvent event)  {
		Bukkit.broadcastMessage(this.getClass().getName());
		if(inventory.equals(event.getInventory()) && event.getInventorySlots().size() > 1)
			event.setCancelled(true);
	}
	
	
	/**
	 * If inventory closing is not allowed prevent inventory closing. Otherwise clear player inventory data
	 * and shut down this inventory
	 * @param event
	 */
	@EventHandler
	public void inventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId()) && this.inventory.equals(event.getInventory())) {

			//Get the current inventory of the player
			KPlayer kp = KPlayer.getKPlayer((Player)event.getPlayer());
			Object data = kp.getData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory");
			
			//Can this inventory be closed this way OR is player holding different inventory
			if(this.allowClose || (data != null && !((Inventory)data).equals(inventory))) {
				this.close();
			} else {
				if(!openingClosed) {
					openingClosed = true;
					//Open the inventory back up to player
					reopenClosed();
					closeDenied();
					openingClosed = false;
				}
			}
		}
	}
	
	
	/**
	 * Player tries to close this inventory but is not allowed to
	 */
	private void closeDenied() {
		//Play the deny sound
		KaranteeniPlugin.getSoundHandler().playSound(player, Sounds.NO.get());
	}
	
	
	/**
	 * Close this inventory if player leaves the game
	 * @param event
	 */
	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {
		if(event.getPlayer().getUniqueId().equals(player.getUniqueId()))
			holder.close();
	}
}