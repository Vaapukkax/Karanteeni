package net.karanteeni.statmanager.level.execute;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class LocalGroupSetExecutor extends Executor<String> {
	private Group newGroup;
	private KaranteeniPerms plugin;
	
	public LocalGroupSetExecutor(ConfigurationSection config, String path, String key) throws IllegalArgumentException {
		super(config, path, key); 
		
		plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		newGroup = plugin.getGroupModel().getLocalGroupList().getGroup(this.getString());
		
		if(newGroup == null) throw new IllegalArgumentException("Could not find a group with ID " + this.getString());
	}
	

	@Override
	public void execute(Player player) {
		// set the group asynchronously
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(!plugin.getPlayerModel().setLocalGroup(player, newGroup)) {
					Bukkit.getLogger().log(Level.SEVERE, "Could not set the local group for player " + player.getUniqueId());
				}
			}
		};
		
		runnable.runTaskAsynchronously(plugin);
	}
	
	
	public static void register(String key) {
		LocalGroupSetExecutor.register(key, LocalGroupSetExecutor.class);
	}
}
