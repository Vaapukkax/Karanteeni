package net.karanteeni.nature.sit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

public class SitCommand extends BareCommand implements TranslationContainer {
	protected static HashMap<UUID, ArmorStand> riding = new HashMap<UUID, ArmorStand>();
	
	public SitCommand() {
		super(Katura.getPlugin(Katura.class), 
				"sit",
				"/sit",
				"makes the player sit",
				Katura.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		// get the command sender player
		Player player = (Player)sender;
		
		// check if the player is sitting
		if(player.getVehicle() != null)
			return new CommandResult(
					Katura.getTranslator().getTranslation(plugin, player, "sit.already-sitting"), 
					ResultType.INVALID_ARGUMENTS);
		
		// check if the player is in air
		if(!player.isOnGround())
			return new CommandResult(
					Katura.getTranslator().getTranslation(plugin, player, "sit.on-air"), 
					ResultType.INVALID_ARGUMENTS);
		
		
		Location standLocation = player.getLocation().subtract(0, 1.65, 0);
		standLocation.setPitch(90);
		ArmorStand armorStand = (ArmorStand)player.getWorld().spawnEntity(standLocation, EntityType.ARMOR_STAND);
		armorStand.setGravity(false);
		armorStand.setBasePlate(false);
		armorStand.setVisible(false);
		armorStand.setInvulnerable(true);
		armorStand.setCollidable(false);
		armorStand.setCanPickupItems(false);
		
		// cache the player sit
		riding.put(player.getUniqueId(), armorStand);
		
		armorStand.addPassenger(player);
		Katura.getSoundHandler().playSound(player, Sounds.EQUIP.get());
		
		return CommandResult.SUCCESS;
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}


	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerRandomTranslation(plugin, "sit.sat", "You sat down");
		Katura.getTranslator().registerTranslation(plugin, "sit.already-sitting", "You are already sitting");
		Katura.getTranslator().registerTranslation(plugin, "sit.on-air", "You can't sit on air");
	}
}
