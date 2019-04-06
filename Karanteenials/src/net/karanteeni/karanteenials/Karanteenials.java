package net.karanteeni.karanteenials;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.karanteenials.player.home.DelHomeCommand;
import net.karanteeni.karanteenials.player.home.HomeCommand;
import net.karanteeni.karanteenials.player.home.ListHomesCommand;
import net.karanteeni.karanteenials.player.home.SetHomeCommand;
import net.karanteeni.karanteenials.teleport.Back;
import net.karanteeni.karanteenials.teleport.TeleportAccept;
import net.karanteeni.karanteenials.teleport.TeleportDeny;
import net.karanteeni.karanteenials.teleport.TeleportToggle;
import net.karanteeni.karanteenials.teleport.TpAsk;
import net.karanteeni.karanteenials.teleport.TpAskHere;

public class Karanteenials extends KaranteeniPlugin
{
	PlayerFunctionality data;
	
	public Karanteenials() {
		super(true);
	}
	
	@Override
	public void onEnable()
	{
		data = new PlayerFunctionality(this);
		
		registerEvents();
		registerCommands();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	private void registerEvents()
	{
		
	}
	
	private void registerCommands()
	{
		(new HomeCommand(this)).register();
		(new SetHomeCommand(this)).register();
		(new DelHomeCommand(this)).register();
		(new ListHomesCommand(this)).register();
		(new Back(this)).register();
		(new TeleportToggle(this)).register();
		
		(new TpAsk(this)).register();
		(new TpAskHere(this)).register();
		(new TeleportAccept(this)).register();
		(new TeleportDeny(this)).register();
	}
	
	/**
	 * Returns the data manager of this plugin
	 * @return data manager of this plugin
	 */
	public PlayerFunctionality getPlayerData()
	{ return data; }
}