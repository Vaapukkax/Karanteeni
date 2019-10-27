package net.karanteeni.nature.entity.carry;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.karanteeni.core.timers.KaranteeniTimer;

public class CarryEffects implements KaranteeniTimer {
	private Set<UUID> carriers = null;
	private TreeMap<UUID, PotionEffectType> effects = null;
	
	public CarryEffects(Set<UUID> carriers) {
		effects = new TreeMap<UUID, PotionEffectType>();
		this.carriers = carriers;
	}
	
	
	/**
	 * Clear potion effects from player who throws the entity away
	 * @param uuid
	 */
	public void clearEffect(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && !player.isOnline())
			return;
		
		PotionEffectType effect = effects.get(uuid);
		if(effect == null)
			return;
			
		// get effect, verify its custom and remove if it is custom
		PotionEffect potionEffect = player.getPotionEffect(effect);
		if(!potionEffect.hasIcon() && !potionEffect.hasParticles())
			player.removePotionEffect(effect);
		
		
	}
	
	
	@Override
	public void runTimer() {
		for(UUID uuid : carriers) {
			Player player = Bukkit.getPlayer(uuid);
			// check if player carries anything
			if(player == null || !player.isOnline() || player.getPassengers().size() == 0) {
				// of not, remove the effect
				effects.remove(player);
				carriers.remove(player.getUniqueId());
				continue;
			}
			
			switch (player.getPassengers().get(0).getType()) {
			case CHICKEN:
				effects.put(uuid, PotionEffectType.SLOW_FALLING);
				break;
			default:
				effects.remove(uuid);
				break;
			}
		}
		
		for(Entry<UUID, PotionEffectType> entry : effects.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			player.addPotionEffect(new PotionEffect(entry.getValue(), 40, 1, false, false, false), false);
		}
	}
	

	@Override
	public void timerStopped() {
		
	}

	
	@Override
	public void timerWait() {
		
	}
}
