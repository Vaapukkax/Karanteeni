package net.karanteeni.tester;

import org.bukkit.Bukkit;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.tester.events.ChatEvent;

public class TesterMain extends KaranteeniPlugin{

	public TesterMain() {
		super(true);
	}

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new ChatEvent(), this);
		Bukkit.getConsoleSender().sendMessage("§aKaranteeniTester has been enabled!");
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage("§cKaranteeniTester has been disabled!");
	}
}
