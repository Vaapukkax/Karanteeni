package net.karanteeni.karanteeniperms.bungee.groups;

public class ExtendedPermission {
	private String permission;
	private boolean positive = true;
	//private boolean starPermission = false;
	
	public ExtendedPermission(String permission, boolean positive/*, boolean starPermission*/) {
		// cut the modifiers
		if(permission.length() > 0 && permission.charAt(0) == '-')
			permission = permission.substring(1);
		
		this.positive = positive;
	}
	
	
	public ExtendedPermission(String permission) {
		// get modifiers
		if(permission.length() > 1 && permission.charAt(0) == '-') // negate perm
			positive = false;
		if(!positive) {
			this.permission = permission.substring(1);
		} else {
			this.permission = permission;
		}
	}
	
	
	/**
	 * The parsed normal permission
	 * @return
	 */
	public String getPermission() {
		return this.permission;
	}
	
	
	/**
	 * Is this permission positive
	 * @return true if normal, false if negation perm
	 */
	public boolean isPositive() {
		return positive;
	}
	
	
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
				perm.permission.equals(permission);
	}
	
	
	/**
	 * Returns the permission in its original form
	 */
	public String toString() {
		String name = permission;
		if(!positive)
			name = "-" + name;
		return name;
	}
}
