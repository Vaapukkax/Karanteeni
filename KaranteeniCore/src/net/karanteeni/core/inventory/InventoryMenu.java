/**
 * 
 */
package net.karanteeni.core.inventory;

import java.util.HashMap;
import java.util.LinkedList;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.Sounds;

/**
 * Multi page inventory menu manager which automizes menu creation out of minecraft inventories.
 * @author Nuubles
 *
 */
public abstract class InventoryMenu<T extends KaranteeniPlugin> implements Listener {

	protected HashMap<String, InventoryBase<T>> inventories = new HashMap<String, InventoryBase<T>>();
	protected Player player;	
	protected LinkedList<InventoryBase<T>> history = new LinkedList<InventoryBase<T>>();
	protected HashMap<String, Object> data = new HashMap<String, Object>();
	KaranteeniPlugin plugin;
	
	private final static short RESULT_DIV = 11;
	public static final byte 
	SUCCESS 				= 0,
	FAIL 					= 1,
	CLICK 					= 2,
	NO_PERMISSION 			= 3,
	NEXT_PAGE 				= 4,
	PREVIOUS_PAGE 			= 5,
	RETURN 					= 6,
	OPEN_PAGE 				= 7,
	CLOSE_PAGE 				= 8,
	CHANGE 					= 9,
	SILENT 					= 10,
	SUCCESS_CLOSE 				= 11,
	FAIL_CLOSE 					= 12,
	CLICK_CLOSE 				= 13,
	NO_PERMISSION_CLOSE 		= 14,
	NEXT_PAGE_CLOSE 			= 15,
	PREVIOUS_PAGE_CLOSE 		= 16,
	RETURN_CLOSE 				= 17,
	OPEN_PAGE_CLOSE 			= 18,
	CLOSE_PAGE_CLOSE 			= 19,
	CHANGE_CLOSE 				= 20,
	SILENT_CLOSE 				= 21,
	SUCCESS_RETURN 			= 22,
	FAIL_RETURN 			= 23,
	CLICK_RETURN 			= 24,
	NO_PERMISSION_RETURN 	= 25,
	NEXT_PAGE_RETURN 		= 26,
	PREVIOUS_PAGE_RETURN 	= 27,
	RETURN_RETURN 			= 28,
	OPEN_PAGE_RETURN 		= 29,
	CLOSE_PAGE_RETURN 		= 30,
	CHANGE_RETURN 			= 31,
	SILENT_RETURN 			= 32;
	
	/**
	 * Creates a new translated inventorymenu for player
	 * @param player
	 */
	public InventoryMenu(KaranteeniPlugin plugin, Player player, InventoryBase<T> main) {
		//Can player close this inventory
		this.player = player;
		//this.history.addFirst(main);
		this.inventories.put("main", main);
		main.setHolder(this);
	}
	
