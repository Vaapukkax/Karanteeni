package net.karanteeni.simplevote;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class VoteCMD extends Command {

	public VoteCMD() {
		super("vote"); 
	}

	
	@Override
	public void execute(CommandSender sender, String[] arg1) {
		ComponentBuilder builder1 = new ComponentBuilder("§a>§2>§a> ");
		ComponentBuilder builder2 = new ComponentBuilder("§a>§2>§a> ");
		TextComponent l1 = new TextComponent(TextComponent.fromLegacyText("https://minecraft-mp.com/server/194896/vote/"));
		TextComponent l2 = new TextComponent(TextComponent.fromLegacyText("https://topg.org/Minecraft/in-515100"));
		l1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft-mp.com/server/194896/vote/"));
		l1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://topg.org/Minecraft/in-515100"));
		builder1.append(l1);
		builder2.append(l2);
		
		sender.sendMessage(builder1.create());
		sender.sendMessage(builder2.create());
	}
}
