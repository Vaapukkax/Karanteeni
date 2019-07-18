package net.karanteeni.chatar.component.click;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.karanteeni.chatar.Chatar;

public class ClickTexts {
	// key value pairs of hover texts to use in ChatComponents
	private final HashMap<String, ClickComponent> clickTexts = new HashMap<String, ClickComponent>();
	
	/**
	 * Removes a given hover component from map
	 * @param key key used to remove the component
	 * @return removed component or null
	 */
	public ClickComponent removeClick(String key) {
		return clickTexts.remove(key);
	}
	
	
	/**
	 * Adds a new hover component to hovertexts
	 * @param key key to access the component with
	 * @param hoverComponent component to add
	 * @return true if addition successful, false if key already exists
	 */
	public boolean registerClick(String key, ClickComponent hoverComponent) {
		if(clickTexts.containsKey(key)) return false;
		
		// create config section if it does not exist
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		if(!chatar.getSettings().isSet("USABLE_CLICK_TAGS")) {
			chatar.getSettings().set("USABLE_CLICK_TAGS", new ArrayList<String>());
			chatar.saveSettings();
		}
		
		// add the tag if it does not contain the given tag
		List<String> clickTags = chatar.getSettings().getStringList("USABLE_CLICK_TAGS");
		if(!clickTags.contains(key)) {
			clickTags.add(key);
			chatar.getSettings().set("USABLE_CLICK_TAGS", clickTags);
			chatar.saveSettings();
		}
		
		
		// put to the map
		clickTexts.put(key, hoverComponent);
		return true;
	}
	
	
	/**
	 * Returns a registered hover component
	 * @param key
	 * @return
	 */
	public ClickComponent getClick(String key) {
		return clickTexts.get(key);
	}
}