	/**
	 * Click sounds etc.
	 * Action types are the following:
	 * 0 - do nothing
	 * 1 - close inventory
	 * 2 - go to the previous inventory
	 */
	public void clickResult(byte click) {		
		byte action = (byte)(click / InventoryMenu.RESULT_DIV);
		
		switch(click % InventoryMenu.RESULT_DIV) {
		case InventoryMenu.CHANGE:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_CHANGE.get());
			break;
		case InventoryMenu.CLICK:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK.get());
			break;
		case InventoryMenu.NO_PERMISSION:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_NO_PERMISSION.get());
			break;
		case InventoryMenu.SUCCESS:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_SUCCESS.get());
			break;
		case InventoryMenu.CLOSE_PAGE:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_CLOSE_PAGE.get());
			break;
		case InventoryMenu.FAIL:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_FAIL.get());
			break;
		case InventoryMenu.NEXT_PAGE:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_NEXT_PAGE.get());
			break;
		case InventoryMenu.OPEN_PAGE:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_OPEN_PAGE.get());
			break;
		case InventoryMenu.PREVIOUS_PAGE:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_PREVIOUS_PAGE.get());
			break;
		case InventoryMenu.RETURN:
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_RETURN.get());
			break;
		default:
			break;
		}
		
		switch(action) {
		case 1:
			history.peek().close();
			break;
		case 2:
			previousPage();
			break;
		default:
			break;
		}
	}

	
	/**
	 * Returns the inventory found with the given key
	 * @param key key to look for the stored inventory
	 * @return found inventory or null if none found
	 */
	public InventoryBase<T> getInventory(String key) {
		return this.inventories.get(key);
	}
	
	
	/**
	 * Opens the inventory with given key to player
	 * @param key
	 */
	public void openInventory(String key) {
		if(player != null && player.isOnline()) {
			InventoryBase<T> menu = inventories.get(key);
			if(menu != null) {
				//InventoryBase<T> inv = this.history.peek();
				
				// if opening first inventory as in the first open ever don't close
				//if(history.size() != 1 && inv != menu) {
				InventoryBase<T> old = this.history.peek();
				// if there's a previous inventory close it
				if(old != null)
					old.close();
				
				this.history.addFirst(menu); // to history
				//}
				
				menu.openInventory(); // open the active inventory
			}
		}
	}

	
	/**
	 * Opens the inventory with given key to player
	 * @param key
	 */
	public void openInventory(String key, boolean storeInHistory) {
		if(player != null && player.isOnline()) {
			InventoryBase<T> menu = inventories.get(key);
			if(menu != null) {
				//InventoryBase<T> inv = this.history.peek();
				
				//if(history.size() != 1 && inv != menu) {
					this.history.peek().close();
					if(storeInHistory)
						this.history.addFirst(menu); // to history
				//}
				menu.openInventory(); // open the active inventory
			}
		}
	}
	
	
	/**
	 * Returns to the previous page in history;
	 * @return true if a previous inventory is found, false if not found and inventory is closed
	 */
	public boolean previousPage() {
		history.removeFirst().close();
		InventoryBase<T> openable = history.peek();
		
		
		if(openable == null) {
			close();
			return false;
		} else {
			openable.openInventory();
			return true;
		}
	}
	
	
	/**
	 * Closes the inventory system
	 */
	public void close() {
		for(InventoryBase<T> inventory : inventories.values())
			inventory.close();
	}
	
	
	/**
	 * Returns the currently active inventory
	 * @return the currently active inventory
	 */
	public InventoryBase<T> getActiveInventory() {
		return this.history.peek();
	}
	
	
	/**
	 * Returns a given value from the datamap
	 * @param key key to retrieve the object with
	 * @return the found object
	 */
	@SuppressWarnings("unchecked")
	public <V> V getObject(String key) {
		return (V)this.data.get(key);
	}
	
	
	/**
	 * Sets a value to the data map
	 * @param key key to store the value with
	 * @param value value to store
	 * @return null or possible previous value at this location
	 */
	@SuppressWarnings("unchecked")
	public <V> V setObject(String key, V value) {
		Object t = this.data.put(key, value);
		if(t == null) return null;
		return (V)t;
	}
	
	
	/**
	 * Check if there exists an inventory with the given key
	 * @param key key the inventory is stored with
	 * @return true if inventory exists, false otherwise
	 */
	public boolean hasInventory(String key) {
		return this.inventories.containsKey(key);
	}
	
	
	/**
	 * Checks if theres data stored to the map with the given key
	 * @param key key to check
	 * @return true if data is stored with the given key, false if not
	 */
	public boolean hasObject(String key) {
		return this.data.containsKey(key);
	}
	
	
	/**
	 * Adds a new inventory to the inventory collection 
	 * @param inv inventory to add
	 * @param key key to get to the inventory
	 */
	public void addInventory(InventoryBase<T> inv, String key) {
		this.inventories.put(key, inv);
		inv.setHolder(this);
	}
}