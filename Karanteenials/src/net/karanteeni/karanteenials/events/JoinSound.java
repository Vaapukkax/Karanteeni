package net.karanteeni.karanteenials.events;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.karanteenials.Karanteenials;

public class JoinSound implements Listener {
	private Sound sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
	private float pitch = 1.7f;
	private float volume = 0.3f;
	private float duration = 1.8f;
	private Karanteenials plugin = null;
	
	public JoinSound(Karanteenials plugin) {
		this.plugin = plugin;
		
		if(!plugin.getConfig().isSet("joineffect.sound")) {
			plugin.getConfig().set("joineffect.sound", sound.toString());
			plugin.saveConfig();
		}
		
		try {
			sound = Sound.valueOf(plugin.getConfig().getString("joineffect.sound"));
		} catch(Exception e) {
			sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
		}
		
		if(!plugin.getConfig().isSet("joineffect.pitch")) {
			plugin.getConfig().set("joineffect.pitch", pitch);
			plugin.saveConfig();
		}
		
		pitch = (float)plugin.getConfig().getDouble("joineffect.pitch");
		
		if(!plugin.getConfig().isSet("joineffect.volume")) {
			plugin.getConfig().set("joineffect.volume", volume);
			plugin.saveConfig();
		}
		
		volume = (float)plugin.getConfig().getDouble("joineffect.volume");
		
		if(!plugin.getConfig().isSet("joineffect.titleduration")) {
			plugin.getConfig().set("joineffect.titleduration", duration);
			plugin.saveConfig();
		}
		
		duration = (float)plugin.getConfig().getDouble("joineffect.titleduration");
		
		Karanteenials.getTranslator().registerTranslation(plugin, "joineffect.title", "Welcome back");
		Karanteenials.getTranslator().registerTranslation(plugin, "joineffect.subtitle", "Long time no see %player%");	
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().playSound(event.getPlayer().getLocation(), sound, SoundCategory.PLAYERS, volume, pitch);
		Karanteenials.getMessager().sendTitle(0.3f, 0.3f, duration, event.getPlayer(), 
				Karanteenials.getTranslator().getTranslation(plugin, event.getPlayer(), "joineffect.title"), 
				Karanteenials.getTranslator().getTranslation(plugin, event.getPlayer(), "joineffect.subtitle")
				.replace("%player%", event.getPlayer().getName()), Sounds.NONE.get());
	}
}
