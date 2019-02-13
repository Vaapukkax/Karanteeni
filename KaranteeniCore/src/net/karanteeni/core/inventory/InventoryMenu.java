/**
 * 
 */
package net.karanteeni.core.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.players.KPlayer;

/**
 * @author Matti
 *
 */
public abstract class InventoryMenu implements Listener {

	protected Inventory inventory;
	protected Player player;
	private ItemStack back;
	private ItemStack close;
	private ItemStack empty;
	private boolean allowClose;
	private boolean openingClosed = false;

	/**
	 * Creates a new translated inventorymenu for player
	 * @param player
	 */
	public InventoryMenu(KaranteeniPlugin plugin, Player player, InventoryType type, int size, boolean useEmptyItems, boolean allowClose, String title)
	{
		//Can player close this inventory
		this.allowClose = allowClose;
		this.player = player;
		//Creates the inventory
		if(type.equals(InventoryType.CHEST))
			inventory = Bukkit.createInventory(player, size, title);
		else
			inventory = Bukkit.createInventory(player, type, title);
		
		//Create the BACK item
		back = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(KaranteeniPlugin.getTranslator().getTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), player, "inventory-back"));
		back.setItemMeta(meta);
		
		//Create the CLOSE item
		close = new ItemStack(Material.BARRIER, 1);
		meta = close.getItemMeta();
		meta.setDisplayName(KaranteeniPlugin.getTranslator().getTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), player, "inventory-close"));
		close.setItemMeta(meta);

		//Create the EMPTY item
		empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
		meta = empty.getItemMeta();
		meta.setDisplayName(KaranteeniPlugin.getTranslator().getTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), player, "inventory-empty"));
		empty.setItemMeta(meta);
		
		//Fill the inventory with empty items
		if(useEmptyItems)
			for(int i = 0; i < inventory.getSize(); ++i)
				inventory.setItem(i, empty);
		
		//Register inventory events
		Bukkit.getPluginManager().registerEvents(this, plugin);

		//Allow subclass to fill items
		fillItems();
		
		//Open inventory to player
		openInventory();
	}
	
	/**
	 * Player clicks this inventory
	 * @param event
	 */
	@EventHandler
	public abstract void menuClick(InventoryClickEvent event);
	
	/**
	 * Click sounds etc.
	 */
	protected void onClick(MenuClick click)
	{		
		if(click == MenuClick.CLICK)
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK.get());
		else if(click == MenuClick.SUCCESS)
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_SUCCESS.get());
		else if(click == MenuClick.NO_PERMISSION)
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_NO_PERMISSION.get());
		else if(click == MenuClick.FAIL)
			KaranteeniCore.getSoundHandler().playSound(player, Sounds.CLICK_FAIL.get());
	}
	
	/**
	 * Fills the inventoryItems to this inventory
	 */
	protected abstract void fillItems();
	
	/**
	 * Is this inventory the current inventory and is the item not null
	 * @param inv
	 * @param item
	 * @return
	 */
	protected boolean isValid(Inventory inv, ItemStack item, Player player)
	{
		if(inventory.equals(inv))
			if(item != null)
				if(this.player.getUniqueId().equals(player.getUniqueId()))
					return true;
		
		return false;
	}
	
	/**
	 * Opens this inventory to player
	 */
	private void openInventory()
	{
		if(player != null && player.isOnline())
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(KaranteeniCore.getPlugin(KaranteeniCore.class), new Runnable() {
				@Override
				public void run() {
					//Set this inventory to be the current inventory of the player
					KPlayer kp = KPlayer.getKPlayer(player);
					kp.setData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory", inventory);
					player.openInventory(inventory);
				}
			}, 0);
		}
		else
			this.closeInventory();
	}
	
	/**
	 * Player drags items in this inventory
	 * @param event
	 */
	@EventHandler
	public void menuDrag(InventoryDragEvent event) 
	{
		if(inventory.equals(event.getInventory()) && event.getInventorySlots().size() > 1)
			event.setCancelled(true);
	}
	
	/**
	 * Player closes this inventory
	 * @param event
	 */
	@EventHandler
	public void inventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId()) && this.inventory.equals(event.getInventory())) {

			//Get the current inventory of the player
			KPlayer kp = KPlayer.getKPlayer((Player)event.getPlayer());
			Object data = kp.getData(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory");
			
			//Can this inventory be closed this way OR is player holding different inventory
			if(this.allowClose || (data != null && !((Inventory)data).equals(inventory)))
			{
				this.closeInventory();
			}
			else
			{
				if(!openingClosed)
				{
					openingClosed = true;
					//Open the inventory back up to player
					openInventory();
					closeDenied();
					openingClosed = false;
				}
			}
		}
	}
	
	/**
	 * Player tries to close this inventory but is not allowed to
	 */
	private void closeDenied()
	{
		//Play the deny sound
		KaranteeniPlugin.getSoundHandler().playSound(player, Sounds.NO.get());
	}
	
	/**
	 * Close this inventory if player leaves the game
	 * @param event
	 */
	@EventHandler
	public void playerLeave(PlayerQuitEvent event)
	{
		if(event.getPlayer().getUniqueId().equals(player.getUniqueId()))
			this.closeInventory();
	}
	
	/**
	 * Returns the empty item
	 * @return
	 */
	protected ItemStack getEmpty()
	{
		return new ItemStack(empty);
	}
	
	/**
	 * Returns the close item
	 * @return
	 */
	protected ItemStack getClose()
	{
		return new ItemStack(close);
	}
	
	/**
	 * Returns the back item
	 * @return
	 */
	protected ItemStack getBack()
	{
		return new ItemStack(back);
	}
	
	/**
	 * Closes this inventory which has been opened by the player
	 * @param p
	 */
	public void closeInventory() {
		//Get the current holding inventory of the player
		KPlayer kp = KPlayer.getKPlayer(player);
		
		//Player was found
		if(kp != null)
		{
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
	
	public static enum MenuClick {
		SUCCESS,
		FAIL,
		NO_PERMISSION,
		CLICK;
	}
}