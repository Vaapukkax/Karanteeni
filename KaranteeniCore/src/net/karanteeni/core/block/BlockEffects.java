package net.karanteeni.core.block;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.particle.CoordinateMath;

/**
 * Class for creating effects to blocks
 * @author Matti
 *
 */
public class BlockEffects {
	private HashMap<Effect, TriConsumer<Location, Particle, SoundType>> effectFunc = 
			new HashMap<Effect, TriConsumer<Location, Particle, SoundType>>();
	
	/**
	 * Initialize the blockEffects variables to functions
	 */
	protected BlockEffects()
	{
		effectFunc.put(Effect.ENCHANT, this::enchantEffect);
		effectFunc.put(Effect.CUBE, this::cubeEffect);
	}
	
	/**
	 * Different effect types for blocks
	 * @author Matti
	 *
	 */
	public static enum Effect {
		ENCHANT,
		CUBE
	}
	
	//============[ CALL EFFECTS ]============
	
	/**
	 * Creates an effect to block b
	 * @param b
	 * @param effect
	 * @param particle
	 * @param sound
	 */
	public void createEffect(Block b, Effect effect, Particle particle, SoundType sound)
	{
		//Run the function with these parameters
		if(effectFunc.containsKey(effect))
			effectFunc.get(effect).accept(b.getLocation().add(0.5, 0.5, 0.5), 
					particle, sound);
	}
	
	/**
	 * Creates an effect to block b
	 * @param b
	 * @param effect
	 * @param particle
	 */
	public void createEffect(Block b, Effect effect, Particle particle)
	{
		//Run the function with these parameters
		if(effectFunc.containsKey(effect))
			effectFunc.get(effect).accept(b.getLocation().add(0.5, 0.5, 0.5), 
					particle, Sounds.NONE.get());
	}
	
	/**
	 * Creates an effect to block b
	 * @param l
	 * @param effect
	 * @param particle
	 * @param sound
	 */
	public void createEffect(Location l, Effect effect, Particle particle, SoundType sound)
	{
		if(effectFunc.containsKey(effect))
			effectFunc.get(effect).accept(l, particle, sound);
	}
	
	/**
	 * Creates an effect to block b
	 * @param l
	 * @param effect
	 * @param particle
	 */
	public void createEffect(Location l, Effect effect, Particle particle)
	{
		//Run the function with these parameters
		if(effectFunc.containsKey(effect))
			effectFunc.get(effect).accept(l, particle, Sounds.NONE.get());
	}
	
	// ===================[ BREAKING EFFECTS ]=====================
	
	/**
	 * Plays the enchant effect for block
	 * @param l
	 * @param particle
	 * @param sound
	 */
	private void enchantEffect(Location l, Particle particle, SoundType sound)
	{
		//Create breaking sound
		KaranteeniCore.getSoundHandler().playSound(l.getWorld(), sound, l);
		//Create breaking effect
		l.getWorld().spawnParticle(particle, l, 15, 0.3,0.3,0.3,0.3);
	}
	
	/**
	 * Plays the enchant effect for block
	 * @param l
	 * @param particle
	 * @param sound
	 */
	private void cubeEffect(Location l, Particle particle, SoundType sound)
	{
		//Create breaking sound
		KaranteeniCore.getSoundHandler().playSound(l.getWorld(), sound, l);
		
		List<Location> coords = 
				CoordinateMath.getHollowCube(l.clone().subtract(0.5, 0.5, 0.5), l.clone().add(0.5, 0.5, 0.5), 0.25f);

		//Create breaking effect
		for(Location loc : coords)
			l.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
	}
}
