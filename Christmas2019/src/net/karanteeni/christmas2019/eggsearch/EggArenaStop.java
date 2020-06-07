package net.karanteeni.christmas2019.eggsearch;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.text.Prefix;

public class EggArenaStop extends CommandComponent {

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
		
		if(!Christmas.getInstance().getGameState().isGameOngoing()) {
			player.sendMessage(Prefix.NEGATIVE + "Peli ei ole päällä");
			return CommandResult.SUCCESS;
		}
		
		if(!Christmas.getInstance().getGameState().finishGame()) {
			player.sendMessage(Prefix.NEGATIVE + "Epäonnistuttiin pelin luonnollisessa sammutuksessa. Peli pakkosammutetaan...");
			if(!Christmas.getInstance().getGameState().forceStop()) {
				player.sendMessage(Prefix.PLUSNEGATIVE + "Epäonnistuttiin pelin pakkosammutuksessa; odota joko sisäistä luonnollista sammumista muutama minuutti tai käynnistä serveri uudelleen");
			}
		} else {
			player.sendMessage(Prefix.NEUTRAL + "Sammutettiin peli");
		}
		
		return CommandResult.SUCCESS;
	}
}
