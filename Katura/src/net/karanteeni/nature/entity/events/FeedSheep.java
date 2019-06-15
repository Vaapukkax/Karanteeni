package net.karanteeni.nature.entity.events;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import net.karanteeni.nature.Katura;

public class FeedSheep implements Listener {

	private static String configKey = "sheep.feed-with-hayblock";
	private static String speedKey = "sheep.jump-speed";
	private static double maxSpeed; // max speed the sheep feeding can have
	
	public FeedSheep()
	{
		Plugin pl = Katura.getPlugin(Katura.class);
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey)) {
			pl.getConfig().set(configKey, true);
			pl.saveConfig();
		}
		
		if(!pl.getConfig().isSet(speedKey)) {
			pl.getConfig().set(speedKey, 0.6);
			pl.saveConfig();
		}
		
		maxSpeed = pl.getConfig().getDouble(speedKey);
	}
	
	/**
	 * Feed clicked sheep with hay block and remove the block from the hand of the player
	 * @param event
	 */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=false)
	public void onRightClick(PlayerInteractEntityEvent event) 
	{
		if(!Katura.getPlugin(Katura.class).getConfig().getBoolean(configKey))
			return;
		
		//Tee event vain kerran!
		if(event.getHand().equals(EquipmentSlot.HAND))
		{
			//Tee vain lampailla
			if(event.getRightClicked().getType().equals(EntityType.SHEEP))
			{
				Player p = event.getPlayer();
				Sheep sheep = (Sheep)event.getRightClicked();
				
				//Akuinen lambi, jota ei oo keritty
				if(sheep.isAdult() && sheep.isSheared() && p.getInventory().getItemInMainHand().getType().equals(Material.HAY_BLOCK))
				{
					//V�hennet��n yksi hein�block k�dest�
					p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
					
					//Nostetaan lammas ilmaan, otetaan villa p��lle ja toistetaan ��ni
					sheep.setSheared(false);
					sheep.getLocation().getWorld().playSound(sheep.getLocation(), Sound.ENCHANT_THORNS_HIT, SoundCategory.NEUTRAL, 4F, 1.7F);
					sheep.setVelocity(sheep.getVelocity().setY(Math.min(sheep.getVelocity().getY() + maxSpeed, maxSpeed)));
					
					//Tehd��n partikkelit
					sheep.getLocation().getWorld().spawnParticle(Particle.CLOUD, sheep.getLocation(), 20, 0.5, 0.5, 0.5);
				}
			}
		}
	}
}
