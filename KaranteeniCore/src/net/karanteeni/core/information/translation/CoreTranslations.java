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
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.return", "§cReturn");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.close", "§4Close");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.accept", "§aAccept");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.empty", "§7");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.next_page", "§aNext Page");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.previous_page", "§aPrevious Page");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.no_permission", "§7No permission");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "INVENTORY.accept-not-ready", "§cPlease finish action");
		
		/*KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "year", "Y");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "month", "MON");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "week", "W");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "day", "D");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "hour", "H");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "minute", "MIN");
		KaranteeniPlugin.getTranslator().registerTranslation(KaranteeniCore.getPlugin(KaranteeniCore.class), "second", "S");*/
	}
}
