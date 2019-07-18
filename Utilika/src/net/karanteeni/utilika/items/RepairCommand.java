package net.karanteeni.utilika.items;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.utilika.Utilika;

public class RepairCommand extends BareCommand implements TranslationContainer {

	public RepairCommand() {
		super(Utilika.getPlugin(Utilika.class), 
				"repair", 
				"/repair", 
				"Repairs the item in hand", 
				Utilika.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	/**
	 * no autofill required
	 */
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		
		Player player = (Player)sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		// check if we have an item
		if(item == null)
			return new CommandResult(Utilika.getTranslator().getTranslation(plugin, sender, "cannot-repair"),
					ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		ItemMeta meta = item.getItemMeta();
		if(!(meta instanceof Damageable))
			return new CommandResult(Utilika.getTranslator().getTranslation(plugin, sender, "cannot-repair"),
					ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		Damageable dMeta = (Damageable)meta;
		if(!dMeta.hasDamage())
			return new CommandResult(Utilika.getTranslator().getTranslation(plugin, sender, "cannot-repair"),
					ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		
		dMeta.setDamage(0);
		item.setItemMeta((ItemMeta)dMeta);
		Utilika.getSoundHandler().playSound(player, Sounds.EQUIP.get());
		player.updateInventory();
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Utilika.getTranslator().registerTranslation(plugin, "repaired", "You tool has been repaired");
		Utilika.getTranslator().registerTranslation(plugin, "cannot-repair", "You cannot repair this item");
	}
}
