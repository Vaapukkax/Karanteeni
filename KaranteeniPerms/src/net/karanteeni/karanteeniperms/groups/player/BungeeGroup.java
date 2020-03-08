package net.karanteeni.karanteeniperms.groups.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import net.karanteeni.bungee.core.KaranteeniPlugin;
import net.karanteeni.core.data.ObjectPair;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

public class BungeeGroup implements Comparable<BungeeGroup> {
	private GroupData				groupData;
	private final String 			ID;
	//private List<Permission> 	permissions;
	private TreeSet<BungeeGroup> 	inheritedGroups 		= new TreeSet<BungeeGroup>();
	// entities using this group and their permission attachments connected to this group
	private HashMap<UUID, ObjectPair<Integer, PermissionAttachment>> attachments = new HashMap<UUID, ObjectPair<Integer, PermissionAttachment>>();
	private KaranteeniPerms 	pl;
	private HashMap<Locale, String[]> groupNames = new HashMap<Locale, String[]>(); // key, { shortName, longName }

	
	protected BungeeGroup(String ID,
			String prefix,
			String suffix,
			String defaultLongName,
			String defaultShortName,
			Locale[] locales,
			String[] longNames,
			String[] shortNames,
			String[] permissions) throws IllegalArgumentException {
		if(locales.length != longNames.length || longNames.length != shortNames.length)
			throw new IllegalArgumentException("Locale and translation count do not match");
		
		this.pl = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		this.ID = ID;
		this.groupData = new GroupData(prefix, suffix, defaultLongName, defaultShortName, Arrays.asList(permissions));
		
		for(int i = 0; i < locales.length; ++i) {
			groupNames.put(locales[i], new String[] {shortNames[i], longNames[i]});
		}
	}
	
	
	/**
	 * Gets the ID of this group
	 * @return id of this group
	 */
	public String getID() {
		return this.ID;
	}
	

	/**
	 * Marks the given group as inherited group
	 * @param group
	 */
	protected void addInheritance(final BungeeGroup group) {
		inheritedGroups.add(group);
	}
	
	
	/**
	 * Returns the raw format of players prefix
	 * §6[§7%group%§6]
	 * @return Groups prefix in its raw format
	 */
	public String getRawPrefix()
	{ return this.groupData.getPrefix(); }
	
	
	/**
	 * Returns the raw format of players prefix
	 * §6[§7%group%§6]
	 * @return Groups prefix in its raw format
	 */
	public String getRawSuffix()
	{ return this.groupData.getSuffix(); }
	
	
	
	/**
	 * Returns the translated prefix of group
	 * @param locale which locale to be translated to
	 * @param shortened should this return the short rank name
	 * @return Prefix of group like [GroupName] or [GN] if shortened
	 * set to false
	 */
	public String getPrefix(Locale locale, boolean shortened) {
		// use default name by default
		String rankName = shortened ? groupData.getGroupShortName() : groupData.getGroupName(); 
		String[] groupNames = this.groupNames.get(locale);
		
		// group name found for this locale
		if(groupNames != null) {
			rankName = groupNames[shortened ? 0 : 1];
		} else {
			// group name not found, look for default locale
			groupNames = this.groupNames.get(KaranteeniPlugin.getTranslator().getDefaultLocale());
			if(groupNames != null) {
				rankName = groupNames[shortened ? 0 : 1];
			}
		}
		
		return this.groupData.getPrefix().replace(Group.GROUP_STRING_TAG, rankName); 
	}
	
	
	/**
	 * Get all the groups directly inherited by this group
	 * @return
	 */
	public Set<BungeeGroup> getInheritedGroups()
	{ return this.inheritedGroups; }
	
	
	/**
	 * Returns the translated prefix of group
	 * @param player player to which locale should this be translated to
	 * @param shortened should this return the short rank name
	 * @return Prefix of group like [GroupName] or [GN] if shortened
	 * set to false
	 */
	public String getPrefix(Player player, boolean shortened) {		
		return getPrefix(Locale.forLanguageTag(player.getLocale()), shortened);
	}
	
	
	/**
	 * Registers this player to use this group
	 * @param player player to use the permissions of this group
	 */
	protected void registerUser(Player player) {
		// register inherited groups
		for(BungeeGroup group : inheritedGroups)
			group.registerUser(player);
		
		PermissionAttachment attch = player.addAttachment(this.pl);
		// if player already has this attachment, increase the counter of times this attachment has been inherited
		if(attachments.containsKey(player.getUniqueId())) {
			ObjectPair<Integer, PermissionAttachment> entry = attachments.get(player.getUniqueId());
			entry.first += 1;
		} else {
			// add permissions to this player
			for(ExtendedPermission perm : this.groupData.getPermissions())
				attch.setPermission(perm.getPermission(), perm.isPositive());
				
			attachments.put(player.getUniqueId(), new ObjectPair<Integer, PermissionAttachment>(1, attch));
		}
	}
	
	
	/**
	 * Unregisters user from using this group. If other groups also use this those need to be unregistered too
	 * @param player player to remove from registration
	 */
	protected boolean unregisterUser(Player player) {
		if(!attachments.containsKey(player.getUniqueId()))
			return false;
		ObjectPair<Integer, PermissionAttachment> pair = attachments.get(player.getUniqueId());
		pair.first -= 1;
		
		// if nothing uses this group remove the permissions of this group from player
		if(pair.first == 0) {
			attachments.remove(player.getUniqueId());
			player.removeAttachment(pair.second);
			
			for(BungeeGroup group : inheritedGroups)
				group.unregisterUser(player);
		}
		
		return true;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BungeeGroup))
			return false;
		return ((BungeeGroup)obj).ID.equals(this.ID);
	}


	@Override
	public int compareTo(BungeeGroup group) {
		return ID.compareTo(group.ID);
	}
}