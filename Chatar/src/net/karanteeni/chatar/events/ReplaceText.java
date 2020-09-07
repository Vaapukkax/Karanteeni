package net.karanteeni.chatar.events;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.karanteeni.chatar.Chatar;

public class ReplaceText implements Listener {
	private static final String PATH = "text-replace";
	private HashMap<String, String> textLibrary = new HashMap<String, String>();
	
	
	public ReplaceText(Chatar plugin) {
		if(!plugin.getSettings().isSet(PATH)) {
			plugin.getSettings().set(getPath("example"), "elpmaxe");
			plugin.saveSettings();
		}
		
		for(final String key : plugin.getSettings().getConfigurationSection(PATH).getKeys(false)) {
			textLibrary.put(key.toLowerCase(), plugin.getSettings().getString(getPath(key)));
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.isCancelled())
			return;
		
		StringBuffer text = new StringBuffer(event.getMessage());
		StringBuffer lowercaseMessage = new StringBuffer(event.getMessage().toLowerCase());
		for(Entry<String, String> pair : textLibrary.entrySet()) {
			int i = 0;

			while((i = lowercaseMessage.indexOf(pair.getKey(), i)) != -1) {
				text.replace(i, i + pair.getKey().length(), pair.getValue());
				lowercaseMessage.replace(i, i + pair.getKey().length(), pair.getValue());
				i += pair.getValue().length();
			}
		}
		event.setMessage(text.toString());
	}
	
	
	private String getPath(final String path) {
		return PATH + "." + path;
	}
}
