package net.karanteeni.core.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class TranslationKeyLoader extends CommandLoader {
	public static final String TRANSLATION_KEY = "core.translationkey";
	private boolean onlyRandom;
	
	public TranslationKeyLoader(boolean before, boolean onlyRandom) {
		super(before);
		this.onlyRandom = onlyRandom;
	}

	
	@Override
	protected void onRegister() {		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return CommandResult.INVALID_ARGUMENTS;
		if(!KaranteeniCore.getTranslator().getAllTranslationKeys().contains(args[0]))
			return CommandResult.INVALID_ARGUMENTS;
		this.chainer.setObject(TRANSLATION_KEY, args[0]);
		
		return CommandResult.SUCCESS;
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 1)
			return null;
		List<String> keys = null;
		if(!onlyRandom) {
			keys = KaranteeniCore.getTranslator().getAllTranslationKeys();
		} else {
			keys = KaranteeniCore.getTranslator().getRandomTranslationKeys();
		}
		if(args.length == 0)
			return keys;
		
		return this.filterByPrefix(keys, args[0], true);
	}
}
