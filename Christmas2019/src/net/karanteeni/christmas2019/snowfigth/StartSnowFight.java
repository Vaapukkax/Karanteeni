package net.karanteeni.christmas2019.snowfigth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class StartSnowFight extends BareCommand {

	private SnowFightTimer timer = null;
	
	public StartSnowFight(KaranteeniPlugin plugin) {
		super(plugin, 
				"startsnowfight", 
				"yeet", 
				"yeets beets", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
	}

	
	@Override
	public List<String> autofill(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!sender.isOp())
			return CommandResult.NO_PERMISSION;
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		if(timer != null && timer.isOnGoing()) {
			sender.sendMessage(Prefix.NEGATIVE + "Matsi on jo päällä!");
			return CommandResult.SUCCESS;
		}
		
		timer = new SnowFightTimer(Christmas.getPlugin(Christmas.class));
		
		return CommandResult.SUCCESS;
	}

}
