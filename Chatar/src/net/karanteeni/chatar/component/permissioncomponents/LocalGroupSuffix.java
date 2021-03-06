package net.karanteeni.chatar.component.permissioncomponents;

import java.util.HashMap;
import java.util.Set;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.component.ChatComponent;
import net.karanteeni.chatar.component.hover.HoverComponent;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class LocalGroupSuffix extends ChatComponent implements HoverComponent {
	KaranteeniPerms perms;
	public LocalGroupSuffix(KaranteeniPerms plugin, String key) {
		super(key);
		perms = plugin;
	}

	
	@Override
	public HashMap<Player, BaseComponent> getHover(Player sender, Set<Player> rec) {
		Group group = perms.getPermissionPlayer(sender.getUniqueId()).getGroup();
		HashMap<Player, BaseComponent> comps = new HashMap<Player, BaseComponent>();
		
		// put the translated group names into the map
		for(Player player : rec)
			comps.put(player, new TextComponent(TextComponent.fromLegacyText(group.getPrefix(player, false))));
		
		return comps;
	}

	
	@Override
	public HashMap<Player, TextComponent> getChatText(Player sender, Set<Player> rec) {
		Group group = perms.getPermissionPlayer(sender.getUniqueId()).getGroup();
		HashMap<Player, TextComponent> comps = new HashMap<Player, TextComponent>();
		
		// put the translated group names into the map
		for(Player player : rec)
			comps.put(player, new TextComponent(TextComponent.fromLegacyText(group.getPrefix(player, false))));
		return comps;
	}
}
