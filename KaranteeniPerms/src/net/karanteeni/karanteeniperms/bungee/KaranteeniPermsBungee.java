package net.karanteeni.karanteeniperms.bungee;

import net.karanteeni.bungee.core.KaranteeniPlugin;
import net.karanteeni.karanteeniperms.bungee.groups.GroupList;
import net.karanteeni.karanteeniperms.bungee.groups.GroupRequestListener;
import net.karanteeni.karanteeniperms.bungee.groups.JoinBungeeEvent;
import net.karanteeni.karanteeniperms.bungee.groups.PlayerModel;

public class KaranteeniPermsBungee extends KaranteeniPlugin {
	private GroupList groupList;
	private PlayerModel playerModel;
	private static KaranteeniPermsBungee instance;

	
	public KaranteeniPermsBungee() {
		super("KaranteeniPerms");
		instance = this;
		try {
			this.groupList = new GroupList(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		this.playerModel = new PlayerModel(this);
	}
	
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		registerChannels();
		registerEvents();
	}
	
	
	/**
	 * Registers the channels used by this plugin
	 */
	private void registerChannels() {
		this.getProxy().registerChannel("karanteeniperms:groups");
	}
	
	
	/**
	 * Registers the events used by this plugin
	 */
	private void registerEvents() {
		getProxy().getPluginManager().registerListener(this, new JoinBungeeEvent());
		getProxy().getPluginManager().registerListener(this, new GroupRequestListener());
	}
	
	
	/**
	 * Returns the active instance of the permission plugin
	 * @return active instance of the permission plugin
	 */
	public static KaranteeniPermsBungee getInstance() {
		return instance;
	}
	
	
	public GroupList getGroupList() {
		return this.groupList;
	}
	
	
	public PlayerModel getPlayerModel() {
		return this.playerModel;
	}
}
