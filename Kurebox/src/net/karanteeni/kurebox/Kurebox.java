package net.karanteeni.kurebox;

import java.util.Arrays;
import org.bukkit.Material;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.kurebox.commands.Music;
import net.karanteeni.kurebox.commands.Radio;
import net.karanteeni.kurebox.events.NextSongEvent;
import net.karanteeni.kurebox.events.PlayerQuit;

public class Kurebox extends KaranteeniPlugin implements TranslationContainer {
	private MusicManager manager = null;
	
	public Kurebox() {
		super(true);
	}

	
	@Override
	public void onEnable() {
		registerTranslations();
		registerConfigKeys();
		manager = new MusicManager(this);
		
		registerEvents();
		registerCommands();
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
		getServer().getPluginManager().registerEvents(new NextSongEvent(this), this);		
	}
	
	
	private void registerCommands() {
		Music musicCommand = new Music(this);
		musicCommand.register();
		Radio radioCommand = new Radio(this);
		radioCommand.register();
	}
	
	
	/**
	 * Gets the music manager of this plugin
	 * @return music manager of this plugin
	 */
	public MusicManager getMusicManager() {
		return this.manager;
	}


	@Override
	public void registerTranslations() {
		getTranslator().registerTranslation(this, "icon.random", "§eRandom song");
		getTranslator().registerTranslation(this, "icon.radio", "§eRadio");
		getTranslator().registerTranslation(this, "icon.stop-music", "§cStop music");
		getTranslator().registerTranslation(this, "icon.vanilla-discs", "§6Vanilla discs");
		getTranslator().registerTranslation(this, "icon.vanilla-music", "§6Vanilla music");
		getTranslator().registerTranslation(this, "icon.custom-music", "§6Server music");
		getTranslator().registerTranslation(this, "game-music.menu", "§eMenu");
		getTranslator().registerTranslation(this, "game-music.nether", "§eNether");
		getTranslator().registerTranslation(this, "game-music.survival", "§eSurvival");
		getTranslator().registerTranslation(this, "game-music.creative", "§eCreative");
		getTranslator().registerTranslation(this, "game-music.underwater", "§eUnderwater");
		getTranslator().registerTranslation(this, "game-music.end", "§eEnd");
		getTranslator().registerTranslation(this, "game-music.dragon", "§eDragon");
		getTranslator().registerTranslation(this, "game-music.end-screen", "§eEnd screen");
	}
	
	
	/**
	 * Registers config values
	 */
	private void registerConfigKeys() {
		if(!getConfig().isSet("icons.radio")) {
			getConfig().set("icons.radio", Material.JUKEBOX.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-discs-menu")) {
			getConfig().set("icons.vanilla-discs-menu", Material.BOOK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.server-music-menu")) {
			getConfig().set("icons.server-music-menu", Material.WRITABLE_BOOK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music-menu")) {
			getConfig().set("icons.vanilla-music-menu", Material.KNOWLEDGE_BOOK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.random")) {
			getConfig().set("icons.random", Material.SLIME_BALL.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.stop")) {
			getConfig().set("icons.stop", Material.REDSTONE_BLOCK.toString());
			saveConfig();
		}
		
		if(!getConfig().isSet("icons.vanilla-music.survival")) {
			getConfig().set("icons.vanilla-music.survival", Material.EMERALD_ORE.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.nether")) {
			getConfig().set("icons.vanilla-music.nether", Material.NETHERRACK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.menu")) {
			getConfig().set("icons.vanilla-music.menu", Material.GRASS_BLOCK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.creative")) {
			getConfig().set("icons.vanilla-music.creative", Material.EMERALD_BLOCK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.underwater")) {
			getConfig().set("icons.vanilla-music.underwater", Material.BLUE_STAINED_GLASS.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.end")) {
			getConfig().set("icons.vanilla-music.end", Material.END_STONE.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.dragon")) {
			getConfig().set("icons.vanilla-music.dragon", Material.PURPLE_GLAZED_TERRACOTTA.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.vanilla-music.end-screen")) {
			getConfig().set("icons.vanilla-music.end-screen", Material.PURPUR_BLOCK.toString());
			saveConfig();
		}
		if(!getConfig().isSet("icons.song-icons")) {
			getConfig().set("icons.song-icons", Arrays.asList(
					Material.MUSIC_DISC_13.toString(),
					Material.MUSIC_DISC_BLOCKS.toString(),
					Material.MUSIC_DISC_CAT.toString(),
					Material.MUSIC_DISC_CHIRP.toString(),
					Material.MUSIC_DISC_FAR.toString(),
					Material.MUSIC_DISC_MALL.toString(),
					Material.MUSIC_DISC_MELLOHI.toString(),
					Material.MUSIC_DISC_STAL.toString(),
					Material.MUSIC_DISC_STRAD.toString(),
					Material.MUSIC_DISC_WAIT.toString()));
			saveConfig();
		}
	}
}
