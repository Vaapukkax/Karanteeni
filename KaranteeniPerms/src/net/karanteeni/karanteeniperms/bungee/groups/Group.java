package net.karanteeni.karanteeniperms.bungee.groups;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.karanteeni.bungee.core.configuration.YamlConfig;
import net.karanteeni.core.data.ObjectPair;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class Group implements Comparable<Group> {
	private GroupData			groupData;
	private final String 		ID;
	private boolean 			def;
	//private List<Permission> 	permissions;
	private TreeSet<Group> 	inheritedGroups 		= new TreeSet<Group>();
	// entities using this group and their permission attachments connected to this group
	private HashMap<UUID, ObjectPair<Integer, PermissionAttachment>> attachments = new HashMap<UUID, ObjectPair<Integer, PermissionAttachment>>();
	private KaranteeniPermsBungee 	pl;
	private static YamlConfig 	groupConfig;
	private static final String GROUP_SECTION 			= "Groups";
	private static final String RANK_SHORT_NAME_DEST 	= GROUP_SECTION + ".%s.shortname";
	private static final String RANK_LONG_NAME_DEST 	= GROUP_SECTION + ".%s.name";
	private static final String SAVE_GROUP_LOCATION 	= GROUP_SECTION + ".%s.";
	//private BiConsumer<UUID, String> addPermission;
	//private BiConsumer<UUID, String> removePermission;
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
			List<String> spigotPermissions,
			List<String> bungeePermissions)
	{
		pl = KaranteeniPermsBungee.getInstance();
		this.ID = ID;
		
		KaranteeniPermsBungee.getTranslator().registerTranslation(
				pl, 
				String.format(RANK_LONG_NAME_DEST, ID), 
				rankName);
		KaranteeniPermsBungee.getTranslator().registerTranslation(
				pl, 
				String.format(RANK_SHORT_NAME_DEST, ID), 
				rankShortName);
		
		/*this.addPermission = addPermission;
		this.removePermission = removePermission;*/
		this.groupData = new GroupData(prefix, suffix, rankName, rankShortName, spigotPermissions, bungeePermissions);
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
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path, 
				value);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public int getCustomInt(Plugin plugin, String path) {
		return groupConfig.getConfig().getInt(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public String getCustomString(Plugin plugin, String path) {
		return groupConfig.getConfig().getString(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<String> getCustomStringList(Plugin plugin, String path) {
		return groupConfig.getConfig().getStringList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public boolean getCustomBoolean(Plugin plugin, String path) {
		return groupConfig.getConfig().getBoolean(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Boolean> getCustomBooleanList(Plugin plugin, String path) {
		return groupConfig.getConfig().getBooleanList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public Object getCustomData(Plugin plugin, String path) {
		return groupConfig.getConfig().get(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<?> getCustomDataList(Plugin plugin, String path) {
		return groupConfig.getConfig().getList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public double getCustomDouble(Plugin plugin, String path) {
		return groupConfig.getConfig().getDouble(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}

	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Double> getCustomDoubleList(Plugin plugin, String path) {
		return groupConfig.getConfig().getDoubleList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public long getCustomLong(Plugin plugin, String path) {
		return groupConfig.getConfig().getLong(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	
	
	/**
	 * Returns the custom in from path
	 * @param plugin plugin of which path
	 * @param path path to data
	 * @return value at path
	 */
	public List<Long> getCustomLongList(Plugin plugin, String path) {
		return groupConfig.getConfig().getLongList(
				String.format(SAVE_GROUP_LOCATION, ID)+"CustomData."+plugin.getDescription().getName()+"."+path);
	}
	

	/**
	 * Get all the groups directly inherited by this group
	 * @return
	 */
	public Set<Group> getInheritedGroups()
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
			rankName = KaranteeniPermsBungee.getTranslator().getTranslation(
				pl, locale, String.format(RANK_LONG_NAME_DEST, ID));
		else
			rankName = KaranteeniPermsBungee.getTranslator().getTranslation(
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
	public String getPrefix(ProxiedPlayer player, boolean shortened) {
		String rankName = null; 
		
		if(!shortened)
			rankName = KaranteeniPermsBungee.getTranslator().getTranslation(
				pl, player, String.format(RANK_LONG_NAME_DEST, ID));
		else
			rankName = KaranteeniPermsBungee.getTranslator().getTranslation(
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
			rankName = KaranteeniPermsBungee.getTranslator().getTranslation(
				pl, sender, String.format(RANK_LONG_NAME_DEST, ID));
		else
			rankName = KaranteeniPermsBungee.getTranslator().getTranslation(
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
	{ return KaranteeniPermsBungee.getTranslator().getTranslation(pl, locale, String.format(RANK_LONG_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName(ProxiedPlayer player)
	{  return KaranteeniPermsBungee.getTranslator().getTranslation(pl, player, String.format(RANK_LONG_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName(CommandSender sender)
	{  return KaranteeniPermsBungee.getTranslator().getTranslation(pl, sender, String.format(RANK_LONG_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getName() { 
		return KaranteeniPermsBungee.getTranslator().getTranslation(pl, 
				KaranteeniPermsBungee.getTranslator().getDefaultLocale(),
				String.format(RANK_LONG_NAME_DEST, ID));
	}
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName(Locale locale)
	{ return KaranteeniPermsBungee.getTranslator().getTranslation(pl, locale, String.format(RANK_SHORT_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName(ProxiedPlayer player)
	{ return KaranteeniPermsBungee.getTranslator().getTranslation(pl, player, String.format(RANK_SHORT_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName(CommandSender sender)
	{ return KaranteeniPermsBungee.getTranslator().getTranslation(pl, sender, String.format(RANK_SHORT_NAME_DEST, ID)); }
	
	
	/**
	 * Returns the name for this rank
	 * @return
	 */
	public String getShortName() { 
		return KaranteeniPermsBungee.getTranslator().getTranslation(pl, 
				KaranteeniPermsBungee.getTranslator().getDefaultLocale(),
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
			KaranteeniPermsBungee.getTranslator().setTranslation(this.pl, locale, 
					String.format(RANK_LONG_NAME_DEST, ID), name);
		} else {
			KaranteeniPermsBungee.getTranslator().setTranslation(this.pl, locale, 
					String.format(RANK_SHORT_NAME_DEST, ID), name);
		}
	}
	
	
	/**
	 * Does this group or inherited groups have the given permission
	 * @param perm
	 * @return
	 */
	public boolean hasPermission(ExtendedPermission perm, boolean spigotSide) { 
		if(!this.groupData.hasPermission(perm, spigotSide)) {
			for(Group group : inheritedGroups) {
				if(group.hasPermission(perm, spigotSide))
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
	public boolean hasPermission(String permission, boolean spigotSide) { 
		if(!this.groupData.hasPermission(permission, spigotSide)) {
			for(Group group : inheritedGroups) {
				if(group.hasPermission(permission, spigotSide))
					return true;
			}
			return false;
		}
		return true; 
	}
	
	
	/**
	 * Get the permissions of this group
	 * @return
	 */
	public LinkedList<ExtendedPermission> getPermissions(boolean spigotSide) {
		if(spigotSide)
			return this.groupData.getSpigotPermissions();
		else
			return this.groupData.getBungeePermissions();
	}
	
	
	/**
	 * Get all the permissions this group and inherited groups have
	 * @return
	 */
	public LinkedList<ExtendedPermission> getFullPermissions(boolean spigotSide) {
		LinkedList<ExtendedPermission> perms = null;
		
		if(spigotSide) {
			perms = this.groupData.getSpigotPermissions();
		} else {
			perms = this.groupData.getBungeePermissions();
		}
		
		for(Group group : inheritedGroups) {
			perms.addAll(group.getPermissions(spigotSide));
		}
		return perms;		
	}
	
	
	/**
	 * Registers this player to use this group
	 * @param player player to use the permissions of this group
	 */
	protected void registerUser(ProxiedPlayer player) {
		// register inherited groups
		for(Group group : inheritedGroups)
			group.registerUser(player);
		PermissionPlayer pPlayer = this.pl.getPlayerModel().getPermissionPlayer(player.getUniqueId());
		
		PermissionAttachment attch = pPlayer.addAttachment();
		// if player already has this attachment, increase the counter of times this attachment has been inherited
		if(attachments.containsKey(player.getUniqueId())) {
			ObjectPair<Integer, PermissionAttachment> entry = attachments.get(player.getUniqueId());
			entry.first += 1;
		} else {
			// add permissions to this player
			for(ExtendedPermission perm : this.groupData.getBungeePermissions())
				attch.addPermission(perm);
				
			attachments.put(player.getUniqueId(), new ObjectPair<Integer, PermissionAttachment>(1, attch));
		}
	}
	
	
	/**
	 * Unregisters user from using this group. If other groups also use this those need to be unregistered too
	 * @param player player to remove from registration
	 */
	protected boolean unregisterUser(ProxiedPlayer player) {
		if(!attachments.containsKey(player.getUniqueId()))
			return false;
		ObjectPair<Integer, PermissionAttachment> pair = attachments.get(player.getUniqueId());
		pair.first -= 1;
		
		PermissionPlayer pPlayer = this.pl.getPlayerModel().getPermissionPlayer(player.getUniqueId());
		
		// if nothing uses this group remove the permissions of this group from player
		if(pair.first == 0) {
			attachments.remove(player.getUniqueId());
			pPlayer.removeAttachment(pair.second);
			
			for(Group group : inheritedGroups)
				group.unregisterUser(player);
		}
		
		return true;
	}
	
	
	/**
	 * Removes the attachments from the memory for the given player
	 * @param player player to remove from attachments
	 */
	protected void destroyUser(ProxiedPlayer player) {
		ObjectPair<Integer, PermissionAttachment> pair = this.attachments.remove(player.getUniqueId());
		PermissionPlayer pPlayer = this.pl.getPlayerModel().getPermissionPlayer(player.getUniqueId());
		
		if(pair != null) {
			pPlayer.removeAttachment(pair.second);
			for(Group group : inheritedGroups)
				group.destroyUser(player);			
		}
	}
	
	
	/**
	 * Saves the group variables to the config file
	 * @return
	 */
	public boolean saveGroup() {
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"default", this.def);
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"prefix", this.groupData.getPrefix());
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"suffix", this.groupData.getSuffix());
		
		List<String> inheritedIDs = new LinkedList<String>();
		LinkedList<String> spigotPerms = new LinkedList<String>();
		LinkedList<String> bungeePerms = new LinkedList<String>();
		
		for(Group g : this.inheritedGroups)
			inheritedIDs.add(g.ID);
		
		for(ExtendedPermission perm : this.groupData.getSpigotPermissions()) {			
			spigotPerms.add(perm.toString());
		}
		
		for(ExtendedPermission perm : this.groupData.getBungeePermissions()) {			
			bungeePerms.add(perm.toString());
		}
		
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"inherited-groups", inheritedIDs);
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"bungee-permissions", bungeePerms);
		groupConfig.getConfig().set(String.format(SAVE_GROUP_LOCATION, ID)+"spigot-permissions", spigotPerms);
		return groupConfig.saveConfig();
	}
	
	
	/**
	 * Generates a new default group to config and saves it.
	 * @return Was the save successful
	 */
	private static boolean generateDefaultGroup() {
		Group group = new Group("myGroup", 
				"DefaultGroup", 
				"DG", 
				"§f[§7"+GROUP_STRING_TAG+"§f] ", 
				" §6> §f", 
				true, 
				new ArrayList<String>(),
				new ArrayList<String>());
		
		return group.saveGroup();
	}
	
	
	/**
	 * Add a new permission to this group. Adds permission but
	 * when saving fails, the permission will be gone after restart
	 * @param perm
	 * @return was the new permission inserted or did it already exist
	 */
	public boolean addPermission(String perm, boolean save, boolean spigotSide) {
		KaranteeniPermsBungee perms = KaranteeniPermsBungee.getInstance();
		if(perms.getGroupList() != null) //If groupmodel is null, this is a reload
		
		if(this.groupData.hasPermission(perm, spigotSide))
			return false;
		if(!this.groupData.addPermission(perm, spigotSide))
			return false;
		if(save) {
			if(this.saveGroup()) {
				if(!spigotSide)
				for(ObjectPair<Integer, PermissionAttachment> attch : this.attachments.values())
					attch.second.addPermission(perm);
				return true;
			} else {
				return false;
			}
		}
		
		if(!spigotSide)
		for(ObjectPair<Integer, PermissionAttachment> attch : this.attachments.values())
			attch.second.addPermission(perm);
		return true;
	}
	
	
	/**
	 * Removes a given permission from group
	 * @param perm
	 * @return True if removal was successful, false if nothing to remove
	 */
	public boolean removePermission(String perm, boolean save, boolean spigotSide) {
		boolean permissionRemoved = this.groupData.removePermission(perm, spigotSide);
		if(!permissionRemoved)
			return false;
		
		if(save) {
			if(this.saveGroup()) {
				if(!spigotSide)
				for(ObjectPair<Integer, PermissionAttachment> pairs : this.attachments.values())
					pairs.second.removePermission(perm);
				return true;
			} else {
				return false;
			}
		}
		
		if(!spigotSide)
		for(ObjectPair<Integer, PermissionAttachment> pairs : this.attachments.values())
			pairs.second.removePermission(perm);
		return true;
	}
	
	
	/**
	 * Load all the groups that are present in the config and return them
	 * @return All groups that could be loaded from the config
	 */
	public static Collection<Group> getGroupsFromConfig(
			KaranteeniPermsBungee plugin) throws Exception {
		if(groupConfig == null)
			groupConfig = new YamlConfig(plugin, "Groups.yml");
		
		HashMap<String, Group> groups = new HashMap<String, Group>();
		
		Configuration groupSecs = 
				groupConfig.getConfig().getSection(GROUP_SECTION);
		
		//Verify that the at least one group exists. If not, try to create one
		if(groupSecs == null || groupSecs.getKeys() == null)
			if(!generateDefaultGroup())
				throw new Exception("Failed to load/generate any groups!");
			else
				groupSecs = groupConfig.getConfig().getSection(GROUP_SECTION);
		
		//Which group inherits which groups
		HashMap<String, List<String>> groupInheritance = new HashMap<String, List<String>>();
		
		//Run through all the groups
		for(String key : groupSecs.getKeys()) {
			boolean def = groupSecs.getBoolean(key+".default");
			String prefix = groupSecs.getString(key+".prefix");
			String suffix = groupSecs.getString(key+".suffix");
			groupInheritance.put(key, groupSecs.getStringList(key+".inherited-groups"));
			List<String> spigotPermissions = groupSecs.getStringList(key+".spigot-permissions");
			List<String> bungeePermissions = groupSecs.getStringList(key+".bungee-permissions");
			
			//Check if all the group has all values loaded
			if(prefix == null)
				continue;
			if(suffix == null)
				continue;
			
			//Loud thi gruups lonk ant short neim
			String longName = KaranteeniPermsBungee.getTranslator().getTranslation(plugin, 
					KaranteeniPermsBungee.getTranslator().getDefaultLocale(), 
					String.format(RANK_LONG_NAME_DEST, key));
			String shortName = KaranteeniPermsBungee.getTranslator().getTranslation(plugin, 
					KaranteeniPermsBungee.getTranslator().getDefaultLocale(), 
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
					spigotPermissions,
					bungeePermissions);
			
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
					System.out.println("Cannot inherit nonexistent group '"+groupID+"'");
		}
		
		return groups.values();
	}
	
	
	/**
	 * Converts to spigot sided bungeegroup ready to send
	 * @return
	 */
	public byte[] convertToSpigotBungeeGroup() throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		// write all singular data
		// ID
		out.writeUTF(ID);
		out.writeUTF(this.getRawPrefix());
		out.writeUTF(this.getSuffix());
		out.writeUTF(this.getName());
		out.writeUTF(this.getShortName());
		
		// write locale count
		out.writeByte(KaranteeniPermsBungee.getTranslator().getLocales().size());
		// write locales
		for(Locale locale : KaranteeniPermsBungee.getTranslator().getLocales())
			out.writeUTF(locale.toLanguageTag());
		
		// write rank long names in every language
		for(Locale locale : KaranteeniPermsBungee.getTranslator().getLocales())
			out.writeUTF(this.getName(locale));
		
		// write rank short names in every language
		for(Locale locale : KaranteeniPermsBungee.getTranslator().getLocales())
			out.writeUTF(this.getShortName(locale));
		
		// write amount of permissions
		out.writeInt(this.groupData.getSpigotPermissions().size());
		// write permissions
		for(ExtendedPermission permission : this.groupData.getSpigotPermissions())
			out.writeUTF(permission.toString());
		
		// write amount of groups inherited
		out.writeByte(this.inheritedGroups.size());
		for(Group group : this.inheritedGroups)
			out.writeUTF(group.ID);
		
		return b.toByteArray();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group))
			return false;
		return ((Group)obj).ID.equals(this.ID);
	}


	@Override
	public int compareTo(Group group) {
		return ID.compareTo(group.ID);
	}
}