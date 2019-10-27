package net.karanteeni.simplevote;

import net.karanteeni.bungee.KaranteeniBungee;
import net.md_5.bungee.api.plugin.Listener;

public class SimpleVotes extends KaranteeniBungee implements Listener {
	private static SimpleVotes instance;
	
	public SimpleVotes() {
		super("SimpleVotes");
		instance = this;
	}
	
	
	@Override
	public void onEnable() {
		enableCommands();
		enableEvents();
	}
	
	
	/**
	 * Returns an instance of the currently used foxet plugin
	 * @return instance of the plugin
	 */
	public static SimpleVotes getInstance() {
		return instance;
	}
	
	
	/**
	 * Register plugin commands
	 */
	private void enableCommands() {
		this.getProxy().getPluginManager().registerCommand(this, new VoteCMD());
	}
	
	
	/**
	 * Register plugin events
	 */
	private void enableEvents() {
		this.getProxy().getPluginManager().registerListener(this, new ReceiveVote(this));
	}
}
