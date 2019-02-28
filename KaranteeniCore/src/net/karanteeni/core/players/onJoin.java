package net.karanteeni.core.players;

import java.sql.PreparedStatement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.DatabaseConnector;

/**
 * 
 * @author Matti
 *
 */
public class onJoin implements Listener{

	/**
	 * Adds player to KPlayer list when player joins
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerJoin(PlayerJoinEvent event)
	{
		//Add player to database
		addToDatabase(event.getPlayer());
		
		new KPlayer(event.getPlayer());
	}
	
	/**
	 * Removes player from KPlayer list when player quits
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onQuit(PlayerQuitEvent event)
	{
		KPlayer kp = KPlayer.getKPlayer(event.getPlayer());
		
		if(kp != null)
			kp.destroy();
	}
	
	
	/**
	 * Adds the given player to database
	 * @param player
	 */
	private void addToDatabase(final Player player)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return;
		
		//check if this player is already in the database
		/*try {
			Statement st = db.getStatement();
			ResultSet set = st.executeQuery("SELECT * FROM " + PlayerHandler.PlayerDataKeys.PLAYER_TABLE + 
					" WHERE "+PlayerHandler.PlayerDataKeys.UUID+"='"+player.getUniqueId()+"';");
			
			if(set.next())
			{
				set.close();
				st.close();
				return;
			}
			set.close();
			st.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
		
		String query = 
			"INSERT IGNORE INTO " + PlayerHandler.PlayerDataKeys.PLAYER_TABLE +
			" (" + PlayerHandler.PlayerDataKeys.UUID + ", " + 
			PlayerHandler.PlayerDataKeys.DISPLAY_NAME + ", " + 
			PlayerHandler.PlayerDataKeys.NAME +
			") VALUES (?, ?, ?);";
		
		//Save the players data to the database
		try {
			PreparedStatement st = db.prepareStatement(query);
			st.setString(1, player.getUniqueId().toString());
			st.setString(2, player.getDisplayName());
			st.setString(3, player.getName());
			
			st.executeUpdate();
			st.close();
			//db.runQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
