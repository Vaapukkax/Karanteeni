package net.karanteeni.christmas2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.text.Prefix;

public class Luukut extends BareCommand {

	public Luukut(KaranteeniPlugin plugin) {
		super(plugin, 
				"luukut", 
				"luukut", 
				"creates a nabi :3", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		Player player = (Player)sender;
		
		List<Integer> avaamattomat = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24));
		List<Integer> completed = plugin.getConfig().getIntegerList("players.completed."+player.getUniqueId());
		avaamattomat.removeAll(completed);
		
		if(avaamattomat.isEmpty()) {
			player.sendMessage(Prefix.PLUSPOSITIVE + "Olet avannut jo kaikki joululuukut!");
			return CommandResult.SUCCESS;
		}
		
		StringBuffer nums = new StringBuffer();
		for(int num : avaamattomat) {
			nums.append(num);
			nums.append(", ");
		}
		// delete last 2 chars
		nums.deleteCharAt(nums.length()-1);
		nums.deleteCharAt(nums.length()-1);
		
		player.sendMessage(Prefix.NEUTRAL + "Kurkistat kalenteriisi, sekä huomaat että sinulla on vielä avaamatta luukut\n" + nums.toString());
		
		return CommandResult.SUCCESS;
	}

}
