package net.karanteeni.groups.player;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.command.CommandSender;

import net.karanteeni.core.database.QueryState;
import net.karanteeni.groups.KaranteeniPerms;

/**
 * This class is responsible for updating and giving out
 * @author Nuubles
 *
 */
public class PermissionPlayer {
	//private List<Permission> permissions = new ArrayList<Permission>();
	private KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
	private GroupData groupData;
	private UUID uuid;
	private Group group;
	private Predicate<PermissionPlayer> savePrefix;
	private Predicate<PermissionPlayer> resetPrefix;
	private Predicate<PermissionPlayer> saveSuffix;
	private Predicate<PermissionPlayer> resetSuffix;
	private Predicate<PermissionPlayer> saveGroupName;
	private Predicate<PermissionPlayer> resetGroupName;
	private Predicate<PermissionPlayer> saveGroupShortName;
	private Predicate<PermissionPlayer> resetGroupShortName;
	
	public PermissionPlayer(
			UUID uuid,
			Group group,
			GroupData groupData,
			Predicate<PermissionPlayer> savePrefix, 
			Predicate<PermissionPlayer> resetPrefix,
			Predicate<PermissionPlayer> saveSuffix,
			Predicate<PermissionPlayer> resetSuffix,
			Predicate<PermissionPlayer> saveGroupName,
			Predicate<PermissionPlayer> resetGroupName,
			Predicate<PermissionPlayer> saveGroupShortName,
			Predicate<PermissionPlayer> resetGroupShortName)
	{
		this.uuid = uuid;
		this.group = group;
		this.groupData = groupData;
		this.savePrefix = savePrefix;
		this.resetPrefix = resetPrefix;
		this.saveSuffix = saveSuffix;
		this.resetSuffix = resetSuffix;
		this.saveGroupName = saveGroupName;
		this.resetGroupName = resetGroupName;
		this.saveGroupShortName = saveGroupShortName;
		this.resetGroupShortName = resetGroupShortName;
	}
	
