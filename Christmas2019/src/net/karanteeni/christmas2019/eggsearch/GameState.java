package net.karanteeni.christmas2019.eggsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.block.BlockEffects.Effect;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.timers.KaranteeniTimer;

public class GameState {
	private volatile ConcurrentHashMap<UUID, Integer> points = new ConcurrentHashMap<UUID, Integer>();
	private boolean gameOngoing = false;
	private boolean editModeOn = false;
	private boolean gameFinishing = false;
	private UUID editor;
	private volatile ConcurrentHashMap<UUID, EggBlock> eggBlocks = new ConcurrentHashMap<UUID, EggBlock>();
	private volatile int eggBlocksBroken;
	private EggEditDisplayer editDisplayer;
	private EggGameRunner timer;
	private static int GAME_LENGTH_MINUTES = 5;
	
	
	/**
	 * Returns the uuids of players and their points
	 * @return
	 */
	public synchronized Map<UUID, Integer> getPoints() {
		return Collections.unmodifiableMap(points);
	}
	
	
	/**
	 * Begins the arena edit mode
	 * @param uuid
	 * @return
	 */
	public boolean beginEdit(UUID uuid) {
		if(gameOngoing || editModeOn || gameFinishing)
			return false;
		
		for(EggBlock block : eggBlocks.values())
			block.setEditMode(true);
		editDisplayer = new EggEditDisplayer();
		Christmas.getTimerHandler().registerTimer(editDisplayer, 10);
		this.editModeOn = true;
		this.editor = uuid;
		return true;
	}
	
	
	public void unregisterEgg(UUID uuid) {
		eggBlocks.remove(uuid);
	}
	
	
	public void registerEgg(EggBlock egg) {
		eggBlocks.put(egg.getUUID(), egg);
	}
	
	
	/**
	 * removes all the eggs from the database
	 * @return
	 */
	public boolean destroyAllEggs() {
		if(gameOngoing || editModeOn || gameFinishing)
			return false;
		
		for(EggBlock egg : eggBlocks.values()) {
			egg.destroy();
		}
		eggBlocks.clear();
		return true;
	}
	
	
	/**
	 * Finishes the edit mode gracefully
	 * @param uuid
	 * @return
	 */
	public boolean finishEdit(UUID uuid) {
		if(editor == null && !editor.equals(uuid))
			return false;
		
		for(EggBlock block : eggBlocks.values())
			block.setEditMode(false);
		regenerateBlocks();
		this.editor = null;
		this.editModeOn = false;
		if(editDisplayer != null)
			Christmas.getTimerHandler().unregisterTimer(editDisplayer);
		editDisplayer = null;
		
		return true;
	}
	
	
	public synchronized Collection<EggBlock> getEggs() {
		return eggBlocks.values();
	}
	
	
	/**
	 * Regenerates all egg blocks to the map
	 * @return
	 */
	public boolean regenerateBlocks() {
		if(gameOngoing || editModeOn || gameFinishing)
			return false;
		
		for(EggBlock block : eggBlocks.values()) {
			block.generateEgg();
		}
		
		eggBlocksBroken = 0;
		return true;
	}
	
	
	/**
	 * Starts the game with animations etc.
	 * @return
	 */
	public boolean startGame() {
		if(editModeOn || gameFinishing || eggBlocks.size() == 0)
			return false;
		if(!regenerateBlocks()) {
			return false;
		}
		
		BossBar bar_ = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
		gameOngoing = true;
		timer = new EggGameRunner(bar_);

		
		Christmas.getTimerHandler().registerTimer(timer, 5);
		return true;
	}
	
	
	/**
	 * Adds the given amount of points to the given player
	 * @param uuid
	 * @param pointCount
	 * @return new points or -1 if game is finishing
	 */
	public synchronized int eggBroken(UUID uuid, EggBlock block) {
		int result = -1;
		
		if(gameFinishing)
			return result;
		result = block.getPoints();
		
		++eggBlocksBroken;
		if(points.containsKey(uuid)) {
			result += points.get(uuid);
		}
		
		points.put(uuid, result);
		
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
			Christmas.getMessager().sendActionBar(player, Sounds.NONE.get(), String.format("§2>§a> §d§l+%s §a<§2<", block.getPoints()));
		
		if(eggBlocksBroken == eggBlocks.size()) {
			finishGame();
		}
		return result;
	}
	
	
	/**
	 * Clears points from a single specific player
	 * @param uuid
	 * @return
	 */
	public Integer clearPoints(UUID uuid) {
		return points.remove(uuid);
	}
	
	
	/**
	 * Forcefully stops the game
	 * @return
	 */
	public synchronized boolean forceStop() {
		if(!gameOngoing || gameFinishing)
			return false;
		
		if(timer != null)
			Christmas.getTimerHandler().unregisterTimer(timer);
		
		this.points.clear();
		gameOngoing = false;
		gameFinishing = false;
		return true;
	}
	
	
	/**
	 * Finishes the game gracefully with all effects and animations
	 * @return
	 */
	public synchronized boolean finishGame() {
		if(!gameOngoing || gameFinishing)
			return false;
		gameFinishing = true;
		
		if(timer != null)
			Christmas.getTimerHandler().unregisterTimer(timer);
		
		broadcastPoints();
		gameOngoing = false;
		
		
		gameFinishing = false;
		return true;
	}
	
	
	private synchronized void broadcastPoints() {
		PriorityQueue<Cluster> players = new PriorityQueue<Cluster>((x, y) -> y.key - x.key);
		
		HashMap<Integer, Cluster> map = new HashMap<Integer, Cluster>();		
		for(Entry<UUID, Integer> p : points.entrySet()) {
			Player player = Bukkit.getPlayer(p.getKey());
			if(player == null) continue;
			int count = p.getValue();
			
			if(map.containsKey(count)) {
				map.get(count).players.add(player);
			} else {
				Cluster cluster = new Cluster(count);
				cluster.players.add(player);
				players.add(cluster);
				map.put(count, cluster);
			}
		}
		
		int pos = 1;
		StringBuffer str = new StringBuffer("");
		while(!players.isEmpty()) {
			Cluster cluster = players.poll();
			for(Player player : cluster.players) {
				str.insert(0, "§d" + pos + ": §e" + player.getName() + "§d Kerätyt munat: §e" + cluster.key + "\n");
				//sender.sendMessage("§6" + pos + ": §e" + player.getName() + "§6 Lanterns: §e" + cluster.key);
			}
			pos += cluster.players.size();
		}
		
		Bukkit.broadcastMessage(str.substring(0, str.length()-1).toString());
		this.points.clear();
	}
	
	
	public synchronized boolean isGameOngoing() {
		return gameOngoing;
	}
	
	
	public synchronized boolean isEditOngoing() {
		return this.editModeOn;
	}
	
	
	/**
	 * Clears the game finishing tag
	 * @return
	 */
	public synchronized boolean gameFinished() {
		if(!gameFinishing)
			return false;
		return gameFinishing = false;
	}
	
	
	private class EggEditDisplayer implements KaranteeniTimer {
		@Override
		public void runTimer() {
			for(EggBlock egg : eggBlocks.values()) {
				Christmas.getBlockManager().getBlockEffects().createEffect(egg.getBlock(), Effect.CUBE, Particle.END_ROD);
			}
		}
		
		
		@Override
		public void timerStopped() {
			
		}
		
		
		@Override
		public void timerWait() {
			
		}
	}
	
	
	private class EggGameRunner implements KaranteeniTimer {
		BossBar bar;
		String format = "§2>§a> §dMunia jäljellä: %s §a<§2<";
		private int elapsedTime = 20*60*GAME_LENGTH_MINUTES;
		private int maxTime = 20*60*GAME_LENGTH_MINUTES;
		
