package net.karanteeni.tester;

import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateBlock implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		Player player = (Player)sender;
		Block target = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);
		
		if(target == null) {
			player.sendMessage("You're not looking at a block");
		} else {
			TestBlock tb = new TestBlock(target);
			tb.save();
			tb.register();
			player.sendMessage("Created a new actionblock!");
		}
		
		return true;
	}

}
