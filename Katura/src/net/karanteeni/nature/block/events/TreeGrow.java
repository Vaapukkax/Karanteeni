package net.karanteeni.nature.block.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import net.karanteeni.nature.Katura;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.Particles;

public class TreeGrow implements Listener{

	private static String configKey = "tree-grow-effect";
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	public TreeGrow()
	{
		pl = Katura.getPlugin(Katura.class);
		//Save a config option for the lightning effects
		if(!pl.getConfig().isSet(configKey))
		{
			pl.getConfig().set(configKey, true);
			pl.saveConfig();
		}
	}
	
	@EventHandler
	public void plantGrowth(StructureGrowEvent event)
	{		
		if(!event.isCancelled() && event.getBlocks().size() > 4)
		{
			if(!pl.getConfig().getBoolean(configKey))
				return;
			
			//Get the location of the growth event
			Location l = event.getLocation();
			
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(Particles.CLOUD, true, (float)l.getX()+0.5F, (float)l.getY()+2, (float)l.getZ()+0.5F, 1.3F, 1.3F, 1.3F, 0.1F, 50);
			
			l.getWorld().playSound(l, Sound.ENTITY_ITEM_PICKUP, 1F, 0.7F);
			l.getWorld().playSound(l, Sound.BLOCK_WOOL_STEP, 2F, 0.7F);
			
			//Loop all players
	        for(Player player : Bukkit.getOnlinePlayers())
	        {
	        	//Player's world is the same as the trees world
	        	if(player.getWorld().equals(l.getWorld()))
	        	{
	        		double distance = l.distance(player.getLocation());
	        		
	        		//Check that player is not too far away
	        		if(distance < 10)
	        		{
		        		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		        		
	                    Vector direction = player.getLocation().toVector().subtract(l.add(0, -2, 0).toVector()).normalize();
	                    direction.multiply(1.8/(distance*distance));
	                    player.setVelocity(direction);
	        		}
	        	}
	        }
		}	
	}
}
