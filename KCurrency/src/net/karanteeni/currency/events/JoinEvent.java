package net.karanteeni.currency.events;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.currency.KCurrency;

public class JoinEvent implements Listener{

	/**
	 * Creates a default balance for players
	 * @param event
	 */
	@EventHandler (priority=EventPriority.HIGHEST)
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		if(event.getLoginResult() != Result.ALLOWED)
			return;
		createData(event.getUniqueId());
	}
	
	/**
	 * Creates the data for player in to the database
	 * @param player
	 */
	private void createData(final UUID uuid)
	{
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Connection conn = null;
				
				//Add player to database
				try {
					conn = KaranteeniCore.getDatabaseConnector().openConnection();
					Statement st = conn.createStatement();
					//Insert player to database with UUID and default balance amount
					st.executeUpdate("INSERT IGNORE INTO " + KCurrency.getTableName() + " (" + 
							KCurrency.getUUIDName() + ", " + KCurrency.getBalanceName() + ") VALUES ('" + uuid + "', " + 
							KCurrency.getPlugin(KCurrency.class).getConfigHandler().getStartBalance() + ");");
					st.close();
				} catch (Exception e) {
					Bukkit.broadcastMessage("§4Failed to generate balance information to database, please contanct server staff IMMEDIATELY!");
					e.printStackTrace();
				} finally {
					if(conn != null)
						try { conn.close(); } catch(Exception e) { /* ignore */ }
				}
			}
		};
		
		runnable.runTaskAsynchronously(KCurrency.getPlugin(KCurrency.class));
	}
}
