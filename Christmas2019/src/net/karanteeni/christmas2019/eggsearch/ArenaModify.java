package net.karanteeni.christmas2019.eggsearch;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.text.Prefix;

public class ArenaModify extends CommandComponent {

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
		
		Christmas plugin = (Christmas)this.chainer.getPlugin();
		
		if(!plugin.getGameState().beginEdit(player.getUniqueId())) {
			sender.sendMessage(Prefix.NEGATIVE + "Peliä ollaan jo muokkaamassa, ainoastaan yksi pelaaja voi muokata kenttää kerrallaan");
		}
		
		player.sendMessage(Prefix.NEGATIVE + "Aloitettiin areenan muokkaaminen. Luodaksesi muna aseta " + Material.OBSIDIAN.name().toLowerCase() + " maahan, tai hajota olemassaoleva poistaaksesi muna. Munat tallennetaan automaattisesti. Lopeta kentän muokkaaminen komennolla /egg finish-modify");
		return CommandResult.SUCCESS;
	}
}
