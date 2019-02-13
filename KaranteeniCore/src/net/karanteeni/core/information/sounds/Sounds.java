package net.karanteeni.core.information.sounds;

import org.bukkit.Sound;

public enum Sounds {
	NONE(null, 0f, 0f),
	NO(Sound.ENTITY_VILLAGER_NO, 5f, 1.5f),
	NOTIFICATION(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f),
	TELEPORT(Sound.ENTITY_ENDERMAN_TELEPORT, 1000000f, 0.5f),
	SETTINGS(Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1f, 2f),
	EQUIP(Sound.ITEM_ARMOR_EQUIP_LEATHER, 5f, 0.85f),
	ERROR(Sound.BLOCK_ANVIL_LAND, 5f, 0.85f),
	EAT(Sound.ENTITY_PLAYER_BURP, 5f, 1.7f),
	ELEVATOR(Sound.ENTITY_SHULKER_BULLET_HIT, 2f, 1.8f),
	PUNISHMENT(Sound.BLOCK_ANVIL_LAND, 2f, 1.8f),
	FIREWORK(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 10f, 0.7f),
	COUNTDOWN(new Sound[] {Sound.BLOCK_NOTE_BLOCK_FLUTE, Sound.BLOCK_NOTE_BLOCK_PLING}, new Float[] {10f, 0.7f}, new Float[] {0.6f, 0.6f}),
	COUNTDOWN_STOP(new Sound[] {Sound.BLOCK_NOTE_BLOCK_FLUTE, Sound.BLOCK_NOTE_BLOCK_PLING}, new Float[] {10f, 1f}, new Float[] {0.9f, 0.9f}),
	FIREWORK_LOW(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2f, 0.2f),
	PLING_HIGH(Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f),
	PLING_LOW(Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.4f),
	CLICK_SUCCESS(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.8f),
	//CLICK_FAIL(new Sound[] {Sound.BLOCK_STONE_BUTTON_CLICK_ON, Sound.ITEM_AXE_STRIP}, new Float[] {0.4f, 100f}, new Float[] {1.7f, 1.8f}),
	CLICK_FAIL(Sound.BLOCK_SNOW_PLACE, 5f, 1.8f),
	CLICK(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.7f, 1.7f),
	CLICK_NO_PERMISSION(Sound.BLOCK_END_PORTAL_FRAME_FILL, 100f, 1.6f);
	
	/**
	 * Rakennin enumille
	 * @param sounds sounds which are used
	 * @param volume volumes for sounds
	 * @param pitch pitch for sounds
	 */
	Sounds(Sound[] sounds, Float[] volume, Float[] pitch)
	{
		this.type = new SoundType(sounds, volume, pitch);
	}
	
	/**
	 * 
	 * @param sounds sound which is used
	 * @param volume volume for sound
	 * @param pitch pitch for sound
	 */
	Sounds(Sound sounds, Float volume, Float pitch)
	{
		this.type = new SoundType(new Sound[] {sounds}, new Float[] {volume}, new Float[] {pitch});
	}
	
	private final SoundType type;
	
	public Sound[] getSound() {return type.getSounds();}
	public Float[] getVolume() {return type.getVolumes();}
	public Float[] getPitch() {return type.getPitches();}
	public SoundType get() {return type;}
}