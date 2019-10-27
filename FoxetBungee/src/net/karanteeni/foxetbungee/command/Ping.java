package net.karanteeni.foxetbungee.command;

import net.karanteeni.foxetbungee.Foxet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Ping extends Command{

	public Ping()  {
		super("ping");
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if(args.length != 1) return;
		
		for(ProxiedPlayer player : Foxet.getInstance().getProxy().getPlayers()) {
			if(args[0].equalsIgnoreCase(player.getName())) {
				args[0] = "";
				StringBuilder builder = new StringBuilder();
				for(String arg : args)
					builder.append(arg).append(" ");
				
				String newString = builder.toString();
				
				player.disconnect(new TextComponent(ChatColor.RED + newString));
			}
		}
	}

}
