package net.karanteeni.karanteenials.functionality;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.karanteeni.karanteenials.Karanteenials;

public class Fly {
	private final Karanteenials plugin;
	
	public Fly(Karanteenials plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Gets the fly state of player if online,
	 * otherwise from the database
	 * @param uuid UUID of the player whose flight will be checked
	 * @return true if fly is on, false if off
	 */
	public boolean isFlyOn(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if(player != null) {
			return player.getAllowFlight();
		}
		return false;
	}
	
	/**
	 * Sets and saves the fly state of player
	 * @param uuid UUID of the player whose fly will be set
	 * @param on true if fly will be set on, false if fly will be set to false
	 * @return true if set and save successful, false otherwise
	 */
	public boolean setFly(UUID uuid, boolean on) {
		Player player = Bukkit.getPlayer(uuid);
		
		if(player != null) {
			player.setAllowFlight(on);
		}
		
		// ADD DATABASE ETC!
		
		return true;
	}
}
