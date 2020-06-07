package net.karanteeni.christmas2019.skinkisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.entity.Player;
import net.karanteeni.core.data.ObjectPair;

public class SkinkisaState {
	private HashMap<UUID, ObjectPair<String, Integer>> pointsAndNames = new HashMap<UUID, ObjectPair<String, Integer>>();
	private TreeSet<UUID> playersNotLost = new TreeSet<UUID>();
	private TreeMap<UUID, SkinkisaBlock> blocks = new TreeMap<UUID, SkinkisaBlock>();
	private HashMap<UUID, UUID> blockPlayers = new HashMap<UUID, UUID>(); // player > block
	private HashMap<UUID, UUID> dropVotes = new HashMap<UUID, UUID>();
	private boolean gameOngoing = false;
	
	
	public void votePlayerToDrop(UUID voter, UUID voted) {
		dropVotes.put(voter, voted);
	}
	
	
	public boolean join(Player player) {
		if(gameOngoing)
			return false;
		if(pointsAndNames.containsKey(player.getUniqueId()))
			return false;
		
		pointsAndNames.put(player.getUniqueId(), new ObjectPair<String, Integer>(player.getName(), 0));
		
		// assign a block to a player
		Set<UUID> blocks = this.blocks.keySet();
		Collection<UUID> reservedBlocks = blockPlayers.values();
		blocks.removeAll(reservedBlocks);
		Iterator<UUID> iter = blocks.iterator();
		if(!iter.hasNext())
			return false;
		UUID blockUUID = iter.next();
		SkinkisaBlock block = this.blocks.get(blockUUID);
		if(block == null)
			return false;
		blockPlayers.put(player.getUniqueId(), blockUUID);
		block.assignPlayer(player.getUniqueId());
		
		return true;
	}
	
	
	public Collection<SkinkisaBlock> getBlocks() {
		return blocks.values();
	}
	
	
	public SkinkisaBlock registerBlock(SkinkisaBlock block) {
		return blocks.put(block.getUUID(), block);
	}
	
	
	public SkinkisaBlock unregisterBlock(SkinkisaBlock block) {
		return blocks.remove(block.getUUID());
	}
	
	
	public boolean leave(Player player) {
		if(!pointsAndNames.containsKey(player.getUniqueId()))
			return false;
		
		pointsAndNames.remove(player.getUniqueId());
		playersNotLost.remove(player.getUniqueId());
		
		UUID blockID = blockPlayers.remove(player.getUniqueId());
		if(blockID != null) {
			SkinkisaBlock block = blocks.get(blockID);
			if(block != null) {
				block.unassignPlayer();
			}
		}
		
		return true;
	}
	
	
	/**
	 * runs the vote scan and returns the players with most negative votes and their blocks
	 * @return
	 */
	public Map<UUID, SkinkisaBlock> runVoteScan() {
		//TreeMap<Integer, List<UUID>> voteCounts = new TreeMap<Integer, List<UUID>>();
		HashMap<UUID, Integer> voteCount = new HashMap<UUID, Integer>();
		for(UUID uuid : dropVotes.values()) {
			if(voteCount.containsKey(uuid))
				voteCount.put(uuid, voteCount.get(uuid));
			else
				voteCount.put(uuid, 0);
		}
		
		ArrayList<UUID> losingPlayers = new ArrayList<UUID>();
		int highestCount = -1;
		for(Entry<UUID, Integer> entry : voteCount.entrySet()) {
			if(entry.getValue() < highestCount)
				continue;
			if(entry.getValue() > highestCount) {
				highestCount = entry.getValue();				
				losingPlayers.clear();
			}
			losingPlayers.add(entry.getKey());
		}
		
		HashMap<UUID, SkinkisaBlock> resultMap = new HashMap<UUID, SkinkisaBlock>();
		for(UUID uuid : losingPlayers) {
			resultMap.put(uuid, blocks.get(blockPlayers.get(uuid)));
		}
		
		return resultMap;
	}
	
	
	/**
	 * marks the given player as lost. either naturally as losing or when kicked
	 * @param player
	 * @return
	 */
	public SkinkisaBlock playerLost(Player player) {
		if(!gameOngoing)
			return null;
		if(!playersNotLost.contains(player.getUniqueId()))
			return null;
		
		playersNotLost.remove(player.getUniqueId());
		UUID blockID = blockPlayers.remove(player.getUniqueId());
		if(blockID == null)
			return null;
		return blocks.get(blockID);
	}
	
	
	public boolean beginGame() {
		if(gameOngoing)
			return false;
		gameOngoing = true;
		return true;
	}
	
	
	public boolean stopGame() {
		if(!gameOngoing)
			return false;
		gameOngoing = false;
		return true;
	}
}
