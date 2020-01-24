package net.karanteeni.tester;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import net.karanteeni.core.block.executable.ActionBlock;

public class TestBlock extends ActionBlock implements ActionBlock.Events.PlayerInteractEvent, ActionBlock.Events.BlockRedstoneEvent {

	public TestBlock(Block block) throws IllegalArgumentException {
		super(block);
	}
	
	
	public TestBlock(Block block, UUID uuid) throws IllegalArgumentException {
		super(block, uuid);
	}

	
	@Override
	public void onLoad() {
		/* ignore */
	}


	@Override
	public void blockRedstoneEvent(BlockRedstoneEvent event) {
		Bukkit.broadcastMessage(event.getBlock().getType().name().toLowerCase() + ": BZZZT!");
	}


	@Override
	public void playerInteractEvent(PlayerInteractEvent event) {
		Bukkit.broadcastMessage("Hi " + event.getPlayer().getName() + "! My name is " + 
				event.getClickedBlock().getType().name().toLowerCase() + 
				" and you clicked me on " + event.getBlockFace().name().toLowerCase() + " side");
	}
}
