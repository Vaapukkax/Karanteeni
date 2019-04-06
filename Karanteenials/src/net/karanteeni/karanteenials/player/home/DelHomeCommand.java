package net.karanteeni.karanteenials.player.home;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class DelHomeCommand extends AbstractCommand {
	public DelHomeCommand(KaranteeniPlugin plugin) {
		super(plugin, 
				"delhome", 
				"/delhome [<name>]", 
				"Deletes a home", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
	}

	// /delhome
	// /delhome <name>
	// /delhome <player> <name>
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) //Don't allow console to access this command
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		Player player = (Player)sender;
		
		String homeName = HomeCommand.DEFAULT_NAME; //home name
		String playerName = null; //playername for other player
		UUID uuid = player.getUniqueId(); //uuid of home owner
		
		if(args.length == 0 || args.length == 1) //Deleting own home
		{
			if(args.length == 1) //new home name
				homeName = args[0].toLowerCase();
			
			if(!player.hasPermission("karanteenials.home.own.remove")) //Check does player have the permission
			{
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			Home home = Home.getHome(uuid, homeName); //Load home
			//Check if the home is further than merge limit
			if(home == null)
			{
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOME_OWN_DOES_NOT_EXIST)
						.replace(HomeCommand.HOME_TAG, homeName));
				return true;
			}
			
			//Set the home
			delHome(player, homeName, home);
		}
		else if(args.length == 2) //Setting other persons home
		{
			if(!player.hasPermission("karanteenials.home.other.remove")) //Check does player have the permission
			{
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			homeName = args[1].toLowerCase();
			playerName = args[0];
			uuid = KaranteeniPlugin.getPlayerHandler().getUUID(playerName); //Load the uuid of the homeowner
			
			if(uuid == null)
			{
				KaranteeniPlugin.getMessager().sendMessage(player, 
						Sounds.NO.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getDefaultMsgs().playerNotFound(sender, playerName));
				return true; //Player was not found, continue
			}
			
			Home home = Home.getHome(uuid, homeName); //Load home
			
			//Check if the home is further than merge limit
			if(home == null)
			{
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOME_OTHER_DOES_NOT_EXIST)
						.replace(HomeCommand.HOME_TAG, homeName)
						.replace(HomeCommand.PLAYER_TAG, playerName));
				return true;
			}
			
			//Set the home
			delHome(player, uuid, playerName, homeName, home);
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
		}
		
		return true;
	}

	/**
	 * Sets the home for this player
	 * @param player player whose home will be set
	 * @param homename name of the home being set
	 * @param home existing home with the same name if exists
	 */
	private void delHome(Player player, String homename, Home home)
	{
		if(!home.delete()) { //Check if home save was successful
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.ERROR.get(), 
					Prefix.ERROR+
					KaranteeniPlugin.getDefaultMsgs().databaseError(player));
			return;
		}
		
		//Message player that the home was set
		KaranteeniPlugin.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL+
				KaranteeniPlugin.getTranslator().getTranslation(plugin, player, 
						HomeCommand.HOME_OWN_REMOVED)
				.replace(HomeCommand.HOME_TAG, homename));
	}
	
	/**
	 * Sets the home for other player
	 * @param player
	 * @param other
	 * @param homename
	 * @param exists
	 */
	private void delHome(Player player, UUID other, String playername, String homename, Home home)
	{
		if(!home.delete()) { //Check if home save was successful
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.ERROR.get(), 
					Prefix.ERROR+
					KaranteeniPlugin.getDefaultMsgs().databaseError(player));
			return;
		}
		
		//Message player that the home was set
		KaranteeniPlugin.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL+
				KaranteeniPlugin.getTranslator().getTranslation(plugin, player, 
						HomeCommand.HOME_OTHER_REMOVED)
				.replace(HomeCommand.HOME_TAG, homename)
				.replace(HomeCommand.PLAYER_TAG, playername));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		Player player = (Player)sender;
		if(args.length == 1 && sender.hasPermission("karanteenials.home.own.remove"))
		{
			List<Home> homes = Home.getHomes(player.getUniqueId());
			List<String> params = new LinkedList<String>();
			
			for(Home home : homes)
				params.add(home.getName());
			
			return filterByPrefix(params, args[0]);
		}
		else if(args.length == 2 && sender.hasPermission("karanteenials.home.other.remove"))
		{
			UUID uuid = KaranteeniPlugin.getPlayerHandler().getUUID(args[0]);
			
			if(uuid == null)
				return null;
			
			List<Home> homes = Home.getHomes(uuid);
			List<String> params = new LinkedList<String>();
			
			for(Home home : homes)
				params.add(home.getName());
			
			return filterByPrefix(params, args[1]);
		}
		
		return null;
	}
}
