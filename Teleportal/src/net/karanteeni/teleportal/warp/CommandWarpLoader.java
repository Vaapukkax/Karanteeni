package net.karanteeni.teleportal.warp;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class CommandWarpLoader extends CommandLoader implements TranslationContainer {
	
	public CommandWarpLoader(boolean before) {
		super(before);
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		// check if a warp is given
		if(args.length == 0) {
			return new CommandResult(
					Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), 
							sender, 
							"warp.does-not-exist")
							.replace("%warp%", ""),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get());
		}
		
		Warp warp = Warp.loadWarp(args[0], sender);
		
		// if no warp is found, give error message
		if(warp == null) {
			return new CommandResult(
				Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), 
						sender, 
						"warp.does-not-exist")
						.replace("%warp%", args[0]),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		// warp was found, set it to memory as loaded
		this.chainer.setObject("warp", warp);
		return CommandResult.SUCCESS;
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 1)
			return this.filterByPrefix(Warp.getWarpNames(), args[0], false);
		return null;
	}
	
	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"warp.does-not-exist", 
				"No warp with name '%warp%' exists");
	}
}
