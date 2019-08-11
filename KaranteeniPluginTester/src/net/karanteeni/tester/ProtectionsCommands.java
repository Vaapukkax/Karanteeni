package net.karanteeni.tester;


import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

public class ProtectionsCommands {

	public void avaa(Player player)
	{
		Block b = player.getTargetBlock(null, 20);
		Location loc = b.getLocation();
		String owner = Protectables.getBlockOwner(b);
		
		if(Protectables.canAlterBlock(loc, player))
		{
			if (!(owner == null))
			{
				ArrayList<Block> pBlocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
				
				for(Block pBlock : pBlocks)
				{
					if(!Protectables.removeProtection(pBlock))
					{
						KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE PALIKAN AVAAMISESSA!");
						break;
					}
				}
				
				KaranteeniCore.getMessager().sendMessage(player, Sounds.SETTINGS.get(), Prefix.NEUTRAL + "Palikka ei ole enää suojattuna!");
			}
			else
			{
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Palikka on jo avoinna!");
			}
		}
		else{
			if (player.isOp()){
				if(!(owner == null))
				{
					ArrayList<Block> pblocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
					
					for(Block pblock : pblocks)
					{
						if(!Protectables.removeProtection(pblock))
						{
							KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE PALIKAN AVAAMISESSA!");
							break;
						}
					}
					
					KaranteeniCore.getMessager().sendMessage(player, Sounds.NOTIFICATION.get(), Prefix.NEUTRAL + "Avasit juuri pelaajan §e" + owner + "§6 palikan!");
				}
				else
				{
					KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Palikka on jo avoinna!");
				}
			}
			else
			{
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Et voi avata tätä palikkaa!!");
			}
		}
	}
	
	public void info(Player player)
	{
		if(player.isOp() || player.hasPermission("karanteeni.protectioninfo"))
		{
			Block b = player.getTargetBlock(null, 20);
			if (Protectables.isProtectable(b.getType()))
				{
					player.sendMessage("�6�m====================");
					player.sendMessage("�cOmistaja: �4"+TesterMain.getDb().getOwner(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(), b.getLocation().getWorld().getName()));
					
					ArrayList<String> m = new ArrayList<String>();
					if(TesterMain.getDb().showMember(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(), b.getLocation().getWorld().getName()) != null)
					{
						
						m.addAll(TesterMain.getDb().showMember(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(), b.getLocation().getWorld().getName()));
					
						player.sendMessage("�cOsalliset:");
						for(String member : m)
						{
							player.sendMessage("�e"+member);
						}
					}
					
					player.sendMessage("�6�m====================");
				}
		}
	}
	
