package net.karanteeni.teleportal.warp;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class WarpsCommand extends AbstractCommand implements TranslationContainer {
	private final String LIST_COMPONENTS[];
	private final String LIST_FORMAT;
	private final String DESCRIPTION_COLOR;
	private final String NAME_COLOR;
	
	public WarpsCommand() {
		super(Teleportal.getPlugin(Teleportal.class), 
				"warps", 
				"/warps", 
				"Lists all warps the player has the permission to use", 
				Teleportal.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
		
		// register the list style
		if(!plugin.getConfig().isSet("warp.list.format")) {
			plugin.getConfig().set("warp.list.format", "==========[%warp%]==========\n%s\n==========[%warp%]==========");
			plugin.saveConfig();
		}
		// get and set the list style to be used in the warp list
		LIST_FORMAT = plugin.getConfig().getString("warp.list.format");
		LIST_COMPONENTS = LIST_FORMAT.split("\\%s");
		
		// register the list color
		if(!plugin.getConfig().isSet("warp.list.name-color")) {
			plugin.getConfig().set("warp.list.name-color", "§e");
			plugin.saveConfig();
		}
		NAME_COLOR = plugin.getConfig().getString("warp.list.name-color");
	
		if(!plugin.getConfig().isSet("warp.list.description-color")) {
			plugin.getConfig().set("warp.list.description-color", "§6");
			plugin.saveConfig();
		}
		DESCRIPTION_COLOR = plugin.getConfig().getString("warp.list.description-color");
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(plugin, "warp.multiple", "Warps");
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// check permission
		if(!sender.hasPermission("teleporta.warp.use") && !sender.hasPermission("teleporta.warp.send")) {
			Teleportal.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Teleportal.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		BaseComponent warpList = createWarpList(Warp.getWarps(sender));
		
		// empty textcomponent to which all parts will be added
		TextComponent main = new TextComponent();
		
		String listStyle = LIST_FORMAT;
		int counter = 0;
		
		// until the style has been completed
		while(!listStyle.equals("")) {
			// check if we're in %s or format part
			if(listStyle.startsWith("%s")) {
				main.addExtra(warpList);
				listStyle = listStyle.substring(2);
			} else {
				main.addExtra(
						ArrayFormat.combine(TextComponent.fromLegacyText(LIST_COMPONENTS[counter].replace(
						"%warp%", 
						Teleportal.getTranslator().getTranslation(plugin, sender, "warp.multiple")))));
				
				listStyle = listStyle.substring(LIST_COMPONENTS[counter].length()); // replace the first chunk of the style
				
				++counter; // increase the counter
			}
		}
		
		//Teleportal.getMessager().sendMessage(sender, Sounds.PLING_HIGH.get(), warpList);
		// send the usable warp list to player
		Teleportal.getMessager().sendMessage(sender, Sounds.PLING_HIGH.get(), main);
		
		return true;
	}
	
	
	/**
	 * Creates a list of warps from given warps
	 * @param warps warps to be formed into a list
	 * @return list of warps in hoverable text format
	 */
	private BaseComponent createWarpList(List<Warp> warps) {
		// emty main text component
		//TextComponent main = new TextComponent();
		//TextComponent divider = new TextComponent(", ");
		//boolean firstAdded = false;
		List<BaseComponent> components = new ArrayList<BaseComponent>();
		
		
		// loop all warps
		for(Warp warp : warps) {
			BaseComponent warpComponent = ArrayFormat.combine(TextComponent.fromLegacyText(warp.getDisplayName()));
			
			// set hover event to warp component
			warpComponent.setHoverEvent(new HoverEvent( 
					HoverEvent.Action.SHOW_TEXT, 
							TextComponent.fromLegacyText(
							NAME_COLOR + warp.getName() + 
							((warp.getDescription() != null && warp.getDescription().length() > 0)? 
							("\n" + DESCRIPTION_COLOR + warp.getDescription()):""))));

			// set click event to warp component
			warpComponent.setClickEvent(new ClickEvent( 
					ClickEvent.Action.RUN_COMMAND, 
					"/warp " + warp.getName() ));
			
			components.add(warpComponent);
			// add divider to the main component
			/*if(firstAdded)
				main.addExtra(divider);
			
			// add the warp itself to the list
			main.addExtra(warpComponent);
			firstAdded = true;*/
		}
		
		return ArrayFormat.join(components, ", ");
		//return main;
	}
}
