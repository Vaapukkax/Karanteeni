package net.karanteeni.nature.external_api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class CoreProtectAccess {
	public CoreProtectAPI getCoreProtect() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
     
        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 6) {
            return null;
        }

        return CoreProtect;
	}
}
