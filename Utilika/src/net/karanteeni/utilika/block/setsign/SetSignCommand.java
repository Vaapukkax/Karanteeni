package net.karanteeni.utilika.block.setsign;

import java.util.List;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.utilika.Utilika;

public class SetSignCommand extends CommandChainer implements TranslationContainer {

	public SetSignCommand(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage, List<String> params) {
		super(plugin, command, usage, description, permissionMessage, params);
		registerTranslations();
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		if(args.length < 1)
			return CommandResult.INVALID_ARGUMENTS;
		
		// get the player who ran the command
		Player player = (Player)sender;
		int line = this.getObject("line");
		String text = this.getObject("newName");
		
		// get the target block and check whether it is a sign
		Block targetBlock = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
		if(!BlockType.SIGNS.contains(targetBlock.getType()) && !BlockType.WALL_SIGNS.contains(targetBlock.getType())) {
			return new CommandResult(Utilika.getTranslator().getTranslation(
					Utilika.getPlugin(Utilika.class), 
					sender, 
					"setsign.no-sign"), ResultType.INVALID_ARGUMENTS);
		}
		
		Sign sign = (Sign)targetBlock.getState();
		sign.setLine(line-1, text);
		sign.update();
		
		// send the success message
		Utilika.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
				Utilika.getTranslator().getTranslation(
						Utilika.getPlugin(Utilika.class), 
						sender, 
						"setsign.set-line")
				.replace("%line%", line+"")
				.replace("%text%", text));
		
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void registerTranslations() {
		Utilika plugin = Utilika.getPlugin(Utilika.class);
		Utilika.getTranslator().registerTranslation(plugin, "setsign.set-line", "Set the line %line% to %text%");
		Utilika.getTranslator().registerTranslation(plugin, "setsign.no-sign", "You're not looking at a sign");
	}
}
