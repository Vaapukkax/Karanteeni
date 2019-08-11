package net.karanteeni.tester;

import org.bukkit.Bukkit;

import net.karanteeni.core.KaranteeniPlugin;

public class TesterMain extends KaranteeniPlugin{
	private static Database db;
	public TesterMain() {
		super(true);
	}

	@Override
	public void onEnable() {
		db = new SQLite(this);
		db.load();
		db.load2();
		db.load3();
		
		getServer().getPluginManager().registerEvents(new ProtectionEvents(), this);
		
		ProtectionsCMD pcmd = new ProtectionsCMD();
    	this.getCommand("avaa").setExecutor(pcmd);
    	this.getCommand("lukitse").setExecutor(pcmd);
    	this.getCommand("+oikeudet").setExecutor(pcmd);
    	this.getCommand("-oikeudet").setExecutor(pcmd);
    	this.getCommand("avaakaikille").setExecutor(pcmd);
    	this.getCommand("suojaus").setExecutor(pcmd);
    	
		this.getCommand("trust").setExecutor(new Trust());
    	this.getCommand("untrust").setExecutor(new Untrust());
	}
	
	public static Database getDb() {
		return db;
	}
	
	@Override
	public void onDisable() {
		
	}
}
