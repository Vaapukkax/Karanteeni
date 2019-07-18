package net.karanteeni.foxet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.karanteeni.core.command.AbstractCommand;
import net.md_5.bungee.api.ChatColor;

public class TestCommand extends AbstractCommand implements PluginMessageListener {

	public TestCommand() {
		super(Foxet.getPlugin(Foxet.class), "teast", "aaa", "aaa", "aaa", Arrays.asList());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		String msg = "";
		for(String arg : args)
			msg += arg + " ";
		Bukkit.broadcastMessage(msg);
		send2((Player)sender, msg);
		//Bukkit.broadcastMessage(msg);
		return true;
	}

	
	public static void send(Player p, String channel, String command){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(channel);
            out.writeUTF(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(Foxet.getPlugin(Foxet.class), "BungeeCord", b.toByteArray());
	}
	
	// send data to bungeecord PLAYER
	public static void send2(Player p, String command){
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("get");
        output.writeUTF(ChatColor.LIGHT_PURPLE + command);
        p.sendPluginMessage(Foxet.getPlugin(Foxet.class), "BungeeCord", output.toByteArray());
	}
	
	// message received from bungeecord
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			 String subChannel = in.readUTF();
			 if(subChannel.equals("get")) {
				 String toSend = in.readUTF();
				 for(Player player : Bukkit.getOnlinePlayers()) {
					 player.sendMessage(toSend);
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
