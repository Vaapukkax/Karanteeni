package net.karanteeni.wgef.events.callable;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import net.karanteeni.wgef.WGEF;
import net.karanteeni.wgef.events.RegionEnterEvent;
import net.karanteeni.wgef.events.RegionExitEvent;

public class RegionMusic implements Listener {
	private HashMap<String, RadioSongPlayer> songs = new HashMap<String, RadioSongPlayer>(); // songs in a given directory
	private HashMap<UUID, String> listeners = new HashMap<UUID, String>(); // players listening to songs
	private WGEF plugin;
	private File songDir;
	public static String ENTER_PLAY_DIR_SONGS = "enter-play-dir-songs";
	
	public RegionMusic(WGEF plugin) {
		this.plugin = plugin;
		// create the subfolder /songs/
		String path = plugin.getDataFolder().getPath();
		path += File.separator;
		path += "songs";
		songDir = new File(path);
		songDir.mkdirs();
	}
	
	
	@EventHandler
	public void onRegionEnter(RegionEnterEvent event) {
		String flagValue = getHighestRankingSongDirectoryInRegion(event.getPlayer(), event.getRegions());
		
		if(flagValue == null)
			return;
		
		playSong(event.getPlayer(), flagValue);
	}
	
	
	@EventHandler
	public void onRegionLeave(RegionExitEvent event) {
		if(!listeners.containsKey(event.getPlayer().getUniqueId()))
			return;
		
		// check if this region is playing for player
		String flagValue = getHighestRankingSongDirectoryInRegion(event.getPlayer(), event.getRegions());
		
		// check if the highest flag in playing is the one player has left
		if(listeners.get(event.getPlayer().getUniqueId()).equals(flagValue)) {
			// stop the radio song player for the player
			RadioSongPlayer rsp = songs.get(flagValue);
			rsp.removePlayer(event.getPlayer());
			
			// check if player is in any newer song player regions
			String newSongDir = getHighestRankingSongDirectoryInRegion(event.getPlayer(), 
					plugin.getWorldGuard().getRegions(event.getPlayer().getLocation()).getRegions());
			
			// start playing the new song for the player
			if(newSongDir != null)
				playSong(event.getPlayer(), newSongDir);
			
			// check which radios aren't used anymore
			clearNotUsedRadios();
		}
	}
	
	
	/**
	 * Removes all the radios which aren't used
	 */
	private void clearNotUsedRadios() {
		Iterator<Entry<String,RadioSongPlayer>> iter = songs.entrySet().iterator();
		
		// remove all not used songs
		while(iter.hasNext()) {
			Entry<String, RadioSongPlayer> entry = iter.next();
			if(entry.getValue().getPlayerUUIDs().size() == 0) {
				entry.getValue().destroy();
				iter.remove();
			}
		}
	}
	
	
	/**
	 * Starts playing the songs in the given directory for player
	 * @param player
	 * @param flagValue
	 */
	private void playSong(Player player, String flagValue) {
		// if player is already listening to a song stop it
		if(listeners.containsKey(player.getUniqueId())) {
			RadioSongPlayer rsp = songs.get(listeners.get(player.getUniqueId()));
			if(rsp != null)
				rsp.removePlayer(player);
			listeners.remove(player.getUniqueId());
			clearNotUsedRadios();
		}
		
		// check if this song is already made and if so, use those songs
		if(songs.containsKey(flagValue)) {
			listeners.put(player.getUniqueId(), flagValue);
			songs.get(flagValue).addPlayer(player);
		} else {
			// this song doesn't exist yet, create a new one
			// folder path to songs
			String filePath = songDir.getPath() + File.separator + flagValue;
			File songFolder = new File(filePath);
			songFolder.mkdirs(); // make the folder if it doesn't yet exist
			
			// playlist for songs
			Playlist playList = null;
			
			// load the songs in the folder
			for(File file : songFolder.listFiles()) {
				Song song = NBSDecoder.parse(file);
				if(song == null)
					continue;
				if(playList != null)
					playList.add(song);
				else
					playList = new Playlist(song);
			}
			
			if(playList != null) {
				// create a new radio player
				RadioSongPlayer rsp = new RadioSongPlayer(playList);
				rsp.setRepeatMode(RepeatMode.ALL);
				rsp.setRandom(true);
				rsp.addPlayer(player);
				
				// add the player as a song listener
				songs.put(flagValue, rsp);
				listeners.put(player.getUniqueId(), flagValue);
				rsp.setVolume((byte)25);
				rsp.setPlaying(true);
			}
		}
	}
	
	
	/**
	 * Returns the highest ranking song flag value in the given regions
	 * @param player
	 * @param regions
	 * @return
	 */
	private String getHighestRankingSongDirectoryInRegion(Player player, Set<ProtectedRegion> regions) {
		int priority = 0;
		String flagValue = null;
		StringFlag songFlag = (StringFlag)plugin.getWorldGuard().getFlag(ENTER_PLAY_DIR_SONGS);
		
		// check all regions and get the highest ranking string flag
		for(ProtectedRegion region : regions) {
			String value = region.getFlag(songFlag);
			if(value == null)
				continue;
			
			// get the highest ranking value for flag
			if(flagValue == null || priority < region.getPriority()) {
				flagValue = value;
				priority = region.getPriority();
			}
		}
		return flagValue;
	}
}
