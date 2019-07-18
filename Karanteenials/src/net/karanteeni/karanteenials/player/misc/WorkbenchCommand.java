package net.karanteeni.karanteenials.player.misc;

import java.util.Arrays;
import java.util.List;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.karanteenials.Karanteenials;

public class WorkbenchCommand extends BareCommand {

	public WorkbenchCommand() {
		super(
				Karanteenials.getPlugin(Karanteenials.class), 
				"workbench", 
				"/workbench", 
				"Opens the workbench to a player", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		player.openWorkbench(null, true);
		Karanteenials.getSoundHandler().playSound(player, Sounds.EQUIP.get(), SoundCategory.BLOCKS);
		
		return CommandResult.SUCCESS;
	}

}
