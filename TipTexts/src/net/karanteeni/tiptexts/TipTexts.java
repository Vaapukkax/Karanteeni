package net.karanteeni.tiptexts;

import net.karanteeni.core.KaranteeniPlugin;

public class TipTexts extends KaranteeniPlugin{
	public static final String FORMAT = "format";
	public static final String TIPS = "tips";
	public static final String MESSAGE = "%msg%";
	private Displayer displayer;
	
	public TipTexts() {
		super(true);
	}

	
	@Override
	public void onEnable() {
		// initialize the texts
		getTranslator().registerTranslation(this, FORMAT, "§f[§aTIP§f] §e" + MESSAGE);
		getTranslator().registerRandomTranslation(this, TIPS, "Press §6space§e to jump");
		
		// initialize the limit in seconds
		if(!this.getConfig().isSet("limit-seconds")) {
			this.getConfig().set("limit-seconds", 900); // 15 minute interval
			this.saveConfig();
		}
		
		// create a new displayer
		displayer = new Displayer(this.getConfig().getInt("limit-seconds"), this);
		displayer.run();
	}
	
	
	@Override
	public void onDisable() {
		displayer.stop();
	}
}
