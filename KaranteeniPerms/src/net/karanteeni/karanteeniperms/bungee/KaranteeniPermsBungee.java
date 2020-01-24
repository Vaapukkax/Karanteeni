package net.karanteeni.karanteeniperms.bungee;

import net.karanteeni.bungee.core.KaranteeniPlugin;
import net.karanteeni.karanteeniperms.bungee.groups.GroupList;

public class KaranteeniPermsBungee extends KaranteeniPlugin {
	private GroupList groupList;
	
	public KaranteeniPermsBungee() {
		super("KaranteeniPerms");
	}
	
	
	public GroupList getGroupList() {
		return this.groupList;
	}
}
