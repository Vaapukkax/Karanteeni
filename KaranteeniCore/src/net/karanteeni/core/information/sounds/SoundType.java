package net.karanteeni.core.information.sounds;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.Sound;

/**
 * Useful class for creating custom sounds
 * @author Nuubles
 *
 */
public class SoundType
{
	Entry<Sound[], Entry<Float[], Float[]> > sound = null;
	
	public SoundType(Sound[] sounds, Float[] volume, Float[] pitch)
	{
		sound = new SimpleEntry<Sound[], Entry<Float[], Float[]> >(sounds, new SimpleEntry<Float[], Float[]>(volume, pitch));
	}
	
	public SoundType(Sound sound, Float volume, Float pitch)
	{
		this.sound = new SimpleEntry<Sound[], Entry<Float[], Float[]>>
			(new Sound[] {sound}, new SimpleEntry<Float[], Float[]>(new Float[] {volume}, new Float[] {pitch}));
	}
	
	/**
	 * Return the sounds of this soundType
	 * @return
	 */
	public Sound[] getSounds()
	{
		return sound.getKey();
	}
	
	/**
	 * Return the volumes of this soundtype
	 * @return
	 */
	public Float[] getVolumes()
	{
		return sound.getValue().getKey();
	}
	
	/**
	 * Return the pitches of this soundtype
	 * @return
	 */
	public Float[] getPitches()
	{
		return sound.getValue().getValue();
	}
}