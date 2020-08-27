package net.karanteeni.christmas2019.snowfigth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.information.bossbar.TimedBossBar;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.timers.KaranteeniTimer;

public class SnowFightTimer implements KaranteeniTimer, Listener {
	private Christmas plugin = null;
	private HashMap<UUID, Integer> points = new HashMap<UUID, Integer>();
	private boolean tick = true;
	private long nextSnowBall;
	private int snowBallLimit = 51;
	private int fightDuration = 300000; // 5 mins
	private int initialSnowBallCount = 10;
	private int initDuration = 5; // 5 seconds
	private ItemStack snowBall = null;
	TimedBossBar leadingPlayer = null;
	TimedBossBar nextBall = null;
	private List<Player> leadingPlayers = new LinkedList<Player>();
	private long startTime = 0;
	private short maxPlayerDistBeforePointsLost = 20;
	private String snowBallName = "§eV§fa§el§fk§eo§fs§eu§fk§el§fa§ea§fm§eu§fn§ea";
	
	public SnowFightTimer(Christmas plugin) {
		this.plugin = plugin;
		nextSnowBall = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		snowBall = new ItemStack(Material.EGG, 1);
		ItemMeta meta = snowBall.getItemMeta();
		meta.setDisplayName(snowBallName);
		snowBall.setItemMeta(meta);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		BossBar bar = Bukkit.createBossBar("§2>§4>§2> §d!§MUNANHEITTO§d! §2<§4<§2<", BarColor.GREEN, BarStyle.SOLID, BarFlag.CREATE_FOG);
		// create the bossbar with animated text
		leadingPlayer = new TimedBossBar(new LinkedList<Player>(Bukkit.getOnlinePlayers()), bar, fightDuration/1000 + initDuration/20, 
				Arrays.asList(
						"§2> §4>§2> §d!§aMUNANHEITTO§d! §2<§4<§2<",
						"§2> §4> §2> §d!§aMUNANHEITTO§d! §2< §4< §2<",
						"§2 >§4>§2> §d! §aMUNANHEITTO §d! §2<§4< §2<",
						"§2>§4>§2> §d! §aM UNANHEITTO §d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMU NANHEITTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUN ANHEITTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNA NHEITTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNAN HEITTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNANH EITTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNANHE ITTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNANHEI TTO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNANHEIT TO§d! §2<§4<§2<",
						"§2>§4>§2> §d!§aMUNANHEITT O§d! §2<§4<§2<"), true);
		// play the bossbar
		Christmas.getTimerHandler().registerTimer(leadingPlayer, 3);
		SnowFightTimer self = this;

		// register and run this timer
		BukkitRunnable runnable = new BukkitRunnable() {
			private int count = self.initDuration;
			
			@Override
			public void run() {
				// when the count reaches 0 start the match and disable this timer
				if(count <= 0) {
					for(Player player : Bukkit.getOnlinePlayers())
						Christmas.getMessager().sendTitle(0, 0, 1, player, "§dSOTA", "", Sounds.COUNTDOWN_STOP.get());
					// give each online player 10 snowballs
					for(Player player : Bukkit.getOnlinePlayers()) {
						giveSnowBall(player, initialSnowBallCount);
						// play the start sound
						player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, SoundCategory.MASTER, 10000, 1.5f);
					}
					
					
					List<Player> players = new LinkedList<Player>(Bukkit.getOnlinePlayers());
					BossBar bar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
					List<String> texts = new LinkedList<String>(Arrays.asList(
							"§dMunitaan",
							"§dMunitaan.",
							"§dMunitaan..",
							"§dMunitaan...",
							"§dMunitaan..",
							"§dMunitaan."
							));
					nextBall = new TimedBossBar(players, bar, (snowBallLimit/20)+(0.5f), texts, true, false);
					
					Christmas.getTimerHandler().registerTimer(nextBall, 2);
					Christmas.getTimerHandler().registerTimer(self, snowBallLimit);
					this.cancel();
				} else {
					for(Player player : Bukkit.getOnlinePlayers())
						Christmas.getMessager().sendTitle(0, 0, 1, player, "§d"+count--, "", Sounds.COUNTDOWN.get());
				}
			}
		};
		runnable.runTaskTimer(plugin, 0, 20);
	}
	
	
	@Override
	public void runTimer() {
		// if the match is over stop this timer
		if(startTime + fightDuration <= System.currentTimeMillis())
			Christmas.getTimerHandler().unregisterTimer(this);
		
		// give players their snowballs
		for(Player player : Bukkit.getOnlinePlayers()) {
			giveSnowBall(player, (int)Math.ceil(Math.random()*3)); // give players 1-3 snowballs each time
		}
		
		// update the bossbar displaying leading players
		updateBossBar();
		
		// check if player is too far, if so punish them
		for(Player player : Bukkit.getOnlinePlayers())
			checkCheating(player);
		
		// refresh the bossbar
		nextBall.resetTime();
	}

	
	// called when the match ends
	@Override
	public void timerStopped() {
		// unregister events
			PlayerQuitEvent.getHandlerList().unregister(this);
			ProjectileHitEvent.getHandlerList().unregister(this);
			PlayerTeleportEvent.getHandlerList().unregister(this);
			new BukkitRunnable() {
				@Override
				public void run() {
					nextBall.removeBar();					
				}
			}.runTask(plugin);
					// announce winners
					printLeadingPlayers();
	}

	
	@Override
	public void timerWait() {
		tick = !tick;
		
		// display players their points
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!points.containsKey(player.getUniqueId())) {
				points.put(player.getUniqueId(), 0);
				leadingPlayer.addPlayer(player);
			}
			
			Christmas.getMessager().sendActionBar(
					player, 
					Sounds.NONE.get(), 
					String.format(tick?"§2>§a>§a §d%s §aPistettä §a<§2<":"§2> §a>§a §d%s §aPistettä §a< §2<", 
							points.get(player.getUniqueId())));
		}
		
		// display a particle effect at each of the leading players
		for(Player player : leadingPlayers) {
			playParticleEffect(player);
		}
	}
	
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// remove the points of the player who quits
		this.points.remove(event.getPlayer().getUniqueId());
	}
	
	
	// prevent players teleporting when the match is ongoing
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if(!event.getPlayer().isOp())
		event.setCancelled(true);
	}
	
	
	@EventHandler(ignoreCancelled = true)
	public void onProjectileHit(ProjectileHitEvent event) {
		if(!(event.getHitEntity() instanceof Player) || !(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		
		Player hit = (Player)event.getHitEntity();
		Player shooter = (Player)event.getEntity().getShooter();
		
		// check if the player hit themselves
		if(!hit.getUniqueId().equals(shooter.getUniqueId())) {
			shooter.playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 100f, 1.5f);
			hit.playSound(hit.getLocation(), Sound.ENTITY_ARROW_SHOOT, SoundCategory.MASTER, 100f, 1.7f);
			
			if(leadingPlayers.contains(hit))
				points.put(shooter.getUniqueId(), points.get(shooter.getUniqueId())+3);
			else
				points.put(shooter.getUniqueId(), points.get(shooter.getUniqueId())+2);			
			points.put(hit.getUniqueId(), Math.max(0, points.get(hit.getUniqueId())-1));
		} else {
			// remove 2 points if hit themselves
			points.put(hit.getUniqueId(), Math.max(0, points.get(hit.getUniqueId())-2));
			hit.playSound(hit.getLocation(), Sound.ENTITY_ILLUSIONER_HURT, SoundCategory.MASTER, 1000, 1.7f);
			hit.getWorld().spawnParticle(
					Particle.SLIME, 
					hit.getLocation().add(0,2,0), 
					20, 
					0.2, 
					0.2, 
					0.2,
					0.5);
		}
	}
	
	
	/**
	 * If the player is further than 12 blocks away from other players remove 1 point
	 * @param player
	 */
	private void checkCheating(Player player) {
		List<Player> players = Christmas.getEntityManager().getNearbyPlayers(player.getLocation(), maxPlayerDistBeforePointsLost);
		
		// subtract points if the player is too far from others
		if(players == null || players.size() <= 1) {
			points.put(player.getUniqueId(), Math.max(0, points.get(player.getUniqueId())-1));
			player.playSound(player.getLocation(), Sound.ENTITY_BAT_HURT, SoundCategory.MASTER, 0.3f, 1.3f);
		}
	}
	
	
	/**
	 * Plays the christmas particle effect at the given player
	 * @param player
	 */
	private void playParticleEffect(Player player) {
		player.getWorld().spawnParticle(
				Particle.REDSTONE, 
				player.getLocation().add(0, 1, 0), 
				1, 
				1, 
				1, 
				1,
				0.5,
				new DustOptions(Color.FUCHSIA, 0.8f));
		player.getWorld().spawnParticle(
				Particle.TOTEM, 
				player.getLocation().add(0,0.2,0), 
				2, 
				0.2, 
				0.2, 
				0.2,
				0.5);
	}
	
	
	/**
	 * Updates the bossbar displaying the top players
	 */
	private void updateBossBar() {
		// get the player with highest points
		int highestPoints = -1;
		List<String> playerWithHighestPoints = new LinkedList<String>();
		List<Player> players = new LinkedList<Player>();
		
		for(Entry<UUID, Integer> entry : points.entrySet()) {
			if(highestPoints <= entry.getValue()) {
				if(highestPoints < entry.getValue()) {
					playerWithHighestPoints.clear();
					players.clear();
				}
				
				highestPoints = entry.getValue();
				Player p = Bukkit.getPlayer(entry.getKey());
				if(p != null) {
					playerWithHighestPoints.add(p.getName());
					players.add(p);
				}
			}
		}
		
		// display the players with the highest points
		StringBuffer buffer = new StringBuffer();
		for(String txt : playerWithHighestPoints) {
			buffer.append(txt);
			buffer.append(" ");
		}
		if(buffer.length() > 0)
			buffer.deleteCharAt(buffer.length()-1);
		leadingPlayer.setStaticText(String.format("§2> §a%s §2<", buffer.toString()));
		leadingPlayers = players;
	}
	
	
	/**
	 * Check if this match is still ongoing
	 * @return
	 */
	public boolean isOnGoing() {
		return System.currentTimeMillis() <= this.fightDuration+this.startTime;
	}
	
	
	/**
	 * Gives a snowball to player
	 * @param player
	 */
	private void giveSnowBall(Player player, int count) {
		while(count-- >= 0)
			player.getInventory().addItem(snowBall);
	}
	
	
	/**
	 * Announce the winning players of this match
	 */
	private void printLeadingPlayers() {
		PriorityQueue<Cluster> players = new PriorityQueue<Cluster>((x, y) -> y.key - x.key);
		HashMap<Integer, Cluster> map = new HashMap<Integer, Cluster>();		
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission("karanteenials.player.gamemode.spectator.self"))
				continue;
			int points = this.points.get(player.getUniqueId());
			
			if(map.containsKey(points)) {
				map.get(points).players.add(player);
			} else {
				Cluster cluster = new Cluster(points);
				cluster.players.add(player);
				players.add(cluster);
				map.put(points, cluster);
			}
		}
		
		int pos = 1;
		StringBuffer str = new StringBuffer("");
		while(!players.isEmpty()) {
			Cluster cluster = players.poll();
			for(Player player : cluster.players) {
				str.insert(0, "§d" + pos + ": §b" + player.getName() + "§d Points: §b" + cluster.key + "\n");
			}
			pos += cluster.players.size();
		}
		
		if(str.length() > 0)
			str.substring(0, str.length()-1);
		for(Player player : Bukkit.getOnlinePlayers())
			player.sendMessage(str.toString());
		Bukkit.getLogger().log(Level.SEVERE, str.toString());
	}
	
	
	class Cluster implements Comparable<Cluster> {
		Integer key;
		List<Player> players = new ArrayList<Player>();
		
		public Cluster(int key) {
			this.key = key;
		}
		
		
		@Override
		public int compareTo(Cluster o) {
			return key.compareTo(o.key);
		}
	}
}
