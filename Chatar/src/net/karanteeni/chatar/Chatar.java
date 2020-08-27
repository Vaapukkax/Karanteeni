package net.karanteeni.chatar;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.command.broadcast.BroadcastCommand;
import net.karanteeni.chatar.command.ignore.Ignore;
import net.karanteeni.chatar.command.ignore.IgnoreAddComponent;
import net.karanteeni.chatar.command.ignore.IgnoreData;
import net.karanteeni.chatar.command.ignore.IgnoreListComponent;
import net.karanteeni.chatar.command.ignore.IgnoreRemoveComponent;
import net.karanteeni.chatar.command.message.Message;
import net.karanteeni.chatar.command.message.Reply;
import net.karanteeni.chatar.command.message.SocialSpy;
import net.karanteeni.chatar.component.ChatComponent;
import net.karanteeni.chatar.component.click.ClickTexts;
import net.karanteeni.chatar.component.hover.ComponentDisplayName;
import net.karanteeni.chatar.component.hover.ComponentName;
import net.karanteeni.chatar.component.hover.HoverTexts;
import net.karanteeni.chatar.component.permissioncomponents.LocalGroupName;
import net.karanteeni.chatar.component.permissioncomponents.LocalGroupPrefix;
import net.karanteeni.chatar.component.permissioncomponents.LocalGroupShortName;
import net.karanteeni.chatar.component.permissioncomponents.LocalGroupSuffix;
import net.karanteeni.chatar.component.preset.PlayerComponent;
import net.karanteeni.chatar.events.JoinQuitMessages;
import net.karanteeni.chatar.events.custom.PlayerChatEvent;
import net.karanteeni.chatar.events.custom.PlayerMessageEvent;
import net.karanteeni.chatar.events.custom.implementing.RemoveIgnoringRecipients;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.TextComponent;

public class Chatar extends KaranteeniPlugin {
	private final HashMap<String, ChatComponent> chatComponents = new HashMap<String, ChatComponent>();
	private final IgnoreData ignoreData = new IgnoreData();
	private final HoverTexts hoverTexts = new HoverTexts();
	private final ClickTexts clickTexts = new ClickTexts();
	private SocialSpy socialSpy;
	private PermissionChecker pChecker;
	private final String FORMAT_SUBPATH = "FORMAT.";
	private static final 	Pattern TAG_PATTERN = Pattern.compile("\\%[^%]+\\%");
	
