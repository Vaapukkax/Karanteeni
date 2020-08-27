package net.karanteeni.christmas2019;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.text.Prefix;

public class DeleteNappi extends BareCommand {

	public DeleteNappi(KaranteeniPlugin plugin) {
		super(plugin, 
				"deletenappi", 
				"deletenappi <num>", 
				"deletes a nabi :3", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"));
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
		
		int nappi = -1;
		
		try { 
			nappi = Integer.parseInt(args[0]);
			if(nappi < 1 || nappi > 24)
				return CommandResult.INVALID_ARGUMENTS;
		} catch(Exception e) {
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		
		ChristmasButton btn = ((Christmas)plugin).buttons.get(nappi);
		if(btn == null) {
			sender.sendMessage(Prefix.NEGATIVE + "Nappia " + nappi + " ei ole olemassa");
			return CommandResult.SUCCESS;
		}
		
		QueryState btnRemoval = btn.destroy();
		
		if(btnRemoval != QueryState.REMOVAL_SUCCESSFUL) {
			sender.sendMessage(Prefix.NEGATIVE + "Virhe poistettaessa joulunappia");
			return CommandResult.SUCCESS;
		}
		
		
		Christmas.getPlugin(Christmas.class).buttons.remove(btn.getDay());
		sender.sendMessage(Prefix.POSITIVE + "Joulunappi " + btn.getDay() + " poistettu onnistuneesti!");
		
		return CommandResult.SUCCESS;
	}
}
