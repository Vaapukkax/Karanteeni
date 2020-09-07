package net.karanteeni.randomitems.events;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.randomitems.RandomItems;
import net.karanteeni.randomitems.items.FortuneCookie;

public class FortuneCookieCrack implements Listener {
	private RandomItems plugin;
	
	public FortuneCookieCrack() {
		plugin = RandomItems.getPlugin(RandomItems.class);
		RandomItems.getTranslator().registerRandomTranslation(plugin, "cookie-crack-random", "Zombies are hostile");
		RandomItems.getTranslator().registerRandomTranslation(plugin, "cookie-crack-player", "%player% likes you");
	}
	
	
	@EventHandler
	public void fortuneCookieCraft(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if(item == null)
			return;
		
		if(!FortuneCookie.isFortuneCookie(item))
			return;
		
		Player player = event.getPlayer();
		Location particleLoc = player.getLocation().add(player.getLocation().add(0, 1.5, 0).getDirection().multiply(1));
		particleLoc.getWorld().spawnParticle(Particle.CRIT, particleLoc.add(0, 1.5, 0), 4);
		particleLoc.getWorld().playSound(player.getLocation(), Sound.BLOCK_NETHERRACK_STEP, SoundCategory.PLAYERS, 1, 1.6f);
		displayText(player);
	}
	
	
	private void displayText(Player player) {
		Random random = new Random();
		double displayPlayer = random.nextDouble();
		
		if(displayPlayer < 0.1) {
			List<Player> players = RandomItems.getEntityManager().getNearbyPlayers(player.getLocation(), 20);
			
			if(players.size() > 1) {
				Iterator<Player> iter = players.iterator();
				
				// Remove players who the player cannot see
				while(iter.hasNext()) {
					Player iterPlayer = iter.next();
					if(iterPlayer.getUniqueId().equals(player.getUniqueId())) {
						iter.remove();
						continue;
					}
					
					if(!player.canSee(iterPlayer)) {
						iter.remove();
						continue;
					}
					
					if(iterPlayer.getGameMode().equals(GameMode.SPECTATOR)) {
						iter.remove();
						continue;
					}
					
					if(iterPlayer.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) {
						iter.remove();
						continue;
					}
				}
				
				if(players.size() > 0) {
					Player selectedPlayer = players.get(random.nextInt(players.size()));
					RandomItems.getMessager().sendMessage(player,
							Sounds.NONE.get(),
							Prefix.NEUTRAL +
							RandomItems.getTranslator().getRandomTranslation(
									plugin,
									player,
									"cookie-crack-player")
							.replace("%player%", selectedPlayer.getName()));
				} else {
					displayPlayer = Double.NaN;
				}
			} else {
				displayPlayer = Double.NaN;
			}
		} else {
			displayPlayer = Double.NaN;
		}
		
		if(Double.isNaN(displayPlayer)) {
			RandomItems.getMessager().sendMessage(player,
					Sounds.NONE.get(),
					Prefix.NEUTRAL +
					RandomItems.getTranslator().getRandomTranslation(
							plugin,
							player,
							"cookie-crack-random"));
		}
	}
}
