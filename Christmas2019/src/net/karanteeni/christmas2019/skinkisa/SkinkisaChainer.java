package net.karanteeni.christmas2019.skinkisa;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;

public class SkinkisaChainer extends CommandChainer {
	private SkinkisaState state;
	
	public SkinkisaChainer(KaranteeniPlugin plugin) {
		super(plugin, 
				"skinkisa", 
				"/skinkisa", 
				"skinkisoja varten tehty komento", 
				Christmas.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		state = new SkinkisaState();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.SUCCESS;
	}

	
	
}
