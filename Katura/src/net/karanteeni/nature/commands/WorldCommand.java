package net.karanteeni.nature.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.nature.Katura;

public class WorldCommand extends AbstractCommand implements TranslationContainer{

	private static HashMap<String,List<String>> params = new HashMap<String,List<String>>();
	private static HashMap<String,BiConsumer<CommandSender, String[]>> consumerMap = 
			new HashMap<String,BiConsumer<CommandSender, String[]>>();
	
	private final static String SPAWN_LIMIT 			= "spawn-limit";
	private final static String AUTO_SAVE 				= "auto-save";
	private final static String SPAWN 					= "spawn";
	private final static String DIFFICULTY 				= "difficulty";
	private final static String KEEP_SPAWN_IN_MEMORY 	= "keep-spawn-in-memory";
	private final static String TICKS_PER_SPAWN 		= "ticks-per-spawn";
	private final static String PVP 					= "pvp";
	private final static String INFO 					= "info";
	
	private final static String PROPERTY				= "%property%";
	private final static String VALUE					= "%value%";
	private final static String WORLD					= "%world%";
	
	public WorldCommand() {
		super(Katura.getPlugin(Katura.class), 
				"world", 
				"world help", 
				"Used to edit the properties of a world", 
				Arrays.asList("set"));
		
		params.put(AUTO_SAVE, Arrays.asList("true","false"));
		consumerMap.put(AUTO_SAVE, this::setAutoSave);
		
		params.put(SPAWN, Arrays.asList(""));
		consumerMap.put(SPAWN, this::setSpawn);
		
		params.put(DIFFICULTY, 
				Arrays.asList(
						Difficulty.PEACEFUL.name().toLowerCase(), 
						Difficulty.EASY.name().toLowerCase(), 
						Difficulty.NORMAL.name().toLowerCase(), 
						Difficulty.HARD.name().toLowerCase()));
		consumerMap.put(DIFFICULTY, this::setDifficulty);
		
		params.put(KEEP_SPAWN_IN_MEMORY, Arrays.asList("true","false"));
		consumerMap.put(KEEP_SPAWN_IN_MEMORY, this::setKeepSpawnInMemory);
		
		params.put(SPAWN_LIMIT, Arrays.asList("monster", "animal", "ambient", "water-animal"));
		consumerMap.put(SPAWN_LIMIT, this::setSpawnLimit);
		
		params.put(TICKS_PER_SPAWN, Arrays.asList("animal","monster"));
		consumerMap.put(TICKS_PER_SPAWN, this::setTicksPerSpawn);
		
		params.put(PVP, Arrays.asList("true","false"));
		consumerMap.put(PVP, this::setPVP);
		
		params.put(INFO, Arrays.asList(""));
		consumerMap.put(INFO, this::showInfo);
		
		this.registerTranslations();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) 
	{
		
		if(args.length < 2)
		{
			sendIncorrectParameters(sender);
			return true;
		}
		
		String firstParam = args[0].toLowerCase();
		String secondParam = args[1].toLowerCase();
		String thirdParam = args.length>2?args[2].toLowerCase():"";
		
		//Does player have the permission to modify this
		if(!hasPermission(sender,firstParam)) {
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + Katura.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		if(!params.containsKey(firstParam) || 
				!params.get(firstParam).contains(thirdParam) ||
				Bukkit.getWorld(secondParam) == null)
		{
			sendIncorrectParameters(sender);
			return true;
		}
		
		BiConsumer<CommandSender,String[]> consumer = consumerMap.get(firstParam);
		if(consumer == null) //Consumer was not found
		{
			sendIncorrectParameters(sender);
			return true;
		}
		
		consumer.accept(sender, args);
				
		/*Player player = (Player)sender;
		
		player.getWorld().setAmbientSpawnLimit(1);
		player.getWorld().setAnimalSpawnLimit(1);
		player.getWorld().setWaterAnimalSpawnLimit(1);
		player.getWorld().setMonsterSpawnLimit(1);
		
		player.getWorld().setAutoSave(true);
		player.getWorld().setDifficulty(Difficulty.EASY);
		player.getWorld().setKeepSpawnInMemory(true);
		player.getWorld().setPVP(false);
		player.getWorld().setSpawnLocation(player.getLocation());
		
		player.getWorld().setTicksPerAnimalSpawns(1);
		player.getWorld().setTicksPerMonsterSpawns(1);*/
		
		return true;
	}
	
	/**
	 * Checks if given sender has a given katura permission
	 * @param sender
	 * @param permission
	 * @return
	 */
	private boolean hasPermission(CommandSender sender, String permission)
	{ return sender.hasPermission("katura.world."+permission); }
	
	/**
	 * Sends the incorrect params message to player
	 * @param sender
	 */
	private void sendIncorrectParameters(CommandSender sender)
	{
		Katura.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE + Katura.getDefaultMsgs().incorrectParameters(sender));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(args.length == 1)
			return filterByPrefix(new ArrayList<String>(params.keySet()), args[0]);
		else if(args.length == 2)
		{
			if(!params.containsKey(args[0]))
				return null;
			
			List<String> worlds = new ArrayList<String>();
			for(World world : Bukkit.getWorlds())
				worlds.add(world.getName());
			return filterByPrefix(worlds, args[1]);
		}
		else if(args.length == 3)
			return filterByPrefix(params.get(args[0]), args[2]);
		return null;
	}
	
