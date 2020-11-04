package net.karanteeni.utilika.events;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.karanteeni.utilika.Utilika;
import net.karanteeni.utilika.worldguard.WorldGuardManager;

public class InvisibleItemFrame implements Listener {
	private static final String INVISIBILITY_KEY = "ut.inv";
	private Utilika plugin;
	
	public InvisibleItemFrame() {
		plugin = Utilika.getPlugin(Utilika.class);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onSplash(ProjectileHitEvent event) {
		ThrownPotion potion = null;
		if(!(event.getEntity() instanceof ThrownPotion))
			return;
		potion = (ThrownPotion)event.getEntity();
		

		if(potion.getEffects().isEmpty())
			return;
		boolean containsInvisibility = false;
		for(PotionEffect e : potion.getEffects()) {
			if(e.getType() == PotionEffectType.INVISIBILITY) {
				containsInvisibility = true;
			}
		}
		if(!containsInvisibility)
			return;
		if(!(event.getEntity().getShooter() instanceof Player))
			return;

		Location hitLocation = null;
		if(event.getHitBlock() != null)
			hitLocation = event.getHitBlock().getLocation();
		else
			hitLocation = event.getHitEntity().getLocation();

		List<Entity> itemFrames = Utilika.getEntityManager().getNearbyEntityTypes(hitLocation, EntityType.ITEM_FRAME, 2, 2, 2);
		Player player = (Player)event.getEntity();
		
		for(Entity entity : itemFrames) {
			ItemFrame itemFrame = (ItemFrame)entity;
			// can the player build at the block
            if(!canPlayerPlace(player, itemFrame.getLocation().getBlock()))
            	continue;
            
			if(!itemFrame.hasMetadata(INVISIBILITY_KEY)) {
				itemFrame.setMetadata(INVISIBILITY_KEY, new FixedMetadataValue(plugin, true));
				if(itemFrame.getItem() == null || itemFrame.getItem().getType() != Material.AIR) {
					itemFrame.setVisible(false);
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onFrameBreak(HangingBreakEvent event) {
		if(event.getEntity().getType() != EntityType.ITEM_FRAME)
			return;
		if(event.getEntity().hasMetadata(INVISIBILITY_KEY)) {
			event.getEntity().removeMetadata(INVISIBILITY_KEY, plugin);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void removeFromFrameEvent(EntityDamageByEntityEvent e) {
	    if (e.getEntity() instanceof ItemFrame)
	    	if(e.getEntity().hasMetadata(INVISIBILITY_KEY))
	    		((ItemFrame)e.getEntity()).setVisible(true);
	}
	
	
    @EventHandler
    public void FrameRotate(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            if(!e.getRightClicked().hasMetadata(INVISIBILITY_KEY))
            	return;
            
            // can the player build at the block
            if(!canPlayerPlace(e.getPlayer(), e.getRightClicked().getLocation().getBlock()))
            	return;
        	
        	ItemFrame frame = (ItemFrame)e.getRightClicked();
            if((frame.getItem() == null || frame.getItem().getType() == Material.AIR) &&
            		(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR
            		&& e.getPlayer().getInventory().getItemInOffHand().getType() == Material.AIR))
            	return;
            
            frame.setVisible(false);
        }
    }
    
    
	private boolean canPlayerPlace(Player player, Block block) {
		WorldGuardManager wgm = plugin.getWorldGuardManager();
		if(wgm != null) {
			return wgm.canBuild(player, block);
		}
		return true;
	}
}
