package net.karanteeni.core.command.defaultcomponent;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;

public class IntegerLoader extends CommandLoader implements TranslationContainer {
	private final String key;
	private final int min;
	private final int max;
	private final boolean mandatory;
	
	public IntegerLoader(String key, boolean before, int min, int max) {
		super(before);
		this.key = key;
		this.min = min;
		this.max = max;
		mandatory = true;
	}
	
	
	public IntegerLoader(String key, boolean before, int min, int max, boolean mandatory) {
		super(before);
		this.key = key;
		this.min = min;
		this.max = max;
		this.mandatory = true;
	}

	
	/**
	 * Returns the key used to store the value to the chainer
	 * @return key with which the parsed value was stored to the chainer
	 */
	public String getKey() {
		return key;
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
		
		int integer;
		try {
			integer = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			return new CommandResult( 
					KaranteeniCore.getTranslator().getTranslation(
							KaranteeniCore.getPlugin(KaranteeniCore.class), 
							sender, 
							"command.component-error.integer-invalid")
					.replace("%integer%", args[0]), 
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		if(integer < min || integer > max) {
			return new CommandResult(
					KaranteeniCore.getTranslator().getTranslation(
							KaranteeniCore.getPlugin(KaranteeniCore.class), 
							sender, 
							"command.component-error.integer-not-in-range")
					.replace("%integer%", args[0])
					.replace("%min%", Integer.toString(min))
					.replace("%max%", Integer.toString(max)), 
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		this.chainer.setObject(key, integer);
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		return Arrays.asList(Integer.toString(min), Integer.toString(max));
	}
	
	
	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.integer-invalid", 
				"%integer% is not an integer");
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.integer-not-in-range", 
				"%integer% out of range [%min% - %max%]");
	}
}
