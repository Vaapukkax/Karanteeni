package net.karanteeni.tester.commands.entity;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.tester.TesterMain;

public class EntityCommand extends CommandChainer {
	private final EntityManager manager;
	private final EntityActionMessager messager;
	public static final String ENTITY_COUNT = "entityCount";
	
	public EntityCommand(TesterMain plugin) {
		super(plugin, "entity", 
				"/entity [list/nearest/<uuid>]", 
				"kills and heals entities", 
				KaranteeniCore.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		this.manager = new EntityManager();
		this.messager = new EntityActionMessager(plugin);
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandResult.INVALID_ARGUMENTS;
	}
	
	
	public EntityManager getEntityManager() {
		return this.manager;
	}
	
	
	public EntityActionMessager getEntityActionMessager() {
		return this.messager;
	}
}
