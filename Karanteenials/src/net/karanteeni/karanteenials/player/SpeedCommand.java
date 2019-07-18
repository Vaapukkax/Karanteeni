package net.karanteeni.karanteenials.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class SpeedCommand extends AbstractCommand implements TranslationContainer {

	public SpeedCommand(KaranteeniPlugin plugin) {
		super(plugin, "speed", "/speed <walk/fly> [<player>] <0-10>", "Sets the speed of player",
				Karanteenials.getDefaultMsgs().defaultNoPermission(), Arrays.asList("walk", "fly"));
		registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length == 2) // Player is changing its own speed
		{
			if (!(sender instanceof Player)) {
				Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
						Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().defaultNotForConsole());
				return true;
			}

			String param = getRealParam(args[0]);
			if (param == null) {
				printInvalidArguments(sender);
				return true;
			}
			if (param.equals("walk")) // Changing self walking speed
			{
				double speed = 1.0;
				try {
					speed = Double.parseDouble(args[1]);
				} catch (Exception e) {
					printInvalidArguments(sender);
					return true;
				}
				changeWalkingSpeed((Player) sender, speed);
			} else if (param.equals("fly")) // Changing self flying speed
			{
				double speed = 1.0;
				try {
					speed = Double.parseDouble(args[1]);
				} catch (Exception e) {
					printInvalidArguments(sender);
					return true;
				}
				changeFlyingSpeed((Player) sender, speed);
			}
		} else if (args.length == 3) // Player is changing someone elses speed
		{
			String param = getRealParam(args[0]);
			if (param == null) {
				printInvalidArguments(sender);
				return true;
			}
			if (param.equals("walk")) // Changing self walking speed
			{
				double speed = 1.0;
				try {
					speed = Double.parseDouble(args[2]);
				} catch (Exception e) {
					printInvalidArguments(sender);
					return true;
				}

				Player player = Bukkit.getPlayer(args[1]);
				if (player == null) {
					Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
							Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().playerNotFound(sender, args[1]));
					return true;
				}
				changeWalkingSpeed(sender, player, speed);
			} else if (param.equals("fly")) // Changing self flying speed
			{
				double speed = 1.0;
				try {
					speed = Double.parseDouble(args[2]);
				} catch (Exception e) {
					printInvalidArguments(sender);
					return true;
				}

				Player player = Bukkit.getPlayer(args[1]);
				if (player == null) {
					Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
							Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().playerNotFound(sender, args[1]));
					return true;
				}
				changeFlyingSpeed(sender, player, speed);
			}
		} else {
			printInvalidArguments(sender);
		}

		return true;
	}

	private void changeWalkingSpeed(Player player, double speed) {
		if (!player.hasPermission("karanteenials.player.speed.self.walk")) {
			printNoPermissionError(player);
			return;
		}

		if (!isCorrectSpeed(speed)) {
			printOutofRangeError(player);
			return;
		}

		player.setWalkSpeed(((float) speed) / 5);
		Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(),
				Karanteenials.getTranslator().getTranslation(plugin, player, "settings.speed.set.walk")
						.replace("%speed%", speed + ""));
	}

	private void changeFlyingSpeed(Player player, double speed) {
		if (!player.hasPermission("karanteenials.player.speed.self.fly")) {
			printNoPermissionError(player);
			return;
		}
		if (!isCorrectSpeed(speed)) {
			printOutofRangeError(player);
			return;
		}

		player.setFlySpeed(((float) speed) / 5);
		Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(),
				Karanteenials.getTranslator().getTranslation(plugin, player, "settings.speed.set.fly")
						.replace("%speed%", speed + ""));
	}

	private void changeWalkingSpeed(CommandSender sender, Player player, double speed) {
		if (!player.hasPermission("karanteenials.player.speed.other.walk")) {
			printNoPermissionError(player);
			return;
		}
		if (!isCorrectSpeed(speed)) {
			printOutofRangeError(player);
			return;
		}

		player.setWalkSpeed(((float) speed) / 5);
		Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(),
				Karanteenials.getTranslator().getTranslation(plugin, player, "settings.speed.set.walk")
						.replace("%speed%", speed + ""));
		Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(),
				Prefix.NEUTRAL
						+ Karanteenials.getTranslator().getTranslation(plugin, sender, "settings.speed.set.walk-other")
								.replace("%speed%", speed + "").replace("%player%", player.getName()));
	}

	private void changeFlyingSpeed(CommandSender sender, Player player, double speed) {
		if (!player.hasPermission("karanteenials.player.speed.other.fly")) {
			printNoPermissionError(player);
			return;
		}
		if (!isCorrectSpeed(speed)) {
			printOutofRangeError(player);
			return;
		}

		player.setFlySpeed(((float) speed) / 5);
		Karanteenials.getMessager().sendActionBar(player, Sounds.SETTINGS.get(),
				Karanteenials.getTranslator().getTranslation(plugin, player, "settings.speed.set.fly")
						.replace("%speed%", speed + ""));
		Karanteenials.getMessager().sendMessage(sender, Sounds.SETTINGS.get(),
				Prefix.NEUTRAL
						+ Karanteenials.getTranslator().getTranslation(plugin, sender, "settings.speed.set.fly-other")
								.replace("%speed%", speed + "").replace("%player%", player.getName()));
	}

	/**
	 * Checks if the given speed is in correct range
	 * 
	 * @param speed
	 * @return
	 */
	private boolean isCorrectSpeed(double speed) {
		if (speed < -5)
			return false;
		else if (speed > 5)
			return false;
		return true;
	}

	/**
	 * Prints the incorrect speed message to given commandsender
	 * 
	 * @param sender
	 */
	private void printOutofRangeError(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE
				+ Karanteenials.getTranslator().getTranslation(plugin, sender, "settings.speed.incorrect-speed"));
	}

	/**
	 * Prints the invalid arguments message to sender
	 * 
	 * @param sender
	 */
	private void printInvalidArguments(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
				Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().incorrectParameters(sender));
	}

	/**
	 * Prints the no permission error to given commandsender
	 * 
	 * @param sender
	 */
	private void printNoPermissionError(CommandSender sender) {
		Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(),
				Prefix.NEGATIVE + Karanteenials.getDefaultMsgs().noPermission(sender));
	}

	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "settings.speed.incorrect-speed",
				"The speed entered was incorrect, the range is §e(-5)-5");

		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "settings.speed.set.walk",
				"Your walking speed has been set to %speed%");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "settings.speed.set.fly",
				"Your flying speed has been set to %speed%");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "settings.speed.set.walk-other",
				"You set the walking speed of %player% to %speed%");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, "settings.speed.set.fly-other",
				"You set the flying speed of %player% to %speed%");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length == 1) {
			List<String> params = new ArrayList<String>();
			if (sender.hasPermission("karanteenials.player.speed.walk"))
				params.add("walk");
			if (sender.hasPermission("karanteenials.player.speed.fly"))
				params.add("fly");
			if (params.isEmpty())
				return null;
			return filterByPrefix(params, args[0]);
		} else if (args.length == 2) {
			List<String> params = new ArrayList<String>();
			if (sender.hasPermission("settings.speed.set.fly-other")
					|| sender.hasPermission("settings.speed.set.fly-other"))
				params.addAll(getPlayerNames(args[1]));
			params.addAll(filterByPrefix(Arrays.asList("0", "1", "2", "3", "4", "5"), args[1]));
			return params;
		} else if (args.length == 3 && (sender.hasPermission("settings.speed.set.fly-other")
				|| sender.hasPermission("settings.speed.set.fly-other"))) {
			return filterByPrefix(Arrays.asList("0", "1", "2", "3", "4", "5"), args[1]);
		}
		return null;
	}
}
