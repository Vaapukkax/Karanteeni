package net.karanteeni.core.command.defaultcomponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

/**
 * Loads List of players given in parameters and sets them into data with key "core.players" and if
 * only one players is requested it is set to "core.player". if no data is set then no players has been
 * searched.
 * @author Nuubles
 *
 */
public class PlayerLoader extends CommandLoader implements TranslationContainer {
	private boolean showBukkit;
	private boolean singular;
	private boolean mandatory;
	
	/**
	 * Initializes the player loader class
	 * @param before load before the attached component
	 * @param showBukkit show players also on other servers
	 * @param singular do we require only 1 player
	 * @param is it mandatory to type the requested player names
	 */
	public PlayerLoader(boolean before, boolean showBukkit, boolean singular, boolean mandatory) {
		super(before);
		this.showBukkit = showBukkit;
		this.singular = singular;
		this.mandatory = mandatory;
	}

	
	/**
	 * Initializes the player loader class
	 * @param loader loader to load after this loader
	 * @param before load before the attached component
	 * @param showBukkit show players also on other servers
	 * @param singular do we require only 1 player
	 * @param is it mandatory to type the requested player names
	 */
	public PlayerLoader(CommandLoader loader, boolean before, boolean showBukkit, boolean singular, boolean mandatory) {
		super(loader, before);
		this.showBukkit = showBukkit;
		this.singular = singular;
		this.mandatory = mandatory;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected boolean runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		// no players has been requested
		if(args.length == 0 && !mandatory)
			return !mandatory;
		
		// get requested players
		List<Player> players = KaranteeniCore.getPlayerHandler().getOnlinePlayers(sender, args[0]);
		
		// if there are more players than requested, give error
		if(singular && players.size() > 1) {
			KaranteeniCore.getMessager().sendMessage(
					sender, 
					Sounds.NO.get(), 
					Prefix.NEGATIVE +
					KaranteeniCore.getTranslator().getTranslation(
							KaranteeniCore.getPlugin(KaranteeniCore.class), 
							sender, 
							"players.too-many"));
			return false;
		} else if(singular && players.size() == 0) {
			// no players found with given arguments
			KaranteeniCore.getMessager().sendMessage(
					sender, 
					Sounds.NO.get(), 
					KaranteeniCore.getDefaultMsgs().playerNotFound(sender, args[0]));
		}
		
		// set the data of found players
		if(singular)
			this.chainer.setObject("core.player", players.get(0));
		else
			this.chainer.setObject("core.players", players);
		
		return true;
	}
	
	
	@Override
	public void invalidArguments(CommandSender sender) { }
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1 || args.length > 1)
			return null;
		
		// split args by comma
		String[] parts = args[0].split(",");
		Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
		List<String> res = new ArrayList<String>();
		
		res.addAll(Arrays.asList("@a","@r","@p"));
		// add all players to the array
		players.forEach(player -> { res.add( player.getName() );} );
		
		// search players from all servers
		if(showBukkit) {
			//==================================================================
			// TODO: show bukkit players
		}
		
		// return autofill compatible names
		return this.filterByPrefix(res, parts[parts.length-1], false);
	}


	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"players.too-many", 
				"Too many players found, please specify your search parameters");
	}
}
