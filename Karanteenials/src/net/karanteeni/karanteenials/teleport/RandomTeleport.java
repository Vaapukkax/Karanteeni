package net.karanteeni.karanteenials.teleport;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.DisplayFormat;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.information.Teleporter;
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
					teleportPlayer(KPlayer.getKPlayer(getPlayer(PlayerLoader.PLAYER_KEY_SINGLE)));
					
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
		
		// send a message that random location is under search
		Karanteenials.getMessager().sendActionBar(sender, Sounds.SETTINGS.get(), 
				Karanteenials.getTranslator().getTranslation(plugin, sender, "random-teleport-searching"));
		
		// set data to cache
		kp.setCacheData(plugin, "rtp", System.currentTimeMillis());
		
		// teleport self
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				teleportPlayer(kp);
			}
		};
		
		runnable.runTaskAsynchronously(plugin);
		return CommandResult.SUCCESS;
	}

	
	/**
	 * Teleports a player to a random location
	 * @param player player to teleport to random location
	 */
	private void teleportPlayer(KPlayer kp) {
		World w = Bukkit.getWorlds().get(0);
		Teleporter tp = getRTPLocation(w, -range, -range, range, range);

		// no location found, player no sound
		if(tp == null || !kp.getPlayer().isOnline()) { 
			Karanteenials.getSoundHandler().playSound(kp.getPlayer(), Sounds.NO.get());
			kp.removeData(plugin, "rtp");
			return;
		}
		
		// set the back location to the given player
		net.karanteeni.karanteenials.functionality.Back back = new net.karanteeni.karanteenials.functionality.Back(kp.getPlayer());
		back.setBackLocation(kp.getPlayer().getLocation());
		
		// run in sync
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// teleport the player
				tp.teleport(kp.getPlayer(), false, true, false, TeleportCause.PLUGIN);
				//tp.preciseTeleport(player, true);
				BossBar bar = Bukkit.createBossBar(
						Karanteenials.getTranslator().getTranslation(plugin, kp.getPlayer(), "random-teleport"), 
						BarColor.YELLOW, BarStyle.SOLID);
				Karanteenials.getMessager().sendBossbar(kp.getPlayer(), Sounds.TELEPORT.get(), 4f, 20, true, bar);
			}
		};
		
		Bukkit.getScheduler().runTask(plugin, runnable);
		
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
		Random r = new Random();
		
		for(int i = 0; i < 10 && tp == null; ++i) {
			int x = (int)(xRangeMin >= xRangeMax ? xRangeMin : r.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin);
	        int z = (int)(zRangeMin >= zRangeMax ? zRangeMin : r.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin);
	        // the odd x is to fix the get incorrect chunk
	        ChunkSnapshot shot = world.getChunkAt(new Location(world, x < 0 ? x - 0.5 : x + 0.5, 100, z < 0 ? z - 0.5 : z + 0.5)).getChunkSnapshot();
	        //ChunkSnapshot shot = world.getChunkAt(new Location(world, x < 0 ? (x%16==0?x+1:x) : x, 100, z)).getChunkSnapshot();
	        
	        // check if the block at location is safe
	        // get chunk specific coordinates
	        int x_ = x < 0 ? 15 + (x % 16) : x % 16;
	        int z_ = z < 0 ? 15 + (z % 16) : z % 16;
	        int y_ = 256;
	        Material type = null;
	        
	        do {
	        	Material t = shot.getBlockType(x_, --y_, z_);
        		if(t == Material.AIR || t == Material.CAVE_AIR)
        			continue;
        		else if(t == Material.WATER || t == Material.LAVA || t == Material.FIRE || t == Material.CAMPFIRE)
        			break;
        		
	        	type = t;
	        } while(y_ > 0 && type == null);
	        
	        // check block safety
	        if(type != null) {
	        	// safe location
	        	Teleporter tp_ = new Teleporter(new Location(world, 
	        				x < 0 ? x - 0.5 : x + 0.5, 
    						type.isSolid() ? y_ + 1 : y_, // if not solid then it's grass etc. Dont care about grass 
    						z < 0 ? z - 0.5 : z + 0.5, 
	        			Math.random()>0.5? (float)Math.random()*180 : (float)Math.random()*-180, 
    	    			Math.random()>0.5 ? (float)Math.random()*45 : (float)Math.random()*-25));
        		return tp_;
	        }
		}
		
		return null;
    }
	

	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleport", "> Teleported to random location <");
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleport-searching", "> Searching random location <");
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleport-cooldown", "> Cooldown: %seconds%s <");
		Karanteenials.getTranslator().registerTranslation(plugin, "random-teleported-player", "Teleported %player% to random location");
	}

}