		public EggGameRunner(BossBar bar_) {
			this.bar = bar_;
		}
		
		@Override
		public void runTimer() {
			if(--elapsedTime == 0 && !gameFinishing && gameOngoing) {
				finishGame();
			} else {					
				String text = String.format(format, eggBlocks.size()-eggBlocksBroken);
				bar.setTitle(text);
				for(Player player : Bukkit.getOnlinePlayers())
				if(!bar.getPlayers().contains(player)) {
					bar.addPlayer(player);
				}
				
				if(maxTime != 0)
					bar.setProgress((double)elapsedTime/maxTime);
				
				for(EggBlock egg : eggBlocks.values()) {
					if(egg.isBroken())
						continue;
					Location loc = egg.getBlock().getLocation().add(0.5, 0.5, 0.5);
					Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 4, 4, 4);
					for(Entity entity : entities) {
						if(entity instanceof Player) {
							loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 2, 0.5, 0.5, 0.5);
							break;
						}
					}
				}
				
				for(Entry<UUID, Integer> entry : points.entrySet()) {
					Player player = Bukkit.getPlayer(entry.getKey());
					if(player != null)
						Christmas.getMessager().sendActionBar(player, Sounds.NONE.get(), String.format("§2>§a> §dPisteet: §5§l%s §a<§2<", entry.getValue()));
				}
			}
		}

		
		@Override
		public void timerStopped() {
			if(!gameFinishing) {					
				finishGame();
			}
			
			if(bar != null && bar.getPlayers().size() > 0) {
				bar.removeAll();
			}
		}
		

		@Override
		public void timerWait() {
			if(--elapsedTime == 0 && !gameFinishing && gameOngoing) {
				finishGame();
			}
		}
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


