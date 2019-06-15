package net.karanteeni.karanteenials.functionality;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

/**
 * Acts as an access point to blockdata, (such as player has blocked another player)
 * @author Nuubles
 *
 */
public class PlayerBlock extends TableContainer
{
	private final NamespacedKey blockdata;
	
	public PlayerBlock(KaranteeniPlugin plugin) {
		super();
		blockdata = new NamespacedKey(plugin,"tp-block");
	}
	
	@Override
	protected void initTable() {
		try {
			DatabaseConnector db = Karanteenials.getDatabaseConnector();
			Statement st = db.getStatement();
			st.execute("CREATE TABLE IF NOT EXISTS blockedplayers ("+
					"blocked VARCHAR(128) NOT NULL,"+
					"blocker VARCHAR(128) NOT NULL,"+
					"PRIMARY KEY (blocked,blocker),"+
					"FOREIGN KEY (blocked) REFERENCES player(UUID),"+
					"FOREIGN KEY (blocker) REFERENCES player(UUID));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if other has blocked asker
	 * @param asker may have been blocked by other
	 * @param other may have blocked asker
	 * @return true if is other has blocked asker
	 */
	public boolean isBlocked(UUID asker, UUID other) 
	{
		Player a = Bukkit.getPlayer(asker);
		Player b = Bukkit.getPlayer(other);
		if(a != null && b != null) //If online try to use cache
			return isBlocked(a,b);
		
		List<UUID> blockedUUIDs = getBlockedUUIDs(other);
		if(blockedUUIDs == null)
			return false;
		return blockedUUIDs.contains(asker);
	}
	
	/**
	 * Checks if other has blocked asker
	 * @param asker who may have been blocked by asker
	 * @param other player who may have blocked asker
	 * @return true if is other has blocked asker
	 */
	public boolean isBlocked(Player asker, Player other) 
	{
		KPlayer oth = KPlayer.getKPlayer(other);
		
		if(oth.dataExists(blockdata)) 
		{
			List<?> blocked = oth.getList(blockdata);
			if(blocked == null || blocked.isEmpty()) //If no blocked players then it is not blocked
				return false;
			
			return blocked.contains(asker.getUniqueId());
		}
		else // data does not exist, load it into cache
		{
			List<UUID> blocks = getBlockedUUIDs(other.getUniqueId());
			if(blocks == null)
				return false;
			
			oth.setData(blockdata, blocks);
			return blocks.contains(asker.getUniqueId());
		}
	}
	
	/**
	 * Returns all uuid the given uuid has blocked directly from the database
	 * @param asker uuid of the player who has blocked others
	 * @return list of blocked uuids
	 */
	public List<UUID> getBlockedUUIDs(UUID asker) 
	{
		try {
			DatabaseConnector db = Karanteenials.getDatabaseConnector();
			Statement st = db.getStatement();
			ResultSet rs = st.executeQuery("SELECT blocked FROM blockedplayers WHERE blocker = '"+asker.toString()+"';");
			
			ArrayList<UUID> uuids = new ArrayList<UUID>();
			while(rs.next())
				uuids.add(UUID.fromString(rs.getString(1)));
			return uuids;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * The given player blocks the player with uuid given
	 * @param player
	 * @param blocked
	 * @return -1 if database error, 
	 * 0 if tried to block itself, 
	 * 1 if there already is a block, 
	 * 2 if block was successful
	 */
	@SuppressWarnings({ "unchecked" })
	public int addBlock(Player player, UUID blocked) {
		if(player.getUniqueId().equals(blocked)) return 0;
		
		if(isBlocked(player.getUniqueId(), blocked)) return 1;
		
		try {
			DatabaseConnector db = Karanteenials.getDatabaseConnector();
			Statement st = db.getStatement();
			int rows = st.executeUpdate("INSERT INTO blockedplayers (blocked,blocker) VALUES ('"+
					blocked.toString()+"','"+player.getUniqueId().toString()+"');");
			
			if(rows <= 0)
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		KPlayer kp = KPlayer.getKPlayer(player);
		if(!kp.dataExists(blockdata)) { //Add to cache
			kp.setData(blockdata, new ArrayList<UUID>());
		}
		((List<UUID>)kp.getList(blockdata)).add(blocked);
		
		return 2;
	}
	
	/**
	 * The given player unblocks the player with uuid given
	 * @param player
	 * @param blocked
	 * @return -1 if database error, 
	 * 0 if there was no block to begin with, 
	 * 1 if unblock was successful
	 */
	@SuppressWarnings({ "unchecked" })
	public int removeBlock(Player player, UUID blocked) {
		if(player.getUniqueId().equals(blocked)) return 0;
		
		if(!isBlocked(player.getUniqueId(), blocked)) return 0;
		
		try {
			DatabaseConnector db = Karanteenials.getDatabaseConnector();
			Statement st = db.getStatement();
			int rows = st.executeUpdate("DELETE FROM blockedplayers WHERE blocked = '"+
					blocked.toString()+"' AND blocker = '"+player.getUniqueId().toString()+"';");
			
			if(rows <= 0)
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		KPlayer kp = KPlayer.getKPlayer(player);
		if(!kp.dataExists(blockdata)) //Add to cache
			kp.setData(blockdata, new ArrayList<UUID>());
		else
			((List<UUID>)kp.getList(blockdata)).remove(blocked);
		
		return 1;
	}
}