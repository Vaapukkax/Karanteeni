package net.karanteeni.karanteenials.player.gamemode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;

public class GameModeLoader extends CommandLoader {
	private static final HashMap<String, GameMode> modeMap = new HashMap<String, GameMode>();
	
	public GameModeLoader(boolean before) {
		super(before);
		// add values to mode map
		for(GameMode mode : GameMode.values())
			modeMap.put(mode.name().toLowerCase(), mode);
		
		modeMap.put("0", GameMode.SURVIVAL);
		modeMap.put("1", GameMode.CREATIVE);
		modeMap.put("2", GameMode.ADVENTURE);
		modeMap.put("3", GameMode.SPECTATOR);
		modeMap.put("s", GameMode.SURVIVAL);
		modeMap.put("c", GameMode.CREATIVE);
		modeMap.put("a", GameMode.ADVENTURE);
		modeMap.put("sp", GameMode.SPECTATOR);
	}

	
	@Override
	protected void onRegister() {
		
	}

	
	@Override
	protected CommandResult runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length < 1) return CommandResult.INVALID_ARGUMENTS;
		GameMode mode = modeMap.get(args[0].toLowerCase());
		if(mode == null) return CommandResult.INVALID_ARGUMENTS;
		this.chainer.setObject("gamemode", mode);
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length != 1) return null;
		List<String> res = new ArrayList<String>();
		for(GameMode mode : GameMode.values())
			res.add(mode.name().toLowerCase());
		
		return this.filterByPrefix(res, args[0], false);
	}
}
