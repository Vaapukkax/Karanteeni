package net.karanteeni.chatar.component.preset;

import java.util.HashMap;
import java.util.Set;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.component.ChatComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerComponent extends ChatComponent {

	public PlayerComponent(String key) {
		super(key);
	}

	
	@Override
	public HashMap<Player, TextComponent> getChatText(Player sender, Set<Player> receivers) {
		HashMap<Player, TextComponent> map = new HashMap<Player, TextComponent>();
		for(Player player : receivers)
			map.put(player, new TextComponent(TextComponent.fromLegacyText(sender.getDisplayName())));
		return map;
	}
}
