package net.karanteeni.karanteeniperms.groups.player;

import org.bukkit.permissions.Permission;

public class ExtendedPermission {
	private Permission permission;
	private boolean positive = true;
	//private boolean starPermission = false;
	
	public ExtendedPermission(String permission, boolean positive/*, boolean starPermission*/) {
		// cut the modifiers
		if(permission.length() > 0 && permission.charAt(0) == '-')
			permission = permission.substring(1);
		/*if(permission.length() == 1 && permission.charAt(0) == '*')
			permission = "";
		if(permission.length() > 1 && permission.charAt(permission.length()-1) == '*' && permission.charAt(permission.length()-2) == '.')
			permission = permission.substring(0, permission.length()-2);*/
		
		this.permission = new Permission(permission);
		
		this.positive = positive;
		//this.starPermission = starPermission;
	}
	
	
	public ExtendedPermission(String permission) {
		// get modifiers
		if(permission.length() > 1 && permission.charAt(0) == '-') // negate perm
			positive = false;
		/*if(permission.length() == 1 && permission.charAt(0) == '*') {
			starPermission = true;
			this.permission = new Permission("");
			return;
		} else if(permission.length() > 1 && permission.charAt(permission.length()-1) == '*' && permission.charAt(permission.length()-2) == '.') {
			starPermission = true;
		}*/
		
		// parse permission without modifiers
		/*if(starPermission && !positive) {
			this.permission = new Permission(permission.substring(1, permission.length()-1));
		} else*/ if(!positive) {
			this.permission = new Permission(permission.substring(1));
		}/* else if (starPermission) {
			this.permission = new Permission(permission.substring(0, permission.length()-2));
		}*/ else {
			this.permission = new Permission(permission);
		}
	}
	
	
	/**
	 * The parsed normal permission
	 * @return
	 */
	public Permission getPermission() {
		return this.permission;
	}
	
	
	/**
	 * Is this permission positive
	 * @return true if normal, false if negation perm
	 */
	public boolean isPositive() {
		return positive;
	}
	
	
	/**
	 * Is this a star permission
	 * @return
	 */
	/*public boolean isStar() {
		return starPermission;
	}*/
	
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ExtendedPermission)) {
			// check if the permission is same on the string level
			if(!(o instanceof String))
				return false;
			String perm = (String)o;
			
			return this.toString().equals(perm);
		}
		
		ExtendedPermission perm = ((ExtendedPermission)o);
		
		return perm.positive == positive && 
				//perm.starPermission == starPermission && 
				perm.permission.getName().equals(permission.getName());
	}
	
	
	/**
	 * Returns the permission in its original form
	 */
	public String toString() {
		String name = permission.getName();
		/*if(starPermission)
			if(name.length() == 0)
				name = "*";
			else
				name += ".*";*/
		if(!positive)
			name = "-" + name;
		return name;
	}
}
