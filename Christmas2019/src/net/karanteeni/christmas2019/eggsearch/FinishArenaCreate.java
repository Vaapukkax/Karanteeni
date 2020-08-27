package net.karanteeni.christmas2019.eggsearch;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.text.Prefix;

public class FinishArenaCreate extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		Player player = (Player)sender;
		Christmas plugin = (Christmas)this.chainer.getPlugin();
		
		if(!plugin.getGameState().finishEdit(player.getUniqueId())) {
			sender.sendMessage(Prefix.NEGATIVE + "Et ole areenan muokkaaja");
			return CommandResult.OTHER;
		}
		
		player.sendMessage(Prefix.NEGATIVE + "Lopetettiin areenan muokkaaminen");
		return CommandResult.SUCCESS;
	}
}
