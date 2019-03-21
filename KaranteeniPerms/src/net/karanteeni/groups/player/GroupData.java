package net.karanteeni.groups.player;

import java.util.List;

public class GroupData {
	private List<String> permissions;
	private String prefix;
	private String suffix;
	private String groupName;
	private String groupShortName;
	
	public GroupData(String prefix, String suffix, String groupName, String groupShortName, List<String> permissions)
	{
		this.prefix = prefix;
		this.suffix = suffix;
		this.groupName = groupName;
		this.groupShortName = groupShortName;
		this.permissions = permissions;
	}
	
	public GroupData(String prefix, String suffix, List<String> permissions)
	{
		this.prefix = prefix;
		this.suffix = suffix;
		this.permissions = permissions;
	}
	
	public String getPrefix()
	{ return this.prefix; }
	
	public String getSuffix()
	{ return this.suffix; }
	
	public String getGroupName()
	{ return this.groupName; }
	
	public String getGroupShortName()
	{ return this.groupShortName; }
	
	public void setPrefix(String prefix)
	{ this.prefix = prefix; }
	
	public void setSuffix(String suffix)
	{ this.suffix = suffix; }
	
	public void setGroupName(String groupName)
	{ this.groupName = groupName; }
	
	public void setGroupShortName(String groupShortName)
	{ this.groupShortName = groupShortName; }
	
	public boolean hasPermission(String permission)
	{ return this.permissions.contains(permission); }
	
	public boolean addPermission(String permission)
	{ return this.permissions.add(permission); }

	public boolean removePermission(String permission)
	{ return this.permissions.remove(permission); }
	
	public List<String> getPermissions()
	{ return this.permissions; }
}
