package net.karanteeni.tester.commands;

import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import net.karanteeni.core.block.executable.ActionBlock;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.text.Prefix;

public class HelloWorldBlock extends ActionBlock implements ActionBlock.Events.PlayerInteractEvent {
	private Long earliestPress = null;
	private Integer day = null;
	
	public HelloWorldBlock(Block block) throws IllegalArgumentException {
		super(block);
	}
	
	
	public HelloWorldBlock(Block block, UUID uuid) throws IllegalArgumentException {
		super(block, uuid);
	}

	
	@Override
	public void playerInteractEvent(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if(earliestPress <= System.currentTimeMillis() || event.getPlayer().isOp())
			Christmas.getPlugin(Christmas.class).getChatManager().startPlayer(event.getPlayer().getUniqueId(), day);
		else
			event.getPlayer().sendMessage(Prefix.NEGATIVE + "Et voi vielä avata tätä luukkua!");
	}

	
	/**
	 * Sets the earliest time this button can be pressed
	 * @param pressTime
	 */
	public void setPressTime(long pressTime, int day) {
		this.earliestPress = pressTime;
		this.day = day;
	}
	
	
	@Override
	public QueryState save() throws IllegalArgumentException {
		Christmas plugin = Christmas.getPlugin(Christmas.class);
		if(earliestPress == null)
			throw new IllegalArgumentException("Earliest press time has not been set!");
		if(day == null)
			throw new IllegalArgumentException("Day time has not been set!");
		if(plugin.getConfig().contains("buttons."+day))
			return QueryState.INSERTION_FAIL_ALREADY_EXISTS;
		// put this button to the button map
		plugin.buttons.put(day, this);
		
		QueryState result = super.save();
		
		plugin.getConfig().set("buttons."+day+".uuid", getUUID().toString());
		plugin.getConfig().set("buttons."+day+".time", earliestPress.toString());
		plugin.saveConfig();
		
		return result;
	}
	
	
	public int getDay() {
		return this.day;
	}
	
	
	@Override
	public QueryState destroy() {
		QueryState state = super.destroy();
		if(state == QueryState.REMOVAL_SUCCESSFUL) {
			Christmas plugin = Christmas.getPlugin(Christmas.class);
			plugin.getConfig().set("buttons."+day, null);
			plugin.saveConfig();
		}
		
		return state;
	}
	
	
	@Override
	public void onLoad() {
		Christmas plugin = Christmas.getPlugin(Christmas.class);
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("buttons");
		for(String key : section.getKeys(false)) {
			int day = -1;
			try {
				day = Integer.parseInt(key);
			} catch (Exception e) { }
			
			// check if this is the correct button
			if(plugin.getConfig().getString("buttons."+day+".uuid").equals(getUUID().toString())) {
				String time = plugin.getConfig().getString("buttons."+day+".time");
				try {
					earliestPress = Long.parseLong(time);
				} catch(Exception e) {
				}
				this.day = day;
				plugin.buttons.put(day, this);
				break;
			}
		}
	}
}
