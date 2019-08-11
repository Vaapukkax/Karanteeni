package net.karanteeni.karanteenials.player.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;
import net.md_5.bungee.api.ChatColor;

public class NickLimitLength implements Listener, TranslationContainer {
	private int maxLength;
	private int minLength;
	
	public NickLimitLength(Karanteenials plugin) {
		// set save and load nick max length
		if(!plugin.getConfig().isSet("nick.max-length")) {
			plugin.getConfig().set("nick.max-length", 16);
			plugin.saveConfig();
		}
		this.maxLength = plugin.getConfig().getInt("nick.max-length");
		
		// set save and load nick min length
		if(!plugin.getConfig().isSet("nick.min-length")) {
			plugin.getConfig().set("nick.min-length", 16);
			plugin.saveConfig();
		}
		this.minLength = plugin.getConfig().getInt("nick.min-length");
		
		registerTranslations();
	}
	
	
	@EventHandler
	public void nickSet(NickSetEvent event) {
		if(ChatColor.stripColor(event.getNick()).length() > maxLength) { // check if nick is too long
			event.setCancelled(true);
			
			Karanteenials.getMessager().sendMessage(event.getSetter(), 
					Sounds.NO.get(), 
					Prefix.NEGATIVE +
					Karanteenials.getTranslator().getTranslation(Karanteenials.getPlugin(Karanteenials.class), 
							event.getSetter(), "nick.too-long"));
		} else if(ChatColor.stripColor(event.getNick()).length() < minLength) { // check if nick is too short
			event.setCancelled(true);
			
			Karanteenials.getMessager().sendMessage(event.getSetter(), 
					Sounds.NO.get(), 
					Prefix.NEGATIVE +
					Karanteenials.getTranslator().getTranslation(Karanteenials.getPlugin(Karanteenials.class), 
							event.getSetter(), "nick.too-short"));
		}
	}

	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(Karanteenials.getPlugin(Karanteenials.class), 
				"nick.too-short", "The nick you set is too short");
		Karanteenials.getTranslator().registerTranslation(Karanteenials.getPlugin(Karanteenials.class), 
				"nick.too-long", "The nick you set is too long");
	}
}
