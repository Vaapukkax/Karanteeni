package net.karanteeni.karanteenials.functionality;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.entity.Player;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

public class TpToggle {
	private static final String TELEPORT_TOGGLE = "blockstp";
	private final Karanteenials plugin;
	
	public TpToggle(Karanteenials plugin) {
		this.plugin = plugin;
	}
	
	
	public static void initTable() {
		Connection conn = null;
		try {
			conn = Karanteenials.getDatabaseConnector().openConnection();
			Statement st = conn.createStatement();
			st.execute("CREATE TABLE IF NOT EXISTS tptoggle ("+
					"UUID VARCHAR(128) NOT NULL,"+
					"serverID VARCHAR(128) NOT NULL,"+
					"FOREIGN KEY (UUID) REFERENCES player(UUID),"+
					"FOREIGN KEY (serverID) REFERENCES server(ID),"+
					"PRIMARY KEY (UUID,serverID));");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Checks if given player has tptoggle on
	 * @param player player who may have tptoggle on
	 * @return true if tp toggle is on (teleports disabled) and false if on (others can teleport)
	 */
	public boolean isTpToggleOn(Player player)
	{
		KPlayer kp = KPlayer.getKPlayer(player);
		
		if(kp.dataExists(plugin, TELEPORT_TOGGLE)) {
			return kp.getBoolean(plugin, TELEPORT_TOGGLE);
		}
		
		Connection conn = null;
		boolean result = false;
		try {
			conn = Karanteenials.getDatabaseConnector().openConnection();
			PreparedStatement st = conn.prepareStatement("SELECT * FROM tptoggle WHERE UUID = ? AND serverID = ?;");
			st.setString(1, player.getUniqueId().toString());
			st.setString(2, Karanteenials.getServerIdentificator());
			ResultSet set = st.executeQuery();
			if(set.next()) {
				kp.setData(plugin, TELEPORT_TOGGLE, true); //Set loaded data to cache
				result = true;
			} else {
				kp.setData(plugin, TELEPORT_TOGGLE, false); //Set loaded data to cache
				result = false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * Checks whether or not tptoggle allows player to teleport to another player
	 * @param asker The one who wants to teleport
	 * @param destination The player to whom they're trying to teleport to
	 * @return true if can teleport, false otherwise
	 */
	public boolean doesTeleportToggleAllow(Player asker, Player destination) 
	{
		if(!asker.isOnline() || !destination.isOnline())
			return false;
		
		if(asker.isOp())
			return true;
		
		boolean allows = isTpToggleOn(destination);
		if(!allows) //Player allows teleport
			return true;
		else if(allows && destination.isOp()) //Player is op and does not allow teleport
			return false;
		//Player does not allow teleport, check player power levels
		if(plugin.getPlayerData().getPowerLevel().getPowerLevel(asker) >= plugin.getPlayerData().getPowerLevel().getPowerLevel(destination))
			return true;
		return false;
	}
	
	
	/**
	 * Modifies the tptoggle value of given player
	 * @param player player who will receive new tptoggle value
	 * @param blockValue value for new tptoggle
	 */
	public void setTpToggle(Player player, boolean blockValue)  {
		KPlayer kp = KPlayer.getKPlayer(player);
		kp.setData(plugin, TELEPORT_TOGGLE, blockValue); //Set to cache
		Connection conn = null;
		
		if(blockValue)  {
			try {
				conn = Karanteenials.getDatabaseConnector().openConnection();
				PreparedStatement st = conn.prepareStatement("INSERT INTO tptoggle (UUID,serverID) VALUES (?,?);");
				st.setString(1, player.getUniqueId().toString());
				st.setString(2, Karanteenials.getServerIdentificator());
				st.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				conn = Karanteenials.getDatabaseConnector().openConnection();
				PreparedStatement st = conn.prepareStatement("DELETE FROM tptoggle WHERE UUID = ? AND serverID = ?;");
				st.setString(1, player.getUniqueId().toString());
				st.setString(2, Karanteenials.getServerIdentificator());
				st.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
