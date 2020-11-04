package net.karanteeni.missionnpcs.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.missionnpcs.MissionNPCs;

public class NPCLoader extends CommandLoader implements TranslationContainer {
	public static final String SELECTED_NPC_KEY = "npc";
	
	public NPCLoader() {
		super(true);
		this.parameterLength = 0;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
		if(npc == null)
			return new CommandResult(
					Prefix.NEGATIVE +
					MissionNPCs.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "select-npc"),
					ResultType.INVALID_ARGUMENTS);
		this.chainer.setObject(SELECTED_NPC_KEY, npc);
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		MissionNPCs.getTranslator().registerTranslation(this.chainer.getPlugin(), "select-npc", "You have no NPC selected");
	}
}
