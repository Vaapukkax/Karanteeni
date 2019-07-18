package net.karanteeni.nature.commands.weather;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.time.TimeData;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

public class Weather extends CommandChainer implements TranslationContainer {
	private static final String CLEAR 	= "clear";
	private static final String RAIN 	= "rain";
	private static final String STORM 	= "storm";
	
	public Weather() {
		super(Katura.getPlugin(Katura.class), 
				"weather", 
				"/weather", 
				"Show and change weather info", 
				Katura.getDefaultMsgs().defaultNoPermission(),
				Arrays.asList());
		registerTranslations();
		parameterLength = 1; // this component has 1 parameter more than default
	}
	
	
	@Override
	public void registerTranslations() {
		Katura.getTranslator().registerRandomTranslation(this.plugin, "weather.show.sun", 
				"The sun now shines");
		Katura.getTranslator().registerRandomTranslation(this.plugin, "weather.show.rain", 
				"It's starting to rain");
		Katura.getTranslator().registerRandomTranslation(this.plugin, "weather.show.storm", 
				"You can hear the lightnings strike in the distance");
	}

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length != 1 && args.length != 2)
			return CommandResult.INVALID_ARGUMENTS;
		
		World world = null;
		
		// get the sender world OR default world if sender is console
		if(sender instanceof Entity) { // get entity command sender
			world = ((Entity)sender).getLocation().getWorld();
		} else if(sender instanceof BlockCommandSender) { // get command block command sender
			world = ((BlockCommandSender)sender).getBlock().getWorld();
		} else {
			world = Bukkit.getWorlds().get(0); // get default world
		}
		
		// get the future weather duration
		int ticks = world.getWeatherDuration();
		if(this.isSet("core.time")) {
			TimeData time = this.getObject("core.time");
			if(!time.isInvalid())
				ticks = (int)Math.min(Integer.MAX_VALUE, time.asTicks());
		}
		
		// set weather to correct state
		switch (args[0].toLowerCase()) {
		case CLEAR:
			if(!sender.hasPermission("katura.weather.clear"))
				return CommandResult.NO_PERMISSION;
			world.setStorm(false);
			world.setWeatherDuration(ticks);
			Katura.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
					Katura.getTranslator().getRandomTranslation(plugin, sender, "weather.show.sun"));
			break;
		case RAIN:
			if(!sender.hasPermission("katura.weather.rain"))
				return CommandResult.NO_PERMISSION;
			world.setStorm(true);
			world.setThundering(false);
			world.setWeatherDuration(ticks);
			Katura.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
					Katura.getTranslator().getRandomTranslation(plugin, sender, "weather.show.rain"));
			break;
		case STORM:
			if(!sender.hasPermission("katura.weather.storm"))
				return CommandResult.NO_PERMISSION;
			world.setStorm(true);
			world.setThundering(true);
			world.setWeatherDuration(ticks);
			world.setThunderDuration(ticks);
			Katura.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
					Katura.getTranslator().getRandomTranslation(plugin, sender, "weather.show.storm"));
			break;
		default:
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		return this.filterByPrefix(Arrays.asList(CLEAR, RAIN, STORM), args[0], false);
	}
}