	private void setSpawnLimit(CommandSender sender, String[] args)
	{
		Bukkit.broadcastMessage("spawnlimit");
	}
	
	/**
	 * Sets the property whether or not the world autosave is enabled
	 * @param sender
	 * @param args
	 */
	private void setAutoSave(CommandSender sender, String[] args)
	{
		boolean val = true;
		try {
			val = Boolean.parseBoolean(args[2]);
		} catch(Exception e) {
			sendIncorrectParameters(sender);
			return;
		}
		World world = Bukkit.getWorld(args[1]);
		
		world.setAutoSave(val);
		Katura.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + Katura.getTranslator().getTranslation(
						this.plugin, sender, "set-property")
				.replace(VALUE, Boolean.toString(val))
				.replace(PROPERTY, args[0].toLowerCase())
				.replace(WORLD, world.getName()));
	}
	
	private void setDifficulty(CommandSender sender, String[] args)
	{
		Difficulty val = Difficulty.valueOf(args[2].toUpperCase());
		
		if(val == null)
		{
			sendIncorrectParameters(sender);
			return;
		}
		
		World world = Bukkit.getWorld(args[1]);
		
		world.setDifficulty(val);
		Katura.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + Katura.getTranslator().getTranslation(
						this.plugin, sender, "set-property")
				.replace(VALUE, val.name())
				.replace(PROPERTY, args[0].toLowerCase())
				.replace(WORLD, world.getName()));
	}
	
	private void setKeepSpawnInMemory(CommandSender sender, String[] args)
	{
		boolean val = true;
		try {
			val = Boolean.parseBoolean(args[2]);
		} catch(Exception e) {
			sendIncorrectParameters(sender);
			return;
		}
		World world = Bukkit.getWorld(args[1]);
		
		world.setKeepSpawnInMemory(val);
		Katura.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + Katura.getTranslator().getTranslation(
						this.plugin, sender, "set-property")
				.replace(VALUE, Boolean.toString(val))
				.replace(PROPERTY, args[0].toLowerCase())
				.replace(WORLD, world.getName()));
	}
	
	private void setTicksPerSpawn(CommandSender sender, String[] args)
	{
		Bukkit.broadcastMessage("ticksperspawn");
	}
	
	private void setPVP(CommandSender sender, String[] args)
	{
		boolean val = true;
		try {
			val = Boolean.parseBoolean(args[2]);
		} catch(Exception e) {
			sendIncorrectParameters(sender);
			return;
		}
		World world = Bukkit.getWorld(args[1]);
		
		world.setPVP(val);
		Katura.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL + Katura.getTranslator().getTranslation(
						this.plugin, sender, "set-property")
				.replace(VALUE, Boolean.toString(val))
				.replace(PROPERTY, args[0].toLowerCase())
				.replace(WORLD, world.getName()));
	}
	
	private void showInfo(CommandSender sender, String[] args)
	{
		World world = Bukkit.getWorld(args[1]);
		sender.sendMessage("view info of world");
	}
	
	private void setSpawn(CommandSender sender, String[] args)
	{
		Bukkit.broadcastMessage("spawn");
	}

	@Override
	public void registerTranslations() 
	{
		Katura.getTranslator().registerTranslation(this.plugin, 
				"set-property", 
				"Set the property " + PROPERTY + " to " + VALUE + " in world " + WORLD);
	}
}
