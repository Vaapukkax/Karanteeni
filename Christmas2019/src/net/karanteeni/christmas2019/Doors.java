package net.karanteeni.christmas2019;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import net.karanteeni.core.block.BlockCollection;

public class Doors {
	private boolean isFence;
	private List<Block> blocks;
	
	public Doors(boolean isFence, List<Location> locs) {
		blocks = new ArrayList<Block>();
		for(Location loc : locs)
			blocks.add(loc.getBlock());
		this.isFence = isFence;
	}
	
	
	public void open() {
		if(isFence) {
			// get all doors which consist of multiple blocks
			for(Block block : blocks) {
				// accept only oak fences
				if(block.getType() != Material.OAK_FENCE)
					continue;
				
				BlockCollection coll = BlockCollection.scanBlockTypes(block, false, true);
				// destroy the fence blocks
				for(Block b : coll) {
					b.setType(Material.AIR);
					b.getState().update();
				}
			}
		} else {
			// they are doors, open them
			for(Block block : blocks) {
				// open only doors
				if(!(block.getState() instanceof Door))
					continue;
				
				Door door = (Door)block.getState();
				door.setOpen(true);
				block.getState().update();
			}
		}
	}
}
