package net.karanteeni.karanteenials.player.misc;

import java.util.Arrays;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.Karanteenials;

public class NearCommand extends BareCommand implements TranslationContainer {
	private float range = 50;
	
	public NearCommand() {
		super(Karanteenials.getPlugin(Karanteenials.class), 
				"near", 
				"/near", 
				"Show players near you and their distances", 
				Karanteenials.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
		registerConfig();
	}

	
	/**
	 * Registers config values
	 */
	private void registerConfig() {
		if(!plugin.getConfig().isSet("near-distance")) {
			plugin.getConfig().set("near-distance", range);
			plugin.saveConfig();
		}
		
		range = (float)plugin.getConfig().getDouble("near-distance");
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command arg1, String arg2, String[] args) {
		return null;
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		
		List<Player> players = Karanteenials.getEntityManager().getNearbyPlayers(player.getLocation(), range);
		Karanteenials plugin = (Karanteenials)this.plugin;
		int powerLevel = plugin.getPlayerData().getPowerLevel().getPowerLevel(player);

		// map of players and distances to them
		StringBuilder resultRows = new StringBuilder();
		String rowFormat = Karanteenials.getTranslator().getTranslation(plugin, player, "near.rows");
		int rowCount = 0;
		
		// combine the found players into a string
		for(Player p : players) {
			// don't add self to the list
			if(p.getUniqueId().equals(player.getUniqueId())) continue;
			
			// don't add gm3 or invisible players to the list
			if(!player.canSee(p) || 
					(p.getGameMode() == GameMode.SPECTATOR && player.getGameMode() != GameMode.SPECTATOR) ||
					(player.getGameMode() != GameMode.SPECTATOR && p.hasPotionEffect(PotionEffectType.INVISIBILITY))) 
				continue;
			
			// check players power level
			if(powerLevel >= plugin.getPlayerData().getPowerLevel().getPowerLevel(player)) {
				// add the found player to the string
				++rowCount;
				resultRows.append(rowFormat.replace("%player%", p.getDisplayName())
						.replace("%distance%", String.format("%.1f", player.getLocation().distance(p.getLocation()))));
				resultRows.append("\n");
			}
		}
		
		if(rowCount > 0) {
			// send the resulting rows to the player
			resultRows.substring(0, resultRows.length()-1);
			Karanteenials.getMessager().sendMessage(player, Sounds.EQUIP.get(), 
					Karanteenials.getTranslator().getTranslation(plugin, player, "near.format").replace("%rows%", resultRows.toString()));
		} else {
			// no players found, send the none found message
			Karanteenials.getMessager().sendMessage(player, Sounds.EQUIP.get(), 
					Prefix.NEUTRAL + Karanteenials.getTranslator().getTranslation(plugin, player, "near.none-found"));
		}
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Karanteenials.getTranslator().registerTranslation(plugin, "near.format", 
				"==========[ NEAR ]==========\n%rows%\n==========[ NEAR ]==========");
		
		Karanteenials.getTranslator().registerTranslation(plugin, "near.rows", "%player% : %distance% blocks");
		Karanteenials.getTranslator().registerTranslation(plugin, "near.none-found", "No players near you");
	}
}
