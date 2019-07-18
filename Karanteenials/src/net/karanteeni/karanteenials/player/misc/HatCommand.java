package net.karanteeni.karanteenials.player.misc;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class HatCommand extends CommandChainer implements TranslationContainer {

	public HatCommand() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"hat", 
				"/hat", 
				"Sets the current item as hat", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerRandomTranslation(plugin, "hat.set.empty", "Unfortunately you can't put your hand on your head in minecraft");
		Karanteenials.getTranslator().registerRandomTranslation(plugin, "hat.unset", "You took your hat off");
		Karanteenials.getTranslator().registerRandomTranslation(plugin, "hat.set.common", "You are now stylish");
		Karanteenials.getTranslator().registerTranslation(plugin, "hat.set."+Material.OAK_LOG.name(), "You wood");
	}

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		// get the item in players hand
		Player player = (Player)sender;
		// get the item in players main hand
		ItemStack held = player.getInventory().getItemInMainHand();
		ItemStack helmet = player.getInventory().getHelmet();
		
		// if held item and helmet are empty don't do anything
		if((held == null || held.getType() == Material.AIR) && (helmet == null || helmet.getType() == Material.AIR)) {
			return new CommandResult(
					Karanteenials.getTranslator().getRandomTranslation(plugin, sender, "hat.set.empty"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		boolean setNew = false;
		boolean hatTaken = false;
		
		// take hat off
		if(helmet != null && helmet.getType() != Material.AIR) {
			// set item in helmet slot to null (remove)
			hatTaken = true;
			player.getInventory().setHelmet(null);
			player.getInventory().setItemInMainHand(helmet);
		}
		
		// set new hat
		if(held != null && held.getType() != Material.AIR) {
			setNew = true; // hat has been set, not only taken off
			if(!hatTaken) // if hat has not been taken, don't clear hand
				player.getInventory().setItemInMainHand(null);
			player.getInventory().setHelmet(held);
		}
		
		// player set new hat
		if(setNew) {
			String message;
			
			if(Karanteenials.getTranslator().hasTranslation(plugin, sender, "hat.set."  + held.getType().name())) {
				// if custom message has been set to this item, use it
				message = Karanteenials.getTranslator().getTranslation(plugin, sender, "hat.set."+ held.getType().name());
			} else {
				// use regular message
				message = Karanteenials.getTranslator().getRandomTranslation(plugin, sender, "hat.set.common");
			}
			
			// send the message to the player
			Karanteenials.getMessager().sendActionBar(
					sender, 
					new SoundType(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1.3f),
					message);
		} else { // player only removed old hat
			// send the message to the player
			Karanteenials.getMessager().sendActionBar(
					sender, 
					new SoundType(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 0.5f),
					Karanteenials.getTranslator().getRandomTranslation(plugin, sender, "hat.unset"));
		}
		
		return CommandResult.SUCCESS;
	}

}
