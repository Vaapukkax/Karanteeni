package net.karanteeni.karpet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.timers.KaranteeniTimer;
import net.karanteeni.karpet.worldguard.WorldGuardManager;

public class CarpetHandler implements KaranteeniTimer {
	private LinkedHashMap<Player, Carpet> carpets = new LinkedHashMap<Player, Carpet>();
	private HashMap<Player, Location> movementLocked = new HashMap<Player, Location>();
	private List<Material> availableBlocks;
	private List<Material> clippableBlocks;
	private Material[][] defaultLayout;
	private Karpet plugin;
	private WorldGuardManager wgm = null;
	
	public CarpetHandler(WorldGuardManager wgm) {
		super();
		this.plugin = Karpet.getPlugin(Karpet.class);
		
		this.wgm = wgm;
		
		initializeDatabaseTable();
		defaultLayout = loadDefaultLayout();
		availableBlocks = loadAvailableBlocks();
		clippableBlocks = loadClippableBlocks();
		Carpet.initialize(plugin, clippableBlocks.isEmpty()?null:clippableBlocks);
	}
	
	
	/**
	 * Returns the default layout to be used in the carpet
	 * @return the default carpet layout
	 */
	public Material[][] getDefaultLayout() {
		return this.defaultLayout;
	}
	
	
	/**
	 * Loads the default carpet layout to use when player has no previous layout
	 * @return default layout defined in the config
	 */
	private Material[][] loadDefaultLayout() {
		String defaultRow = Material.GLASS.name() + "," + 
				Material.GLASS.name() + "," + 
				Material.GLASS.name() + "," + 
				Material.GLASS.name() + "," + 
				Material.GLASS.name();
		Material[][] layout = new Material[5][5];
		
		// loop the area of blocks
		for(int i = 1; i <= 5; ++i) {
			String key = i+"";
			if(!plugin.getConfig().isSet(key)) {
				plugin.getConfig().set(key, defaultRow);
				plugin.saveConfig();
			}
			
			// row of the materials
			String row = plugin.getConfig().getString(key);
			String[] materials = row.split(",");
			for(int l = 0; l < 5; ++l)
				layout[i-1][l] = Material.valueOf(materials[l]);
		}
		
		return layout;
	}
	
	
	/**
	 * Initializes the database table to contain players carpet layouts
	 */
	private void initializeDatabaseTable() {
		Statement stmt = Karpet.getDatabaseConnector().getStatement();
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS carpet ( "
					+ "uuid VARCHAR(64) NOT NULL, "
					+ "layout VARCHAR(770) NOT NULL, "
					+ "tools VARCHAR(770), "
					+ "FOREIGN KEY (uuid) REFERENCES player(UUID),"
					+ "PRIMARY KEY (uuid));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Returns all blocks that can be used as materials in the creation of carpets
	 * @return blocks to use in carpets
	 */
	public List<Material> getCarpetMaterials() {
		return this.availableBlocks;
	}
	
	
	/**
	 * Returns all blocks that the carpet can override
	 * @return blocks that the carpet can go though
	 */
	public List<Material> getClippableBlocks() {
		return this.clippableBlocks;
	}
	
	
	/**
	 * Loads all blocks which are allowed to be used in carpet creation from the config
	 * @return list of materials allowed to be in the carpets
	 */
	private List<Material> loadAvailableBlocks() {
		List<Material> materials = new ArrayList<Material>();
		
		// initialize config
		if(!plugin.getConfig().isSet("carpet.allowed-blocks")) {
			plugin.getConfig().set("carpet.allowed-blocks", Arrays.asList(Material.GLASS.name(), Material.LIGHT_GRAY_STAINED_GLASS.name()));
			plugin.saveConfig();
		}
		
		List<String> materialList = plugin.getConfig().getStringList("carpet.allowed-blocks");
		
		// add all materials to a list
		for(String m : materialList)
			materials.add(Material.valueOf(m));
		return materials;
	}
	
	
	/**
	 * Loads all blocks which are allowed to be used in carpet creation from the config
	 * @return list of materials allowed to be in the carpets
	 */
	private List<Material> loadClippableBlocks() {
		List<Material> materials = new ArrayList<Material>();
		
		// initialize config
		if(!plugin.getConfig().isSet("carpet.clippable-blocks")) {
			plugin.getConfig().set("carpet.clippable-blocks", 
					Arrays.asList(
							Material.AIR.name(), 
							Material.CAVE_AIR.name(),
							Material.WATER.name(),
							Material.LAVA.name(),
							Material.TALL_GRASS.name(),
							Material.GRASS.name(),
							Material.DANDELION.name(),
							Material.POPPY.name(),
							Material.FERN.name(),
							Material.DEAD_BUSH.name(),
							Material.OAK_SAPLING.name()));
			plugin.saveConfig();
		}
		
		List<String> materialList = plugin.getConfig().getStringList("carpet.clippable-blocks");
		
		// add all materials to a list
		for(String m : materialList)
			materials.add(Material.valueOf(m));
		return materials;
	}
	
	
	/**
	 * Loads the 
	 * @param uuid
	 * @return
	 */
	public Material[][] getLayout(UUID uuid) {
		Statement stmt = Karpet.getDatabaseConnector().getStatement();
		try {
			ResultSet set = stmt.executeQuery("SELECT layout, tools FROM carpet WHERE uuid = '" + uuid.toString() + "';");
			
			if(set.next()) {
				// get and split the layout into parts
				String layoutCombined = set.getString(1);
				String[] layoutSplitted = layoutCombined.split(",");
				
				// if not enough blocks in layout use the default one 
				if(layoutSplitted.length != 25) {
					Material[][] mat = new Material[5][5];
					for(int i = 0; i < 5; ++i)
					for(int l = 0; l < 5; ++l)
						mat[i][l] = defaultLayout[i][l];
					return mat;
				}
				
				// the layout to be returned
				Material[][] layout = new Material[5][5];
				//Material[][] toolLayout = new Material[5][5];
				
				// get the material types of the layout from the splitted string and store them to the array
				for(int y = 0; y < 5; ++y)
				for(int x = 0; x < 5; ++x) {
					layout[x][y] = Material.valueOf(layoutSplitted[(5*y) + x]);
				}
				
				// return layout
				return layout;
			} else {
				Material[][] mat = new Material[5][5];
				for(int i = 0; i < 5; ++i)
				for(int l = 0; l < 5; ++l)
					mat[i][l] = defaultLayout[i][l];
				return mat;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Material[][] mat = new Material[5][5];
			for(int i = 0; i < 5; ++i)
			for(int l = 0; l < 5; ++l)
				mat[i][l] = defaultLayout[i][l];
			return mat;
		}
	}
	
	
	/**
	 * Sets the layout for a given player
	 * @param uuid
	 * @param layout
	 * @return true if change and save were successful, false otherwise
	 */
	public boolean setLayout(Player player, Material[][] layout) {
		if(carpets.containsKey(player)) {
			carpets.get(player).setLayout(layout);
		}
		
		// serialize the layout to a string
		String layoutString = "";
		for(int y = 0; y < 5; ++y)
		for(int x = 0; x < 5; ++x) {
			if(y == 4 && x == 4)
				layoutString += layout[x][y].name();
			else
				layoutString += layout[x][y].name() + ",";
		}
		
		// store the string to the database
		Statement stmt = Karpet.getDatabaseConnector().getStatement();
		try {
			stmt.executeUpdate("INSERT INTO carpet(uuid, layout) VALUES ('"
					+ player.getUniqueId().toString() + "', '"
					+ layoutString + "') ON DUPLICATE KEY UPDATE layout = '" + layoutString + "';");
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Adds a carpet to the given player
	 * @param player
	 */
	public void addCarpet(Player player) {
		Carpet carpet = new Carpet(getLayout(player.getUniqueId()));
		carpets.put(player, carpet);
	}
	
	
	/**
	 * Removes an active carpet from the given player
	 * @param player player to remove the carpet from
	 */
	public void removeCarpet(Player player) {
		Carpet carpet = carpets.remove(player);
		if(carpet != null)
			carpet.remove();
		movementLocked.remove(player);
	}
	
	
	/**
	 * Checks if the given player has a magic carpet on
	 * @param player player who is being checked
	 * @return true if carpet is on, false if not
	 */
	public boolean hasCarpet(Player player) {
		return carpets.containsKey(player);
	}
	
	
	/**
	 * Returns the world guard flag manager
	 * @return wg flag manager
	 */
	public WorldGuardManager getFlagManager() {
		return wgm;
	}
	
	
	@Override
	public void runTimer() {
		for(Entry<Player, Carpet> entry : carpets.entrySet()) {
			// check if the carpet is in allowed region
			if(wgm != null && !wgm.isCarpetAllowed(entry.getKey().getLocation())) {
				removeCarpet(entry.getKey());
				Karpet.getMessager().sendActionBar(
						entry.getKey(), 
						Sounds.EQUIP.get(), 
						Karpet.getTranslator().getRandomTranslation(plugin, entry.getKey(), "carpet.disabled"));
				continue;
			}
			
			// draw the carpet and if the player is sneaking, lower the carpet
			if(entry.getKey().isSneaking() && entry.getKey().isOnGround()) {
				entry.getValue().draw(entry.getKey().getLocation().subtract(0, 2, 0));
				// prevent carpet from moving up
				movementLocked.put(entry.getKey(), entry.getKey().getLocation());
			} else {
				// if player is trying to descend and has not done so yet, wait
				if(movementLocked.containsKey(entry.getKey())) {
					// if player is not sneaking anymore, prevent movement lock
					if(entry.getKey().isSneaking()) {
						if(movementLocked.get(entry.getKey()).getY() - 0.3 <= entry.getKey().getLocation().getY())
							continue;
					} else {
						movementLocked.remove(entry.getKey());
					}
				}
				
				entry.getValue().draw(entry.getKey().getLocation().subtract(0, 1, 0));
			}
		}
	}

	
	@Override
	public void timerStopped() {
		Iterator<Entry<Player, Carpet>> iter = carpets.entrySet().iterator();
		
		// remove all carpets and clear the map of them
		while(iter.hasNext()) {
			Entry<Player, Carpet> pair = iter.next();
			pair.getValue().remove();
			
			iter.remove();
			if(pair.getValue() != null)
				pair.getValue().remove();
			movementLocked.remove(pair.getKey());
		}
	}

	
	@Override
	public void timerWait() { }
}