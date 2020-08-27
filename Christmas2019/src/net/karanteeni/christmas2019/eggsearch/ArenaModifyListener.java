package net.karanteeni.christmas2019.eggsearch;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.text.Prefix;

public class ArenaModifyListener implements Listener {
	@EventHandler
	public void placeEgg(BlockPlaceEvent event) {
		if(!event.getPlayer().isOp() || !Christmas.getInstance().getGameState().isEditOngoing() || event.getBlock().getType() != Material.OBSIDIAN)
			return;
		
		
		event.setCancelled(true);
		
		if(Christmas.getActionBlockManager().getActionBlock(event.getBlock().getLocation()) != null) {
			event.getPlayer().sendMessage(Prefix.NEGATIVE + "Tässä kohdassa on jo muna");
			return;
		}
		
		EggBlock block = new EggBlock(event.getBlock());
		if(QueryState.INSERTION_SUCCESSFUL == block.save()) {
			block.register();
			Bukkit.getScheduler().runTaskLater(Christmas.getInstance(), new Runnable() {
				@Override
				public void run() {
					Christmas.getInstance().getGameState().registerEgg(block);
					block.setEditMode(true);
					event.getPlayer().sendMessage(Prefix.NEUTRAL + "Lisättiin muna areenalle");
				}
			}, 1);
		} else {
			event.getPlayer().sendMessage(Prefix.NEGATIVE + "Tässä kohdassa on jo muna");
		}
	}
}
