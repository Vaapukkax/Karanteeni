package net.karanteeni.groups.player;

import java.util.TreeMap;

public class GroupList {
	private TreeMap<String, Group> groups = new TreeMap<String, Group>(String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * Initializes the grouphandler
	 */
	public GroupList()
	{
		
	}
	
	/**
	 * Adda a local group to the grouplist
	 * @param group
	 */
	public void addGroup(Group group)
	{
		groups.put(group.getName(), group);
	}
	
	/**
	 * Adda a local group to the grouplist
	 * @param group
	 */
	public void removeGroup(String name)
	{
		groups.remove(name);
	}
	
	/**
	 * Deletes all local groups
	 */
	public void clearGroups()
	{
		groups.clear();
	}
	
	/**
	 * Returns the group by name
	 * @param name
	 * @return
	 */
	public Group getGroup(String name)
	{
		return groups.get(name);
	}
}
