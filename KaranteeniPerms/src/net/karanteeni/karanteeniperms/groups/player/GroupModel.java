package net.karanteeni.karanteeniperms.groups.player;

import java.util.UUID;
import java.util.function.BiConsumer;

public class GroupModel {
	/** Local groups only on this server */
	private GroupList localGroups;
	
	public GroupModel(BiConsumer<UUID,String> addPermission, 
			BiConsumer<UUID,String> removePermission) throws Exception
	{
		//Load this servers groups
		this.localGroups = new GroupList(addPermission, removePermission);
	}
	
	/**
	 * Get the local group list
	 * @return
	 */
	public GroupList getLocalGroupList()
	{ return this.localGroups; }
}
