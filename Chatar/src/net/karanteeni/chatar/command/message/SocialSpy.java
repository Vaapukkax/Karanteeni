package net.karanteeni.chatar.command.message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.players.KPlayer;
// TODO MOVE TO Foxet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// TODO MOVE TO Foxet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// TODO MOVE TO Foxet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// TODO MOVE TO Foxet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// TODO MOVE TO Foxet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class SocialSpy {
	Chatar plugin;
	
	public SocialSpy() {
		plugin = Chatar.getPlugin(Chatar.class);
		initTable();
	}
	
	
	/**
	 * Check if the given uuid player has socialspy on
	 * @param uuid uuid of the player being checked
	 * @return true if socialspy is on, false if off
	 */
	public boolean hasSocialSpy(UUID uuid) {
		Statement stmt = Chatar.getDatabaseConnector().getStatement();
		try {
			ResultSet rs = stmt.executeQuery("SELECT player FROM socialspy WHERE player = " + uuid.toString() + ";");
			
			// if something is found return true, false otherwise
			return rs.next();
		} catch (SQLException e) {
			Bukkit.broadcastMessage(Prefix.ERROR + Chatar.getDefaultMsgs().defaultDatabaseError());
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * Returns the socialspy state of a given player
	 * @param player player whose socialspy will be returned
	 * @return true if player has socialspy, false otherwise
	 */
	public boolean hasOnlineSocialSpy(Player player) {
		KPlayer kp = KPlayer.getKPlayer(player);
		return kp.dataExists(plugin, "socialspy");
	}
	
	
	/**
	 * Loads the players socialspy state to the KPlayer class
	 * @param player player who may have the socialspy on
	 */
	public void loadSocialSpy(Player player) {
		KPlayer kp = KPlayer.getKPlayer(player);
		// load and set data only if the loaded data is true (aka. save memory)
		boolean hasSocialSpy = hasSocialSpy(player.getUniqueId());
		if(hasSocialSpy)
			kp.setData(plugin, "socialspy", hasSocialSpy);
	}
	
	
	/**
	 * Sets the socialspy state to the database
	 * @param uuid uuid of player whose socialspy is being changed
	 * @param on should the ss be on or off (true or false)
	 * @return true if success, false if error
	 */
	public boolean setSocialSpy(UUID uuid, boolean on) {
		Statement stmt = Chatar.getDatabaseConnector().getStatement();
		
		if(on) {
			try {
				int res = stmt.executeUpdate("INSERT INTO socialspy (player) VALUES (" + uuid.toString() + ");");
				return res > 0;
			} catch (SQLException e) {
				Bukkit.broadcastMessage(Prefix.ERROR + Chatar.getDefaultMsgs().defaultDatabaseError());
				e.printStackTrace();
			}
		} else {
			try {
				int res = stmt.executeUpdate("DELETE FROM socialspy WHERE player = " + uuid.toString() + ";");
				return res > 0;
			} catch (SQLException e) {
				Bukkit.broadcastMessage(Prefix.ERROR + Chatar.getDefaultMsgs().defaultDatabaseError());
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	private void initTable() {
		Statement stmt = Chatar.getDatabaseConnector().getStatement();
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS socialspy \n("+
					"player VARCHAR(64) NOT NULL,"+
				    "\nPRIMARY KEY (player),"+
				    "\nFOREIGN KEY (player) REFERENCES player(UUID)"+
					"\n);");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
