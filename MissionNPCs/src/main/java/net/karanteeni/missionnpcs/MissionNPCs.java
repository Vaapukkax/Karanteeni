package net.karanteeni.missionnpcs;

import org.bukkit.event.EventHandler;
import lombok.Getter;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.BranchComponent;
import net.karanteeni.missionnpcs.command.MNPC;
import net.karanteeni.missionnpcs.mission.MissionManager;
import net.karanteeni.missionnpcs.npc.NPCManager;
import net.karanteeni.missionnpcs.npc.PlayerClickNPC;
import net.karanteeni.missionnpcs.requirement.Requirements;

/**
 * uwu
 *
 */
public class MissionNPCs extends KaranteeniPlugin {
	@Getter private Requirements requirements;
	@Getter private NPCManager npcManager;
	@Getter private MissionManager missionManager;
	
	public MissionNPCs() {
		super(true);
	}

	
	@Override
	public void onLoad() {
		this.requirements = new Requirements();
		this.missionManager = new MissionManager(this);
		this.npcManager = new NPCManager(this);
	}
	
	
	@EventHandler
	public void onEnable() {
		registerEvents();
		registerCommands();
	}
	
	
	private void registerEvents() {
		this.getServer().getPluginManager().registerEvents(new PlayerClickNPC(this), this);
	}
	
	
	private void registerCommands() {
		MNPC mnpc = new MNPC(this);
		BranchComponent missionBranch = new BranchComponent();
		BranchComponent npcBranch = new BranchComponent();
		mnpc.addComponent("mission", missionBranch);
		mnpc.addComponent("npc", npcBranch);
	}
}
