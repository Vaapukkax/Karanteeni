package net.karanteeni.missionnpcs.npc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import lombok.NonNull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.npc.NPC;
import net.karanteeni.missionnpcs.MissionNPCs;
import net.karanteeni.missionnpcs.mission.Mission;

public class NPCManager {
	public HashMap<Integer, Set<Mission>> npcMissions = new HashMap<Integer, Set<Mission>>();
	public Set<UUID> listeningToNPC = new HashSet<UUID>();
	private MissionNPCs plugin;
	
	public NPCManager(MissionNPCs plugin) {
		this.plugin = plugin;
	}
	
	
	public boolean doesNPCHaveMissions(NPC npc) {
		if(!npcMissions.containsKey(npc.getId()))
			return false;
		
		removeNPCData(npc.getId());
		return true;
	}
	
	
	/**
	 * Removes all data associated to this npc
	 * @param npcId
	 */
	public boolean removeNPCData(int npcId) {
		if(!npcMissions.containsKey(npcId))
			return false;
		
		//this.npcMissions.remo
		
		return true;
	}
	
	
	/**
	 * Attaches the given mission to a given NPC.
	 * @param id
	 * @param mission
	 * @return true if attached, false if mission is already defined
	 */
	public boolean attachMission(final int id, @NonNull Mission mission) {
		if(!this.npcMissions.containsKey(id))
			registerNPC(id);
		Set<Mission> missions = this.npcMissions.get(id);
		return missions.add(mission);
	}
	
	
	private void registerNPC(final int id) {
		MissionNPCs.getTranslator().registerTranslation(plugin, "npc-idle."+id, "Hello traveler");
	}
	
	
	/**
	 * Attempts to start the mission for the given player. If not successful,
	 * speaks the idle text instead
	 * @param id
	 * @param player
	 * @return
	 */
	public boolean startMission(final int id, @NonNull final Player player) {
		Set<Mission> missions = this.npcMissions.get(id);
		if(missions == null)
			return false;
		
		for(final Mission mission : missions) {
			if(mission.canActivate(player))
				mission.activate(player);
			else
				speakIdleText(id, player);
		}
		return true;
	}
	
	
	/**
	 * Makes the given npc speak the idle text to the given player
	 * @param id
	 * @param player
	 */
	private void speakIdleText(final int id, @NonNull final Player player) {
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		String text = MissionNPCs.getTranslator().getTranslation(plugin, player, "npc-idle." + id);
		SpeechContext context = new SpeechContext(npc, text, player);
		npc.getDefaultSpeechController().speak(context);
	}
	
	
	/**
	 * Removes all references to the given mission 
	 * @param mission mission to remove the references to
	 * @return
	 */
	public int removeMission(@NonNull final Mission mission) {
		int referenceCount = 0;
		Iterator<Entry<Integer, Set<Mission>>> iter = this.npcMissions.entrySet().iterator();
		
		while(iter.hasNext()) {
			Entry<Integer, Set<Mission>> missions = iter.next();
			if(missions.getValue() == null) 
				continue;
			missions.getValue().remove(mission);
			if(missions.getValue().isEmpty()) {
				iter.remove();
				allMissionsRemoved(missions.getKey());
			}
			++referenceCount;
		}
		return referenceCount;
	}
	
	
	/**
	 * Removes all references to this mission from the given NPC
	 * @param mission
	 * @param id
	 * @return
	 */
	public boolean removeMission(@NonNull final Mission mission, final int id) {
		Set<Mission> missions = this.npcMissions.get(id);
		if(missions == null)
			return false;
		missions.remove(mission);
		if(missions.isEmpty()) {
			this.npcMissions.remove(id);
			allMissionsRemoved(id);
		}
		return true;
	}
	
	
	private void allMissionsRemoved(final int id) {
		Bukkit.getLogger().log(Level.INFO, String.format("NPC %s has no more missions."
			+" The NPC will be removed from the mission registry and translations", id));
	}
}
