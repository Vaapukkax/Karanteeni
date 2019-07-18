package net.karanteeni.core.command.defaultcomponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
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
	private boolean includeOffline = false;
	public final static String PLAYER_KEY_MULTIPLE = "core.players";
	public final static String PLAYER_KEY_SINGLE = "core.player";
	public final static String PLAYER_KEY_OFFLINE_SINGLE = "core.offlineplayer";
	
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
	 * Initilizes the player loader class
	 * @param before load before the component attached to
	 * @param showBukkit show bukkit players
	 * @param singular load only a single player
	 * @param mandatory does this must return something
	 * @param includeOffline include offline players (will set data as uuid). if true, only one offline player will be returned at max
	 */
	public PlayerLoader(boolean before, boolean showBukkit, boolean singular, boolean mandatory, boolean includeOffline) {
		super(before);
		this.showBukkit = showBukkit;
		if(!includeOffline)
			this.singular = singular;
		else
			this.singular = true;
		this.mandatory = mandatory;
		this.includeOffline = includeOffline;
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
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		// no players has been requested
		if(args.length == 0) {
			if(mandatory)
				return CommandResult.INVALID_ARGUMENTS;
			return CommandResult.SUCCESS;
		}
		
		// get requested players
		List<Player> players = KaranteeniCore.getPlayerHandler().getOnlinePlayers(sender, args[0]);
		
		// if there are more players than requested, give error
		if(singular && players.size() > 1) {
			return new CommandResult(
					KaranteeniCore.getTranslator().getTranslation(
							KaranteeniCore.getPlugin(KaranteeniCore.class), 
							sender, 
							"players.too-many"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		} else if(singular && players.size() == 0 && !includeOffline) {
			// check if we should try to load an offline player. offline player is always singular
			if(mandatory) {
				// no players found with given arguments
				return new CommandResult(KaranteeniCore.getDefaultMsgs().playerNotFound(sender, args[0]),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get());
			} else {
				return CommandResult.SUCCESS;
			}
		}
		
		// if no offline players should be included or some players are found
		if(!includeOffline || (players != null && players.size() > 0)) {
			// set the data of found players
			if(singular)
				this.chainer.setObject(PLAYER_KEY_SINGLE, players.get(0));
			else
				this.chainer.setObject(PLAYER_KEY_MULTIPLE, players);
		} else {
			// load offline player uuid and set to memory
			UUID uuid = KaranteeniCore.getPlayerHandler().getUUID(args[0]);
			if(uuid == null) {
				if(mandatory) {
					// no players found with given arguments
					return new CommandResult(KaranteeniCore.getDefaultMsgs().playerNotFound(sender, args[0]),
							ResultType.INVALID_ARGUMENTS,
							Sounds.NO.get());
				} else {
					return CommandResult.SUCCESS;
				}
			}
			
			// player found with argument
			this.chainer.setObject(PLAYER_KEY_OFFLINE_SINGLE, uuid);
		}
		
		// successful command execution
		return CommandResult.SUCCESS;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1 || args.length > 1)
			return null;
		
		// split the string into multiple parts
		String[] parts = args[0].split(",");
		Character lastChar = (args[0].length() != 0) ? args[0].charAt(args[0].length()-1) : null;
		String res = args[0];
		
		// get all player names
		Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
		Set<String> playerNames = new HashSet<String>();
		for(Player player : players)
			playerNames.add(player.getName());
		playerNames.addAll(Arrays.asList("@a", "@r", "@p"));
		
		if(showBukkit) {
			// TODO BUKKIT NAMES
		}
		
		// at the beginning of arg or comma so requires special handling
		if(lastChar == null || lastChar.charValue() == ',') {
			List<String> results = new ArrayList<String>();
			// add all compatible names to the results array
			for(String name : playerNames)
				results.add(res + name);
			return results;
		}
		
		// get the last part of players
		String lastPart = parts[parts.length-1].toLowerCase();
		
		// replace the last letters with nothing from the result to allow gluing
		res = res.substring(0, res.length()-lastPart.length());
		
		List<String> results = new ArrayList<String>();
		
		// add all compatible names to the results array
		for(String name : playerNames)
			if(name.toLowerCase().startsWith(lastPart))
				results.add(res + name);
		
		return results;
	}


	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"players.too-many", 
				"Too many players found, please specify your search parameters");
	}
}
