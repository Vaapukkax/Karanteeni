package net.karanteeni.foxet.punishment;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.inventory.InventoryBase;
import net.karanteeni.core.inventory.preset.PresetActionItems;
import net.karanteeni.foxet.Foxet;

public class TimeSelector extends InventoryBase<Foxet> implements TranslationContainer {
	long defaultTime = 1000;
	
	public TimeSelector(Foxet plugin, Player player, String title) {
		super(plugin, PresetActionItems.getEmpty(player), player, InventoryType.CHEST, 6, false, title);
		registerTranslations();
	}

	
	@Override
	protected void fillItems() {
		// create time modifier icons
		ItemStack adder = new ItemStack(Material.YELLOW_CONCRETE, 1);
		ItemStack reducer = new ItemStack(Material.RED_CONCRETE_POWDER, 1);
		
		// set and create the time adders
		ActionTimeModify modifier = new ActionTimeModify(60000, adder);
		modifier.setItemName("§c+1 minute");
		this.setAction(modifier, 0, 1);
		
		modifier = new ActionTimeModify(600000, adder);
		modifier.setItemName("§c+10 minutes");
		this.setAction(modifier, 2, 1);
		
		modifier = new ActionTimeModify(3600000, adder);
		modifier.setItemName("§c+1 hour");
		this.setAction(modifier, 4, 1);
		
		modifier = new ActionTimeModify(36000000, adder);
		modifier.setItemName("§c+10 hours");
		this.setAction(modifier, 6, 1);
		
		modifier = new ActionTimeModify(86400000, adder);
		modifier.setItemName("§c+1 day");
		this.setAction(modifier, 8, 1);
		
		modifier = new ActionTimeModify(864000000, adder);
		modifier.setItemName("§c+10 days");
		this.setAction(modifier, 0, 3);
		
		modifier = new ActionTimeModify(2592000000l, adder);
		modifier.setItemName("§c+1 month");
		this.setAction(modifier, 2, 3);
		
		modifier = new ActionTimeModify(7776000000l, adder);
		modifier.setItemName("§c+3 months");
		this.setAction(modifier, 4, 3);
		
		modifier = new ActionTimeModify(31104000000l, adder);
		modifier.setItemName("§c+1 year");
		this.setAction(modifier, 6, 3);
		
		modifier = new ActionTimeModify(Long.MAX_VALUE, adder);
		modifier.setItemName("§c+INFINITE");
		this.setAction(modifier, 8, 3);
		
		// create and set time reducers
		modifier = new ActionTimeModify(-60000, reducer);
		modifier.setItemName("§c-1 minute");
		this.setAction(modifier, 0, 0);
		
		modifier = new ActionTimeModify(-600000, reducer);
		modifier.setItemName("§c-10 minutes");
		this.setAction(modifier, 2, 0);
		
		modifier = new ActionTimeModify(-3600000, reducer);
		modifier.setItemName("§c-1 hour");
		this.setAction(modifier, 4, 0);
		
		modifier = new ActionTimeModify(-36000000, reducer);
		modifier.setItemName("§c-10 hours");
		this.setAction(modifier, 6, 0);
		
		modifier = new ActionTimeModify(-86400000, reducer);
		modifier.setItemName("§c-1 day");
		this.setAction(modifier, 8, 0);
		
		modifier = new ActionTimeModify(-864000000, reducer);
		modifier.setItemName("§c-10 days");
		this.setAction(modifier, 0, 2);
		
		modifier = new ActionTimeModify(-2592000000l, reducer);
		modifier.setItemName("§c-1 month");
		this.setAction(modifier, 2, 2);
		
		modifier = new ActionTimeModify(-7776000000l, reducer);
		modifier.setItemName("§c-3 months");
		this.setAction(modifier, 4, 2);
		
		modifier = new ActionTimeModify(-31104000000l, reducer);
		modifier.setItemName("§c-1 year");
		this.setAction(modifier, 6, 2);
		
		modifier = new ActionTimeModify(Long.MIN_VALUE, reducer);
		modifier.setItemName("§c-INFINITE");
		this.setAction(modifier, 8, 2);
		
		// create accept, etc. buttons
		this.setAction(PresetActionItems.getAccept(player, true, true), 8, 5);
		this.setAction(PresetActionItems.getPreviousPage(player), 0, 5);
	}
	
	
	/**
	 * Modifies the ban time. For any other values than Long.MAX_VALUE the value will be in range of 0 - Long.MAX_VALUE-1.
	 * If the time is MAX_VALUE then it will be interpreted as infinite.
	 * @param time time to add to current ban time
	 */
	public void addTime(long time) {
		// resulting new time
		long result = 1000;
		// get the existing time if exists
		if(this.holder.hasObject("time"))
			result = Math.max(1000, this.holder.<Long>getObject("time") + time); 
		
		// set the time if it has not yet been set
		if(!this.holder.hasObject("time"))
			result = Math.max(time, 1000);
		
		// if the result is infinite and the time is not, remove 1 to signal that it is not infinite
		if(result == Long.MAX_VALUE && time != Long.MAX_VALUE)
			--result;
		
		this.holder.setObject("time", result);
	}
	
	
	/**
	 * Refreshes the time displayer
	 */
	public void refreshTimeDisplay() {
		ItemStack display = new ItemStack(Material.END_CRYSTAL, 1);
		ItemMeta meta = display.getItemMeta();
		meta.setDisplayName("ban length");
		display.setItemMeta(meta);
		this.setEmpty(display, 4, 5);
	}
	
	
	/**
	 * Sets the default time to be used in this inventory
	 * @param time time to use as default time
	 */
	public void setDefaultTime(long time) {
		this.defaultTime = Math.max(1000, time);
	}
	

	@Override
	public void onOpen() {
		// generate the default time from the reasons
	}


	@Override
	public void registerTranslations() {
		// TODO Auto-generated method stub
		
	}
}