	public Chatar() {
		super(true);
	}
	
	
	@Override
	public void onLoad() {
		ignoreData.initializeTable();
	}
	
	
	@Override
	public void onEnable() {
		// create a new permission checker if KaranteeniPerms plugin has been loaded
		if(getServer().getPluginManager().isPluginEnabled("KaranteeniPerms"))
			pChecker = new PermissionChecker();
		
		socialSpy = new SocialSpy();
		Bukkit.getLogger().log(Level.WARNING, "This is a stabilized build, please finalize the plugin soon");
		//registerChannels();
		registerEvents();
		registerCommands();
		registerTags();
		registerComponents();
	}
	
	
	private void registerTags() {
		ComponentName cn = new ComponentName();
		clickTexts.registerClick("player-name", cn);
		hoverTexts.registerHover("player-name", cn);
		
		ComponentDisplayName cdn = new ComponentDisplayName();
		clickTexts.registerClick("player-dname", cdn);
		hoverTexts.registerHover("player-dname", cdn);
		
		// register permission plugin tags if the plugin is enabled
		if(pChecker != null && pChecker.getPlugin() != null) {
			LocalGroupPrefix lgc = new LocalGroupPrefix(pChecker.getPlugin(), "group-local-prefix");
			this.registerComponent(lgc, "%group-local-prefix%", null);
			this.getHoverTexts().registerHover("group-local-prefix", lgc);
			
			LocalGroupSuffix lgs = new LocalGroupSuffix(pChecker.getPlugin(), "group-local-suffix");
			this.registerComponent(lgs, "%group-local-suffix%", null);
			this.getHoverTexts().registerHover("group-local-suffix", lgs);
			
			LocalGroupName lgn = new LocalGroupName(pChecker.getPlugin(), "group-local-name");
			this.registerComponent(lgn, "%group-local-name%", null);
			this.getHoverTexts().registerHover("group-local-name", lgn);
			
			LocalGroupShortName lgsn = new LocalGroupShortName(pChecker.getPlugin(), "group-local-sname");
			this.registerComponent(lgsn, "%group-local-sname%", null);
			this.getHoverTexts().registerHover("group-local-sname", lgsn);
		}
	}
	
	
	/**
	 * Registers bungeecord channels used
	 */
	private void registerChannels() {
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}
	
	
	/**
	 * Registers default chat components
	 */
	private void registerComponents() {
		this.registerComponent(new PlayerComponent("player"), "%player-dname%", "/message %player-name%");
		this.registerComponent(new PlayerComponent("playername"), "%player-name%", "/message %player-name%");
	}
	
	
	@Override
	public void onDisable() { }
	
	
	/**
	 * Returns the permissionChecker for KaranteeniPerms
	 * @return
	 */
	public PermissionChecker getPermissionChecker() {
		return this.pChecker;
	}
	
	
	private void registerEvents() {
		PlayerChatEvent.register(this);
		PlayerMessageEvent.register(this);
		this.getServer().getPluginManager().registerEvents(new JoinQuitMessages(this), this);
		this.getServer().getPluginManager().registerEvents(new RemoveIgnoringRecipients(this), this);
	}
	
	
	private void registerCommands() {
		// build the /message command
		PlayerLoader pc = new PlayerLoader(true, true, true, true);
		Message message = new Message();
		message.setPermission("chatar.message.use");
		message.setLoader(pc);
		message.register();
		
		// /reply command
		Reply reply = new Reply();
		reply.setPermission("chatar.reply");
		reply.register();
		
		// /broadcast command
		BroadcastCommand bc = new BroadcastCommand();
		bc.setPermission("chatar.broadcast");
		bc.register();
		
		// /ignore command
		Ignore ignore = new Ignore(this);
		IgnoreAddComponent ignoreAdd = new IgnoreAddComponent();
		ignore.addComponent("add", ignoreAdd);
		IgnoreRemoveComponent ignoreRemove = new IgnoreRemoveComponent();
		ignore.addComponent("remove", ignoreRemove);
		IgnoreListComponent ignoreList = new IgnoreListComponent();
		ignore.addComponent("list", ignoreList);
		PlayerLoader ignoreLoader = new PlayerLoader(true, true, true, true, true);
		ignoreAdd.setLoader(ignoreLoader);
		ignoreRemove.setLoader(ignoreLoader);
		ignore.setPermission("chatar.ignore.use");
		ignore.register();
	}
	
	
	/**
	 * Returns the socialspy class
	 * @return socialspy manager class
	 */
	public SocialSpy getSocialSpy() { return this.socialSpy; }
	
	
	/**
	 * Returns the hover texts of this plugin
	 * @return
	 */
	public HoverTexts getHoverTexts() { return this.hoverTexts; }

	
	/**
	 * Returns the hover texts of this plugin
	 * @return
	 */
	public ClickTexts getClickTexts() { return this.clickTexts; }
	
	
	/**
	 * Registers a new chatcomponent for usage
	 * @param key
	 * @param component
	 */
	public void registerComponent(ChatComponent component, String defaultHover, String defaultClick) {
		// register component to config
		component.register(defaultHover, defaultClick);
		
		chatComponents.put(component.getKey(), component);
	}
	
	
	/**
	 * Registers a message format to be used
	 * @param key key to store the format with
	 * @param format format to store
	 */
	public void registerFormat(String key, String format) {
		if(!getSettings().isSet(FORMAT_SUBPATH + key)) {
			getSettings().set(FORMAT_SUBPATH + key, format);
			saveSettings();
		}
	}
	
	
	/**
	 * Get the players ignoredata
	 * @return data of ignore states
	 */
	public IgnoreData getIgnoreData() {
		return this.ignoreData;
	}
	
	
	/**
	 * Returns the format of a message without any modifications
	 * @param key the key which with the format was stored
	 * @return the format in the stored way
	 */
	public String getRawFormat(String key) {
		if(!getSettings().isSet(FORMAT_SUBPATH + key)) return null;
		return getSettings().getString(FORMAT_SUBPATH + key);
	}
	
	
	/**
	 * Returns the fully formatted message
	 * @param format the format in which the message needs to be formatted
	 * @param sender sender of the message
	 * @param recipients message recipients
	 * @return map in which there's a unique response to each recipient
	 */
	public HashMap<Player, BaseComponent> getFormattedMessage(
			String format, 
			Player sender, 
			HashMap<Player, BaseComponent> recipients) {
		// map to store the messages within
		HashMap<Player, ComponentBuilder> components = new HashMap<Player, ComponentBuilder>();
		
		// add all players to the components map along with a base for string addition
		for(Player player : recipients.keySet())
			components.put(player, new ComponentBuilder(""));
		
		// generate and loop all keywords from format
		Matcher m = TAG_PATTERN.matcher(format);
		//boolean matchFound = false;
		int lastMatchPos = 0;
		while(m.find()) {
			//matchFound = true;
			String group = m.group();
			// cut the % % away
			group = group.substring(1, group.length()-1);
			
			// get the string in between this and previous match
			String notMatchPart = format.substring(lastMatchPos, m.start());
			
			// if the group is %msg% just replace it with message and don't care more
			HashMap<Player, BaseComponent> formattedComponents = null;
			// get and format the components with the keyword in the config
			ChatComponent component = getComponent(group);
			if(component != null)
				formattedComponents = component.asBaseComponent(sender, recipients.keySet());
			
			// add the results to the messages
			for(Player player : recipients.keySet()) {
				ComponentBuilder pc = components.get(player);
				// add the string before the keyword to the result
				pc.append(notMatchPart, FormatRetention.FORMATTING);
				
				// add the formatted chatcomponent to the message to be sent
				if(formattedComponents != null)
					pc.append(formattedComponents.get(player), FormatRetention.FORMATTING);
				else {
					// if no formatted component was found, check if this is the msg tag
					if(!group.equals("msg")) // if not, add the tag name as it was mentioned
						pc.append(m.group(), FormatRetention.FORMATTING);
					else // was the message tag, set either the message
						pc.append(recipients.get(player));
				}
			}
			
			// update the position of the latest match position
			lastMatchPos = m.end();
		}
		
		// add the results to the messages
		if(lastMatchPos < format.length())
		for(Player player : recipients.keySet()) {
			ComponentBuilder pc = components.get(player);
			// add the remaining string
			pc.append(format.substring(lastMatchPos));
		}
		
		// if no matches, just put the format there
		/*if(!matchFound) {
			// TODO
		}*/
		
		HashMap<Player, BaseComponent> results = new HashMap<Player, BaseComponent>();
		for(Entry<Player, ComponentBuilder> entry : components.entrySet())
			results.put(entry.getKey(), new TextComponent(entry.getValue().create()));
		
		return results;
	}

	
	/**
	 * Fills a given map of receivers with given message
	 * @param message message to flood the players with
	 * @param receivers receivers of the message
	 * @return map filled with players and message
	 */
	public HashMap<Player, BaseComponent> fillMap(BaseComponent message, Set<Player> receivers) {
		HashMap<Player, BaseComponent> map = new HashMap<Player, BaseComponent>();
		for(Player player : receivers)
			map.put(player, message);
		return map;
	}
	
	
	/**
	 * Returns the chatcomponent associated with this key or null if no chatcomponent is found
	 * @param key key to search the component with
	 * @return found chatcomponent or null if none found
	 */
	public ChatComponent getComponent(String key) { return chatComponents.get(key); }
	
	
	/**
	 * Removes a given chatComponent from memory
	 * @param component
	 * @return
	 */
	public ChatComponent removeComponent(ChatComponent component) { return chatComponents.remove(component.getKey()); }
}