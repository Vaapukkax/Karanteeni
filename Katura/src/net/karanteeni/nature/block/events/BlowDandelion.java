package net.karanteeni.nature.block.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.karanteeni.core.event.NoActionEvent;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.nature.Katura;

public class BlowDandelion implements Listener{
	private static String KEY = "DANDELION";
	private static String configKey = "blow-dandelions";
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	public BlowDandelion()
	{
		pl = Katura.getPlugin(Katura.class);
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey))
		{
			pl.getConfig().set(configKey, true);
			pl.saveConfig();
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void noActionEvent(NoActionEvent event)
	{
		if(!pl.getConfig().getBoolean(configKey))
			return;
		
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
			return;
		
		if(event.getHand() == null || event.getPlayer().hasMetadata("NPC"))
			return;
		
		if(event.getItem() != null && event.getItem().getType().equals(Material.DANDELION))
			blowDandelion(event.getPlayer(), event.getHand());
	}
	
	/*@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)	
	private void onPlayerInteract(PlayerInteractEvent event)
	{
		if(!event.isCancelled())
			return;
		
		if(!pl.getConfig().getBoolean(configKey))
			return;
			
		if(event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND) || event.getPlayer().hasMetadata("NPC"))
			return;
		
		if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DANDELION))
			blowDandelion(event.getPlayer());
	}
	
	@EventHandler
	private void blockCanBuildEvent(BlockCanBuildEvent event)
	{
		Bukkit.broadcastMessage("Buildable: " + event.isBuildable());
		event.setBuildable(true);
	}*/
	
	/**
	 * Makes the player blow a dandelion
	 * @param p
	 */
	private void blowDandelion(Player p, EquipmentSlot hand)
	{
		KPlayer kp = KPlayer.getKPlayer(p);
		
		//If player is not blowing any dandelions
		if(!kp.dataExists(Katura.getPlugin(Katura.class), KEY))
		{
			kp.setData(Katura.getPlugin(Katura.class), KEY, true);
			
			BukkitRunnable timer = (
				new BukkitRunnable()
				{
					int counter = 0;
					
					@Override
					public void run()
					{
						Location location = p.getEyeLocation();
				        Vector direction = location.getDirection();
				        
				        if(!p.isOnline())
				        	this.cancel();
				        
				        p.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, 
				        		location.getX(), 
				        		location.getY()-0.1, 
				        		location.getZ(), 
				        		0, 
				        		((float) direction.getX()) + (0.5-Math.random()), 
				        		((float) direction.getY()) + (0.5-Math.random()), 
				        		((float) direction.getZ()) + (0.5-Math.random()),
				        		0.1, 
				        		null);
					    
				        List<Entity> entit = p.getNearbyEntities(20, 20, 20);
				        
				        //Toista puhallus��ni pelaajille
				        if(counter%5 == 0)
				        {
					        for(Entity e : entit)
					        	if(e instanceof Player)
					        		((Player) e).playSound(location, Sound.UI_TOAST_IN, 1, 1.3F);
					        p.playSound(location, Sound.UI_TOAST_IN, 1, 1.3F);
				        }
				        
						//Lopeta puhallus
						if(counter == 15)
						{
							this.cancel();
							kp.removeData(Katura.getPlugin(Katura.class), KEY);
						}
						
						counter++;
					}
				});
			
			timer.runTaskTimerAsynchronously(Katura.getPlugin(Katura.class), 1, 1);
			
			ItemStack dand = null;
			if(hand.equals(EquipmentSlot.HAND))
				dand = p.getInventory().getItemInMainHand();
			else
				dand = p.getInventory().getItemInOffHand();
			
			dand.setAmount(dand.getAmount()-1);
			/*if(hand.equals(EquipmentSlot.HAND))
				p.getInventory().setItemInMainHand(dand);
			else
				p.getInventory().setItemInOffHand(dand);*/
		}
	}
}
