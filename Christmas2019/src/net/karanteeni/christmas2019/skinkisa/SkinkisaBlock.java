package net.karanteeni.christmas2019.skinkisa;

import java.util.UUID;
import org.bukkit.block.Block;
import net.karanteeni.core.block.executable.ActionBlock;

public class SkinkisaBlock extends ActionBlock {
	private UUID assignedPlayer;
	
	public SkinkisaBlock(Block block) throws IllegalArgumentException {
		super(block);
		// TODO Auto-generated constructor stub
	}
	
	public SkinkisaBlock(Block block, UUID uuid) throws IllegalArgumentException {
		super(block, uuid);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	
	public void assignPlayer(UUID uuid) {
		this.assignedPlayer = uuid;
	}
	
	
	public void unassignPlayer() {
		this.assignedPlayer = null;
	}
}
