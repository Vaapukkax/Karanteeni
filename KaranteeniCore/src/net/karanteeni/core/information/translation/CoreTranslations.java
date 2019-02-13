package net.karanteeni.core.information.translation;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;

public class CoreTranslations {

	/**
	 * Registers core translations which are not otherwise able to register elsewhere
	 */
	public void registerCoreTranslations()
	{
		//Menutranslations
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory-back", "§cBack");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory-close", "§cClose");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "inventory-empty", "§7#");
		
		/*KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "year", "Y");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "month", "MON");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "week", "W");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "day", "D");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "hour", "H");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "minute", "MIN");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "second", "S");*/
	}
}
