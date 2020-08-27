package net.karanteeni.christmas2019;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class CreateNappi extends BareCommand {

	public CreateNappi(KaranteeniPlugin plugin) {
		super(plugin, 
				"munapeli", 
				"createnappi <num>", 
				"creates a nabi :3", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return this.filterByPrefix(this.getParams(), arg3[0]);
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!sender.isOp())
			return CommandResult.NO_PERMISSION;
		if(args.length != 1)
			return CommandResult.INVALID_ARGUMENTS;
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		Player player = (Player)sender;
		Block block = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);
		
		if(!BlockType.BUTTON.contains(block.getType()))
			return new CommandResult("You can select only buttons", ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		int day = -1;
		
		try {
			day = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		if(day < 1 || day > 24)
			return CommandResult.INVALID_ARGUMENTS;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String dateStr = String.format("%s-12-2019 00:00:00", day);
		long timeMS = 0;
		
		try {
			Date date = sdf.parse(dateStr);
			timeMS = date.getTime();
			Bukkit.broadcastMessage(timeMS + " - " + System.currentTimeMillis());
		} catch(ParseException e) {
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		ChristmasButton btn = new ChristmasButton(block);
		btn.setPressTime(timeMS, day);
		QueryState result = btn.save();
		if(result == QueryState.INSERTION_FAIL_ALREADY_EXISTS) {
			sender.sendMessage(Prefix.NEGATIVE + "Nappi tällä päivällä on jo olemassa! Käytä /deletenappi poistaaksesi se");
			return CommandResult.SUCCESS;
		} else if(result != QueryState.INSERTION_SUCCESSFUL) {
			sender.sendMessage(Prefix.NEGATIVE + "Virhe tallennettaessa nappia");
			return CommandResult.SUCCESS;
		}

		if(!btn.register()) {
			sender.sendMessage(Prefix.NEGATIVE + "Luotiin uusi nappi, mutta sen aktivoinnissa tapahtui virhe");
			return CommandResult.ERROR;
		}
		
		sender.sendMessage(Prefix.POSITIVE + "Luotiin uusi nappi joulukuun päivälle " + day);
		
		return CommandResult.SUCCESS;
	}

}
