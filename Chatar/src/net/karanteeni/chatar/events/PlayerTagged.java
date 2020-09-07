package net.karanteeni.chatar.events;

import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.information.ChatColor;
import net.karanteeni.core.information.sounds.SoundType;

public class PlayerTagged implements Listener {
	private String tagCharacter;
	private String tagFormat;
	private String playerFormat;
	private SoundType notifySound = new SoundType(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.75f);
	
	public PlayerTagged(Chatar plugin) {
		if(!plugin.getSettings().isSet("tag.character")) {
			plugin.getSettings().set("tag.character", "@");
			plugin.saveSettings();
		}
		tagCharacter = ChatColor.translateAll(plugin.getSettings().getString("tag.character"));
		
		if(!plugin.getSettings().isSet("tag.format")) {
			plugin.getSettings().set("tag.format", "&6@");
			plugin.saveSettings();
		}
		tagFormat = ChatColor.translateAll(plugin.getSettings().getString("tag.format"));
		
		if(!plugin.getSettings().isSet("tag.playerFormat")) {
			plugin.getSettings().set("tag.playerFormat", "&e%player%&f");
			plugin.saveSettings();
		}
		playerFormat = ChatColor.translateAll(plugin.getSettings().getString("tag.playerFormat"));
		
		try {			
			if(!plugin.getSettings().isSet("tag.sound")) {
				plugin.getSettings().set("tag.sound", notifySound.getSounds()[0].name());
				plugin.saveSettings();
			}
			Sound sound = Sound.valueOf(plugin.getSettings().getString("tag.sound"));
			
			if(!plugin.getSettings().isSet("tag.volume")) {
				plugin.getSettings().set("tag.volume", notifySound.getVolumes()[0]);
				plugin.saveSettings();
			}
			float volume = (float)plugin.getSettings().getDouble("tag.volume");
			
			if(!plugin.getSettings().isSet("tag.pitch")) {
				plugin.getSettings().set("tag.pitch", notifySound.getPitches()[0]);
				plugin.saveSettings();
			}
			float pitch = (float)plugin.getSettings().getDouble("tag.pitch");
			
			this.notifySound = new SoundType(sound, volume, pitch);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onTag(AsyncPlayerChatEvent event) {
		if(!event.getPlayer().hasPermission("chatar.chat.tag"))
			return;
		
		int index = 0;		
		HashSet<Player> players = new HashSet<Player>();
		StringBuilder message = new StringBuilder(event.getMessage());

		while((index = message.indexOf(tagCharacter, index)) != -1) {
			int lastChar = message.indexOf(" ", index); // continue to next space

			// if at the end of line, use line length instead
			if(lastChar == -1)
				lastChar = message.length();

			if(lastChar == index + 1) // no chars, no name
				continue;

			// get player name
			String playerName = message.substring(index + tagCharacter.length(), lastChar);

			// check if the player is online
			Player player = Bukkit.getPlayer(playerName);
			
			if(player == null || !player.isOnline()) {
				index += tagCharacter.length() + playerName.length();
				continue;
			}
		
			players.add(player);
			
			// format message to use correct format
			message.replace(index, index + tagCharacter.length(), tagFormat);
			index += tagFormat.length();
		
			String formattedPlayerName = playerFormat.replace("%player%", player.getDisplayName());
			message.replace(index, index + playerName.length(), formattedPlayerName);
			index += formattedPlayerName.length();
		}
		event.setMessage(message.toString());
		
		// play sound for player
		for(Player player : players)
			Chatar.getSoundHandler().playSound(player, notifySound);
	}
}
