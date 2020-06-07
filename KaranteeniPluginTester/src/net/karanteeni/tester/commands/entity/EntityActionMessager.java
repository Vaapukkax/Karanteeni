package net.karanteeni.tester.commands.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.data.ArrayFormat;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class EntityActionMessager implements TranslationContainer {
	private final KaranteeniPlugin plugin;
	
	
	public EntityActionMessager(KaranteeniPlugin plugin) {
		this.plugin = plugin;
		registerTranslations();
	}
	
	
	/**
	 * Lists all the entities with premade commands for the player
	 * @param player
	 * @param entities
	 */
	public void listEntitiesForAction(Player player, List<Entity> entities, EntityAction action) {
		if(player == null || entities == null || action == null)
			return;
		
		// build the list base format
		String list = KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.list");
		String suggestedCommand;
		if(action == EntityAction.HEAL) {			
			list = list.replace("%action%", KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-heal"));
			suggestedCommand = "/entity heal %uuid%";
		} else if(action == EntityAction.KILL) {
			list = list.replace("%action%", KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-kill"));
			suggestedCommand = "/entity kill %uuid%";
		} else {
			list = list.replace("%action%", KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-teleport"));
			suggestedCommand = "/entity teleport %uuid%";
		}
		
		// build list header and footer from the format
		int entityIndex = list.indexOf("%entities%");
		BaseComponent[] listHeader = TextComponent.fromLegacyText(list.substring(0, entityIndex));
		BaseComponent[] listFooter = TextComponent.fromLegacyText(list.substring(entityIndex + 10, list.length()));
		
		
		// build clickable entity texts
		List<BaseComponent> entityActions = new ArrayList<BaseComponent>();
		
		for(Entity entity : entities) {
			String entityName = entity.getCustomName();
			if(entityName == null || entityName.equals(""))
				entityName = entity.getType().name();
			
			BaseComponent result = new TextComponent(TextComponent.fromLegacyText(entityName));
			result.setClickEvent(new ClickEvent(
					Action.RUN_COMMAND, 
					suggestedCommand.replace("%uuid%", entity.getUniqueId().toString())));
			entityActions.add(result);
		}
		
		// combine all the message parts into one component
		BaseComponent result = new TextComponent();
		for(BaseComponent component : listHeader)
			result.addExtra(component);
		result.addExtra(ArrayFormat.join(entityActions, ", "));
		for(BaseComponent component : listFooter)
			result.addExtra(component);
		
		// send the clickable list to the player
		player.spigot().sendMessage(result);
	}
	
	
	public void entityTypeDoesNotExist(Player player, String type) {
		player.sendMessage(Prefix.NEGATIVE + 
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.invalid-entity")
				.replace("%entity%", type));
	}
	
	
	public void noEntitiesFound(Player player) {
		player.sendMessage(Prefix.NEGATIVE + 
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.entities-not-found"));
	}
	
	
	public void noEntitiesFound(Player player, EntityType type, UUID uuid) {
		String message;
		if(uuid == null) {	
			message = KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.entities-not-found-of-type")
					.replace("%type%", type.name().toLowerCase());
		} else {
			message = KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.entity-not-found-uuid")
					.replace("%uuid%", uuid.toString());
		}
		
		player.sendMessage(Prefix.NEGATIVE + message);
	}
	
	
	public void entityKilled(Player player, UUID uuid, EntityType type) {
		player.sendMessage(Prefix.NEUTRAL + 
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-kill-result-single")
				.replace("%type%", type.name().toLowerCase())
				.replace("%uuid%", uuid.toString()));
	}
	
	
	public void entitiesKilled(Player player, int amount) {
		player.sendMessage(Prefix.NEUTRAL + 
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-kill-result")
				.replace("%amount%", Integer.toString(amount)));
	}
	
	
	public void entityTypesKilled(Player player, int amount, EntityType type) {
		player.sendMessage(Prefix.NEUTRAL + 
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-kill-result-type")
				.replace("%amount%", Integer.toString(amount))
				.replace("%type%", type.name().toLowerCase()));
	}
	
	
	public void entityHealed(Player player, UUID uuid, EntityType type) {
		player.sendMessage(Prefix.NEUTRAL +
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-heal-result-single")
				.replace("%type%", type.name().toLowerCase())
				.replace("%uuid%", uuid.toString()));
	}
	
	
	public void entitiesHealed(Player player, int amount) {
		player.sendMessage(Prefix.NEUTRAL +
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-heal-result")
				.replace("%amount%", Integer.toString(amount)));
	}
	
	
	public void entityTypesHealed(Player player, int amount, EntityType type) {
		player.sendMessage(Prefix.NEUTRAL +
				KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-heal-result-type")
				.replace("%amount%", Integer.toString(amount))
				.replace("%type%", type.name().toLowerCase()));
	}
	
	
	public void teleportedToEntity(Player player, EntityType type, UUID uuid) {
		String message;
		if(uuid == null) {
			message = KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-teleport-result")
					.replace("%type%", type.name().toLowerCase());
		} else {			
			message = KaranteeniCore.getTranslator().getTranslation(plugin, player, "entity.action-teleport-result-uuid")
					.replace("%uuid%", uuid.toString())
					.replace("%type%", type.name().toLowerCase());
		}
		
		player.sendMessage(Prefix.NEUTRAL + message);
	}
	
	
	@Override
	public void registerTranslations() { 
		KaranteeniCore.getTranslator().registerTranslation(plugin, 
				"entity.list", 
				"=====[List of entities to %action%]=====\n%entities%\n=====[List of entities to %action%]=====");
		KaranteeniCore.getTranslator().registerTranslation(plugin, 
				"entity.action-kill", 
				"kill");
		KaranteeniCore.getTranslator().registerTranslation(plugin, 
				"entity.action-heal", 
				"heal");
		KaranteeniCore.getTranslator().registerTranslation(plugin, 
				"entity.action-teleport", 
				"teleport to");
		KaranteeniCore.getTranslator().registerTranslation(plugin, 
				"entity.invalid-entity", 
				"Entity type %entity% does not exist");
		
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.entities-not-found-of-type", 
				"Could not find any entities of type %type%");
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.entities-not-found", 
				"Could not find any entities");
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.entity-not-found-uuid", 
				"Count not find entity %uuid%");
		
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-kill-result-single", 
				"Killed %uuid% which was of type %type%");
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-kill-result", 
				"Killed %amount% entities");
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-kill-result-type", 
				"Killed %amount% of type %type%");
		
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-teleport-result-uuid", 
				"Teleported to %uuid% which was of type %type%");
		KaranteeniCore.getTranslator().registerTranslation(plugin, 
				"entity.action-teleport-result-type", 
				"Teleported to entity of type %type%");
		
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-heal-result-single", 
				"Healed %uuid% which was of type %type%");
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-heal-result", 
				"Healed %amount% entities");
		KaranteeniCore.getTranslator().registerTranslation(plugin,
				"entity.action-heal-result-type", 
				"Healed %amount% entities of type %type%");
	}
	
	
	public static enum EntityAction {
		HEAL,
		KILL,
		TELEPORT;
	}
}
