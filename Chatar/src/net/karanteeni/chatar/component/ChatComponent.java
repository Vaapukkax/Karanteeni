package net.karanteeni.chatar.component;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.chatar.component.click.ClickComponent;
import net.karanteeni.chatar.component.hover.HoverComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class ChatComponent {
	private String key;
	private String format; // format in which hovertexts are got from texts %hover%
	private String clickFormat;
	private ClickEvent.Action clickAction;
	private ClickEvent clickEvent;
	private HoverEvent hoverEvent;
	private static final String COMPONENT_SUBPATH = "COMPONENT.";
	private static final Pattern TAG_PATTERN = Pattern.compile("\\%[^%]+\\%");
	
	
	public ChatComponent(String key) { 
		this.key = key;
		format = loadHoverFormat();
		clickFormat = loadClickFormat();
		clickAction = loadClickAction();
	}
	
	
	public ChatComponent(String key, ClickEvent clickEvent) { 
		this.key = key;
		this.clickEvent = clickEvent;
		format = loadHoverFormat();
		clickFormat = loadClickFormat();
		clickAction = loadClickAction();
	}
	
	
	public ChatComponent(String key, HoverEvent hoverEvent) { 
		this.key = key;
		this.hoverEvent = hoverEvent;
		format = loadHoverFormat();
		clickFormat = loadClickFormat();
		clickAction = loadClickAction();
	}
	
	
	public ChatComponent(String key, ClickEvent clickEvent, HoverEvent hoverEvent) {
		this.key = key;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
		format = loadHoverFormat();
		clickFormat = loadClickFormat();
		clickAction = loadClickAction();
	}
	
	
	/**
	 * Registers this chat component to the Settings.yml file
	 * @param defaultHover default text to show with hover
	 * @param defaultClick default text to suggest with clickEvent if one is set
	 */
	public final void register(String defaultHover, String defaultClick) {
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		
		if(!chatar.getSettings().isSet(COMPONENT_SUBPATH + key + ".HOVER")) {
			if(defaultHover != null) {
				chatar.getSettings().set(COMPONENT_SUBPATH + key + ".HOVER", defaultHover);
				format = defaultHover;
			} else {
				chatar.getSettings().set(COMPONENT_SUBPATH + key + ".HOVER", "§dDefault\n§6Hover");
				format = "§dDefault\n§6Hover";
			}
			chatar.saveSettings();
		}
		
		if(!chatar.getSettings().isSet(COMPONENT_SUBPATH + key + ".CLICK.TYPE") || !chatar.getSettings().isSet(COMPONENT_SUBPATH + key + ".CLICK.INPUT")) {
			if(clickEvent != null) {
				chatar.getSettings().set(COMPONENT_SUBPATH + key + ".CLICK.TYPE", clickEvent.getAction().name());
				chatar.getSettings().set(COMPONENT_SUBPATH + key + ".CLICK.INPUT", defaultClick);
				clickFormat = defaultClick;
			} else {
				chatar.getSettings().set(COMPONENT_SUBPATH + key + ".CLICK.TYPE", "NONE");
				chatar.getSettings().set(COMPONENT_SUBPATH + key + ".CLICK.INPUT", "Default Click");
				clickFormat = "Default Click";
			}
			
			chatar.saveSettings();
		}
	}
	
	
	/**
	 * Loads the format to use with hover texts
	 * @return hover text format
	 */
	private final String loadHoverFormat() {
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		return chatar.getSettings().getString(COMPONENT_SUBPATH + key + ".HOVER");
	}
	
	
	/**
	 * Loads the format to use with hover texts
	 * @return hover text format
	 */
	private final String loadClickFormat() {
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		return chatar.getSettings().getString(COMPONENT_SUBPATH + key + ".CLICK.INPUT");
	}
	
	
	/**
	 * Loads the format to use with hover texts
	 * @return hover text format
	 */
	private final ClickEvent.Action loadClickAction() {
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		String type = chatar.getSettings().getString(COMPONENT_SUBPATH + key + ".CLICK.TYPE");
		// if type is NONE, don't use any click input
		if(type == null || type.equals("NONE"))
			return null;
		
		ClickEvent.Action action = ClickEvent.Action.SUGGEST_COMMAND;
		try {
			action = ClickEvent.Action.valueOf(type); 
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.CONFIG, "Invalid COMPONENT.-.CLICK.TYPE. Please use one of the following: NONE "
					+ ClickEvent.Action.CHANGE_PAGE.name() + " " + ClickEvent.Action.OPEN_FILE.name() + " " + 
					ClickEvent.Action.OPEN_URL.name() + " " + ClickEvent.Action.RUN_COMMAND.name() +
					" " + ClickEvent.Action.SUGGEST_COMMAND.name(), e);
		}
		
		return action;
	}
	
	
	/**
	 * Sets the clickEvent
	 * @param clickEvent
	 */
	public final void setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
	}
	
	
	public final ClickEvent getClickEvent() {
		return this.clickEvent;
	}
	
	
	/**
	 * Returns the key of this component
	 * @return
	 */
	public final String getKey() {
		return this.key;
	}
	
	
	/**
	 * Returns the main text to show in chat
	 * @return text to show in chat
	 */
	public abstract HashMap<Player, TextComponent> getChatText(Player sender, Set<Player> receivers);
	
	
	/**
	 * Returns the text to put into the text field of component
	 * @return
	 */
	public HashMap<Player, BaseComponent> asBaseComponent(Player sender, Set<Player> receivers) {
		// text to show in chat
		HashMap<Player, TextComponent> chatText = getChatText(sender, receivers);
		
		// hover text to add if hoverEvent is not null
		HashMap<Player, BaseComponent> hover = null;
		if(hoverEvent == null)
			hover = createHoverText(sender, receivers);
		
		HashMap<Player, ClickEvent> clicks = null;
		if(clickEvent == null && clickAction != null)
			clicks = createClickEvent(sender, receivers);
		
		// create a new base for each message
		for(Entry<Player, TextComponent> entry : chatText.entrySet()) {
			// add hover event
			if(hoverEvent != null && entry.getValue().getHoverEvent() == null) {
				entry.getValue().setHoverEvent(hoverEvent);
			} else if (hoverEvent == null && entry.getValue().getHoverEvent() == null) {
				entry.getValue().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hover.get(entry.getKey())}));
			}
			
			// add click event
			if(clickEvent != null && entry.getValue().getClickEvent() == null) { // if event is given use it instead
				entry.getValue().setClickEvent(clickEvent);
			} else if (clickEvent == null && entry.getValue().getClickEvent() == null && clickAction != null) { // if no events given use config
				ClickEvent ce = clicks.get(entry.getKey());
				if(ce != null)
					entry.getValue().setClickEvent(ce);
			}
		}
		
		// create and return new map
		return new HashMap<Player, BaseComponent>(chatText);
	}

	
	protected final HashMap<Player, ClickEvent> createClickEvent(Player sender, Set<Player> receivers) {
		// create new component for all hover texts
		HashMap<Player, String> clickTexts = new HashMap<Player, String>();
		Matcher m = TAG_PATTERN.matcher(clickFormat);
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		
		int lastMatchPos = 0;
		boolean matchFound = false;

		// loop all matches in hover
		while(m.find()) {
			matchFound = true;
			String group = m.group();
			// cut the % % away
			group = group.substring(1, group.length()-1);
		
			// get the string in between this and previous match
			String notMatchPart = clickFormat.substring(lastMatchPos, m.start());
		
			// get the possible click component
			ClickComponent click = chatar.getClickTexts().getClick(group);
			HashMap<Player, String> clickComponents = null;

			// create nullCheck for hover
			if(click != null)
				clickComponents = click.getClick(sender, receivers);
			else {// if no component was found, add the found string
				clickComponents = new HashMap<Player, String>();
			
				// add the component to the message of each player
				for(Player player : receivers)
					clickComponents.put(player, m.group());
			}

			// add the component to each player
			for(Player player : receivers) {
				// create new list if one does not already exist
				if(!clickTexts.containsKey(player))
					clickTexts.put(player, "");
				
				// combine the click texts to one map
				clickTexts.put(player, clickTexts.get(player) + notMatchPart + clickComponents.get(player));
			}
			
			// set the index of last match
			lastMatchPos = m.end();
		}
		
		// if no matches found, just add the bare text base
		if(!matchFound)
		for(Player player : receivers)
			clickTexts.put(player, "");
		
		// add the rest of the click text
		// add the component to each player
		if(lastMatchPos != clickFormat.length()-1)
		for(Player player : receivers) {
			// add the remaining part of the string
			clickTexts.put(player, clickTexts.get(player) + clickFormat.substring(lastMatchPos));
		}
		
		// convert the texts to events
		HashMap<Player, ClickEvent> components = new HashMap<Player, ClickEvent>();
		for(Entry<Player, String> entry : clickTexts.entrySet())
			components.put(entry.getKey(), new ClickEvent(clickAction, entry.getValue()));
		
		// return the built hover component
		return components;
	}
	
	
	/**
	 * Implements the hover event to this component
	 * @param component
	 * @return
	 */
	protected final HashMap<Player, BaseComponent> createHoverText(Player sender, Set<Player> receivers) {
		// create new component for all hover texts
		HashMap<Player, BaseComponent> components = new HashMap<Player, BaseComponent>();
		Matcher m = TAG_PATTERN.matcher(format);
		Chatar chatar = Chatar.getPlugin(Chatar.class);
		
		int lastMatchPos = 0;
		boolean matchFound = false;
		// loop all matches in hover
		while(m.find()) {
			matchFound = true;
			String group = m.group();
			// cut the % % away
			group = group.substring(1, group.length()-1);

			// get the string in between this and previous match
			String notMatchPart = format.substring(lastMatchPos, m.start());
			
			// get the possible hover component
			HoverComponent hover = chatar.getHoverTexts().getHover(group);
			HashMap<Player, BaseComponent> hoverComponents = null;

			// create nullCheck for hover
			if(hover != null)
				hoverComponents = hover.getHover(sender, receivers);
			else {// if no component was found, add the found string
				hoverComponents = new HashMap<Player, BaseComponent>();
				BaseComponent component = buildComponent(m.group());
				
				// add the component to the message of each player
				for(Player player : receivers)
					hoverComponents.put(player, component);
			}
			
			// add the component to each player
			for(Player player : receivers) {
				// create new list if one does not already exist
				if(!components.containsKey(player)) {
					components.put(player, new TextComponent());
				}
				
				// add hover to the components list to return
				components.get(player).addExtra(buildComponent(notMatchPart));
				components.get(player).addExtra(hoverComponents.get(player));
			}
			
			// set the index of last match
			lastMatchPos = m.end();
		}
		
		// if no matches found, just add the bare text
		if(!matchFound)
		for(Player player : receivers)
			components.put(player, new TextComponent(""));
		
		// add the rest of the hover text
		// add the component to each player
		if(lastMatchPos != format.length()-1)
		for(Player player : receivers) {
			// add the remaining part of the string
			components.get(player).addExtra(buildComponent(format.substring(lastMatchPos)));
		}
		
		// return the built hover component
		return components;
	}
	
	
	/**
	 * Converts the given string to a basecomponent class
	 * @return
	 */
	public static final TextComponent buildComponent(String text) {
		return new TextComponent(TextComponent.fromLegacyText(text));
	}
}
