package net.karanteeni.core.information.sounds;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundHandler {
	
	/**
	 * Plays a sound to a player
	 * @param player player to which the sound will be played
	 * @param sound the sounds to be played
	 * @param category the category for the sounds
	 */
	public void playSound(Player player, SoundType sound, SoundCategory category)
	{
		for(int i = 0; i < sound.getSounds().length; ++i)
		{
			player.playSound(player.getLocation(), sound.getSounds()[i], category, sound.getVolumes()[i], sound.getPitches()[i]);
		}
	}
	
	/**
	 * Plays a sound to a player
	 * @param player player to which the sound will be played
	 * @param sound the sounds to be played
	 * @param category the category for the sounds
	 * @param location The location of the sounds
	 */
	public void playSound(Player player, SoundType sound, SoundCategory category, Location location)
	{
		for(int i = 0; i < sound.getSounds().length; ++i)
		{
			player.playSound(location, sound.getSounds()[i], category, sound.getVolumes()[i], sound.getPitches()[i]);
		}
	}
	
	/**
	 * Plays a sound to a player
	 * @param player player to which the sound will be played
	 * @param sound the sounds to be played
	 */
	public void playSound(Player player, SoundType sound)
	{
		for(int i = 0; i < sound.getSounds().length; ++i)
		{
			player.playSound(player.getLocation(), sound.getSounds()[i], sound.getVolumes()[i], sound.getPitches()[i]);
		}
	}
	
	/**
	 * Plays a sound to a player
	 * @param player player to which the sound will be played
	 * @param sound the sounds to be played
	 * @param location The location of the sounds
	 */
	public void playSound(Player player, SoundType sound, Location location)
	{
		for(int i = 0; i < sound.getSounds().length; ++i)
		{
			player.playSound(location, sound.getSounds()[i], sound.getVolumes()[i], sound.getPitches()[i]);
		}
	}
	
	/**
	 * Plays a sound to a player
	 * @param players player to which the sound will be played
	 * @param sound the sounds to be played
	 * @param category the category for the sounds
	 */
	public void playSound(List<Player> players, SoundType sound, SoundCategory category)
	{
		for(Player player : players)
			for(int i = 0; i < sound.getSounds().length; ++i)
				player.playSound(player.getLocation(), sound.getSounds()[i], category, sound.getVolumes()[i], sound.getPitches()[i]);
	}
	
	/**
	 * Plays a sound to a player
	 * @param players player to which the sound will be played
	 * @param sound the sounds to be played
	 * @param category the category for the sounds
	 * @param location The location of the sounds
	 */
	public void playSound(List<Player> players, SoundType sound, SoundCategory category, Location location)
	{
		for(Player player : players)
			for(int i = 0; i < sound.getSounds().length; ++i)
				player.playSound(location, sound.getSounds()[i], category, sound.getVolumes()[i], sound.getPitches()[i]);
	}
	
	/**
	 * Plays a sound to a player
	 * @param players player to which the sound will be played
	 * @param sound the sounds to be played
	 */
	public void playSound(List<Player> players, SoundType sound)
	{
		for(Player player : players)
			for(int i = 0; i < sound.getSounds().length; ++i)
				player.playSound(player.getLocation(), sound.getSounds()[i], sound.getVolumes()[i], sound.getPitches()[i]);
	}
	
	/**
	 * Plays a sound to a player
	 * @param players player to which the sound will be played
	 * @param sound the sounds to be played
	 * @param location The location of the sounds
	 */
	public void playSound(List<Player> players, SoundType sound, Location location)
	{
		for(Player player : players)
			for(int i = 0; i < sound.getSounds().length; ++i)
				player.playSound(location, sound.getSounds()[i], sound.getVolumes()[i], sound.getPitches()[i]);
	}
	
	/**
	 * Plays a sound in a world
	 * @param world the world where the sound is played at
	 * @param sound the sounds which will be played
	 * @param category the category for the played sounds
	 * @param location the location at which the sound will be played
	 */
	public void playSound(World world, SoundType sound, SoundCategory category, Location location)
	{
		for(int i = 0; i < sound.getSounds().length; ++i)
		{
			world.playSound(location, sound.getSounds()[i], category, sound.getVolumes()[i], sound.getPitches()[i]);
		}
	}
	
	/**
	 * Plays a sound in a world
	 * @param world the world where the sound is played at
	 * @param sound the sounds which will be played
	 * @param location the location at which the sound will be played
	 */
	public void playSound(World world, SoundType sound, Location location)
	{
		for(int i = 0; i < sound.getSounds().length; ++i)
		{
			world.playSound(location, sound.getSounds()[i], sound.getVolumes()[i], sound.getPitches()[i]);
		}
	}
}
