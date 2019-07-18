package net.karanteeni.nature.commands.time;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

/**
 * Loads the tick count from given parameter in command
 * @author Nuubles
 *
 */
public class TickParser extends CommandLoader implements TranslationContainer {

	public TickParser(boolean before) {
		super(before);
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		DateTime time = DateTime.parseTime(args[0]);
		// if no time could be parsed, it is invalid argument
		if(time == null)
			return new CommandResult(Katura.getTranslator().getTranslation(
						this.chainer.getPlugin(), 
						sender, 
						"time.parse-error"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		
		this.chainer.setObject("ticks", time.getTicks());
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		// get the keys for datetime
		List<String> fill = DateTime.getKeys();
		
		// add times between 1000 ticks
		for(int i = 2000; i <= 24000; i += 2000)
			fill.add(Integer.toString(i));
		
		return this.filterByPrefix(fill, args[0], false);
	}
	
	
	/*@Override
	public void invalidArguments(CommandSender sender) {
		Katura.getMessager().sendMessage(
				sender, 
				Sounds.NO.get(), 
				Prefix.NEGATIVE +
				Katura.getTranslator().getTranslation(
						this.chainer.getPlugin(), 
						sender, 
						"time.parse-error"));
	}*/


	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"time.parse-error", 
				"Input could not be parsed as ticks");
	}
}
