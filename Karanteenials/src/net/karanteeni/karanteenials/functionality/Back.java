package net.karanteeni.karanteenials.functionality;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

public class Back {
	//Key to access the back data of given player
			private NamespacedKey backKey = new NamespacedKey(Karanteenials.getPlugin(Karanteenials.class), "back");
			private Player player;
			
			public Back(Player player) {
				this.player = player;
			}
			
			/**
			 * Gets the back location of a player
			 * @param player Player whose back location will be returned
			 * @return location of back or null if no back location foudn 
			 */
			public Location getBackLocation() {
				KPlayer kp = KPlayer.getKPlayer(player);
				if(kp == null)
					return null;
				return kp.getLocationData(backKey);
			}
			
			/**
			 * Sets the back location of player
			 * @param player player whose back location is set
			 */
			public void setBackLocation(Location location) {
				KPlayer kp = KPlayer.getKPlayer(player);
				if(kp == null)
					return;
				kp.setCacheData(backKey, location);
			}
			
			/**
			 * Clears the back location data from given player
			 * @param player Player whose back location will be erased
			 * @return the location removed
			 */
			public Location clearBackLocation() {
				KPlayer kp = KPlayer.getKPlayer(player);
				if(kp == null)
					return null;
				Object data = kp.removeData(backKey);
				if(data == null)
					return null;
				return (Location)data;
			}
		}