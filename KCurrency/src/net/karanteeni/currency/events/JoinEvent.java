package net.karanteeni.currency.events;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.currency.KCurrency;

public class JoinEvent implements Listener{

	/**
	 * Creates a default balance for players
	 * @param event
	 */
	@EventHandler (priority=EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event)
	{
		createData(event.getPlayer());
	}
	
	/**
	 * Creates the data for player in to the database
	 * @param player
	 */
	private void createData(final Player player)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return;
		
		//check if this player is already in the database
		try {
			ResultSet set = db.executeQuery("SELECT * FROM " + KCurrency.getTableName() + 
					" WHERE "+KCurrency.getUUIDName()+"='"+player.getUniqueId()+"';");
			
			if(set.next())
				return;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//Add player to database
		try {
			//Insert player to database with UUID and default balance amount
			KCurrency.getDatabaseConnector().runQuery("INSERT INTO " + KCurrency.getTableName() + " (" + 
					KCurrency.getUUIDName() + ", " + KCurrency.getBalanceName() + ") VALUES ('" + player.getUniqueId() + "', " + 
					KCurrency.getPlugin(KCurrency.class).getConfigHandler().getStartBalance() + ");");
		} catch (Exception e) {
			Bukkit.broadcastMessage("§4Failed to generate balance information to database, please contanct server staff IMMEDIATELY!");
			Bukkit.broadcastMessage("§4Virhe tilin luomisessa tietokantaan, otathan yhteytt� palvelimen ylläpitoon VÄLITTÖMÄSTI!");
			e.printStackTrace();
		}
	}
}
