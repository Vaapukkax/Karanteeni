package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.Collection;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;
import net.karanteeni.karanteeniperms.bungee.groups.Group;

public class GroupList {
	private Group defaultGroup;
	private TreeMap<String, Group> groups = new TreeMap<String, Group>(String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * Initializes the grouphandler
	 */
	/*public GroupList(Group defaultGroup)
	{ this.defaultGroup = defaultGroup; }*/
	
	/**
	 * Load the grouplist with configuration groups.
	 */
	public GroupList(KaranteeniPermsBungee plugin) throws Exception {
		if(plugin.getGroupList() != null) throw new IllegalArgumentException("Grouplist has already been generated");
			
		Collection<Group> groups;
		
		groups = Group.getGroupsFromConfig(plugin);
		
		
		//Loop each bungee group through
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
	 * Returns the group by name
	 * @param name
	 * @return
	 */
	public Group getGroup(String id)
	{ return groups.get(id); }
}
