package net.karanteeni.groups.player;

/**
 * This enum is used to convey information about
 * addition or information of permission additions or removals
 * @author Nuubles
 *
 */
public enum PermissionModifyState {
	ADDITION_SUCCESSFUL, 
	ADDITION_DATABASE_FAIL, 
	ADDITION_ALREADY_EXISTS_FAIL, 
	REMOVAL_SUCCESSFUL, 
	REMOVAL_DATABASE_FAIL, 
	REMOVAL_NO_PERMISSION_FAIL
}
