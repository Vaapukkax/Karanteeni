package net.karanteeni.karanteenials.teleport;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.DisplayFormat;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.core.information.Teleporter.EyeLevel;
import net.karanteeni.core.information.Teleporter.SAFENESS;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.time.TimeData;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

public class RandomTeleport extends CommandChainer implements TranslationContainer {
	private int range;
	private long timeLimit;
	private List<String> rtpWorlds;
	
	public RandomTeleport() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"rtp", 
				"/rtp [<player>]", 
				"teleport to a random location", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
		registerConfig();
	}
	
	
	/**
	 * Registers and loads the config of this random teleport
	 */
	private void registerConfig() {
		// rtp enabled worlds
		if(!plugin.getConfig().isSet("rtp.worlds")) {
			plugin.getConfig().set("rtp.worlds", Arrays.asList(Bukkit.getWorlds().get(0).getName()));
			plugin.saveConfig();
		}
		
		rtpWorlds = plugin.getConfig().getStringList("rtp.worlds");
		
		// check each world in config if the config values have been set
		for(String world : rtpWorlds) {
			// range for the rtp
			if(!plugin.getConfig().isSet("rtp."+world+".range")) {
				plugin.getConfig().set("rtp."+world+".range", 2000);
				plugin.saveConfig();
			}
			
			range = plugin.getConfig().getInt("rtp."+world+".range");
		}
		
		// cooldown for the rtp
		if(!plugin.getConfig().isSet("rtp.cooldown")) {
			plugin.getConfig().set("rtp.cooldown", 60000);
			plugin.saveConfig();
		}
		
		timeLimit = plugin.getConfig().getInt("rtp.cooldown");
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length > 0) {
			// check if player has permission
			if(!sender.hasPermission("karanteenials.rtp.admin")) return CommandResult.NO_PERMISSION;
			
			// check if player was found
			if(this.hasData(PlayerLoader.PLAYER_KEY_SINGLE))
				return new CommandResult(Karanteenials.getDefaultMsgs().playerNotFound(sender, args[0]),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get());
			
			// send a message that random location is under search
			Karanteenials.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
					Karanteenials.getTranslator().getTranslation(plugin, sender, "random-teleport-searching"));
			
			// teleport the found player
			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					teleportPlayer(getPlayer(PlayerLoader.PLAYER_KEY_SINGLE));
					
					// send the player teleported message to the command sender
					Karanteenials.getMessager().sendMessage(sender, Sounds.NO.get(), 
							Prefix.NEUTRAL +
							Karanteenials.getTranslator().getTranslation(plugin, sender, "random-teleported-player")
							.replace("%player%", getPlayer(PlayerLoader.PLAYER_KEY_SINGLE).getName()));
				}
			};
			
			runnable.runTaskAsynchronously(plugin);
			
			return CommandResult.SUCCESS;
		}
		
		// teleporting self
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		if(!sender.hasPermission("karanteenials.rtp.use")) return CommandResult.NO_PERMISSION;
		
		// check cooldown
		KPlayer kp = KPlayer.getKPlayer((Player)sender);
		if(!sender.hasPermission("karanteenials.rtp.admin") && kp.dataExists(plugin, "rtp")) {
			long time = kp.getObject(plugin, "rtp");
			time = System.currentTimeMillis() - time;
			
			// player is under cooldown
			if(time <= timeLimit) {
				TimeData td = new TimeData(timeLimit - time);
				return new CommandResult(
						Karanteenials.getTranslator().getTranslation(plugin, sender, "random-teleport-cooldown")
						.replace("%seconds%", td.getSeconds()+""),
						ResultType.INVALID_ARGUMENTS,
						Sounds.NO.get(),
						DisplayFormat.ACTIONBAR);
			} else kp.removeData(plugin, "rtp");
		}
		
		// set data to cache
		kp.setCacheData(plugin, "rtp", System.currentTimeMillis());
		
		// send a message that random location is under search
		Karanteenials.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
				Karanteenials.getTranslator().getTranslation(plugin, sender, "random-teleport-searching"));
		
		// teleport self
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				teleportPlayer((Player)sender);
			}
		};
		
		runnable.runTaskAsynchronously(plugin);
		return CommandResult.SUCCESS;
	}

	
	/**
	 * Teleports a player to a random location
	 * @param player player to teleport to random location
	 */
	private void teleportPlayer(Player player) {
		World w = Bukkit.getWorlds().get(0);
		Teleporter tp = getRTPLocation(w, -range, -range, range, range);

		// no location found, player no sound
		if(tp == null) Karanteenials.getSoundHandler().playSound(player, Sounds.NO.get());
		
		// set the back location to the given player
		net.karanteeni.karanteenials.functionality.Back back = new net.karanteeni.karanteenials.functionality.Back(player);
		back.setBackLocation(player.getLocation());
		
		// run in sync
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// teleport the player
				tp.preciseTeleport(player, true);
			}
		};
		
		Bukkit.getScheduler().runTask(plugin, runnable);
		
		BossBar bar = Bukkit.createBossBar(
				Karanteenials.getTranslator().getTranslation(plugin, player, "random-teleport"), 
				BarColor.YELLOW, BarStyle.SOLID);
		Karanteenials.getMessager().sendBossbar(player, Sounds.TELEPORT.get(), 4f, 20, true, bar);
	}
	
	
	/**
	 * Get the location for rtp to teleport to
	 * @param world
	 * @param size
	 * @param xRangeMin
	 * @param zRangeMin
	 * @param xRangeMax
	 * @param zRangeMax
	 * @return
	 */
	private Teleporter getRTPLocation(World world, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax) {
		Teleporter tp = null;
		
		// try 10 times to get a random safe location 
		for(int i = 0; i < 10 && (tp == null || SAFENESS.SAFE != tp.isSafe(EyeLevel.Player)); ++i) {
			double x = xRangeMin >= xRangeMax ? xRangeMin : Math.random() * (xRangeMax - xRangeMin) + xRangeMin;
	        double z = zRangeMin >= zRangeMax ? zRangeMin : Math.random() * (zRangeMax - zRangeMin) + zRangeMin;
	        Location l = (new Location(world, x, 255, z, 
	        		Math.random()>0.5? (float)Math.random()*180 : (float)Math.random()*-180, 
	    			Math.random()>0.5 ? (float)Math.random()*45 : (float)Math.random()*-25));
	        tp = new Teleporter(l);
		}
		
        return tp;
    }
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleport", "> Teleported to random location <");
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleport-searching", "> Searching random location <");
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleport-cooldown", "> Cooldown: %seconds%s <");
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleported-player", "Teleported %player% to random location");
	}

}
