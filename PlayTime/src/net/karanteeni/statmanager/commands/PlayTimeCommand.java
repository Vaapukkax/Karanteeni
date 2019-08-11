package net.karanteeni.statmanager.commands;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.time.TimeData;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.statmanager.StatManager;
import net.karanteeni.statmanager.Time;

public class PlayTimeCommand extends CommandChainer implements TranslationContainer {

	public PlayTimeCommand() {
		super(StatManager.getPlugin(StatManager.class), 
				"playtime", 
				"/playtime <player> [set/add/remove <time>]", 
				"Show and modify players playtime", 
				StatManager.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length > 1)
			return CommandResult.SUCCESS;
		
		UUID uuid = null;
		if(this.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE))
			uuid = this.getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE);
		else if(this.hasData(PlayerLoader.PLAYER_KEY_SINGLE))
			uuid = this.<Player>getObject(PlayerLoader.PLAYER_KEY_SINGLE).getUniqueId();
		
		// checking our own play time
		if(uuid == null || ((sender instanceof Player) && ((Player)sender).getUniqueId().equals(uuid))) {
			if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
			if(!sender.hasPermission("statmanager.check-self")) return CommandResult.NO_PERMISSION;
			Time time = null;
			if(uuid != null) time = ((StatManager)plugin).getManager().getTime(uuid);
			else time = ((StatManager)plugin).getManager().getTime(((Player)sender).getUniqueId());
			
			StatManager.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
					StatManager.getTranslator().getTranslation(plugin, sender, "own-time").replace("%time%", formatTime(time.asTimeData())));
		} else {
			if(!sender.hasPermission("statmanager.check-other")) return CommandResult.NO_PERMISSION;
			Time time = ((StatManager)plugin).getManager().forceLoadTime(uuid);
			
			StatManager.getMessager().sendMessage(sender, Sounds.EQUIP.get(), 
					Prefix.NEUTRAL +
					StatManager.getTranslator().getTranslation(plugin, sender, "foreign-time")
					.replace("%time%", formatTime(time.asTimeData()))
					.replace("%player%", StatManager.getPlayerHandler().getName(uuid)));
		}
		
		return CommandResult.SUCCESS;
	}

	
	/**
	 * Formats the given TimeData to a displayable format
	 * @param time time to format
	 * @return formatted time
	 */
	public String formatTime(TimeData time) {
		String res = "";
		if(time.getDays() > 0)
			res += time.getDays() + "d ";
		if(time.getExtraHours() > 0)
			res += time.getExtraHours() + "h ";
		if(time.getExtraMinutes() > 0)
			res += time.getExtraMinutes() + "min ";
		if(time.getExtraSeconds() > 0)
			res += time.getExtraSeconds() + "s ";
		if(res.equals("")) return "none";
		return res.substring(0, res.length()-1);
	}
	

	@Override
	public void registerTranslations() {
		StatManager.getTranslator().registerTranslation(plugin, "own-time", "Your play time is %time%");
		StatManager.getTranslator().registerTranslation(plugin, "foreign-time", "Play time of %player% is %time%");
	}
}
