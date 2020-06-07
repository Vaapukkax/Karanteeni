package net.karanteeni.tester.commands.entity.components;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.EntityTypeLoader;
import net.karanteeni.tester.commands.entity.EntityCommand;

public class TeleportNearest extends CommandComponent {

	@Override
	protected void onRegister() {

	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
				
		EntityType type = null;
		if(this.chainer.hasData(EntityTypeLoader.ENTITY_KEY_SINGLE))
			type = this.chainer.getObject(EntityTypeLoader.ENTITY_KEY_SINGLE);
		EntityCommand chainer = (EntityCommand)this.chainer;
		List<Entity> entity;
				
		if(type == null)
			entity = chainer.getEntityManager().getNearestEntities(player, 1);
		else
			entity = chainer.getEntityManager().getNearestEntities(player, 1, type);
		
		if(entity == null || entity.isEmpty()) {
			chainer.getEntityActionMessager().noEntitiesFound(player);
			return CommandResult.SUCCESS;
		}
		
		player.teleport(entity.get(0));
		chainer.getEntityActionMessager().teleportedToEntity(player, entity.get(0).getType(), null);
		return CommandResult.SUCCESS;
	}
}
