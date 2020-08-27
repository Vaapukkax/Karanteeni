package net.karanteeni.migrate;

import net.karanteeni.core.KaranteeniPlugin;

public class TesterMain extends KaranteeniPlugin {
	
	public TesterMain() {
		super(true);
	}

	@Override
	public void onEnable() {
		MigrateCMD c = new MigrateCMD();
		c.register();
	}
	
	@Override
	public void onDisable() {
		
	}
}
