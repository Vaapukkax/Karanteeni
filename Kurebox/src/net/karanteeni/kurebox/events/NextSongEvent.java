package net.karanteeni.kurebox.events;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import net.karanteeni.kurebox.Kurebox;

/**
 * Displays the name of the song playing to the player when the song changes
 * @author Nuubles
 *
 */
public class NextSongEvent implements Listener {
	private Kurebox plugin;
	
	public NextSongEvent(Kurebox plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onNextSongPlay(SongNextEvent event) {
		Song song = event.getSongPlayer().getSong();
		
		// loop all players listening to this song
		for(UUID uuid : event.getSongPlayer().getPlayerUUIDs()) {
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) continue;

			// figure out what is the name of the song
			String songName = null;
			
			if(song.getTitle() != null && !song.getTitle().equals("")) {
				songName = song.getTitle();
			} else {
				songName = song.getPath().getName().replaceAll(".nbs", "");
			}
			
			// display the song name
			plugin.getMusicManager().displayActionbar(player, songName);
		}
	}
}
