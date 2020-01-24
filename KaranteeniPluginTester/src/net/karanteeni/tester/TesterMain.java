package net.karanteeni.tester;

import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;

public class TesterMain extends KaranteeniPlugin{
	
	public TesterMain() {
		super(true);
		super.getActionBlockManager().registerClass(TestBlock.class);
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginCommand("createaction").setExecutor(new CreateBlock());
	}
	
	
	@Override
	public void onDisable() {
		
	}
}
