package net.karanteeni.missionnpcs.npc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import lombok.NonNull;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.karanteeni.missionnpcs.MissionNPCs;

public class PlayerClickNPC implements Listener {
	private MissionNPCs plugin;
	
	public PlayerClickNPC(@NonNull MissionNPCs plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		NPC npc = event.getNPC();
		Player player = event.getClicker();
		plugin.getNpcManager().startMission(npc.getId(), player);
		
	}
	
	
	@EventHandler
	public void onNPCDelete(NPCRemoveEvent event) {
		plugin.getNpcManager().removeNPCData(event.getNPC().getId());
	}
}
