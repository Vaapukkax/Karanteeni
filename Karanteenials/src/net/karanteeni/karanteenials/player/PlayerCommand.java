package net.karanteeni.karanteenials.player;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.karanteenials.Karanteenials;

public class PlayerCommand extends CommandChainer {

	public PlayerCommand() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"player", 
				"/player", 
				"heal, help, kill burn etc. players", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}

	@Override
	protected boolean runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		return false;
	}
}
