package net.karanteeni.chatar.component.hover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.karanteeni.chatar.Chatar;

public class HoverTexts {
	// key value pairs of hover texts to use in ChatComponents
	private final HashMap<String, HoverComponent> hoverTexts = new HashMap<String, HoverComponent>();
	
	/**
	 * Removes a given hover component from map
	 * @param key key used to remove the component
	 * @return removed component or null
	 */
	public HoverComponent removeHover(String key) {
		return hoverTexts.remove(key);
	}
	
	
	/**
	 * Adds a new hover component to hovertexts
	 * @param key key to access the component with
	 * @param hoverComponent component to add
	 * @return true if addition successful, false if key already exists
	 */
	public boolean registerHover(String key, HoverComponent hoverComponent) {
		if(hoverTexts.containsKey(key)) return false;
		
		// create config section if it does not exist
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		if(!chatar.getSettings().isSet("USABLE_HOVER_TAGS")) {
			chatar.getSettings().set("USABLE_HOVER_TAGS", new ArrayList<String>());
			chatar.saveSettings();
		}
		
		// add the tag if it does not contain the given tag
		List<String> hoverTags = chatar.getSettings().getStringList("USABLE_HOVER_TAGS");
		if(!hoverTags.contains(key)) {
			hoverTags.add(key);
			chatar.getSettings().set("USABLE_HOVER_TAGS", hoverTags);
			chatar.saveSettings();
		}
		
		
		// put to the map
		hoverTexts.put(key, hoverComponent);
		return true;
	}
	
	
	/**
	 * Returns a registered hover component
	 * @param key
	 * @return
	 */
	public HoverComponent getHover(String key) {
		return hoverTexts.get(key);
	}
}
