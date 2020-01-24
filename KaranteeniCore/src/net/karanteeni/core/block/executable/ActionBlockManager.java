package net.karanteeni.core.block.executable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.PermanentLocation;

public class ActionBlockManager {
	private KaranteeniCore plugin = null;
	private TreeMap<UUID, ActionBlock> actionBlocks = new TreeMap<UUID, ActionBlock>();
	// private TreeMap<Block, ActionSign> actionSigns = new TreeMap<Block, ActionSign>();
	private Set<ActionSign> actionSigns = new TreeSet<ActionSign>();
	private Set<Class<? extends ActionBlock>> subBlockClasses = new HashSet<Class<? extends ActionBlock>>();
	private Set<Class<? extends ActionSign>> subSignClasses = new HashSet<Class<? extends ActionSign>>();
	private List<LinkedList<Object>> loadedData = new LinkedList<LinkedList<Object>>();
	private ActionBlockEventManager abeManager = null;
	
	/**
	 * Creates an instance of executable block manager
	 * @param plugin
	 */
	public ActionBlockManager(KaranteeniCore plugin) {
		this.plugin = plugin;
		this.abeManager = new ActionBlockEventManager(this);
	}
	
	
	/**
	 * Returns the currently in use action block event manager
	 * @return action block event manager in use
	 */
	public ActionBlockEventManager getActionBlockEventManager() {
		return this.abeManager;
	}
	
	
	/**
	 * Destroys this manager; removes all class types from memory
	 */
	public void destroyManager() {
		this.actionBlocks.clear();
		this.subBlockClasses.clear();
		this.subSignClasses.clear();
		if(this.loadedData != null)
			this.loadedData.clear();
		this.abeManager = null;
	}
	
	
	/**
	 * Register a given block to receive actions
	 * @param actionBlock block with a given action
	 * @return true if assignment was successful, false if block is already registered
	 */
	protected boolean registerBlock(ActionBlock actionBlock) {
		// check if this location is already reserved
		if(actionBlock == null) return false;
		ActionBlock duplicate = getActionBlock(actionBlock.getBlock());
		if(duplicate != null) return false;
		
		// store the action block
		actionBlocks.put(actionBlock.getUUID(), actionBlock);
		abeManager.addActionBlock(actionBlock);
		
		// store the possible action sign
		if(actionBlock instanceof ActionSign)
			actionSigns.add((ActionSign)actionBlock);
		
		return true;
	}
	
	
	/**
	 * Checks if a given actionblock is registered for action
	 * @param actionBlock actionblock registered
	 * @return true if registered, false otherwise
	 */
	protected boolean isBlockRegistered(ActionBlock actionBlock) {
		return actionBlocks.containsKey(actionBlock.getUUID());
	}
	
	
	/**
	 * Unregisters the given actionblock
	 * @param block block to be unregistered
	 * @return true if unregistered, false if none found
	 */
	protected boolean unregisterBlock(ActionBlock block) {
		if(block == null) return false;
		
		// remove from action blocks
		if(actionBlocks.remove(block.getBlock()) == null) return false;
		abeManager.removeActionBlock(block);
		
		// remove from action signs
		if(block instanceof ActionSign)
			return actionSigns.remove(block.getBlock());
		return true;
	}
	
	
	/**
	 * Saves the actionblock to the database along with the location
	 * @param block block to save
	 * @return INSERTION_SUCCESSFUL if save was successful
	 */
	protected QueryState saveActionBlock(ActionBlock block) {
		PermanentLocation permLoc = new PermanentLocation(block.getUUID(), block.getBlock().getLocation());
		if(!permLoc.saveLocation()) {
			return QueryState.INSERTION_FAIL_OTHER;
		}
		
		Connection conn = null;
		QueryState result = null;
		
		try {
			conn = KaranteeniPlugin.getDatabaseConnector().openConnection();
			
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO actionblock(uuid,permission,serverID,classtype) VALUES(?,?,?,?)");
			stmt.setString(1, block.getUUID().toString());
			if(block.getPermission() == null)
				stmt.setNull(2, java.sql.Types.NVARCHAR);
			else
				stmt.setString(2, block.getPermission());
			stmt.setString(3, KaranteeniCore.getServerIdentificator());
			stmt.setString(4, block.getClass().getName());
			if(stmt.executeUpdate() == 1)
				result = QueryState.INSERTION_SUCCESSFUL;
			else
				result = QueryState.INSERTION_FAIL_OTHER;
				
		} catch(SQLException e) {
			result = QueryState.INSERTION_FAIL_OTHER;
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
	 * Removes the registration from the database along with registered location
	 * @param block block to remove from database
	 * @return REMOVAL_SUCCESSFUL is success
	 */
	protected QueryState destroyActionBlock(ActionBlock block) {
		Connection conn = null;
		QueryState result = null;
		
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			PreparedStatement stmt = conn.prepareStatement(
					"DELETE FROM actionblock WHERE uuid = ?;");
			stmt.setString(1, block.getUUID().toString());
			
			if(stmt.executeUpdate() != 0) {
				PermanentLocation permLoc = new PermanentLocation(block.getUUID(), block.getBlock().getLocation());
				if(permLoc.deleteLocation())
					result = QueryState.REMOVAL_SUCCESSFUL;
				else
					result = QueryState.REMOVAL_FAIL_OTHER;
			}
			else
				result = QueryState.REMOVAL_FAIL_OTHER;
				
		} catch(SQLException e) {
			result = QueryState.REMOVAL_FAIL_OTHER;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// remove from maps
		if(result == QueryState.REMOVAL_SUCCESSFUL) {
			actionBlocks.remove(block.getUUID());
			if(block instanceof ActionSign)
				actionSigns.remove(block);
		}
		
		return result;
	}
	
	
	/**
	 * Loads the blocks from the data read from database.
	 * readBlocksFromDatabase must be called before this.
	 * This will clear the loadedData member
	 */
	public void loadReadBlockData() {
		for(List<Object> data : this.loadedData) {
			// get data from the list
			UUID uuid = (UUID)data.remove(0);
			String permission = (String)data.remove(0);
			String classType = (String)data.remove(0);
			String worldName = (String)data.remove(0);
			double x = (Double)data.remove(0);
			double y = (Double)data.remove(0);
			double z = (Double)data.remove(0);
			
			// get the world and return if none found
			World world = Bukkit.getWorld(worldName);
			if(world == null) continue;
			
			Location loc = new Location(world, x, y, z);
			
			/// the code below this line loads the class, and generates the actionblock from it ///
			
			// check if loaded block is a block or a sign
			for(Class<? extends ActionBlock> clazz : subBlockClasses)
			if(clazz.getName().equals(classType)) {
				// load the location of the block
				
				Block block = loc.getBlock();
				try {
					// try to create a new class out of this block
					if(permission != null) {
						Constructor<? extends ActionBlock> cstr = clazz.getConstructor(Block.class, UUID.class, String.class);
						ActionBlock aBlock = cstr.newInstance(block, uuid, permission);	
						aBlock.onLoad();
						registerBlock(aBlock); // register created class
						abeManager.addActionBlock(aBlock);
					} else {
						Constructor<? extends ActionBlock> cstr = clazz.getConstructor(Block.class, UUID.class);
						ActionBlock aBlock = cstr.newInstance(block, uuid);	
						aBlock.onLoad();
						registerBlock(aBlock); // register created class
						abeManager.addActionBlock(aBlock);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// load all signs
			for(Class<? extends ActionSign> clazz : subSignClasses)
			if(clazz.getName().equals(classType)) {
				// load the location of the block
				Block block = PermanentLocation.loadLocation(uuid).getLocation().getBlock();
				try {
					// try to create a new class out of this block
					if(permission != null) {
						Constructor<? extends ActionSign> cstr = clazz.getConstructor(Block.class, UUID.class, String.class);
						ActionBlock aBlock = cstr.newInstance(block, uuid, permission);	
						aBlock.onLoad();
						registerBlock(aBlock); // register created class
						abeManager.addActionBlock(aBlock);
					} else {
						Constructor<? extends ActionSign> cstr = clazz.getConstructor(Block.class, UUID.class);
						ActionBlock aBlock = cstr.newInstance(block, uuid);	
						aBlock.onLoad();
						registerBlock(aBlock); // register created class
						abeManager.addActionBlock(aBlock);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// clear the loadedData
		this.loadedData = null;
	}
	
	
	/**
	 * Loads the blocks from the database. All registerClass calls must be called before this
	 */
	public void readBlocksFromDatabase() {
		Connection conn = null;
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			// select all actionblocks and their locations
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT uuid,permission,classtype,world,x,y,z FROM actionblock, location WHERE actionblock.serverID = ? AND location.serverID = ? AND uuid = id;");
			stmt.setString(1, KaranteeniCore.getServerIdentificator());
			stmt.setString(2, KaranteeniCore.getServerIdentificator());
			ResultSet set = stmt.executeQuery();
			
			// loop all the blocks from the database
			while(set.next()) {
				String uuidStr = set.getString(1);
				String permission = set.getString(2);
				String classType = set.getString(3);
				
				// get location
				String worldName = set.getString(4);
				double x = set.getDouble(5);
				double y = set.getDouble(6);
				double z = set.getDouble(7);
				
				LinkedList<Object> data = new LinkedList<Object>();
				UUID uuid = UUID.fromString(uuidStr);
				data.addAll(Arrays.asList(uuid, permission, classType, worldName, x, y, z));
				
				this.loadedData.add(data);
			}
		} catch(SQLException e) {
			e.printStackTrace();
			// ignored
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
	 * Registers a class to be used when loading blocks from database
	 * @param clazz class to use when loading
	 * @return true if class was registered, false if it is invalid or already registered
	 */
	@SuppressWarnings("unchecked")
	public boolean registerClass(Class<? extends ActionBlock> clazz) {
		// verify the class given is not null or abstract class
		if(clazz == null || Modifier.isAbstract(clazz.getModifiers())) return false;
		
		if(clazz.isAssignableFrom(ActionSign.class))
			return subSignClasses.add((Class<? extends ActionSign>) clazz);
		else
			return subBlockClasses.add(clazz);	
	}
	
	
	/**
	 * Returns the action assigned to this block
	 * @param location the location the action block is in
	 * @return found action block or null
	 */
	public ActionBlock getActionBlock(Location location) {
		Block block = location.getBlock();
		if(block == null)
			return null;
		else
			return getActionBlock(block);
	}
	
	
	/**
	 * Returns the action assigned to this block
	 * @param block used to retrieve the actionblock
	 * @return found action block or null
	 */
	public ActionBlock getActionBlock(Block block) {
		if(block == null) return null;
		List<MetadataValue> metadata = block.getMetadata(ActionBlock.ACTION_BLOCK_ID);
		if(metadata == null || metadata.isEmpty()) return null;
		
		for(MetadataValue meta : metadata)
		if(meta.getOwningPlugin().equals(plugin)) {
			UUID uuid = UUID.fromString(meta.asString());
			ActionBlock ab = this.actionBlocks.get(uuid);
			if(ab != null)
				return ab;
		}
		return null;
	}
	
	
	/**
	 * Initializes the database table for the actionblocks
	 */
	public static void initializeAndLoadDatabase() {
		Connection conn = null;
		try {
			conn = KaranteeniCore.getDatabaseConnector().openConnection();
			Statement st = conn.createStatement();
			st.execute("CREATE TABLE IF NOT EXISTS actionblock("+
					"uuid VARCHAR(64) NOT NULL, "+
					"serverID VARCHAR(64) NOT NULL, "+
					"permission VARCHAR(64) UNIQUE,"+
					"classtype VARCHAR(128) NOT NULL,"+
					"FOREIGN KEY (serverID) REFERENCES server(ID), "+
					"FOREIGN KEY (uuid) REFERENCES location(id),"+
					"PRIMARY KEY (uuid));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
