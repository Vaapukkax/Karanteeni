package net.karanteeni.chatar.component.hover;

import java.util.HashMap;
import java.util.Set;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.component.click.ClickComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentDisplayName implements HoverComponent, ClickComponent {
	@Override
	public HashMap<Player, BaseComponent> getHover(Player sender, Set<Player> receivers) {
		// set playername
		HashMap<Player, BaseComponent> com = new HashMap<Player, BaseComponent>();
		for(Player player : receivers)
			com.put(player, new TextComponent(TextComponent.fromLegacyText(sender.getDisplayName())));
		
		return com;
	}

	
	@Override
	public HashMap<Player, String> getClick(Player sender, Set<Player> receivers) {
		// set playername
		HashMap<Player, String> com = new HashMap<Player, String>();
		for(Player player : receivers)
			com.put(player, sender.getDisplayName());
		
		return com;
	}
}
