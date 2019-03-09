package net.karanteeni.groups.player;

public class GroupModel {
	/** Local groups only on this server */
	private GroupList localGroups;
	
	public GroupModel() throws Exception
	{
		//Load this servers groups
		this.localGroups = new GroupList();
	}
	
	/**
	 * Get the local group list
	 * @return
	 */
	public GroupList getLocalGroupList()
	{ return this.localGroups; }
}
