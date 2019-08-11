package net.karanteeni.chatar.command.broadcast;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;

public class BroadcastCommand extends BareCommand {
	private static String FORMAT;
	
	public BroadcastCommand() {
		super(Chatar.getPlugin(Chatar.class), 
				"broadcast", 
				"/broadcast <message>", 
				"Broadcast a message to the whole server", 
				Chatar.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		
		if(!plugin.getSettings().isSet("broadcast.format")) {
			plugin.getSettings().set("broadcast.format", "§f[§6Broadcast§f] %msg%");
			plugin.saveSettings();
		}
		
		FORMAT = plugin.getSettings().getString("broadcast.format");
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 0)
			return CommandResult.INVALID_ARGUMENTS;
		
		// create a message from the given arguments
		StringBuilder message = new StringBuilder();
		for(String word : args) {
			message.append(word);
			message.append(" ");
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF("ALL");
		
		out.writeUTF(FORMAT.replace("%msg%", message.substring(0, message.length()-1)));
		
		Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
		
		return CommandResult.SUCCESS;
	}
}
