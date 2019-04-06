package net.karanteeni.karanteenials.player.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ListHomesCommand extends AbstractCommand{

	public ListHomesCommand(KaranteeniPlugin plugin) {
		super(plugin, "homes", "/homes", "Show all the homes", KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
		
		if(!plugin.getConfig().isSet("Home.format.row.top")) {
			plugin.getConfig().set("Home.format.row.top", "===========[ "+HomeCommand.HOME_TAG+" ]==========");
			plugin.saveConfig();
		}
		topRow = plugin.getConfig().getString("Home.format.row.top");
		
		if(!plugin.getConfig().isSet("Home.format.row.bottom")) {
			plugin.getConfig().set("Home.format.row.bottom", "===========[ "+HomeCommand.HOME_TAG+" ]==========");
			plugin.saveConfig();
		}
		bottomRow = plugin.getConfig().getString("Home.format.row.bottom");
		
		if(!plugin.getConfig().isSet("Home.format.row.colors")) {
			plugin.getConfig().set("Home.format.row.colors", Arrays.asList('e','a'));
			plugin.saveConfig();
		}
		rowFormat = plugin.getConfig().getCharacterList("Home.format.row.colors");
	}
	
	String topRow;
	List<Character> rowFormat;
	String bottomRow;

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(args.length == 0) //Show own homes
		{
			if(!sender.hasPermission("karanteenials.home.own.use")) {
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			if(!(sender instanceof Player))
			{
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().defaultNotForConsole());
				return true;
			}
				
			Player player = (Player)sender;	
			List<Home> homes = Home.getHomes(player.getUniqueId());
			
			//Create clickable list of homes
			TextComponent component = null;
			ListIterator<Home> iter = homes.listIterator();
			
			//Home home = iter.next();iter.hasNext();home = iter.next()
			while(iter.hasNext())
			{
				Home home = iter.next();
				
				//Create colorized
				BaseComponent[] comp = TextComponent.fromLegacyText(home.getName());
				if(comp.length == 0) continue;
				//Set the color of this home
				comp[0].setColor(ChatColor.getByChar(rowFormat.get(iter.nextIndex()%rowFormat.size())));
				comp[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
				
				if(component == null)
					component = new TextComponent(comp[0]);
				else
					component.addExtra(comp[0]);
				if(iter.hasNext())
					component.addExtra("§r, §r");
			}
			
			//Top row
			KaranteeniPlugin.getMessager().sendMessage(player,
				Sounds.NONE.get(),
				topRow.replace(HomeCommand.HOME_TAG, 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOMES)));
			if(component != null)
				KaranteeniPlugin.getMessager().sendMessage(player, component); //Data
			else
				KaranteeniPlugin.getMessager().sendMessage(player, 
						Sounds.NONE.get(),
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.NO_HOMES));
			//Bottom row
			KaranteeniPlugin.getMessager().sendMessage(player,
				Sounds.NONE.get(),
				bottomRow.replace(HomeCommand.HOME_TAG, 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOMES)));
		}
		else if(args.length == 1) //Show homes of another player
		{
			if(!sender.hasPermission("karanteenials.home.other.use")) {
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
				return true;
			}
			
			UUID uuid = KaranteeniPlugin.getPlayerHandler().getUUID(args[0]);
			
			if(uuid == null) {
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().playerNotFound(sender, args[0]));
				return true;
			}
			
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				List<Home> homes = Home.getHomes(uuid);
				
				//Create clickable list of homes
				TextComponent component = null;
				ListIterator<Home> iter = homes.listIterator();
				
				while(iter.hasNext())
				{
					Home home = iter.next();
					//Create colorized
					BaseComponent[] comp = TextComponent.fromLegacyText(home.getName());
					if(comp.length == 0) continue;
					//Set the color of this home
					comp[0].setColor(ChatColor.getByChar(rowFormat.get(iter.nextIndex()%rowFormat.size())));
					comp[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " +args[0]+ " "+ home.getName()));
					
					if(component == null)
						component = new TextComponent(comp[0]);
					else
						component.addExtra(comp[0]);
					
					if(iter.hasNext())
						component.addExtra("§r, §r");
				}
				
				//Top row
				KaranteeniPlugin.getMessager().sendMessage(player,
					Sounds.NONE.get(),
					topRow.replace(HomeCommand.HOME_TAG, 
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOMES_OF)
						.replace(HomeCommand.PLAYER_TAG, args[0])));
				if(component != null)
					KaranteeniPlugin.getMessager().sendMessage(player, component); //Data
				else
					KaranteeniPlugin.getMessager().sendMessage(player, 
							Sounds.NONE.get(),
							Prefix.NEGATIVE+
							KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.NO_HOMES));
				//Bottom row
				KaranteeniPlugin.getMessager().sendMessage(player,
					Sounds.NONE.get(),
					bottomRow.replace(HomeCommand.HOME_TAG, 
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOMES_OF)
						.replace(HomeCommand.PLAYER_TAG, args[0])));
			}
			else
			{
				//Get all homes
				List<Home> homes = Home.getHomes(uuid);
				List<String> homeList = new ArrayList<String>();
				for(Home home : homes)
					homeList.add(home.getName());
				String home = String.join(", ", homeList);
				
				//Top row
				KaranteeniPlugin.getMessager().sendMessage(sender,
					Sounds.NONE.get(),
					topRow.replace(HomeCommand.HOME_TAG, 
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOMES_OF)
						.replace(HomeCommand.PLAYER_TAG, args[0])));
				if(!homeList.isEmpty())
					KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), home); //Data
				else
					KaranteeniPlugin.getMessager().sendMessage(sender, 
							Sounds.NONE.get(),
							Prefix.NEGATIVE+
							KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.NO_HOMES));
				//Bottom row
				KaranteeniPlugin.getMessager().sendMessage(sender,
					Sounds.NONE.get(),
					bottomRow.replace(HomeCommand.HOME_TAG, 
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOMES_OF)
						.replace(HomeCommand.PLAYER_TAG, args[0])));
			}
		}
		else //Incorrect parameters
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
		
		return true;
	}
}
