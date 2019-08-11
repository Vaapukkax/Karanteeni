package net.karanteeni.core.information;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
//import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.bossbar.TimedBossBar;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.text.TextUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle.EnumTitleAction;

public class Messager {
	
	/**
	 * Sends a message to a player with sound
	 * @param player
	 */
	public void sendMessage(final CommandSender player, final SoundType sound, final String prefix, final String message) {
		player.sendMessage(prefix + message);
		if(player instanceof Player)
			KaranteeniCore.getSoundHandler().playSound((Player)player, sound);
	}
	
	
	/**
	 * Sends a message to a player with sound
	 * @param player
	 */
	public void sendMessage(final CommandSender player, final SoundType sound,final  String message) {
		player.sendMessage(message);
		if(player instanceof Player)
			KaranteeniCore.getSoundHandler().playSound((Player)player, sound);
	}
	
	
	/**
	 * Sends a message to players with sound
	 * @param players
	 * @param sound
	 * @param prefix
	 * @param message
	 */
	public void sendMessage(final List<Player> players, final SoundType sound, final String prefix, final String message) {
		for(Player player : players) {
			player.sendMessage(prefix + message);
			KaranteeniCore.getSoundHandler().playSound(player, sound);
		}
	}
	
	
	/**
	 * Sends a message to players with sound
	 * @param players
	 * @param sound
	 * @param message
	 */
	public void sendMessage(final List<Player> players, final SoundType sound, final String message) {
		for(Player player : players) {
			player.sendMessage(message);
			KaranteeniCore.getSoundHandler().playSound(player, sound);
		}
	}
	
	
	/**
	 * Sends a message to commandsender with sound
	 * @param player
	 * @param sound
	 * @param component
	 */
	public void sendMessage(final CommandSender sender, final SoundType sound, final BaseComponent component) {
		if(sender instanceof Player) {
			((Player)sender).spigot().sendMessage(component);
			KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
		} else {
			sender.sendMessage(component.toLegacyText());
		}
	}
	
	
	/**
	 * Sends a message to a player with sound
	 * @param player
	 * @param sound
	 * @param component
	 */
	public void sendMessage(final Player player, final SoundType sound, final BaseComponent component) {
		player.spigot().sendMessage(component);
		KaranteeniCore.getSoundHandler().playSound(player, sound);
	}
	
	
	/**
	 * Sends a message to players with sound
	 * @param players
	 * @param sound
	 * @param component
	 */
	public void sendMessage(final List<Player> players, final SoundType sound, final BaseComponent component) {
		for(Player player : players) {
			player.spigot().sendMessage(component);
			KaranteeniCore.getSoundHandler().playSound(player, sound);
		}
	}
	
	
	/**
	 * Sends a message to a player with sound
	 * @param player
	 * @param component
	 */
	public void sendMessage(final Player player, final BaseComponent component) {
		player.spigot().sendMessage(component);
	}
	
	
	/**
	 * Sends a message to players with sound
	 * @param players
	 * @param component
	 */
	public void sendMessage(final List<Player> players, final BaseComponent component) {
		for(Player player : players) {
			player.spigot().sendMessage(component);
		}
	}
	
	
	/**
	 * Sends a translated message to players with sound
	 * @param players
	 * @param sound
	 * @param key
	 * @param plugin
	 */
	public void sendTranslatedMessage(final List<Player> players, final SoundType sound, final String key, KaranteeniPlugin plugin) {
		for(Player player : players) {
			player.sendMessage(KaranteeniPlugin.getTranslator().getTranslation(plugin, player, key));
			KaranteeniCore.getSoundHandler().playSound(player, sound);
		}
	}
	
	
	/**
	 * Broadcasts a translated message to players with sound
	 * @param sound
	 * @param key
	 * @param plugin
	 */
	public void broadcastTranslatedMessage(final SoundType sound, final String key, KaranteeniPlugin plugin) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(KaranteeniPlugin.getTranslator().getTranslation(plugin, player, key));
			KaranteeniCore.getSoundHandler().playSound(player, sound);
		}
	}
	
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts
	 * @param player
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param text
	 */
	public void sendBossbar(final Player player, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final String text) {
		BossBar bar = Bukkit.createBossBar(text, BarColor.YELLOW, BarStyle.SOLID);
		
		//Luodaan ajastettu bossbar
		TimedBossBar tbar = new TimedBossBar(player, bar, stay, animated);
		//Sammutetaan ajastettu bossbar
		KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
		
		KaranteeniCore.getSoundHandler().playSound(player, sound);
	}
	
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts
	 * @param player
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param bar
	 */
	public void sendBossbar(final Player player, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final BossBar bar) {
		//Luodaan ajastettu bossbar
		TimedBossBar tbar = new TimedBossBar(player, bar, stay, animated);
		//Sammutetaan ajastettu bossbar
		KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
		
		KaranteeniCore.getSoundHandler().playSound(player, sound);
	}
	
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts
	 * @param players
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param bar
	 */
	public void sendBossbar(final List<Player> players, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final BossBar bar) {
		//Luodaan ajastettu bossbar
		TimedBossBar tbar = new TimedBossBar(players, bar, stay, animated);
		//Sammutetaan ajastettu bossbar
		KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
		
		KaranteeniCore.getSoundHandler().playSound(players, sound);
	}
	
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts
	 * @param player
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param bar
	 * @param texts
	 */
	public void sendBossbar(final Player player, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final BossBar bar, 
			List<String> texts) {
		//Luodaan ajastettu bossbar
		TimedBossBar tbar = new TimedBossBar(player, bar, stay, texts, animated);
		//Sammutetaan ajastettu bossbar
		KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
		
		KaranteeniCore.getSoundHandler().playSound(player, sound);
	}
	
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts 
	 * @param players
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param bar
	 * @param texts
	 */
	public void sendBossbar(final List<Player> players, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final BossBar bar, 
			List<String> texts) {
		//Luodaan ajastettu bossbar
		TimedBossBar tbar = new TimedBossBar(players, bar, stay, texts, animated);
		//Sammutetaan ajastettu bossbar
		KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
		
		KaranteeniCore.getSoundHandler().playSound(players, sound);
	}
	
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts
	 * @param player
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param text
	 */
	public void sendBossbar(final CommandSender sender, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final String text) {
		
		if(sender instanceof Player) {
			BossBar bar = Bukkit.createBossBar(text, BarColor.YELLOW, BarStyle.SOLID);
			//Luodaan ajastettu bossbar
			TimedBossBar tbar = new TimedBossBar((Player) sender, bar, stay, animated);
			//Sammutetaan ajastettu bossbar
			KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
			
			KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
		} else {
			sendMessage(sender, sound, text);
		}
	}
	
	/**
	 * Sends a bossbar to player with sound, staytime on screen, update frequence, fillanimation an texts
	 * @param player
	 * @param sound
	 * @param stay
	 * @param updateFreq
	 * @param animated
	 * @param bar
	 */
	public void sendBossbar(final CommandSender sender, 
			final SoundType sound, 
			final float stay, 
			int updateFreq, 
			boolean animated, 
			final BossBar bar) {
		if(sender instanceof Player) {
			// create timed bossbar
			TimedBossBar tbar = new TimedBossBar((Player)sender, bar, stay, animated);
			// shut down timed bossbar
			KaranteeniCore.getTimerHandler().registerTimer(tbar, updateFreq);
			
			KaranteeniCore.getSoundHandler().playSound((Player)sender, sound);
		} else {
			sendMessage(sender, sound, bar.getTitle());
		}
	}
	
	
	/**
     * Send a title to a player
     * @param fadein fade in time
     * @param fadeout fade out time
     * @param stay time in which the title is solid
     * @param receiver receiver of title
     * @param title title text
     * @param subtitle subtitle text
     * @param sound sound with title
     */
    public void sendTitle(float fadein, float fadeout, float stay, Player receiver, String title, String subtitle, SoundType sound) {
    	title = TextUtil.formatJSON(title);
    	subtitle = TextUtil.formatJSON(subtitle);
    	
    	CraftPlayer player = (CraftPlayer) receiver;
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"§r" + title + "\"}"));
        PacketPlayOutTitle packetPlayOutSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"§r" + subtitle + "\"}"));
        PacketPlayOutTitle packetLength = new PacketPlayOutTitle((int)(fadein*20), (int)(stay*20), (int)(fadeout*20));
        
        player.getHandle().playerConnection.sendPacket(packetLength);
        player.getHandle().playerConnection.sendPacket(packetPlayOutTitle);
        player.getHandle().playerConnection.sendPacket(packetPlayOutSubtitle);

		//Lähetä ääni
        KaranteeniCore.getSoundHandler().playSound(receiver, sound);
    }
    
    
	/**
     * Send a title to a player
     * @param fadein fade in time
     * @param fadeout fade out time
     * @param stay time in which the title is solid
     * @param receiver receiver of title
     * @param title title text
     * @param subtitle subtitle text
     * @param sound sound with title
     */
    public void sendTitle(float fadein, float fadeout, float stay, CommandSender receiver, String title, String subtitle, SoundType sound) {
    	if(receiver instanceof Player) {
	    	title = TextUtil.formatJSON(title);
	    	subtitle = TextUtil.formatJSON(subtitle);
	    	
	    	CraftPlayer player = (CraftPlayer) ((Player)receiver);
	        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"§r" + title + "\"}"));
	        PacketPlayOutTitle packetPlayOutSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"§r" + subtitle + "\"}"));
	        PacketPlayOutTitle packetLength = new PacketPlayOutTitle((int)(fadein*20), (int)(stay*20), (int)(fadeout*20));
	        
	        player.getHandle().playerConnection.sendPacket(packetLength);
	        player.getHandle().playerConnection.sendPacket(packetPlayOutTitle);
	        player.getHandle().playerConnection.sendPacket(packetPlayOutSubtitle);
	
			// send sounds
	        KaranteeniCore.getSoundHandler().playSound((Player)receiver, sound);
    	} else {
    		if(title != null && !title.isEmpty())
    			sendMessage(receiver, sound, title);
    		if(title != null && !title.isEmpty())
    			sendMessage(receiver, sound, subtitle);
    	}
    }
    
    
    /**
     * Sends an actionbar to a player
     * @param receiver Actionbar receiver
     * @param text actionbar text
     * @param sound sound played with actionbar
     */
    public void sendActionBar(final Player receiver, final SoundType sound, String text) {
    	text = TextUtil.formatJSON(text);
    	
    	//Lähetä actionbar
    	CraftPlayer player = (CraftPlayer) receiver;
        IChatBaseComponent chatBaseComponent = ChatSerializer.a("{\"text\": \"§r" + text + "\"}");
        PacketPlayOutTitle packetPlayOutChat = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR ,chatBaseComponent);
        player.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    	
		//Lähetä ääni
        KaranteeniCore.getSoundHandler().playSound(receiver, sound);
    }
    
    
    /**
     * Sends an actionbar to a CommandSender
     * @param receiver Actionbar receiver such as console or player
     * @param text actionbar text
     * @param sound sound played with actionbar
     */
    public void sendActionBar(final CommandSender receiver, final SoundType sound, String text) {
    	if(receiver instanceof Player) {
    		text = TextUtil.formatJSON(text);
        	
        	//send actionbar
        	CraftPlayer player = (CraftPlayer) receiver;
            IChatBaseComponent chatBaseComponent = ChatSerializer.a("{\"text\": \"§r" + text + "\"}");
            PacketPlayOutTitle packetPlayOutChat = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR ,chatBaseComponent);
            player.getHandle().playerConnection.sendPacket(packetPlayOutChat);
        	
    		//send sound
            KaranteeniCore.getSoundHandler().playSound((Player) receiver, sound);
    	} else {
    		// player was console etc. send as a message
    		receiver.sendMessage(text);
    	}
    }
    
    
    /**
     * Sends a clickable list to player
     * 
     * @param receiver receiver of list
     * @param command command executed on click
     * @param parameters parameters for command
     * @param commandPrefix prefix to be put before command
     */
    public static void sendList(CommandSender receiver, String command, String[] parameters, String commandPrefix, String commandSuffix) {
    	String txt = "";
    	//Onko komento mukana
    	if(!(command == null || command.equals(""))) {    		
	    	for(int i = 0; i < parameters.length; ++i) {
				if(parameters.length != 1) {
					if(i == parameters.length - 1) {
						if(i%2==0)
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/"+command+" " + commandPrefix + " " + parameters[i] + " " + commandSuffix + "\"}}";
						else
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/"+command+" " + commandPrefix + " " + parameters[i] + " " + commandSuffix + "\"}}";
					} else {
						if(i%2==0)
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/"+command+" " + commandPrefix + " " + parameters[i] + " " + commandSuffix + "\"}},{\"text\":\", \",\"color\":\"grey\"},";
						else
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/"+command+" " + commandPrefix + " " + parameters[i] + " " + commandSuffix + "\"}},{\"text\":\", \",\"color\":\"grey\"},";
					}
				} else {
					txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/"+command+" " + commandPrefix + " " + parameters[i] + " " + commandSuffix + "\"}}";
				}
			}
    	} else { // no command
    		for(int i = 0; i < parameters.length; ++i) {
				if(parameters.length != 1) {
					if(i == parameters.length - 1) {
						if(i%2==0)
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"yellow\"}";
						else
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"green\"}";
					} else {
						if(i%2==0)
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"yellow\"},{\"text\":\", \",\"color\":\"grey\"},";
						else
							txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"green\"},{\"text\":\", \",\"color\":\"grey\"},";
					}
				} else {
					txt = txt + "{\"text\":\""+ parameters[i] +"\",\"color\":\"yellow\"}";
				}
			}
    	}
		
		txt = "tellraw "+receiver.getName()+" [\"\"," + txt + "]";
		
		//Sends the title command
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), txt);
    }
}
