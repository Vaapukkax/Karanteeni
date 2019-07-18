package net.karanteeni.karanteenials.player.misc;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.karanteenials.Karanteenials;

public class EnderChestCommand extends BareCommand {

	public EnderChestCommand() {
		super(
				Karanteenials.getPlugin(Karanteenials.class), 
				"enderchest", 
				"/enderchest", 
				"Opens the enderchest to a player", 
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
		player.openInventory(player.getEnderChest());
		Karanteenials.getSoundHandler().playSound(player, new SoundType(Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f), SoundCategory.BLOCKS);
		
		return CommandResult.SUCCESS;
	}

}
