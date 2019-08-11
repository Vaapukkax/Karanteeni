package net.karanteeni.karanteeniperms.groups.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.config.YamlConfig;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

public class Group {
	//private String 				prefix 					= "";
	//private String 				suffix 					= "";
	private GroupData			groupData;
	private final String 		ID;
	private boolean 			def;
	//private List<Permission> 	permissions;
	private List<Group> 		inheritedGroups 		= new ArrayList<Group>();
	private KaranteeniPlugin 	pl;
	private static YamlConfig 	groupConfig;
	private static final String GROUP_SECTION 			= "Groups";
	private static final String RANK_SHORT_NAME_DEST 	= GROUP_SECTION + ".%s.shortname";
	private static final String RANK_LONG_NAME_DEST 	= GROUP_SECTION + ".%s.name";
	private static final String SAVE_GROUP_LOCATION 	= GROUP_SECTION + ".%s.";
	private BiConsumer<UUID, String> addPermission;
	private BiConsumer<UUID, String> removePermission;
	protected static final String GROUP_STRING_TAG		= "%group%";
	
	/**
	 * Creates a new group with info
	 * @param ID this is referred when modifying and accessing this group
	 * @param rankName default name for rank in translations
	 * @param rankShortName default short name for rank in translations
	 * @param prefix prefix before player name
	 * @param suffix suffix after player name
	 * @param defaultGroup is this group the default when joined
	 * @param perms permissions the group has
	 */
	public Group(
			String ID, 
			String rankName, 
			String rankShortName, 
			String prefix, 
			String suffix, 
			boolean defaultGroup,
			List<String> perms,
			BiConsumer<UUID,String> addPermission,
			BiConsumer<UUID,String> removePermission)
	{
		pl = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		this.ID = ID;
		
		KaranteeniPerms.getTranslator().registerTranslation(
				pl, 
				String.format(RANK_LONG_NAME_DEST, ID), 
				rankName);
		KaranteeniPerms.getTranslator().registerTranslation(
				pl, 
				String.format(RANK_SHORT_NAME_DEST, ID), 
				rankShortName);
		
		this.addPermission = addPermission;
		this.removePermission = removePermission;
		this.groupData = new GroupData(prefix, suffix, perms);
		//this.permissions = perms;
		this.def = defaultGroup;
		if(groupConfig == null)
			groupConfig = new YamlConfig(pl, "Groups.yml");
	}
	
	
	/**
	 * Check if this group is default group
	 * @return
	 */
	public boolean isDefault()
	{ return this.def; }
	
	
	/**
	 * Add a new group which this group will be inheriting
	 * @param group
	 * @return Was this group not already inherited, true if inheritance was successful, false if already inherited
	 */
	public boolean inheritGroup(final Group group) { 
		boolean addedNew = inheritedGroups.add(group);
		if(!addedNew) //If group is already inherited, don't inherit it again
			return false;
		
		//Loop all permissions of parent group
		/*for(String perm : group.getPermissions())
			this.addPermission(perm, false);
		
		//Get all inherited groups of inherited group
		Deque<Group> queue = new ArrayDeque<Group>();
		queue.addAll(group.getInheritedGroups()); //Get inherited groups from inherited group
		
		while(!queue.isEmpty())
		{
			Group g = queue.pop();
			queue.addAll(getInheritedGroups());
			for(String perm : g.getPermissions())
				this.addPermission(perm, false);
		}*/
		
		return true; //Inheritance of group(s) was successful
	}
	
	
	/**
	 * Saves custom value to config of this group
	 * @param plugin plugin of which data this is
	 * @param path path to data
	 * @param value value of data
	 */
	public void setCustomData(Plugin plugin, String path, Object value) {
		groupConfig.getConfig().set(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path, 
				value);
	}
	
	
	/**
	 * Checks if the custom data in group plugin section is set
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return was the data set
	 */
	public boolean isCustomDataSet(Plugin plugin, String path) {
		return groupConfig.getConfig().isSet(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public int getCustomInt(Plugin plugin, String path) {
		return groupConfig.getConfig().getInt(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public String getCustomString(Plugin plugin, String path) {
		return groupConfig.getConfig().getString(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<String> getCustomStringList(Plugin plugin, String path) {
		return groupConfig.getConfig().getStringList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Integer> getCustomIntList(Plugin plugin, String path) {
		return groupConfig.getConfig().getIntegerList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public boolean getCustomBoolean(Plugin plugin, String path) {
		return groupConfig.getConfig().getBoolean(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Boolean> getCustomBooleanList(Plugin plugin, String path) {
		return groupConfig.getConfig().getBooleanList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public Object getCustomData(Plugin plugin, String path) {
		return groupConfig.getConfig().get(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<?> getCustomDataList(Plugin plugin, String path) {
		return groupConfig.getConfig().getList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public double getCustomDouble(Plugin plugin, String path) {
		return groupConfig.getConfig().getDouble(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}

	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Double> getCustomDoubleList(Plugin plugin, String path) {
		return groupConfig.getConfig().getDoubleList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public long getCustomLong(Plugin plugin, String path) {
		return groupConfig.getConfig().getLong(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Long> getCustomLongList(Plugin plugin, String path) {
		return groupConfig.getConfig().getLongList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public ItemStack getCustomItemStack(Plugin plugin, String path) {
		return groupConfig.getConfig().getItemStack(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Map<?,?>> getCustomMapList(Plugin plugin, String path) {
		return groupConfig.getConfig().getMapList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Character> getCustomCharacterList(Plugin plugin, String path) {
		return groupConfig.getConfig().getCharacterList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public Vector getCustomVector(Plugin plugin, String path) {
		return groupConfig.getConfig().getVector(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getName()+"."+path);
	}
	
	
	/**
	 * Get all the groups directly inherited by this group
	 * @return
	 */
	public Collection<Group> getInheritedGroups()
	{ return this.inheritedGroups; }
	
	
	/**
	 * Get the ID of this group,
	 * no modifications allowed
	 * @return
	 */
	public String getID()
	{ return this.ID; }
	
	
	/**
	 * Returns the raw format of players prefix
	 * §6[§7%group%§6]
	 * @return Groups prefix in its raw format
	 */
	public String getRawPrefix()
	{ return this.groupData.getPrefix(); }
	
	
	/**
	 * Returns the translated prefix of group
	 * @param locale which locale to be translated to
	 * @param shortened should this return the short rank name
	 * @return Prefix of group like [GroupName] or [GN] if shortened
	 * set to false
	 */
	public String getPrefix(Locale locale, boolean shortened) {  
		String rankName = null; 
		
		if(!shortened)
			rankName = KaranteeniPerms.getTranslator().getTranslation(
				pl, locale, String.format(RANK_LONG_NAME_DEST, ID));
		else
			rankName = KaranteeniPerms.getTranslator().getTranslation(
				pl, locale, String.format(RANK_SHORT_NAME_DEST, ID));
		
		return this.groupData.getPrefix().replace(GROUP_STRING_TAG, rankName); 
	}
	
	
	/**
	 * Returns the translated prefix of group
	 * @param player player to which locale should this be translated to
	 * @param shortened should this return the short rank name
	 * @return Prefix of group like [GroupName] or [GN] if shortened
	 * set to false
	 */
	public String getPrefix(Player player, boolean shortened) {
		String rankName = null; 
		
		if(!shortened)
			rankName = KaranteeniPerms.getTranslator().getTranslation(
				pl, player, String.format(RANK_LONG_NAME_DEST, ID));
		else
			rankName = KaranteeniPerms.getTranslator().getTranslation(
				pl, player, String.format(RANK_SHORT_NAME_DEST, ID));
		
		return this.groupData.getPrefix().replace(GROUP_STRING_TAG, rankName);
	}
	
	
	/**
	 * Returns the translated prefix of group
	 * @param sender Commandsender to which language should the prefix be translated to
	 * @param shortened should this return the short rank name
	 * @return Prefix of group like [GroupName] or [GN] if shortened
	 * set to false
	 */
	public String getPrefix(CommandSender sender, boolean shortened) { 
		String rankName = null; 
		
		if(!shortened)
			rankName = KaranteeniPerms.getTranslator().getTranslation(
				pl, sender, String.format(RANK_LONG_NAME_DEST, ID));
		else
			rankName = KaranteeniPerms.getTranslator().getTranslation(
				pl, sender, String.format(RANK_SHORT_NAME_DEST, ID));
		
		return this.groupData.getPrefix().replace(GROUP_STRING_TAG, rankName); 
	}
	
	
	/**
	 * Sets the groups prefix.
	 * @param newPrefix It is recommended to use %group% to
	 * allow translation engine to set the group name
	 * @return Was the group save successful
	 */
	public boolean setPrefix(String newPrefix) {
		this.groupData.setPrefix(newPrefix);
		return this.saveGroup();
	}
	
	
	/**
	 * Sets the groups suffix
	 * @param newSuffix new suffix for the group
	 * @return was the save successful
	 */
	public boolean setSuffix(String newSuffix) {
		this.groupData.setSuffix(newSuffix);
		return this.saveGroup();
	}
	
	
	/**
	 * Returns the suffix for this rank
	 * @return
	 */
	public String getSuffix()
	{ return this.groupData.getSuffix(); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName(Locale locale)
	{ return KaranteeniPerms.getTranslator().getTranslation(pl, locale, String.format(RANK_LONG_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName(Player player)
	{  return KaranteeniPerms.getTranslator().getTranslation(pl, player, String.format(RANK_LONG_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName(CommandSender sender)
	{  return KaranteeniPerms.getTranslator().getTranslation(pl, sender, String.format(RANK_LONG_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName() { 
		return KaranteeniPerms.getTranslator().getTranslation(pl, 
				KaranteeniPerms.getTranslator().getDefaultLocale(),
				String.format(RANK_LONG_NAME_DEST, ID));
	}
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName(Locale locale)
	{ return KaranteeniPerms.getTranslator().getTranslation(pl, locale, String.format(RANK_SHORT_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName(Player player)
	{ return KaranteeniPerms.getTranslator().getTranslation(pl, player, String.format(RANK_SHORT_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName(CommandSender sender)
	{ return KaranteeniPerms.getTranslator().getTranslation(pl, sender, String.format(RANK_SHORT_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName() { 
		return KaranteeniPerms.getTranslator().getTranslation(pl, 
				KaranteeniPerms.getTranslator().getDefaultLocale(),
				String.format(RANK_SHORT_NAME_DEST, ID));
	}
	
	
	/**
	 * Sets the name of the group to given value in the given locale
	 * @param locale
	 * @param name
	 * @param longVersion
	 */
	public void setName(Locale locale, String name, boolean longVersion) {
		if(longVersion) {
			KaranteeniPerms.getTranslator().setTranslation(this.pl, locale, 
					String.format(RANK_LONG_NAME_DEST, ID), name);
			//this.groupData.setGroupName(name);
		} else {
			KaranteeniPerms.getTranslator().setTranslation(this.pl, locale, 
					String.format(RANK_SHORT_NAME_DEST, ID), name);
			//this.groupData.setGroupShortName(name);
		}
	}
	
	
	/**
	 * Does this group or inherited groups have the given permission
	 * @param perm
	 * @return
	 */
	public boolean hasPermission(Permission perm) { 
		if(!this.groupData.hasPermission(perm.getName())) {
			for(Group group : inheritedGroups) {
				if(group.hasPermission(perm))
					return true;
			}
			return false;
		}
		return true;
	}
	
	
	/**
	 * Does this group or inherited groups have the given permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(String permission) { 
		if(!this.groupData.hasPermission(permission)) {
			for(Group group : inheritedGroups) {
				if(group.hasPermission(permission))
					return true;
			}
			return false;
		}
		return true; 
	}
	
	
	/**
	 * Get all the permissions this group and inherited groups have
	 * @return
	 */
	public List<String> getPermissions() { 
		List<String> perms = this.groupData.getPermissions();
		for(Group group : inheritedGroups) {
			perms.addAll(group.getPermissions());
		}
		return perms;
	}
	
	
	/**
	 * Saves the group variables to the config file
	 * @return
	 */
	public boolean saveGroup() {
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"default", this.def);
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"prefix", this.groupData.getPrefix());
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"suffix", this.groupData.getSuffix());
		
		List<String> inheritedIDs = new ArrayList<String>();
		List<String> perms = new ArrayList<String>();
		
		for(Group g : this.inheritedGroups)
			inheritedIDs.add(g.ID);
		
		for(String perm : this.groupData.getPermissions())
			perms.add(perm);
		
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"inherited-groups", inheritedIDs);
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"permissions", perms);
		return groupConfig.save();
	}
	
	
	/**
	 * Generates a new default group to config and saves it.
	 * @return Was the save successful
	 */
	private static boolean generateDefaultGroup(
			BiConsumer<UUID,String> addPermission,
			BiConsumer<UUID,String> removePermission)
	{
		Group group = new Group("myGroup", 
				"DefaultGroup", 
				"DG", 
				"§f[§7"+GROUP_STRING_TAG+"§f] ", 
				" §6> §f", 
				true, 
				new ArrayList<String>(),
				addPermission,
				removePermission);
		
		return group.saveGroup();
	}
	
	
	/**
	 * Add a new permission to this group. Adds permission but
	 * when saving fails, the permission will be gone after restart
	 * @param perm
	 * @return was the new permission inserted or did it already exist
	 */
	public boolean addPermission(String perm, boolean save) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		if(perms.getGroupModel() != null) //If groupmodel is null, this is a reload
		for(Player player : Bukkit.getOnlinePlayers()) { //Loop each player in this group
			//If KPlayer is not loaded, don't try to add permissions
			if(KPlayer.getKPlayer(player) != null) {
				//KPlayer kp = KPlayer.getKPlayer(player);
				if(perms.getPlayerModel().getLocalGroup(player).getID().equals(this.ID))
					this.addPermission.accept(player.getUniqueId(), perm);
			}
		}
		
		if(this.groupData.hasPermission(perm))
			return false;
		if(!this.groupData.addPermission(perm))
			return false;
		if(save)
			return this.saveGroup();
		return false;
	}
	
	
	/**
	 * Removes a given permission from group
	 * @param perm
	 * @return True if removal was successful, false if nothing to remove
	 */
	public boolean removePermission(String perm, boolean save) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		for(Player player : Bukkit.getOnlinePlayers()) //Loop each player in this group
			if(perms.getPlayerModel().getLocalGroup(player).getID().equals(this.ID))
				this.removePermission.accept(player.getUniqueId(), perm);
		
		if(!this.groupData.removePermission(perm))
			return false;
		if(save)
			return this.saveGroup();
		return false;
	}
	
	
	/**
	 * Load all the groups that are present in the config and return them
	 * @return All groups that could be loaded from the config
	 */
	public static Collection<Group> getGroupsFromConfig(
			KaranteeniPerms plugin,
			BiConsumer<UUID, String> addPermission,
			BiConsumer<UUID, String> removePermission) throws Exception
	{
		if(groupConfig == null)
			groupConfig = new YamlConfig(plugin, "Groups.yml");
		
		HashMap<String, Group> groups = new HashMap<String, Group>();
		
		ConfigurationSection groupSecs = 
				groupConfig.getConfig().getConfigurationSection(GROUP_SECTION);
		
		//Verify that the at least one group exists. If not, try to create one
		if(groupSecs == null || groupSecs.getKeys(false) == null)
			if(!generateDefaultGroup(addPermission, removePermission))
				throw new Exception("Failed to load/generate any groups!");
			else
				groupSecs = groupConfig.getConfig().getConfigurationSection(GROUP_SECTION);
		
		//Which group inherits which groups
		HashMap<String, List<String>> groupInheritance = new HashMap<String, List<String>>();
		
		//Run through all the groups
		for(String key : groupSecs.getKeys(false)) {
			boolean def = groupSecs.getBoolean(key+".default");
			String prefix = groupSecs.getString(key+".prefix");
			String suffix = groupSecs.getString(key+".suffix");
			groupInheritance.put(key, groupSecs.getStringList(key+".inherited-groups"));
			List<String> permissions = groupSecs.getStringList(key+".permissions");
			
			//Check if all the group has all values loaded
			if(prefix == null)
				continue;
			if(suffix == null)
				continue;
			
			//Loud thi gruups lonk ant short neim
			String longName = KaranteeniPerms.getTranslator().getTranslation(plugin, 
					KaranteeniPerms.getTranslator().getDefaultLocale(), 
					String.format(RANK_LONG_NAME_DEST, key));
			String shortName = KaranteeniPerms.getTranslator().getTranslation(plugin, 
					KaranteeniPerms.getTranslator().getDefaultLocale(), 
					String.format(RANK_SHORT_NAME_DEST, key));
			
			if(longName == null || longName.length() == 0)
				longName = key;
			
			if(shortName == null || shortName.length() == 0)
				shortName = key;
			
			//Create new Group
			Group g = new Group(
					key,
					longName,
					shortName,
					prefix,
					suffix,
					def,
					permissions,
					addPermission,
					removePermission);
			
			//Add the new group to the map
			groups.put(key, g);
		}
		
		
		//Set the group inheritance correct
		for(Entry<String, List<String>> pair : groupInheritance.entrySet()) {
			Group g = groups.get(pair.getKey());
			if(g == null)
				continue;
			
			//Inherit all groups
			for(String groupID : pair.getValue())
				if(groups.get(groupID) != null)
					g.inheritGroup(groups.get(groupID));
				else
					Bukkit.getLogger().log(Level.CONFIG, "Cannot inherit nonexistent group '"+groupID+"'");
		}
		
		return groups.values();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group))
			return false;
		return ((Group)obj).ID.equals(this.ID);
	}
}