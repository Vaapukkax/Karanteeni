package net.karanteeni.karanteeniperms;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;

public class PermissionBase extends PermissibleBase {

	public PermissionBase(ServerOperator opable) {
		super(opable);
	}

	
	/**
	 * Make permissions accessible
	 * @return true if successful
	 */
	public Set<String> getPermissions() {
		try {
			Field field = PermissibleBase.class.getDeclaredField("permissions");
			if(!field.isAccessible()) {
				field.setAccessible(true);
			}
			
			@SuppressWarnings("unchecked")
			Map<String, PermissionAttachmentInfo> permissions = (Map<String, PermissionAttachmentInfo>) field.get(this);
			return permissions.keySet();				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public boolean hasPermission(String permission) {
		Bukkit.broadcastMessage("Checking permissions in PermissionBase step 1");
		if(super.hasPermission(permission)) return true;
		
		Set<String> permissions = getPermissions();
		Bukkit.broadcastMessage("Checking permissions in PermissionBase step 2");
		for(String perm : permissions) {
			if(perm.charAt(perm.length()-1) != '*') continue;
			perm = perm.substring(0, perm.length()-1);
			if(permission.startsWith(perm));
		}
		
		return false;
	}
	
	
	@Override
	public boolean hasPermission(Permission permission) {
		return hasPermission(permission.getName());
	}
}
