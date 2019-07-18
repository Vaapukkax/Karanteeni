package net.karanteeni.chatar.component.hover;

import java.util.HashMap;
import java.util.Set;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * This class is responsible for returning hover basecomponent
 * to given hover texts 
 * @author Nuubles
 *
 */
public interface HoverComponent {
	public abstract HashMap<Player, BaseComponent> getHover(Player sender, Set<Player> receivers);
}
