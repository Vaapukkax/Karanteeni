package net.karanteeni.karanteenials.enchant;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class EnchantmentLoader extends CommandLoader implements TranslationContainer {

	public EnchantmentLoader() {
		super(true);
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length < 1) return CommandResult.INVALID_ARGUMENTS;
		
		Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
		
		// have we found the enchant?
		if(ench == null) {
			return new CommandResult(
					Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "enchantment.does-not-exist")
					.replace("%enchantment%", args[0].toLowerCase()),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		// save the loaded enchantment to memory
		this.chainer.setObject(EnchantCommand.ENCHANT_KEY, ench);
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"enchantment.does-not-exist", 
				"The enchantment %enchantment% does not exist");
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length != 1) return null;
		
		List<String> enchantments = new ArrayList<String>();
		for(Enchantment ench : Enchantment.values())
			enchantments.add(ench.getKey().getKey().toLowerCase());
		return this.filterByPrefix(enchantments, args[0], false);
	}
}
