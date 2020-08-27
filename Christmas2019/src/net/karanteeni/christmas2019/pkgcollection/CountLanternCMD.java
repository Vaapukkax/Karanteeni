package net.karanteeni.christmas2019.pkgcollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;

public class CountLanternCMD extends BareCommand {

	public CountLanternCMD(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage, List<String> params) {
		super(plugin, command, usage, description, permissionMessage, params);
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// 
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		PriorityQueue<Cluster> players = new PriorityQueue<Cluster>((x, y) -> y.key - x.key);
		
		HashMap<Integer, Cluster> map = new HashMap<Integer, Cluster>();		
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission("karanteenials.player.gamemode.spectator.self"))
				continue;
			int count = 0;

			for(ItemStack item : player.getInventory().getContents())
			if(item != null && 
				item.getItemMeta().getLore() != null && 
				item.getItemMeta().getLore().size() == 1) {
				if(item.getItemMeta().getLore().get(0).equals("§aVihreä paketti"))
					count += item.getAmount() * 3;
				if(item.getItemMeta().getLore().get(0).equals("§cPunainen paketti"))
					count += item.getAmount() * 2;
				if(item.getItemMeta().getLore().get(0).equals("§bSininen paketti"))
					count += item.getAmount() * 1;
				
				// remove items from inventory
				player.getInventory().remove(item);
			}
			
			if(map.containsKey(count)) {
				map.get(count).players.add(player);
			} else {
				Cluster cluster = new Cluster(count);
				cluster.players.add(player);
				players.add(cluster);
				map.put(count, cluster);
			}
		}
		
		int pos = 1;
		StringBuffer str = new StringBuffer("");
		while(!players.isEmpty()) {
			Cluster cluster = players.poll();
			for(Player player : cluster.players) {
				str.insert(0, "§b" + pos + ": §e" + player.getName() + "§b Pakettipisteet: §e" + cluster.key + "\n");
				//sender.sendMessage("§6" + pos + ": §e" + player.getName() + "§6 Lanterns: §e" + cluster.key);
			}
			pos += cluster.players.size();
		}
		
		sender.sendMessage(str.substring(0, str.length()-1).toString());
		
		return CommandResult.SUCCESS;
	}

	
	class Cluster implements Comparable<Cluster> {
		Integer key;
		List<Player> players = new ArrayList<Player>();
		
		public Cluster(int key) {
			this.key = key;
		}
		
		
		@Override
		public int compareTo(Cluster o) {
			return key.compareTo(o.key);
		}
	}
}
