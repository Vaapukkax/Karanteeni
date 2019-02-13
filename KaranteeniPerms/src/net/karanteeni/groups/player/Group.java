package net.karanteeni.groups.player;

import java.util.LinkedList;
import java.util.List;

public class Group {
	private String prefix;
	private String suffix;
	private String groupName;
	private LinkedList<String> permissions = new LinkedList<String>();
	
	/**
	 * Initializes the group
	 */
	public Group(String groupName)
	{
		this.groupName = groupName;
	}
	
	/**
	 * Initializes the group
	 * @param prefix
	 * @param groupName
	 * @param suffix
	 */
	public Group(String prefix, String groupName, String suffix)
	{
		this.prefix = prefix;
		this.groupName = groupName;
		this.suffix = suffix;
	}
	
	/**
	 * Returns the name of this group
	 * @return
	 */
	public String getName()
	{
		return this.groupName;
	}
	
	/**
	 * Returns the player name formatted to the style of this group
	 * @param name
	 * @return
	 */
	public String formatPlayerName(String name)
	{
		return prefix + name + suffix;
	}
	
	/**
	 * Get the permissions of this group
	 * @return
	 */
	public List<String> getPermissions()
	{
		return permissions;
	}
	
	/**
	 * Sets the prefix of this class
	 * @param prefix
	 * @return
	 */
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
	
	/**
	 * Sets the suffix of this group
	 * @param suffix
	 * @return
	 */
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
	
	/**
	 * Gets the prefix of this class
	 * @param prefix
	 * @return
	 */
	public String getPrefix()
	{
		return prefix;
	}
	
	/**
	 * Gets the suffix of this group
	 * @param suffix
	 * @return
	 */
	public String getSuffix()
	{
		return suffix;
	}
}
