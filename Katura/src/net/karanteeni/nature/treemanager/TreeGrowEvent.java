package net.karanteeni.nature.treemanager;

import java.util.List;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class TreeGrowEvent implements Listener {
	
	
	@EventHandler
	public void onTreeGrow(StructureGrowEvent event) {
		TreeGenerator gen = new TreeGenerator();
		List<BlockState> states = gen.generateTree(event.getSpecies(), event.getLocation());
		event.getBlocks().clear();
		event.getBlocks().addAll(states);
	}
}