	/**
	 * Checks if this player has a given permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(String permission, DATA_TYPE type)
	{
		permission = permission.toLowerCase();
		
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
			return (this.group.hasPermission(permission) || 
					this.groupData.hasPermission(permission));
		else if(type == DATA_TYPE.GROUP) //Return group data
			return this.group.hasPermission(permission);
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
			return this.groupData.hasPermission(permission);
		return false;
	}
	
	/**
	 * Returns the prefix of this player
	 * @return
	 */
	public String getPrefix(CommandSender sender, DATA_TYPE type, boolean shortened)
	{
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
		{
			if(shortened)
			{
				String groupName = groupData.getGroupShortName();
				if(groupName == null)
					groupName = group.getShortName(sender);
				
				if(groupData.getPrefix() == null)
					return group.getPrefix(sender, shortened);
				else
					return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupName);
			}
			else
			{
				String groupName = groupData.getGroupName();
				if(groupName == null)
					groupName = group.getName(sender);
				
				if(groupData.getPrefix() == null)
					return group.getPrefix(sender, shortened);
				else
					return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupName);
			}
		}
		else if(type == DATA_TYPE.GROUP) //Return group data
		{
			return group.getPrefix(sender, shortened);
		}
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
		{
			if(shortened)
				return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupShortName());
			else
				return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupName());
		}
		return null;
	}
	
	/**
	 * Returns the prefix of this player
	 * @return
	 */
	public String getPrefix(Locale locale, DATA_TYPE type, boolean shortened)
	{
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
		{
			if(shortened)
			{
				String groupName = groupData.getGroupShortName();
				if(groupName == null)
					groupName = group.getShortName(locale);
				
				if(groupData.getPrefix() == null)
					return group.getPrefix(locale, shortened);
				else
					return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupName);
			}
			else
			{
				String groupName = groupData.getGroupName();
				if(groupName == null)
					groupName = group.getName(locale);
				
				if(groupData.getPrefix() == null)
					return group.getPrefix(locale, shortened);
				else
					return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupName);
			}
		}
		else if(type == DATA_TYPE.GROUP) //Return group data
		{
			return group.getPrefix(locale, shortened);
		}
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
		{
			if(shortened)
				return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupShortName());
			else
				return groupData.getPrefix().replace(Group.GROUP_STRING_TAG, groupData.getGroupName());
		}
		return null;
	}
	
	/**
	 * Returns the raw prefix of this player
	 * @return
	 */
	public String getRawPrefix(DATA_TYPE type)
	{
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
		{
			if(groupData.getGroupName() == null)
				return group.getRawPrefix();
			else
				return groupData.getPrefix();
		}
		else if(type == DATA_TYPE.GROUP) //Return group data
			return group.getRawPrefix();
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
			return groupData.getPrefix();
		return null;
	}
	
	/**
	 * Returns the suffix of this player or
	 * null if using groups suffix
	 * @return
	 */
	public String getSuffix(DATA_TYPE type)
	{
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
			if(this.groupData.getSuffix() == null)
				return this.group.getSuffix();
			else
				return this.groupData.getSuffix();
		else if(type == DATA_TYPE.GROUP) //Return group data
			return this.group.getSuffix();
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
			return this.groupData.getSuffix();
		return null;
	}
	
	/**
	 * Returns the players groupname, or null
	 * if using actual groups group name
	 * @return
	 */
	public String getGroupName(CommandSender sender, DATA_TYPE type, boolean shortened)
	{
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
		{
			if(shortened)
			{
				if(this.groupData.getGroupShortName() == null)
					return this.group.getShortName(sender);
				else
					return this.groupData.getGroupShortName();
			}
			else
			{
				if(this.groupData.getGroupName() == null)
					return this.group.getName(sender);
				else
					return this.groupData.getGroupName();
			}
		}
		else if(type == DATA_TYPE.GROUP) //Return group data
		{
			if(shortened)
				return this.group.getShortName(sender);
			else
				return this.group.getName(sender);
		}
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
		{
			if(shortened)
				return this.groupData.getGroupShortName();
			else
				this.groupData.getGroupName();
		}
		
		return null;
	}
	
	/**
	 * Returns the players groupname, or null
	 * if using actual groups group name
	 * @return
	 */
	public String getGroupName(Locale locale, DATA_TYPE type, boolean shortened)
	{
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
		{
			if(shortened)
			{
				if(this.groupData.getGroupShortName() == null)
					return this.group.getShortName(locale);
				else
					return this.groupData.getGroupShortName();
			}
			else
			{
				if(this.groupData.getGroupName() == null)
					return this.group.getName(locale);
				else
					return this.groupData.getGroupName();
			}
		}
		else if(type == DATA_TYPE.GROUP) //Return group data
		{
			if(shortened)
				return this.group.getShortName(locale);
			else
				return this.group.getName(locale);
		}
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
		{
			if(shortened)
				return this.groupData.getGroupShortName();
			else
				this.groupData.getGroupName();
		}
		
		return null;
	}
	
	/**
	 * Returns the group of this player
	 * @return
	 */
	public Group getGroup()
	{ return this.group; }
	
	/**
	 * Sets and saves the suffix of this player
	 * @param suffix
	 */
	public boolean setSuffix(String suffix)
	{
		this.groupData.setSuffix(suffix);
		return this.saveSuffix.test(this);
	}
	
	/**
	 * Returns the groupdata of this class
	 * @return
	 */
	protected GroupData getGroupData()
	{ return this.groupData; }
	
	/**
	 * Reset the suffix of this player
	 * @return
	 */
	public boolean resetSuffix()
	{
		String suffix = groupData.getSuffix();
		this.groupData.setSuffix(null);
		if(this.resetSuffix.test(this))
			return true;
		else
			this.groupData.setSuffix(suffix);
		return false;
	}
	
	/**
	 * Sets and saves the prefix of this player
	 * @param prefix
	 */
	public boolean setPrefix(String prefix)
	{
		this.groupData.setPrefix(prefix);
		return this.savePrefix.test(this);
	}
	
	
	/**
	 * Resets the prefix of this player
	 * @return
	 */
	public boolean resetPrefix()
	{
		String prefix = groupData.getPrefix();
		this.groupData.setPrefix(null);
		if(this.resetPrefix.test(this))
			return true;
		else
			groupData.setPrefix(prefix);
		
		return false;
	}
	
	/**
	 * Sets and saves the groupname for given player.
	 * Notice: no translation available for this!
	 * If translation is needed, please modify the actual group 
	 * @param groupName
	 */
	public boolean setLocalGroupName(String groupName)
	{
		this.groupData.setGroupName(groupName);
		return this.saveGroupName.test(this);
	}
	
	/**
	 * Resets the local groupname of the player
	 * @return
	 */
	public boolean resetLocalGroupName()
	{
		String groupName = groupData.getGroupName();
		groupData.setGroupName(null);
		if(this.resetGroupName.test(this))
			return true;
		else
			groupData.setGroupName(groupName);
		return false;
	}
	
	/**
	 * Reset the players group name to use players groups group name
	 * @param groupShortName
	 * @return
	 */
	public boolean setLocalGroupShortName(String groupShortName)
	{
		this.groupData.setGroupShortName(groupShortName);
		return this.saveGroupShortName.test(this);
	}
	
	/**
	 * Reset the players shortened groupname to use
	 * the normal groups name
	 * @return
	 */
	public boolean resetLocalGroupShortName()
	{
		String shortName = groupData.getGroupShortName();
		this.groupData.setGroupShortName(null);
		if(this.resetGroupShortName.test(this))
			return true;
		else
			groupData.setGroupShortName(shortName);
		return false;
	}
	
	/**
	 * Checks whether this player has a custom groupname
	 * @return
	 */
	public boolean hasCustomGroupName()
	{ return this.groupData.getGroupName() != null; }
	
	/**
	 * Checks whether this player has a custom prefix
	 * @return
	 */
	public boolean hasCustomPrefix()
	{ return this.groupData.getPrefix() != null; }
	
	/**
	 * Checks if this player has a custom suffix
	 * @return
	 */
	public boolean hasCustomSuffix()
	{ return this.groupData.getSuffix() != null; }
	
	/**
	 * Checks if this player has custom shortened group name
	 * @return
	 */
	public boolean hasCustomShortGroupName()
	{ return this.groupData.getGroupShortName() != null; }
	
	/**
	 * Checks if this player has a given permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(DATA_TYPE type, String permission)
	{ 
		if(type == DATA_TYPE.GROUP_AND_PLAYER) //Return mix
		{
			if(this.groupData.hasPermission(permission))
				return true;
			return this.group.hasPermission(permission);
		}
		else if(type == DATA_TYPE.GROUP) //Return group data
			return this.group.hasPermission(permission);
		else if(type == DATA_TYPE.PLAYER) //Return only player specific data
			return this.groupData.hasPermission(permission);
		return false;
	}
	
	/**
	 * Adds and saves a given permission to player
	 * @param permission
	 * @return
	 */
	public QueryState addPermission(String permission)
	{ return perms.getPlayerModel().addAndSavePlayerPermission(this.uuid, permission, true); }
	
	/**
	 * Returns the local group of this player
	 * @return
	 */
	public Group getLocalGroup()
	{ return perms.getPlayerModel().getLocalGroup(uuid); }
	
	/**
	 * Removes and saves a given permission to player
	 * @param permission
	 * @return
	 */
	public QueryState removePermission(String permission)
	{ return perms.getPlayerModel().removePlayerPermission(uuid, permission, true); }
	
	/**
	 * Returns the uuid of the given player
	 * @return
	 */
	public UUID getUUID()
	{ return this.uuid; }
	
	/**
	 * What kind of data is being requested,
	 * only group, only player, or mix
	 * @author Nuubles
	 *
	 */
	public static enum DATA_TYPE {
		GROUP, PLAYER, GROUP_AND_PLAYER
	}
}
