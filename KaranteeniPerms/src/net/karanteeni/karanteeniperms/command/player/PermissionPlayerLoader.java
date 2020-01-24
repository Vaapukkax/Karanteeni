package net.karanteeni.karanteeniperms.command.player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;

public class PermissionPlayerLoader extends CommandLoader {
	public static final String PERMISSION_PLAYER_KEY = "permissionplayer";
	
	public PermissionPlayerLoader(boolean before) {
		super(before);
	}
	

	@Override
	protected void onRegister() {
		
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1)
			return CommandResult.INVALID_ARGUMENTS;
		
		KaranteeniPerms plugin = (KaranteeniPerms)this.chainer.getPlugin();
		
		// get the UUID of the player searched
		UUID uuid = KaranteeniPerms.getPlayerHandler().getUUID(args[0]);
		if(uuid == null)
			return new CommandResult(Prefix.NEGATIVE + KaranteeniPerms.getDefaultMsgs().playerNotFound(sender, args[0]),
					ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		// get the permissionplayer searched
		PermissionPlayer player = plugin.getPermissionPlayer(uuid);
		
		if(player == null)
			return new CommandResult(Prefix.NEGATIVE + KaranteeniPerms.getDefaultMsgs().playerNotFound(sender, args[0]),
					ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		// store the permissionplayer to memory
		this.chainer.setObject(PERMISSION_PLAYER_KEY, player);
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1)
			return null;
		
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		LinkedList<String> playerNames = new LinkedList<String>();
		for(Player player : players)
			playerNames.add(player.getName());
		
		return this.filterByPrefix(playerNames, args[0], true);
	}
}
