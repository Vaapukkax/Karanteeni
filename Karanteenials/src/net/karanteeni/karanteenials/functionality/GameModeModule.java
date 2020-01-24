package net.karanteeni.karanteenials.functionality;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.karanteenials.Karanteenials;

public class GameModeModule {
	private final String GAMEMODE = "%gamemode%";
	private final String PLAYERS = "%players%";
	
	
	/**
	 * Changes the gamemode of one or more players and checks their power level if they can 
	 * be changed
	 * @param setter who is setting the gamemodes
	 * @param name name to search the players with
	 * @param gm gamemode to be set
	 * @param checkPower should the power level be checked when changing gamemode
	 * @param multiple changing multiple or just one gamemode
	 * @param save Save the gamemodes to database
	 * @return List of players whose gamemode has been changed
	 */
	public List<Player> setGamemode(CommandSender setter, 
			List<Player> players, 
			GameMode gm, 
			boolean checkPower, 
			boolean multiple, 
			boolean save) {
		Karanteenials pl = Karanteenials.getPlugin(Karanteenials.class);
		// get online players based on name and location
		//List<Player> players = null;
		
		//if(setter instanceof Player)
			//players = KaranteeniPlugin.getPlayerHandler().getOnlinePlayers(setter, name);
		//else
		//	players = KaranteeniPlugin.getPlayerHandler().getOnlinePlayers(Bukkit.getWorlds().get(0).getSpawnLocation(), name);
		
		// if no players found, msg about it
		/*if(players.isEmpty()) {
			KaranteeniPlugin.getMessager().sendMessage(setter, Sounds.NO.get(), 
					Prefix.NEGATIVE + 
					KaranteeniPlugin.getDefaultMsgs().playerNotFound(setter, name));
			return null;
		}*/
		
		// only one player allowed and more than one was found
		if(!multiple && players.size() != 1) {
			KaranteeniPlugin.getMessager().sendMessage(setter, Sounds.NO.get(), 
					Prefix.NEGATIVE +
					KaranteeniPlugin.getTranslator().getTranslation(pl, setter, "gamemode.unclear-name")
					.replace(PLAYERS, ArrayFormat.joinSort(ArrayFormat.playersToArray(players), "§f, ")));
		}
		
		List<Player> tooPowerful = new ArrayList<Player>();
		
		// check the power levels of players except if command sender is op
		if(checkPower && setter instanceof Player && !setter.isOp()) {
			int senderPower = pl.getPlayerData().getPowerLevel().getPowerLevel((Player)setter);
			
			// loop each found player and check their power level
			for(Player player : players) {
				if(senderPower <  pl.getPlayerData().getPowerLevel().getPowerLevel(player)) {
					// if level greater than other players then deny level change
					players.remove(player);
					tooPowerful.add(player);
				}
			}
		}
		
		// change the gamemode of players
		for(Player player : players) {
			player.setGameMode(gm);
			// message player about the gm change
			KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
					KaranteeniPlugin.getTranslator().getTranslation(pl, player, "gamemode.changed")
					.replace(GAMEMODE, gm.name()));
		}
		
		// message command sender about the changes in gamemodes
		KaranteeniPlugin.getMessager().sendMessage(setter, Sounds.PLING_HIGH.get(), 
				Prefix.POSITIVE +
				KaranteeniPlugin.getTranslator().getTranslation(pl, setter, "gamemode.set-others")
				.replace(PLAYERS, ArrayFormat.joinSort(ArrayFormat.playersToArray(players), "§f, "))
				.replace(GAMEMODE, gm.name()));
		
		// notify which players you were not able to change 
		if(!tooPowerful.isEmpty()) {
			KaranteeniPlugin.getMessager().sendMessage(setter, Sounds.NO.get(), 
					Prefix.NEGATIVE + 
					KaranteeniPlugin.getTranslator().getTranslation(pl, setter, "gamemode.denied")
					.replace(PLAYERS, ArrayFormat.joinSort(ArrayFormat.playersToArray(tooPowerful), "§f, "))
					.replace(GAMEMODE, gm.name()));
		}
		
		
		if(save) {
			//===========================================//
			//
			// SET GAMEMODE TO DATABASE
			//
			//===========================================//
		}
		
		return players;
	}
	
	
	/**
	 * Sets the gamemode of given player
	 * @param player player whose gamemode is being changed
	 * @param gamemode the new gamemode for the player
	 * @return true if gamemode has been changed
	 */
	public boolean setGamemode(Player player, GameMode gamemode, boolean save) {
		if(player == null || !player.isOnline())
			return false;
		
		Karanteenials plugin = Karanteenials.getPlugin(Karanteenials.class);
		
		// set the gamemode
		player.setGameMode(gamemode);

		if(save) {
			//===========================================//
			//
			// SET GAMEMODE TO DATABASE
			//
			//===========================================//
		}
		
		// inform about gamemode change
		Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(), 
			Karanteenials.getTranslator().getTranslation(plugin, player, "gamemode.set-own")
			.replace(GAMEMODE, gamemode.name()));
		
		return true;
	}
	
	
	/**
	 * Returns the gamemode of a player if online, otherwise gamemode from database.
	 * @param uuid uuid of player whose gamemode is being loaded
	 * @return gamemode of player
	 */
	public GameMode getGameMode(UUID uuid) {
		// get online player data
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline()) {
			return player.getGameMode();
		}
		
		//===========================================//
		//
		// GET GAMEMODE FROM DATABASE
		//
		//===========================================//
		
		return null;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void registerTranslations() {
		Karanteenials pl = Karanteenials.getPlugin(Karanteenials.class);
		
		Karanteenials.getTranslator().registerTranslation(
				pl, 
				"gamemode.set-own", 
				"You set your gamemode to §e" + GAMEMODE);
		
		Karanteenials.getTranslator().registerTranslation(
				pl, 
				"gamemode.set-other", 
				"You set the gamemode of player " + PLAYERS + "§a to §e" + GAMEMODE);
		
		Karanteenials.getTranslator().registerTranslation(
				pl, 
				"gamemode.set-others", 
				"You set the gamemode of " + PLAYERS + "§a to §e" + GAMEMODE);
		
		Karanteenials.getTranslator().registerTranslation(
				pl, 
				"gamemode.changed", 
				"Your gamemode has been changed to §e" + GAMEMODE);
		
		Karanteenials.getTranslator().registerTranslation(
				pl, 
				"gamemode.denied", 
				"The rank of player(s) " + PLAYERS + "§6 was higher and you couldn't change their gamemode to §e" + GAMEMODE);
		
		Karanteenials.getTranslator().registerTranslation(
				pl, 
				"gamemode.unclear-name", 
				"Multiple players found with the given name, please specify [ " + PLAYERS + "§c ]");
	}


	public static void initTable() {
		// TODO Auto-generated method stub
		
	}
}