	public void lukitse(Player player)
	{
		Block b = player.getTargetBlock(null, 20);
		Location loc = b.getLocation();
		String owner = Protectables.getBlockOwner(loc.getBlock());
		
		//Onko sujattava palikka
		if (Protectables.isProtectable(b.getType()))
		{
			if(owner == null)
			{
				ArrayList<Block> pblocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
				
				for(Block pblock : pblocks)
				{
					if(!Protectables.addProtection(pblock.getLocation(), player))
					{
						KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE PALIKAN SUOJAUKSESSA!");
					}
				}
				
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEUTRAL + "Suojasit palikan");
			}
			else if (owner.equals(player.getName().toString()))
			{
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Omistat jo tämän palikan!");
			}
			else
			{
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEUTRAL + "Et omista tätä palikkaa!");
			}
		}
		else
    	{
			KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEUTRAL + "Et voi suojata tätä palikkaa!");
    	}
	}
	
	public void lisaaOikeudet(Player player, String name)
	{
		Block b = player.getTargetBlock(null, 20);
		Location loc = b.getLocation();
		if (Protectables.canAlterBlock(loc, player) || player.isOp())
		{
			Player member = Bukkit.getPlayer(name);
			
			if(member == null)
			{
				
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Pelaajaa ei löydetty");
				return;
			}

			//Ota mahdollisen tuplachestin molemmat chestit
			ArrayList<Block> chestBlocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
			
			//K�y kaikki blockit l�pi
			for(Block pblock : chestBlocks)
			{
				//Lis�� suojaukseen osallinen
				if(!Protectables.addMember(pblock.getLocation(), member))
				{
					//Virhe suojaamisessa
					KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE SUOJAUKSEN LAAJENTAMISESSA!");
					return;
				}
			}
			
			//Suojaus onnistui
			KaranteeniCore.getMessager().sendMessage(player, Sounds.SETTINGS.get(), Prefix.POSITIVE + "Pelaaja §e" + member.getName().toString() + "§a on lisätty suojaukseen!");
		}
		else
		{
			KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEUTRAL + "Et omista tätä palikkaa!");
		}
	}
	
	public void poistaOikeudet(Player player, String name)
	{
		Block b = player.getTargetBlock(null, 20);
		Location loc = b.getLocation();
		Player member = Bukkit.getPlayer(name);
		
		if(name != null && !name.equals("kaikki"))
		{
			if (Protectables.canAlterBlock(loc, player) || player.isOp())
			{
				//Ota mukaan mahdolliset vierekk�iset chestit
				ArrayList<Block> cBlocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
				
				//Poista kaikista kiinniolevista blockeista pelaaja
				for(Block pblock : cBlocks)
				{
					if(!Protectables.removeMember(pblock.getLocation(), player, member))
					{
						KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE OSALLISEN POISTOSSA!");
						return;
					}
				}
				
				KaranteeniCore.getMessager().sendMessage(player, Sounds.SETTINGS.get(), Prefix.POSITIVE + "Pelaaja §e" + member.getName().toString() + "§a on poistettu suojauksesta!");
			}
			else
			{
				
				KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEUTRAL + "Et omista tätä palikkaa!");
			}
		}
		else if (name.equals("kaikki"))
		{
			if(TesterMain.getDb().deleteMember(player.getUniqueId().toString(), "kaikki", /*"kaikki",*/ loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),loc.getWorld().getName()))
			{
				ArrayList<Block> pBlocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
				
				for(Block pBlock : pBlocks)
				{
					if(!TesterMain.getDb().deleteMember(player.getUniqueId().toString(), "kaikki", /*"kaikki",*/ 
							pBlock.getX(), 
							pBlock.getY(), 
							pBlock.getZ(),
							pBlock.getWorld().getName()))
					{
						KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE JULKISEN PÄÄSYN POISTOSSA!");
						break;
					}
				}
				
				KaranteeniCore.getMessager().sendMessage(player, Sounds.SETTINGS.get(), Prefix.NEUTRAL + "Julkinen pääsy palikkaan poistettu");
			}
		}
		else
		{
			KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Pelaajaa ei löydetty");
		}
	}
	
	public void avaaKaikille(Player player)
	{
		Block b = player.getTargetBlock(null, 20);
		Location loc = b.getLocation();
		
		if (Protectables.canAlterBlock(loc, player) || player.isOp())
		{
			ArrayList<Block> pblocks = (ArrayList<Block>) KaranteeniCore.getBlockManager().getBlockTypes().getConnectedChestBlock(b);
			
			for(Block pblock : pblocks)
			{
				if(!TesterMain.getDb().addMember(player.getUniqueId().toString(), "kaikki", "kaikki", 
						pblock.getX(), 
						pblock.getY(), 
						pblock.getZ(),
						pblock.getWorld().getName()))
				{
					KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "VIRHE SUOJAUKSEN LAAJENTAMISESSA!");
					break;
				}
			}
			
			KaranteeniCore.getMessager().sendMessage(player, Sounds.SETTINGS.get(), Prefix.POSITIVE + "Kaikki pelaajat voivat nyt avata suojauksesi, mutteivat pysty lukitsemaan tai rikkomaan sitä! ");
			player.sendMessage(Prefix.POSITIVE + "Voit lukita chestin taas komennolla /-oikeudet kaikki ");
		}
		else
		{
			KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEUTRAL + "Et omista tätä palikkaa!");
		}
	}
}
