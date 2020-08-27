import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CakeEvent implements Listener {
    private HashMap<UUID, Integer> points = new HashMap<UUID, Integer>();
    private int counter = 0;
    Cake c;


    @EventHandler
    public void onCakeInteracted (PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Action a = e.getAction();

        if (b == null || b.getType() != Material.CAKE)
            return;

        if (a != Action.RIGHT_CLICK_BLOCK)
        	return;
        
        BlockState state = b.getState();
        Cake cake = (Cake) state.getBlockData();

        counter++;

        if (!points.containsKey(p.getUniqueId())) {
            points.put(p.getUniqueId(), counter);
        } else {
            points.replace(p.getUniqueId(), counter);
        }
    }

    public HashMap<UUID, Integer> getPointsMap() {
        return points;
    }

    public HashMap<UUID, Integer> insertValues(UUID uuid, Integer value) {
        points.put(uuid, value);

        return points;
    }
}