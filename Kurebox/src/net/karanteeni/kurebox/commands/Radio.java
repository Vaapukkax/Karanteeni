package net.karanteeni.kurebox.commands;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.kurebox.Kurebox;

public class Radio extends CommandChainer implements TranslationContainer {

	public Radio(Kurebox plugin) {
		super(plugin, 
				"radio", 
				"/radio", 
				"Plays the radio from music menu", 
				Kurebox.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return CommandResult.NOT_FOR_CONSOLE;
		Player player = (Player)sender;
		
		// start or stop radio
		Kurebox plugin = (Kurebox)this.plugin;
		if(plugin.getMusicManager().isRadioPlaying(player)) {
			plugin.getMusicManager().stopCustomMusic(player);
			Kurebox.getMessager().sendActionBar(player, Sounds.EQUIP.get(), 
					Kurebox.getTranslator().getTranslation(plugin, player, "radio.disabled"));
		} else {
			plugin.getMusicManager().stopCustomMusic(player);
			plugin.getMusicManager().stopDiscMusic(player);
			plugin.getMusicManager().stopGameMusic(player);
			
			plugin.getMusicManager().playRadio(player);
			Kurebox.getMessager().sendActionBar(player, Sounds.EQUIP.get(), 
					Kurebox.getTranslator().getTranslation(plugin, player, "radio.enabled"));
		}
		
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Kurebox.getTranslator().registerTranslation(plugin, "radio.enabled", "Enabled radio");
		Kurebox.getTranslator().registerTranslation(plugin, "radio.disabled", "Disabled radio");
	}
}
