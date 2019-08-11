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

public class GiveEnchantmentComponent extends CommandComponent implements TranslationContainer {
	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		
		// check if item in hand is enchantable
		if(player.getInventory().getItemInMainHand() == null || 
				player.getInventory().getItemInMainHand().getType() == Material.AIR) {
			return new CommandResult(
					Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "enchantment.give-fail"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		Enchantment ench = this.chainer.getObject(EnchantCommand.ENCHANT_KEY);
		int enchLevel = 1;
		if(this.chainer.hasData(EnchantCommand.LEVEL_KEY))
			enchLevel = this.chainer.getObject(EnchantCommand.LEVEL_KEY);
		
		ItemStack item = player.getInventory().getItemInMainHand();
		item.addUnsafeEnchantment(ench, enchLevel);
		
		Karanteenials.getMessager().sendActionBar(sender, Sounds.EQUIP.get(), 
				Karanteenials.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "enchantment.give")
				.replace("%enchantment%", ench.getKey().getKey()));
		
		player.updateInventory();
		
		return CommandResult.SUCCESS;
	}

	
	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"enchantment.give", 
				"You enchanted the item with %enchantment%");
		Karanteenials.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"enchantment.give-fail", 
				"You cannot enchant your hand");
	}

}
