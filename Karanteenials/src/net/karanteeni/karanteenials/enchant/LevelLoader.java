package net.karanteeni.karanteenials.enchant;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class LevelLoader extends CommandLoader {

	public LevelLoader() {
		super(false);
	}

	
	@Override
	protected void onRegister() { }

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length < 1) return CommandResult.INVALID_ARGUMENTS;
		
		try {
			int level = Integer.parseInt(args[0]);
			
			if(level <= 0 || level > 5000)
				return CommandResult.INVALID_ARGUMENTS;
			
			// save the loaded enchantment to memory
			this.chainer.setObject(EnchantCommand.LEVEL_KEY, level);
		} catch(Exception e) {
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length != 1) return null;
		
		return this.filterByPrefix(Arrays.asList("1", "2", "3"), args[0], false);
	}
}
