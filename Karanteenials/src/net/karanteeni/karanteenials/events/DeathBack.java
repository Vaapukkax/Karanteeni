package net.karanteeni.karanteenials.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;
import net.karanteeni.karanteenials.functionality.Back;

public class DeathBack implements Listener, TranslationContainer {
	
	public DeathBack() {
		registerTranslations();
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onDeath(PlayerDeathEvent event) {
		// does the player have the permission required to allow backing to death point
		if(!event.getEntity().hasPermission("karanteenials.player.death.back"))
			return;
		
		Location deathLocation = event.getEntity().getLocation();
		Back back = new Back(event.getEntity());
		back.setBackLocation(deathLocation);
		
		// instruct player to use /back to return to death position
		Karanteenials.getMessager().sendMessage(event.getEntity(), Sounds.NONE.get(), 
				Prefix.NEUTRAL + Karanteenials.getTranslator().getTranslation(Karanteenials.getPlugin(Karanteenials.class), 
						event.getEntity(), 
						"death-return-with-back"));
	}

	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(Karanteenials.getPlugin(Karanteenials.class), 
				"death-return-with-back", "Return to death location with /back");
	}
}
