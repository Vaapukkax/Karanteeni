package net.karanteeni.utilika.block.setsign;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.utilika.Utilika;

public class LineLoader extends CommandLoader implements TranslationContainer {

	public LineLoader(boolean before) {
		super(before);
	}

	
	@Override
	public void registerTranslations() {
		Utilika plugin = Utilika.getPlugin(Utilika.class);
		Utilika.getTranslator().registerTranslation(plugin, "setsign.invalid-line", "There's no line %line%. Please choose from 1-4");
	}
	

	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		return Arrays.asList("1","2","3","4");
	}
	
	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length < 1)
			return CommandResult.INVALID_ARGUMENTS;
		
		int line = 0;
		
		// parse the integer and check that the line number is correct
		try {
			line = Integer.parseInt(args[0]);
			if(line < 1 || line > 4)
				return new CommandResult( 
						Utilika.getTranslator().getTranslation(this.chainer.getPlugin(), 
						sender, 
						"setsign.invalid-line").replace("%line%", args[0]), ResultType.INVALID_ARGUMENTS);
		} catch(Exception e) {
			return new CommandResult( 
					Utilika.getTranslator().getTranslation(this.chainer.getPlugin(), 
					sender, 
					"setsign.invalid-line").replace("%line%", args[0]), ResultType.INVALID_ARGUMENTS);
		}
		
		this.chainer.setObject("line", line);
		
		return CommandResult.SUCCESS;
	}
}
