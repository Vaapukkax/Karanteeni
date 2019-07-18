package net.karanteeni.chatar.component.click;

import java.util.HashMap;
import java.util.Set;
import org.bukkit.entity.Player;

/**
 * This class is responsible for returning click basecomponent
 * to given click texts 
 * @author Nuubles
 *
 */
public interface ClickComponent {
	public abstract HashMap<Player, String> getClick(Player sender, Set<Player> receivers);
}
