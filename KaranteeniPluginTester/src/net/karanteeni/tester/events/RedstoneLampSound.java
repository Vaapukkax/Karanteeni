package net.karanteeni.tester.events;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.tester.sound.SoundLibrary;

public class RedstoneLampSound implements Listener {
	@EventHandler
	public void onRedstoneLampStart(BlockRedstoneEvent event) {
		if(event.getBlock().getType() != Material.REDSTONE_LAMP)
			return;
		
		List<Player> players = KaranteeniCore.getEntityManager().getNearbyPlayers(event.getBlock().getLocation(), 20);
		
		if(event.getOldCurrent() == 0) {
			KaranteeniCore.getSoundHandler().playSound(players, SoundLibrary.START.get());			
		} else {
			KaranteeniCore.getSoundHandler().playSound(players, SoundLibrary.STOP.get());
		}
	}

}
