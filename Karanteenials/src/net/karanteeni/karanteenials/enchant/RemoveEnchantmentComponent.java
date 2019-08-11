package net.karanteeni.karanteenials.enchant;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class RemoveEnchantmentComponent extends CommandComponent implements TranslationContainer {
	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		
		// get the enchantment from chainer and take up a refence to the item held
		Enchantment ench = this.chainer.getObject(EnchantCommand.ENCHANT_KEY);
		ItemStack item = player.getInventory().getItemInMainHand();
		
		// check if item in hand is enchantable
		if(player.getInventory().getItemInMainHand() == null || 
				player.getInventory().getItemInMainHand().getType() == Material.AIR ||
				!item.containsEnchantment(ench)) {
			return new CommandResult(
					Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "enchantment.remove-fail"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		// remove enchantment
		item.removeEnchantment(ench);
		
		// message result
		Karanteenials.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
				Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "enchantment.remove")
				.replace("%enchantment%", ench.getKey().getKey()));
		
		player.updateInventory();
		
		return CommandResult.SUCCESS;
	}

	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"enchantment.remove", 
				"You removed the enchantment %enchantment%");
		Karanteenials.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"enchantment.remove-fail", 
				"The item you're holding does not have this enchantment");
	}

}
