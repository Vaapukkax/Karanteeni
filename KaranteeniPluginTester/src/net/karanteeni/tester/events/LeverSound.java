package net.karanteeni.tester.events;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.tester.sound.SoundLibrary;

public class LeverSound implements Listener {
	@EventHandler
	public void onRedstoneLampStart(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		Block block = event.getClickedBlock();
		if(block == null || block.getType() != Material.LEVER)
			return;
		
		Powerable leverData = (Powerable)block.getBlockData();
		List<Player> players = KaranteeniCore.getEntityManager().getNearbyPlayers(block.getLocation(), 20);
		
		if(leverData.isPowered()) {
			KaranteeniCore.getSoundHandler().playSound(players, SoundLibrary.STARTUP.get());
		} else {
			KaranteeniCore.getSoundHandler().playSound(players, SoundLibrary.SHUTDOWN.get());
		}
	}

}
