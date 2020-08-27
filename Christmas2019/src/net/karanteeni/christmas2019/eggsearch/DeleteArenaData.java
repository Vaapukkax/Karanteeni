package net.karanteeni.christmas2019.eggsearch;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.text.Prefix;

public class DeleteArenaData extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		Player player = (Player)sender;
		if(!player.isOp())
			return CommandResult.NO_PERMISSION;
		
		if(Christmas.getInstance().getGameState().destroyAllEggs()) {
			sender.sendMessage(Prefix.NEUTRAL + "Destroyed all eggs from the arena");
		} else {
			sender.sendMessage(Prefix.NEGATIVE + "Could not remove eggs. Is the game ongoing, finishing, or is someone editing the eggs?");
		}
		
		return CommandResult.SUCCESS;
	}

}
