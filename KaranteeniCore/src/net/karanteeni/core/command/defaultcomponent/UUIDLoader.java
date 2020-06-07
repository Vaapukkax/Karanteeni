package net.karanteeni.core.command.defaultcomponent;

import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

public class UUIDLoader extends CommandLoader implements TranslationContainer {
	public static final String UUID_KEY = "core.uuid";
	private final boolean mandatory;
	
	public UUIDLoader(boolean before) {
		super(before);
		mandatory = true;
	}

	
	public UUIDLoader(boolean before, boolean mandatory) {
		super(before);
		this.mandatory = mandatory;
	}
	

	@Override
	protected void onRegister() {
		registerTranslations();
	}


	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			if(mandatory)
				return CommandResult.INVALID_ARGUMENTS;
			else
				return CommandResult.SUCCESS;
		}
		
		UUID uuid = null;
		try {
			uuid = UUID.fromString(args[0]);
		} catch(IllegalArgumentException e) {
			return new CommandResult(
					Prefix.NEGATIVE + 
					KaranteeniCore.getTranslator().getTranslation(
							this.chainer.getPlugin(), 
							sender, 
							"command.component-error.uuid-invalid")
					.replace("%uuid%", args[0]), 
					ResultType.INVALID_ARGUMENTS);
		}
		
		this.chainer.setObject(UUID_KEY, uuid);
		
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.uuid-invalid", 
				"Given UUID \"%uuid%\" is invalid");
	}
}
