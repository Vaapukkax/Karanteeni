package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.Collection;
import java.util.LinkedList;
import net.karanteeni.core.data.ObjectPair;

public class GroupData {
	private LinkedList<ExtendedPermission> bungeePermissions;
	private LinkedList<ExtendedPermission> spigotPermissions;
	private String prefix;
	private String suffix;
	private String groupName;
	private String groupShortName;
	
	public GroupData(String prefix, 
			String suffix, 
			String groupName, 
			String groupShortName, 
			Collection<ObjectPair<String, Boolean>> permissions) throws IllegalArgumentException{
		this.prefix = prefix;
		this.suffix = suffix;
		this.groupName = groupName;
		this.groupShortName = groupShortName;
		this.spigotPermissions = new LinkedList<ExtendedPermission>();
		this.bungeePermissions = new LinkedList<ExtendedPermission>();
		for(ObjectPair<String,Boolean> permission : permissions) {
			if(permission.second) {
				spigotPermissions.add(new ExtendedPermission(permission.first));
			} else {
				bungeePermissions.add(new ExtendedPermission(permission.first));
			}
		}
	}
	
	
	public GroupData(String prefix, 
			String suffix, 
			String groupName, 
			String groupShortName, 
			Collection<String> spigotPermissions,
			Collection<String> bungeePermissions) throws IllegalArgumentException{
		this.prefix = prefix;
		this.suffix = suffix;
		this.groupName = groupName;
		this.groupShortName = groupShortName;
		this.spigotPermissions = new LinkedList<ExtendedPermission>();
		this.bungeePermissions = new LinkedList<ExtendedPermission>();
		
		for(String permission : spigotPermissions) {
			this.spigotPermissions.add(new ExtendedPermission(permission));
		}
		
		for(String permission : bungeePermissions) {
			this.bungeePermissions.add(new ExtendedPermission(permission));
		}
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
	
	public boolean hasPermission(String permission, boolean spigotSide) { 
		if(spigotSide) {
			return this.spigotPermissions.contains(new ExtendedPermission(permission));
		} else {
			return this.bungeePermissions.contains(new ExtendedPermission(permission));
		}
	}
	
	public boolean hasPermission(ExtendedPermission permission, boolean spigotSide) { 
		if(spigotSide) {
			return this.spigotPermissions.contains(permission);
		} else {
			return this.bungeePermissions.contains(permission);
		}
	}
	
	public boolean addPermission(String permission, boolean spigotSide) {
		if(spigotSide) {
			return this.spigotPermissions.add(new ExtendedPermission(permission));
		} else {
			return this.bungeePermissions.add(new ExtendedPermission(permission));
		}
	}

	public boolean removePermission(String permission, boolean spigotSide) {
		if(spigotSide) {
			return this.spigotPermissions.remove(new ExtendedPermission(permission));
		} else {
			return this.bungeePermissions.remove(new ExtendedPermission(permission));
		}
	}
	
	public LinkedList<ExtendedPermission> getBungeePermissions() { 
		return new LinkedList<ExtendedPermission>(this.bungeePermissions); 
	}
	
	public LinkedList<ExtendedPermission> getSpigotPermissions() { 
		return new LinkedList<ExtendedPermission>(this.spigotPermissions); 
	}
}
