package net.karanteeni.nature.sit;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import net.karanteeni.nature.Katura;

public class SitArmorstandManager implements Listener {
	
	@EventHandler
	public void onDisable(PluginDisableEvent event) {
		if(event.getPlugin().equals(Katura.getPlugin(Katura.class))) {
			for(ArmorStand stand : SitCommand.riding.values())
				stand.remove();
			SitCommand.riding.clear();
		}
	}
	
	
	@EventHandler
	public void vehicleExit(EntityDismountEvent event) {
		if(event.getEntity() instanceof Player && event.getDismounted().getType() == EntityType.ARMOR_STAND) {
			unsit((Player)event.getEntity());
		}
	}
	
	
	@EventHandler
	public void armorEquipEvent(PlayerArmorStandManipulateEvent event) {
		// check if the armorstand has riders
		if(event.getRightClicked().getPassengers().size() == 0)
			return;
		
		// check if the first rider is player
		if(!(event.getRightClicked().getPassengers().get(0) instanceof Player))
			return;
		
		// if the player is sitting, dont change the armor stand
		Player player = (Player)event.getRightClicked().getPassengers().get(0);
		if(SitCommand.riding.containsKey(player.getUniqueId()));
			event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		unsit(event.getEntity());
	}
	
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		unsit(event.getPlayer());
	}
	
	
	/**
	 * Unseats the player from their seat (armorstand)
	 * @param player player to unseat
	 */
	private void unsit(Player player) {
		if(!SitCommand.riding.containsKey(player.getUniqueId()))
			return;
		
		ArmorStand stand = SitCommand.riding.remove(player.getUniqueId());
		if(stand != null)
			stand.remove();
	}
}
