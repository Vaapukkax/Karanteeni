package net.karanteeni.tester.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.tester.TesterMain;

public class ChatEvent implements Listener{
	KaranteeniPlugin plugin = TesterMain.getPlugin(TesterMain.class);
	
	public ChatEvent()
	{
		TesterMain.getTranslator().registerTranslation(plugin, "chat-message", "Chat message has been sent!");
		TesterMain.getTranslator().registerTranslation(plugin, "actionbar-message", "Actionbar has been sent!");
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		event.setCancelled(true);
		
		String msg = event.getPlayer().getDisplayName() + " > " + event.getMessage() + " ";
		
		List<Player> players = new ArrayList<Player>();
		for(Player player : Bukkit.getOnlinePlayers())
			players.add(player);
		
		/* LÄHETÄ CHATVIESTI */
		TesterMain.getMessager().sendMessage(players, Sounds.PLING_HIGH.get(), TesterMain.getTranslator().getTranslation(plugin, event.getPlayer(), "chat-message"));
		
		/* LÄHETÄ TITLE + ACTIONBAR */
		for(Player player : players)
		{
			TesterMain.getMessager().sendTitle(0.1f, 0.1f, 1.2f, player, "§6"+msg, "§6"+msg, Sounds.NONE.get());
			TesterMain.getMessager().sendActionBar(player, Sounds.NONE.get(), "§6"+TesterMain.getTranslator().getTranslation(plugin, event.getPlayer(), "actionbar-message"));
		}
		
		/* LUO BOSSBAR */
		BossBar bar = Bukkit.createBossBar(msg, BarColor.YELLOW, BarStyle.SOLID);
		
		List<String> texts = new ArrayList<String>();
		texts.add(msg);
		
		for(int i = 0; i < msg.length(); i++)
		{
			char c = texts.get(i).charAt(0);
			String text = texts.get(i).substring(1);
			texts.add(text+c);
		}
		
		/* LÄHETÄ BOSSBAR */
		TesterMain.getMessager().sendBossbar(players, Sounds.NONE.get(), 5f, 3, true, bar, texts);
	}
}
