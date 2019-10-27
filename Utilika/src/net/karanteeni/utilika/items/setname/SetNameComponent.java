package net.karanteeni.utilika.items.setname;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.text.TextUtil;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.utilika.Utilika;

public class SetNameComponent extends CommandLoader implements TranslationContainer {

	public SetNameComponent(boolean before) {
		super(before);
	}


	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length < 1)
			return CommandResult.INVALID_ARGUMENTS;
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		// get the player who ran the command
		Player player = (Player)sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		
		// check if player is holding an item
		if(item == null || item.getType() == Material.AIR) {
			return new CommandResult(Utilika.getTranslator().getTranslation(
					Utilika.getPlugin(Utilika.class), 
					sender, 
					"setname.no-item"), ResultType.INVALID_ARGUMENTS);
		}
		
		// build the item name
		StringBuffer name = new StringBuffer(args[0]);
		for(int i = 1; i < args.length; ++i) {
			name.append(" ");
			name.append(args[i]);
		}
		String itemName = name.toString();
	
		// format the name
		if(sender.hasPermission("utilika.name.color"))
			itemName = TextUtil.formatColor(itemName);
		if(sender.hasPermission("utilika.name.random"))
			itemName = TextUtil.formatMagic(itemName);
		if(sender.hasPermission("utilika.name.format"))
			itemName = TextUtil.formatFormat(itemName);
		
		// save the data
		this.chainer.setObject("newName", itemName);
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Utilika plugin = Utilika.getPlugin(Utilika.class);
		Utilika.getTranslator().registerTranslation(plugin, "setname.set-name", "Set the name of %item% to %name%");
		Utilika.getTranslator().registerTranslation(plugin, "setname.change-name", "Changed the name from %item% to %name%");
	}
}
