package net.karanteeni.karanteenials.player.home;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class SetHomeCommand extends AbstractCommand {
	private int distanceLimit = 10;
	
	public SetHomeCommand(KaranteeniPlugin plugin) {
		super(plugin, "sethome", 
				"/sethome [<name>]", 
				"Sets the players home", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
		
		if(!plugin.getConfig().isSet("Home.merge-distance")) { //Over this distance the home will not be overwritten
			plugin.getConfig().set("Home.merge-distance", 80);
			plugin.saveConfig();
		}
		
		distanceLimit = plugin.getConfig().getInt("Home.merge-distance");
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) { //Don't allow console to access this command
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		Player player = (Player)sender;
		
		String homeName = HomeCommand.DEFAULT_NAME; //new home name
		String playerName = null; //playername for other player
		UUID uuid = player.getUniqueId(); //uuid of home owner
		
		if(args.length == 0 || args.length == 1) { //Setting own home
			if(args.length == 1) //new home name
				homeName = args[0].toLowerCase();
			
			if(!player.hasPermission("karanteenials.home.own.set")) { //Check does player have the permission
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			Home home = Home.getHome(uuid, homeName); //Load home
			//Check if the home is further than merge limit
			if(home != null && player.getLocation().distance(home.getLocation()) > distanceLimit) {
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOME_OWN_ALREADY_EXISTS)
						.replace(HomeCommand.HOME_TAG, homeName));
				return true;
			}
			
			//Set the home
			setHome(player, homeName, home);
		} else if(args.length == 2) { //Setting other persons home		
			if(!player.hasPermission("karanteenials.home.other.set")) { //Check does player have the permission
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
				return true;
			}
			
			homeName = args[1].toLowerCase();
			playerName = args[0];
			uuid = KaranteeniPlugin.getPlayerHandler().getUUID(playerName); //Load the uuid of the homeowner
			
			if(uuid == null) {
				KaranteeniPlugin.getMessager().sendMessage(player, 
						Sounds.NO.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getDefaultMsgs().playerNotFound(sender, playerName));
				return true; //Player was not found, continue
			}
			
			Home home = Home.getHome(uuid, homeName); //Load home
			
			//Check if the home is further than merge limit
			if(home != null && player.getLocation().distance(home.getLocation()) > distanceLimit) {
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(),
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HomeCommand.HOME_OTHER_ALREADY_EXISTS)
						.replace(HomeCommand.HOME_TAG, homeName)
						.replace(HomeCommand.PLAYER_TAG, playerName));
				return true;
			}
			
			//Set the home
			setHome(player, uuid, playerName, homeName, home);
		} else {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
		}
		
		return true;
	}
	

	/**
	 * Checks whether this player can create new homes
	 * @param uuid uuid of the player
	 * @return true if can create more homes
	 */
	private boolean canCreateMoreHomes(UUID uuid) {
		Group group = KaranteeniPerms.getPlugin(KaranteeniPerms.class).getPlayerModel().getLocalGroup(uuid);
		if(group == null)
			return true;
		
		Collection<Home> homes = Home.getHomes(uuid);
		return (homes.size() < KaranteeniPerms.getPlugin(KaranteeniPerms.class)
								.getPlayerModel().getLocalGroup(uuid).getCustomInt(plugin, ".limit.home"));
	}
	
	
	/**
	 * Sets the home for this player
	 * @param player player whose home will be set
	 * @param homename name of the home being set
	 * @param home existing home with the same name if exists
	 */
	private void setHome(Player player, String homename, Home home) {
		if(home == null) { //Create new home
			if(!player.hasPermission("karanteenials.home.bypass-limit") && !canCreateMoreHomes(player.getUniqueId())) {
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HomeCommand.HOME_OWN_MAXCOUNT));
				return;
			}
			
			home = new Home(player.getUniqueId(), homename, player.getLocation());
			if(!home.save()) { //Check if home save was successful
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.ERROR.get(), 
						Prefix.ERROR+
						KaranteeniPlugin.getDefaultMsgs().databaseError(player));
				return;
			}
			
			//Message player that the home was set
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, 
							HomeCommand.HOME_OWN_SET)
					.replace(HomeCommand.HOME_TAG, homename));
		} else { //Merge homes
			home.setLocation(player.getLocation());
			if(!home.save()) { //Check if home save was successful
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.ERROR.get(), 
						Prefix.ERROR+
						KaranteeniPlugin.getDefaultMsgs().databaseError(player));
				return;
			}
			
			//Message player that the home was set
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, 
							HomeCommand.HOME_OWN_MOVED)
					.replace(HomeCommand.HOME_TAG, homename));
		}
	}
	
	
	/**
	 * Sets the home for other player
	 * @param player
	 * @param other
	 * @param homename
	 * @param exists
	 */
	private void setHome(Player player, UUID other, String playername, String homename, Home home)
	{
		if(home == null) //Create new home
		{
			if(!player.hasPermission("karanteenials.home.bypass-limit") && !canCreateMoreHomes(player.getUniqueId())) {
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HomeCommand.HOME_OTHER_MAXCOUNT)
						.replace(HomeCommand.PLAYER_TAG, playername));
				return;
			}
			
			home = new Home(other, homename, player.getLocation());
			if(!home.save()) { //Check if home save was successful
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.ERROR.get(), 
						Prefix.ERROR+
						KaranteeniPlugin.getDefaultMsgs().databaseError(player));
				return;
			}
			
			//Message player that the home was set
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, 
							HomeCommand.HOME_OTHER_SET)
					.replace(HomeCommand.HOME_TAG, homename)
					.replace(HomeCommand.PLAYER_TAG, playername));
		}
		else //Merge homes
		{
			home.setLocation(player.getLocation());
			if(!home.save()) { //Check if home save was successful
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.ERROR.get(), 
						Prefix.ERROR+
						KaranteeniPlugin.getDefaultMsgs().databaseError(player));
				return;
			}
			
			//Message player that the home was set
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, 
							HomeCommand.HOME_OTHER_MOVED)
					.replace(HomeCommand.HOME_TAG, homename)
					.replace(HomeCommand.PLAYER_TAG, playername));
		}
	}
}
