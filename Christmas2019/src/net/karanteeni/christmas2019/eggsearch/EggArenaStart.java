package net.karanteeni.christmas2019.eggsearch;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class EggArenaStart extends CommandComponent {

	@Override
	protected void onRegister() {
		
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp())
			return CommandResult.NO_PERMISSION;
		
		Runnable starter = new Runnable() {
			int countDownTimer = 3;
			@Override
			public void run() {
				boolean shouldStart = false;
				
				//Toistetaan kaikille pelaajille countdown ääni
				for(Player p : Bukkit.getOnlinePlayers()) {
					
					if(countDownTimer != 0 && countDownTimer < 4)
						Christmas.getMessager().sendTitle(0f, 0.3f, 1.2f, p, "§f>  §6§l"+countDownTimer+"§f  <", "", Sounds.COUNTDOWN.get());
					else if(countDownTimer == 0)
					{
						shouldStart = true;
					}
				}
				
				if(shouldStart) {
					if(!Christmas.getInstance().getGameState().startGame()) {
						for(Player p : Bukkit.getOnlinePlayers())
						Christmas.getMessager().sendTitle(0f, 0.3f, 10f, p, "§f>  §4§lVirhe§f  <", "§cPelin aloittamisessa tapahtui odottamaton virhe", Sounds.COUNTDOWN_STOP.get());
						if(Christmas.getInstance().getGameState().getEggs().size() == 0) {
							sender.sendMessage(Prefix.NEGATIVE + "Peliä ei voitu aloittaa: ei rekisteröityjä munia");
						} else if(Christmas.getInstance().getGameState().isEditOngoing()) {
							sender.sendMessage(Prefix.NEGATIVE + "Peliä ei voitu aloittaa: joku muokkaa areenaa");
						} else if(Christmas.getInstance().getGameState().isGameOngoing()) {
							sender.sendMessage(Prefix.NEGATIVE + "Peliä ei voitu aloittaa: peli on jo käynnissä");
						} else {
							sender.sendMessage(Prefix.ERROR + "Sisäinen virhe pelin aloittamisessa");
						}
					} else {
						for(Player p : Bukkit.getOnlinePlayers())
						Christmas.getMessager().sendTitle(0f, 0.3f, 0.8f, p, "§f>  §6§lKerää munia!§f  <", "", Sounds.COUNTDOWN_STOP.get());
					}
				}
				
				//vähennetään ajastinta
				countDownTimer--;
			}
			
		};
		
		Bukkit.getScheduler().runTask(Christmas.getInstance(), starter);
		Bukkit.getScheduler().runTaskLater(Christmas.getInstance(), starter, 20);
		Bukkit.getScheduler().runTaskLater(Christmas.getInstance(), starter, 40);
		Bukkit.getScheduler().runTaskLater(Christmas.getInstance(), starter, 60);
		
		return CommandResult.SUCCESS;
	}
}
