package net.karanteeni.tester;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class ProtectionsCMD implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		
		if (sender instanceof Player && 
    			(label.equalsIgnoreCase("avaa") ||
				label.equalsIgnoreCase("lukitse") ||
				label.equalsIgnoreCase("+oikeudet") ||
				label.equalsIgnoreCase("-oikeudet") ||
				label.equalsIgnoreCase("suojaus") ||
				label.equalsIgnoreCase("avaakaikille")))
    	{
	    	Player player = (Player)sender;
			
	    	ProtectionsCommands prot = new ProtectionsCommands();
	    	
	    	
	    	
	    	//KOMENTO /AVAA
	    	if (command.getLabel().equalsIgnoreCase("avaa"))
	    	{
	    		prot.avaa(player);
	    	}
	    	
	    	//KOMENTO /LUKITSE
	    	else if (command.getLabel().equalsIgnoreCase("lukitse"))
	    	{
	    		prot.lukitse(player);
	    	}
	    	//KOMENTO +OIKEUDET
	    	else if (command.getLabel().equalsIgnoreCase("+oikeudet"))
	    	{
	    		if(args.length > 0)
	    		{
		    		prot.lisaaOikeudet(player, args[0]);
	    		}
	    		else
	    		{
	    			KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + "/+oikeudet (nimi)");
	    		}
	    	}
	    	
	    	//KOMENTO -OIKEUDET
	    	else if (command.getLabel().equalsIgnoreCase("-oikeudet"))
	    	{
	    		if(args.length > 0)
	    		{
		    		prot.poistaOikeudet(player, args[0]);
	    		}
	    		else
	    		{
	    			KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), Prefix.NEGATIVE + "/-oikeudet (nimi)");
	    		}
	    	}
	    	
	    	//KOMENTO AVAAKAIKILLE
	    	else if (command.getLabel().equalsIgnoreCase("avaakaikille"))
	    	{
	    		prot.avaaKaikille(player);
			}
	    	
	    	//KOMENTO /SUOJAUS
	    	if (command.getLabel().equalsIgnoreCase("suojaus"))
	    	{
	    		prot.info(player);
	    	}
    	}
		
		return true;
	}

}
