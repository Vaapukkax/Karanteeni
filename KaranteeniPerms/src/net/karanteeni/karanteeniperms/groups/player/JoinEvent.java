package net.karanteeni.karanteeniperms.groups.player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.PermissionBase;

public class JoinEvent implements Listener{

	/**
	 * Player joins the server, load the players data and permissions
	 * @param event
	 */
	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = false)
	private void onJoin(AsyncPlayerPreLoginEvent event) {
		if(event.getLoginResult() != Result.ALLOWED)
			return;
		
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		
		KPlayer kp = KPlayer.getKPlayer(event.getUniqueId());
		if(kp == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load player permissions for " + event.getUniqueId().toString());
			return;
		}

		perms.getPlayerModel().loadPermissionPlayerData(event.getUniqueId());
		
		kp.addPlayerJoinModifier(EventPriority.LOW, this::setPlayerData);
	}
	
	
	/**
	 * Sets the player data such as permissions
	 * @param kplayer player to whom the data will be set
	 */
	private void setPlayerData(KPlayer kplayer) {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		
		// replace the permissiblebase with permissionbase
		replaceBase(kplayer.getPlayer());
		
		// activate player permissions and group
		perms.getPlayerModel().activatePermissionPlayer(kplayer.getPlayer().getUniqueId());
	}
	
	
	/**
	 * Replaces the permissiblebase of a given player
	 * @param player
	 */
	private void replaceBase(Player player) {
		try {
			Field field = CraftEntity.class.getDeclaredField("perm");
			field.setAccessible(true);
			field.get(player);
			Field modifiersField = field.getClass().getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, new PermissionBase(player));
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to swap from PermissibleBase to PermissionBase");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Unload the permissions of this player
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onQuit(PlayerQuitEvent event) {
		KaranteeniPerms plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		plugin.getPlayerModel().clearPermissionPlayer(event.getPlayer().getUniqueId());
	}
}
