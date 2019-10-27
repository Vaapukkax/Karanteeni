package net.karanteeni.utilika.block.setsign;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.text.TextUtil;

public class SetSignComponent extends CommandLoader {

	public SetSignComponent(boolean before) {
		super(before);
	}


	@Override
	protected void onRegister() {
		
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length < 1)
			args = new String[] {""};
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		// build the item name
		StringBuffer name = new StringBuffer(args[0]);
		for(int i = 1; i < args.length; ++i) {
			name.append(" ");
			name.append(args[i]);
		}
		String itemName = name.toString();
	
		// format the name
		if(sender.hasPermission("utilika.setsign.color"))
			itemName = TextUtil.formatColor(itemName);
		if(sender.hasPermission("utilika.setsign.random"))
			itemName = TextUtil.formatMagic(itemName);
		if(sender.hasPermission("utilika.setsign.format"))
			itemName = TextUtil.formatFormat(itemName);
		
		// save the data
		this.chainer.setObject("newName", itemName);
		
		return CommandResult.SUCCESS;
	}
}
