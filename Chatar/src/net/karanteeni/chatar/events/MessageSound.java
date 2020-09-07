package net.karanteeni.chatar.events;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.information.sounds.SoundType;

public class MessageSound implements Listener {
	private static final String SOUND_PATH = "text-sound.%s.sound";
	private static final String VOLUME_PATH = "text-sound.%s.volume";
	private static final String PITCH_PATH = "text-sound.%s.pitch";
	private HashMap<String, SoundType> soundLibrary = new HashMap<String, SoundType>();
	
	
	public MessageSound(Chatar plugin) {
		if(!plugin.getSettings().isSet("text-sound")) {
			plugin.getSettings().set(getSound("sound"), Sound.BLOCK_NOTE_BLOCK_BIT.toString());
			plugin.getSettings().set(getVolume("sound"), 1f);
			plugin.getSettings().set(getPitch("sound"), 1f);
			plugin.saveSettings();
		}
		
		for(String key : plugin.getSettings().getConfigurationSection("text-sound").getKeys(false)) {
			try {
				String soundText = plugin.getSettings().getString(getSound(key));
				float volume = (float)plugin.getSettings().getDouble(getVolume(key));
				float pitch = (float)plugin.getSettings().getDouble(getPitch(key));
				if(volume == Double.NaN || pitch == Double.NaN) {
					continue;
				}
				Sound sound = Sound.valueOf(soundText);
				soundLibrary.put(key.toLowerCase(), new SoundType(sound, volume, pitch));
			} catch (Exception e) {
				Bukkit.getLogger().log(Level.CONFIG, "Could not parse text replacer sound, the config may be incorrectly defined");
				e.printStackTrace();
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.isCancelled())
			return;
		
		String lowercaseText = event.getMessage().toLowerCase();
		for(Entry<String, SoundType> pair : soundLibrary.entrySet()) {
			if(lowercaseText.contains(pair.getKey())) {
				Chatar.getSoundHandler().playSound(event.getPlayer(), pair.getValue());
			}
		}
	}
	
	private String getSound(final String path) {
		return String.format(SOUND_PATH, path);
	}
	
	private String getVolume(final String path) {
		return String.format(VOLUME_PATH, path);
	}
	
	private String getPitch(final String path) {
		return String.format(PITCH_PATH, path);
	}
}
