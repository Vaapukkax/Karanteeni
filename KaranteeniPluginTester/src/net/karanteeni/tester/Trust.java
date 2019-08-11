package net.karanteeni.tester;


import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class Trust implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		new Thread() {
			public void run() {
				if(!(sender instanceof Player))
				{
					sender.sendMessage("vain pelaaja voi suorittaa komennon");
					return;
				}
				
				if(args.length < 2)
				{
					KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + "/trust <suojauksen nimi> <nimi nimi...> (suojauksen nimen näet /rg i ollessa sen sisällä)");
					return;
				}
				
				Player player = (Player)sender;
				String regionName = args[0];
				
				//Ota pelaajan kohdalta regionit
				ProtectedRegion region = RegionStates.getRegionByName(player.getLocation(), regionName);
				
				//Ei ole suojauksia
				if(region == null)
				{
					KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + "Et ole suojauksen sisällä!");
					
					return;
				}
				
				//Onko pelaaja regionin owner
				if(!RegionStates.isRegionOwner(region, player))
				{
					KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + "Et omista suojausta nimeltä §e"+regionName+"§c!");
					return;
				}
				
				/**
				 * Lis�� kaikki pelaajat suojaukseen
				 */
				for(int i = 1; i < args.length; ++i)
				{
					UUID uuid = KaranteeniCore.getPlayerHandler().getUUID(args[i]);
					
					if(uuid == null)
					{
						KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE+ "Pelaajaa nimeltä §e"+args[i]+"§c ei löydetty!");
						return;
					}
					
					if(RegionStates.addRegionMember(region, uuid))
					{
						KaranteeniCore.getMessager().sendMessage(sender, Sounds.EQUIP.get(), Prefix.POSITIVE+ "Pelaaja §e"+args[i]+"§a lisätty suojaukseen §e"+region.getId()+"§a!");
					}
					else
					{
						KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE+ "Pelaaja §e"+args[i]+"§c on jo suojauksessa §e"+region.getId()+"§c!");
					}
				}
			}
		}.start();
		
		return true;
	}

}