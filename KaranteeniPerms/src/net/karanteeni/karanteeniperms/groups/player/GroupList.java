package net.karanteeni.karanteeniperms.groups.player;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

public class GroupList {
	private Group defaultGroup;
	private TreeMap<String, Group> groups = new TreeMap<String, Group>(String.CASE_INSENSITIVE_ORDER);
	private TreeMap<String, BungeeGroup> bungeeGroups = new TreeMap<String, BungeeGroup>(String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * Initializes the grouphandler
	 */
	/*public GroupList(Group defaultGroup)
	{ this.defaultGroup = defaultGroup; }*/
	
	/**
	 * Load the grouplist with configuration groups.
	 */
	public GroupList(KaranteeniPerms plugin, BungeeGroupBuilder builder) throws Exception {
		if(plugin.getGroupList() != null) throw new IllegalArgumentException("Grouplist has already been generated");
			
		Collection<Group> groups;
		
		groups = Group.getGroupsFromConfig(plugin);
		
		//Loop each group through
		for(Group g : groups) {
			if(g.isDefault() && defaultGroup != null) //Default group has already been set, give a warning
				Bukkit.getLogger().log(Level.WARNING, "Default group ("+defaultGroup.getID()+
						") already set! Changing to "+g.getID()+". "
						+ "Please edit groups config to have only one default group!");
			
			if(g.isDefault()) //Set the default group
				this.defaultGroup = g;
			
			//Put the group to the grouplist
			this.groups.put(g.getID(), g);
		}
		
		List<BungeeGroup> bungeeGroups = builder.requestBungeeGroupData();
		// put all of the groups to the map
		for(BungeeGroup g : bungeeGroups) {
			this.bungeeGroups.put(g.getID(), g);
		}
	}
	
	
	/**
	 * Returns all the groups in this list
	 * @return
	 */
	public Collection<Group> getGroups()
	{ return this.groups.values(); }
	
	
	/**
	 * Adds a local group to the grouplist
	 * @param group
	 */
	public void addGroup(Group group)
	{ groups.put(group.getID(), group); }
	
	
	/**
	 * Adds a defaultgroup to the list
	 * @param group
	 */
	public void setDefaultGroup(Group group)
	{ this.defaultGroup = group; }
	
	
	/**
	 * Get the default group
	 * @return
	 */
	public Group getDefaultGroup()
	{ return this.defaultGroup; }
	
	
	/**
	 * Adda a local group to the grouplist
	 * @param group
	 */
	public void removeGroup(String id)
	{ groups.remove(id); }
	
	
	/**
	 * Deletes all local groups
	 */
	public void clearGroups()
	{ groups.clear(); }
	
	
	/**
	 * Refreshes all bungeegroups. Requests group from bungee, overrides the previous group values
	 * and then refreshes players bungeegroups from spigot side
	 */
	public void refreshBungeeGroups() throws IOException {
		BungeeGroupBuilder builder = new BungeeGroupBuilder();
		List<BungeeGroup> bungeeGroups = builder.requestBungeeGroupData();
		this.bungeeGroups.clear();
		
		// put all of the groups to the map
		for(BungeeGroup g : bungeeGroups) {
			this.bungeeGroups.put(g.getID(), g);
		}
		
		// TODO refresh PermissionPlayers
	}
	
	
	/**
	 * Returns the group by name
	 * @param name
	 * @return
	 */
	public Group getGroup(String id)
	{ return groups.get(id); }
}
