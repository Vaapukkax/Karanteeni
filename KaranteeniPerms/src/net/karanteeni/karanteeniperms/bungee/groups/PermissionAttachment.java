package net.karanteeni.karanteeniperms.bungee.groups;

import java.util.UUID;

public class PermissionAttachment {
	private UUID uuid = null;
	
	public PermissionAttachment() {
		this.uuid = UUID.randomUUID();
	}
	
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	
	public void addPermission(String permission) {
		
	}
	
	
	public void addPermission(ExtendedPermission permission) {
		
	}
	
	
	public void removePermission(String permission) {
		
	}
	
	
	/**
	 * Checks if the player has a given permission.
	 * @param permission permission to look for
	 * @param exact whether or not the permission should be exact. For example
	 * if this is false then permission "plugin.permission" would match with "plugin.permission.*" stored in the attachment.
	 * Otherwise only "plugin.permission.*" would match
	 */
	public boolean hasPermission(String permission, boolean exact) {
		return false;
	}
	
	
	/**
	 * Checks if the given permission is a negated permission. If the permission is negated, no access should be given.
	 * @param permission the permission to be check whether or not it is negated
	 * @return true if negated, false otherwise
	 */
	public boolean isPermissionNegated(String permission) {
		return false;
	}
}
