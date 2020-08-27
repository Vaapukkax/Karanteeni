package net.karanteeni.tester.commands.entity.components;

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
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

public class EntityTeleport extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		UUID uuid = this.chainer.getObject(UUIDLoader.UUID_KEY);
		Player player = (Player)sender;
		EntityActionMessager messager = ((EntityCommand)this.chainer).getEntityActionMessager();
		
		if(uuid != null) {			
			Entity entity = Bukkit.getEntity(uuid);
			
			if(entity == null) {
				messager.noEntitiesFound(player, null, uuid);
			} else {
				player.teleport(entity);
				messager.teleportedToEntity(player, entity.getType(), uuid);
			}
		} else {
			List<Entity> entities = ((EntityCommand)chainer).getEntityManager().getNearestEntities(player, 20);
			messager.listEntitiesForAction(player, entities, EntityAction.TELEPORT);
		}
		
		return CommandResult.SUCCESS;
	}
}
