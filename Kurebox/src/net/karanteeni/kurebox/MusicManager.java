package net.karanteeni.kurebox;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;

public class MusicManager implements TranslationContainer {
	private float volume = 1;
	private final Kurebox plugin;
	private boolean displayActionbar = true;
	private RadioSongPlayer customMusicPlayer;
	private File songDir;
	private LinkedList<Song> songs = new LinkedList<Song>();
	private HashMap<UUID, RadioSongPlayer> loopingSongs = new HashMap<UUID, RadioSongPlayer>();
	
	public MusicManager(Kurebox plugin) {
		this.plugin = plugin;
		initializeConfig();
		registerTranslations();
		initialize();
	}
	
	
	/**
	 * Initializes the radio song player
	 */
	private void initialize() {
		// create song directory
		String path = plugin.getDataFolder().getPath();
		path += File.separator;
		path += "songs";
		songDir = new File(path);
		songDir.mkdirs();
		
		// playlist for sogs
		Playlist playList = null;
		
		// load the songs from music folder
		for(File file : songDir.listFiles()) {
			Song song = NBSDecoder.parse(file);
			
			if(song == null)
				continue;
			
			if(playList == null)
				playList = new Playlist(song);
			else
				playList.add(song);
			songs.add(song);
		}
		
		if(playList != null) {
			this.customMusicPlayer = new RadioSongPlayer(playList);
			this.customMusicPlayer.setAutoDestroy(false);
			this.customMusicPlayer.setRepeatMode(RepeatMode.ALL);
			this.customMusicPlayer.setRandom(true);
			this.customMusicPlayer.setVolume((byte)(volume * Byte.MAX_VALUE));
			this.customMusicPlayer.setPlaying(true);			
		}
	}
	
	
	/**
	 * Returns all the loaded songs
	 * @return all the loaded songs
	 */
	public List<Song> getSongs() {
		return new ArrayList<Song>(songs);
	}
	
	
	/**
	 * Initializes the config for music manager
	 */
	private void initializeConfig() {
		if(!plugin.getConfig().isSet("volume")) {
			plugin.getConfig().set("volume", 0.7f);
			plugin.saveConfig();
		}
		this.volume = (float)plugin.getConfig().getDouble("volume");
		
		if(!plugin.getConfig().isSet("display-played-music")) {
			plugin.getConfig().set("display-played-music", true);
			plugin.saveConfig();
		}
		this.displayActionbar = plugin.getConfig().getBoolean("display-played-music");
	}
	
	
	/**
	 * Stops the vanilla music for this player
	 */
	public void stopGameMusic(final Player player) {
		player.stopSound(Sound.MUSIC_CREATIVE, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_CREDITS, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_END, 			SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_GAME, 			SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_NETHER_NETHER_WASTES, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DRAGON, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_UNDER_WATER, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_MENU, 			SoundCategory.MASTER);
	}
	
	
	/**
	 * Stops all disc music from playing in master category
	 */
	public void stopDiscMusic(final Player player) {
		player.stopSound(Sound.MUSIC_DISC_11, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_13, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_BLOCKS, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_CAT, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_MELLOHI, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_WAIT, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_WARD, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_CHIRP, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_FAR, 		SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_MALL, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_STAL, 	SoundCategory.MASTER);
		player.stopSound(Sound.MUSIC_DISC_STRAD, 	SoundCategory.MASTER);
	}
	
	
	/**
	 * Checks if the player has a radio playing
	 * @param player player whose radio state is being checked
	 * @return true if radio is playing, false otherwise
	 */
	public boolean isRadioPlaying(Player player) {
		return this.customMusicPlayer.getPlayerUUIDs().contains(player.getUniqueId());
	}
	
	
	/**
	 * Stops the custom music from player for the given player
	 * @param player
	 */
	public void stopCustomMusic(final Player player) {
		if(customMusicPlayer != null)
			customMusicPlayer.removePlayer(player);
		RadioSongPlayer looper = loopingSongs.remove(player.getUniqueId());
		if(looper != null)
			looper.destroy();
	}
	
	
	/**
	 * Plays the given vanilla music to the given player
	 * @param player player to play the music to
	 * @param music music to play
	 * @param pitch pitch of music
	 */
	public void playMusic(final Player player, final Sound music, final float pitch) {
		player.playSound(player.getLocation(), music, SoundCategory.MASTER, 10000000, pitch);
		
		// display the played music for player
		if(displayActionbar)
			displayActionbar(player, music.name().toLowerCase());
	}
	
	
	/**
	 * Plays the radio for the player. To cancel radio you need to call stopCustomMusic.
	 * @param player player to play the radio to
	 */
	public void playRadio(final Player player) {
		if(customMusicPlayer != null)
			this.customMusicPlayer.addPlayer(player);
	}
	
	
	/**
	 * Plays the given song to the given player in loop
	 * @param player player to play the song to
	 * @param song song to play in loop
	 */
	public void playSong(final Player player, final Song song) {
		Playlist pl = new Playlist(song);
		RadioSongPlayer looper = new RadioSongPlayer(pl);
		looper.setAutoDestroy(false);
		looper.setVolume((byte)(volume * Byte.MAX_VALUE));
		looper.setRepeatMode(RepeatMode.ALL);
		looper.setPlaying(true);
		looper.addPlayer(player);
		// store the player to map
		loopingSongs.put(player.getUniqueId(), looper);
	}
	
	
	/**
	 * Displays the actionbar of the music played for the player
	 * @param player player to whom the song is being played
	 * @param songName name of the song being played
	 */
	public void displayActionbar(final Player player, final String songName) {
		Kurebox.getMessager().sendActionBar(player, Sounds.NONE.get(), 
				Kurebox.getTranslator().getRandomTranslation(plugin, player, "playing-song").replace("%song%", songName));
	}


	@Override
	public void registerTranslations() {
		Kurebox.getTranslator().registerRandomTranslation(plugin, "playing-song", "Now playing: %song%");
	}
}
