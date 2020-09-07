package net.karanteeni.core.commands;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.players.KPlayer;

public class CacheClearCommand extends CommandChainer implements TranslationContainer {
	private static final String PLAYER_CACHE_EMPTY = "player-cache-empty";
	private static final String CLEARED_CACHE = "player-cache-cleared";
	private static final String KICK_CLEARING_CACHE = "kick-clearing-cache";
	
	
	public CacheClearCommand(KaranteeniPlugin plugin) {
		super(plugin, "clearcache", "/clearcache <player>", "Forces the player cache to clear", KaranteeniCore.getDefaultMsgs().defaultNoPermission(), Arrays.asList());
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return CommandResult.INVALID_ARGUMENTS;
		
		if(!sender.hasPermission("karanteenicore.clear-player-cache"))
			return CommandResult.NO_PERMISSION;
		
		UUID uuid = null;
		if(this.hasData(PlayerLoader.PLAYER_KEY_SINGLE)) {
			Player player = this.getObject(PlayerLoader.PLAYER_KEY_SINGLE);
			uuid = player.getUniqueId();
		} else if(this.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE)) {
			uuid = this.getObject(PlayerLoader.PLAYER_KEY_SINGLE);
		}
		
		KPlayer kp = KPlayer.getKPlayer(uuid);
		
		if(kp == null)
			return new CommandResult(Prefix.NEGATIVE +
				KaranteeniCore.getTranslator().getTranslation(plugin, sender, PLAYER_CACHE_EMPTY).replace("%player%", args[0]),
				ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		if(kp.getPlayer() != null && kp.getPlayer().isOnline()) {
			kp.getPlayer().kickPlayer(KaranteeniCore.getTranslator().getTranslation(plugin, sender, KICK_CLEARING_CACHE));
		}
		
		kp.clearCache();
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.isOnline()) {
				KaranteeniCore.getMessager().sendMessage(sender,
						Sounds.SETTINGS.get(),
						Prefix.NEUTRAL + KaranteeniCore.getTranslator().getTranslation(plugin, sender, CLEARED_CACHE).replace("%player%", args[0]));
			}
		} else {
			KaranteeniCore.getMessager().sendMessage(sender,
					Sounds.SETTINGS.get(),
					Prefix.NEUTRAL + KaranteeniCore.getTranslator().getTranslation(plugin, sender, CLEARED_CACHE).replace("%player%", args[0]));
		}

		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(plugin, PLAYER_CACHE_EMPTY, "The cache of %player% is empty");
		KaranteeniCore.getTranslator().registerTranslation(plugin, CLEARED_CACHE, "Cleared the cache of %player%");
		KaranteeniCore.getTranslator().registerTranslation(plugin, KICK_CLEARING_CACHE, "Your cache is being cleared.\nThis will force your data to reload once you join the server");
	}
}
