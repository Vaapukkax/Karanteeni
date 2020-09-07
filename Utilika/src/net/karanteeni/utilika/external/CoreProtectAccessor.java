package net.karanteeni.utilika.external;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.karanteeni.utilika.Utilika;

public class CoreProtectAccessor {
	private Utilika plugin;
	
	public CoreProtectAccessor(Utilika utilika) {
		this.plugin = utilika;
	}
	
	
	public boolean registerBlockPlacement(Player player, Material material, Location loc, BlockData blockData) {
		CoreProtectAPI api = getCoreProtect();
		if(api == null)
			return false;
		
		return api.logPlacement(player.getName(), loc, material, blockData);
	}
	
	
	private CoreProtectAPI getCoreProtect() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("CoreProtect");
     
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
