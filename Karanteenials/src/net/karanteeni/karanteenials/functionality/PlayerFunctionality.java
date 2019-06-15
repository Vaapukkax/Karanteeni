package net.karanteeni.karanteenials;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.groups.KaranteeniPerms;
import net.karanteeni.groups.player.Group;

/**
 * Commonly needed functions and data in Karanteenials, such as Back data
 * used to return player to the position before teleportation
 * @author Nuubles
 *
 */
public class PlayerFunctionality {
	Karanteenials plugin;
	private static final String POWER_LEVEL = "power_level";
	private final TpToggle tptoggle;
	private final PlayerBlock playerBlock; 
	
	public PlayerFunctionality(Karanteenials plugin) {
		this.plugin = plugin;
		tptoggle = new TpToggle();
		playerBlock = new PlayerBlock();
		
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		//Power level for groups
		for(Group group : perms.getGroupModel().getLocalGroupList().getGroups())
			if(!group.isCustomDataSet(plugin, POWER_LEVEL)) //Add the power level to each group if not set
				group.setCustomData(plugin, POWER_LEVEL, 0);
	}
	
	/**
	 * Returns the Karanteenials power level of given uuids group
	 * @param uuid uuid of whose power level is being checked
	 * @return power level of uuid or -1 if not found
	 */
	public int getPowerLevel(UUID uuid) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		Group group = perms.getPlayerModel().getLocalGroup(uuid);
		if(group == null)
			return -1;
		return group.getCustomInt(plugin, POWER_LEVEL);
	}
	
	/**
	 * Returns the Karanteenials power level of given players group
	 * @param player player whose power level is being checked
	 * @return power level of player or -1 if players group is not found
	 */
	public int getPowerLevel(Player player) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		Group group = perms.getPlayerModel().getLocalGroup(player);
		if(group == null)
			return -1;
		return group.getCustomInt(plugin, POWER_LEVEL);
	}
	
	/**
	 * Returns the player block data manager class
	 * @return
	 */
	public PlayerBlock getBlockPlayer() {
		return this.playerBlock;
	}
	
	/**
	 * Returns the tptoggle data manager class
	 * @return
	 */
	public TpToggle getTpToggle() {
		return this.tptoggle;
	}
	
	/**
	 * Acts as a cache and access point to tptoggle data
	 * @author Nuubles
	 *
	 */
	public class TpToggle extends TableContainer
	{
		private static final String TELEPORT_TOGGLE = "blockstp";
		
		@Override
		protected void initTable() {
			try {
				DatabaseConnector db = Karanteenials.getDatabaseConnector();
				Statement st = db.getStatement();
				st.execute("CREATE TABLE IF NOT EXISTS tptoggle ("+
						"UUID VARCHAR(128) NOT NULL,"+
						"serverID VARCHAR(128) NOT NULL,"+
						"FOREIGN KEY (UUID) REFERENCES player(UUID),"+
						"FOREIGN KEY (serverID) REFERENCES server(ID),"+
						"PRIMARY KEY (UUID,serverID));");
			} catch (SQLException e) {
				e.printStackTrace();
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
			
			try {
				DatabaseConnector db = Karanteenials.getDatabaseConnector();
				PreparedStatement st = db.prepareStatement("SELECT * FROM tptoggle WHERE UUID = ? AND serverID = ?;");
				st.setString(1, player.getUniqueId().toString());
				st.setString(2, Karanteenials.getServerIdentificator());
				ResultSet set = st.executeQuery();
				if(set.next()) {
					kp.setData(plugin, TELEPORT_TOGGLE, true); //Set loaded data to cache
					return true;
				}
				kp.setData(plugin, TELEPORT_TOGGLE, false); //Set loaded data to cache
				return false;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
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
			
			boolean allows = isTpToggleOn(destination);
			if(!allows) //Player allows teleport
				return true;
			else if(allows && destination.isOp()) //Player is op and does not allow teleport
				return false;
			//Player does not allow teleport, check player power levels
			if(getPowerLevel(asker) >= getPowerLevel(destination))
				return true;
			return false;
		}
		
		/**
		 * Modifies the tptoggle value of given player
		 * @param player player who will receive new tptoggle value
		 * @param blockValue value for new tptoggle
		 */
		public void setTpToggle(Player player, boolean blockValue) 
		{
			KPlayer kp = KPlayer.getKPlayer(player);
			kp.setData(plugin, TELEPORT_TOGGLE, blockValue); //Set to cache
			
			if(blockValue) 
			{
				try {
					DatabaseConnector db = Karanteenials.getDatabaseConnector();
					PreparedStatement st = db.prepareStatement("INSERT INTO tptoggle (UUID,serverID) VALUES (?,?);");
					st.setString(1, player.getUniqueId().toString());
					st.setString(2, Karanteenials.getServerIdentificator());
					st.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else
			{
				try {
					DatabaseConnector db = Karanteenials.getDatabaseConnector();
					PreparedStatement st = db.prepareStatement("DELETE FROM tptoggle WHERE UUID = ? AND serverID = ?;");
					st.setString(1, player.getUniqueId().toString());
					st.setString(2, Karanteenials.getServerIdentificator());
					st.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Acts as an access point to blockdata, (such as player has blocked another player)
	 * @author Nuubles
	 *
	 */
	public class PlayerBlock extends TableContainer
	{
		private final NamespacedKey blockdata;
		
		PlayerBlock() {
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
	
	/**
	 * Location used to handle the location before teleport of player 
	 * @author Nuubles
	 *
	 */
	public static class Back {
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
	
	/**
	 * Tablecontainer abstract class to enforce database table creation
	 * @author Nuubles
	 *
	 */
	private static abstract class TableContainer {
		TableContainer() {
			initTable();
		}
		
		/**
		 * Initializes database table
		 */
		protected abstract void initTable();
	}
}