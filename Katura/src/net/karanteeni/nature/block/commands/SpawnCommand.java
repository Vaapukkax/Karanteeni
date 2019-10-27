package net.karanteeni.nature.block.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

public class SpawnCommand extends BareCommand implements TranslationContainer {

	public SpawnCommand() {
		super(Katura.getPlugin(Katura.class), 
				"spawner", 
				"/spawner <entity>", 
				"Sets the spawner entity type in hand", 
				Katura.getDefaultMsgs().defaultNoPermission(), Arrays.asList());
		registerTranslations();
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(arg3.length != 1) return null;
		List<String> values = new ArrayList<String>();
		for(EntityType entity : EntityType.values())
			if(entity.isSpawnable() && sender.hasPermission("katura.setspawner."+entity.name().toLowerCase()))
				values.add(entity.name().toLowerCase());
		
		return filterByPrefix(values, arg3[0], false);
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		
		// verify that the player is holding a spawner block in hand
		if(player.getInventory().getItemInMainHand().getType() != Material.SPAWNER)
			return new CommandResult(Katura.getTranslator().getTranslation(plugin, sender, "please-hold-spawner"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		
		EntityType type = null;
		if(args.length != 1) return CommandResult.INVALID_ARGUMENTS;
		
		try {
			type = EntityType.valueOf(args[0].toUpperCase());
		} catch(Exception e) {
			return new CommandResult(Katura.getTranslator().getTranslation(plugin, sender, "invalid-entitytype"), 
					ResultType.NO_PERMISSION, 
					Sounds.NO.get());
		}
		
		// check does the player have the permission to modify the spawner in hand
		if(!sender.hasPermission("katura.setspawner."+type.name().toLowerCase())) 
			return new CommandResult(Katura.getTranslator().getTranslation(plugin, sender, "no-permission-for-entity"),
					ResultType.NO_PERMISSION, Sounds.NO.get());
		
		// change the items lore in hand to change the spawned type
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Arrays.asList("§7"+type.name()));
		item.setItemMeta(meta);
		player.getInventory().setItemInMainHand(item);
		player.updateInventory();
		
		Katura.getSoundHandler().playSound(player, Sounds.EQUIP.get());
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerTranslation(plugin, "invalid-entitytype", "Invalid entity type");
		Katura.getTranslator().registerTranslation(plugin, "no-permission-for-entity", "You don't have permission to this entity type");
		Katura.getTranslator().registerTranslation(plugin, "please-hold-spawner", "You have to hold a spawner to change its entity type");
	}
}
