package net.karanteeni.tester.commands.entity.components;

import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.UUIDLoader;
import net.karanteeni.tester.commands.entity.EntityActionMessager;
import net.karanteeni.tester.commands.entity.EntityCommand;
import net.karanteeni.tester.commands.entity.EntityActionMessager.EntityAction;

public class EntityHeal extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		UUID uuid = this.chainer.getObject(UUIDLoader.UUID_KEY);
		EntityActionMessager messager = ((EntityCommand)this.chainer).getEntityActionMessager();
		Player player = (Player)sender;
		
		if(uuid != null) { // healing an entity with uuid
			Entity entity = ((EntityCommand)chainer).getEntityManager().healEntity(uuid);
			
			if(entity == null) {
				messager.noEntitiesFound(player, null, uuid);
			} else {
				messager.entityHealed(player, uuid, entity.getType());
			}
		} else { // displaying clickable field for nearby healable entities
			List<Entity> entities = ((EntityCommand)chainer).getEntityManager().getNearestEntities(player, 20);
			messager.listEntitiesForAction(player, entities, EntityAction.HEAL);
		}
		
		return CommandResult.SUCCESS;
	}
}
