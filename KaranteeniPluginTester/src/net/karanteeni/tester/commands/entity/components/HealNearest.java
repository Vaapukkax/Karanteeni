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

public class HealNearest extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		int count = 1;
		EntityType type = null;
		Player player = (Player)sender;
		
		if(this.chainer.hasData(EntityCommand.ENTITY_COUNT))
			count = this.chainer.getObject(EntityCommand.ENTITY_COUNT);		
		if(this.chainer.hasData(EntityTypeLoader.ENTITY_KEY_SINGLE))
			type = this.chainer.getObject(EntityTypeLoader.ENTITY_KEY_SINGLE);
		EntityCommand chainer = (EntityCommand)this.chainer;
		
		List<Entity> healed;
		if(type == null) {
			healed = chainer.getEntityManager().healNearestEntities(player, count);
		} else {
			healed = chainer.getEntityManager().healNearestEntities(player, count, type);
		}
		
		if(healed != null && healed.size() != 0) {
			if(type == null)
				chainer.getEntityActionMessager().entitiesHealed(player, healed.size());
			else
				chainer.getEntityActionMessager().entityTypesHealed(player, healed.size(), type);
		} else {
			if(type == null)
				chainer.getEntityActionMessager().noEntitiesFound(player);
			else
				chainer.getEntityActionMessager().noEntitiesFound(player, type, null);
		}
		
		return CommandResult.SUCCESS;
	}

}
