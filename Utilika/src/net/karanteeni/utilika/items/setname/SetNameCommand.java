package net.karanteeni.utilika.items.setname;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.utilika.Utilika;

public class SetNameCommand extends CommandChainer {

	public SetNameCommand(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage, List<String> params) {
		super(plugin, command, usage, description, permissionMessage, params);
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = this.filterByPrefix(this.defaultAutofill(sender, cmd, label, args), args[0], false);
		list.add("<name>");
		return list;
	}
	
	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
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
		
		// get the name for the item
		String name = this.getObject("newName");
		
		ItemMeta meta = item.getItemMeta();
		String previousName = meta.getDisplayName();
		boolean hasPreviousName = meta.hasDisplayName();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		player.getInventory().setItemInMainHand(item);
		
		// send the success message
		if(name == null || name.equals(""))
			if(!sender.hasPermission("utilika.name.reset"))
				return CommandResult.NO_PERMISSION;
			else
				Utilika.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
						Utilika.getTranslator().getTranslation(
								Utilika.getPlugin(Utilika.class), 
								sender, 
								"setname.reset").replace("%item%", item.getType().toString()));
		else if(hasPreviousName)
			Utilika.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
					Utilika.getTranslator().getTranslation(
							Utilika.getPlugin(Utilika.class), 
							sender, 
							"setname.set-name")
					.replace("%item%", previousName)
					.replace("%name%", meta.getDisplayName()));
		else
			Utilika.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
					Utilika.getTranslator().getTranslation(
							Utilika.getPlugin(Utilika.class), 
							sender, 
							"setname.set-name")
					.replace("%item%", item.getType().toString())
					.replace("%name%", meta.getDisplayName()));
		
		return CommandResult.SUCCESS;
	}
}
