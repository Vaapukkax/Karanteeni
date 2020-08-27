package net.karanteeni.tester.sound;

import org.bukkit.Sound;
import net.karanteeni.core.information.sounds.SoundType;

public enum SoundLibrary {
	STARTUP(Sound.ENTITY_ELDER_GUARDIAN_HURT, 5f, 0.8f),
	SHUTDOWN(Sound.ENTITY_ELDER_GUARDIAN_HURT, 5f, 0.6f),
	START(Sound.ENTITY_SHULKER_CLOSE, 1f, 1.5f),
	STOP(Sound.ENTITY_SHULKER_CLOSE, 1f, 0.3f),
	POPUP(new Sound[]{Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.UI_TOAST_IN}, new Float[] {0.2f, 10f}, new Float[] {0.8f, 2f}),
	DEATH(Sound.BLOCK_BELL_USE, 20f, 0.2f),
	HEAL(Sound.ENTITY_SPLASH_POTION_BREAK, 20f, 1.7f);
	
	SoundLibrary(Sound[] sounds, Float[] volume, Float[] pitch) {
		this.type = new SoundType(sounds, volume, pitch);
	}
	
	SoundLibrary(Sound sounds, Float volume, Float pitch) {
		this.type = new SoundType(new Sound[] {sounds}, new Float[] {volume}, new Float[] {pitch});
	}
	
	private final SoundType type;
	public SoundType get() {return type;}
}
