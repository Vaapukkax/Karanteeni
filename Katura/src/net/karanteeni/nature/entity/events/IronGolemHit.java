package net.karanteeni.nature.entity.events;

import java.util.Arrays;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.players.KPlayer;

/**
 * Makes the player drop their pants and held item when hit by an iron golem
 * @author Nuubles
 *
 */
public class IronGolemHit implements Listener {
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		if(!(event.getDamager() instanceof IronGolem))
			return;
		
		Player player = (Player)event.getEntity();
		ItemStack leggings = player.getInventory().getItem(EquipmentSlot.LEGS);
		ItemStack heldItem = player.getInventory().getItem(EquipmentSlot.HAND);
		
		KPlayer kp = KPlayer.getKPlayer(player);
		
		if(leggings != null) {
			player.getInventory().setItem(EquipmentSlot.LEGS, null);
			kp.dropItemsAtPlayer(Arrays.asList(leggings));
		}
		
		if(heldItem != null) {
			player.getInventory().setItem(EquipmentSlot.HAND, null);
			kp.dropItemsAtPlayer(Arrays.asList(heldItem));
		}
	}
}
