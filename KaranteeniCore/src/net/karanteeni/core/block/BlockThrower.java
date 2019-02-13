package net.karanteeni.core.block;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class BlockThrower {
	
	/**
	 * Throws this block with this speed from ground
	 * @param block
	 * @param speed
	 */
	public void throwBlock(Block block, Vector speed)
	{
		BlockData data = block.getBlockData();
		
		block.setType(Material.AIR);
		block.getState().update();
		
		FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation().add(0,0,0), data);
		fb.setVelocity(speed);
	}
	
	/**
	 * Throws this block with this speed from ground
	 * @param blocks
	 * @param speed
	 */
	public void throwBlocks(List<Block> blocks, double speed)
	{
		Random r = new Random();
		for(Block block : blocks)
		{
			BlockData data = block.getBlockData();
			block.setType(Material.AIR);
			block.getState().update();
			FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation().add(0,0.2,0), data);
			fb.setVelocity(new Vector(speed/2 - r.nextDouble()*speed, r.nextDouble()*speed, speed/2 - r.nextDouble()*speed));
		}
	}
	
	/**
	 * Throws this block with this speed from ground
	 * @param blocks
	 * @param speed
	 */
	public void throwBlocksSpherically(Block block, double speed, boolean upOnly)
	{
		BlockData data = block.getBlockData().clone();
		Location loc = block.getLocation().add(0,10,0);
		
		block.setType(Material.AIR);
		block.getState().update();
		
		if(!upOnly)
		for(double i = -Math.PI/2; i <= Math.PI/2; i += Math.PI/6)
		{
			for(double j = 0; j <= Math.PI*2; j+=Math.PI/6)
			{
				double x = Math.cos(i) * Math.sin(j);
				double y = Math.cos(i) * Math.cos(j);
				double z = Math.sin(i);
				
				FallingBlock fb = block.getWorld().spawnFallingBlock(loc, data);
	    		fb.setVelocity(new Vector(x/Math.PI*speed, y/Math.PI*speed, z/Math.PI*speed));
			}
		}
	}
}
