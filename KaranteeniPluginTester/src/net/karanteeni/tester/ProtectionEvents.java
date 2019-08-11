package net.karanteeni.tester;



import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.md_5.bungee.api.ChatColor;

public class ProtectionEvents implements Listener{
	
	/**
	 * Inventoryn avaaminen
	 * @param event
	 */
	@EventHandler
	public void onOpen(InventoryOpenEvent event)
	{
		if(event.getInventory().getHolder() != null && event.getInventory().getLocation() != null)
		{
			if ((event.getInventory().getType().equals(InventoryType.CHEST) ||
				event.getInventory().getType().equals(InventoryType.DISPENSER) ||
				event.getInventory().getType().equals(InventoryType.DROPPER) ||
				event.getInventory().getType().equals(InventoryType.FURNACE) ||
				event.getInventory().getType().equals(InventoryType.SHULKER_BOX) ||
				event.getInventory().getType().equals(InventoryType.HOPPER)))
			{
				
				//Pelaaja on op, annetaan tiedot
				String owner = Protectables.getBlockOwner(event.getInventory().getLocation().getBlock());
				
				if (!Protectables.canInteractBlock(event.getInventory().getLocation(), (Player)event.getPlayer()))
				{
					if (event.getPlayer().isOp())
					{
						if (!(owner == null))
						{
							event.getPlayer().sendMessage(ChatColor.YELLOW + "Palikan omistaa: " + ChatColor.LIGHT_PURPLE + owner);
						}
					}
					else
					{
						if (!(owner == null))
						{
							//Pelaaja ei ole op, ev�t��n avaus
							event.setCancelled(true);
							KaranteeniCore.getMessager().sendMessage(event.getPlayer(), Sounds.NO.get(), 
									Prefix.NEGATIVE + "Et omista tätä palikkaa!");
							//SendMessage.sendMessage(event.getPlayer(), PlaySound.NO, SendMessage.NEGATIVE, KMessages.lockedNotYours());
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Palikan hajottaminen
	 * @param event
	 */
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location b_loc = block.getLocation();
		String world = b_loc.getWorld().getName().toString();		
		
		if (Protectables.isProtectable(block.getType()))
		{

			boolean owner = Protectables.canAlterBlock(b_loc, player);
			String ownername = Protectables.getBlockOwner(block);
			
			if(owner)
			{
				if(!Protectables.removeProtection(block))
					event.setCancelled(true);
				return;
			}
			else
			{
				if (event.getPlayer().isOp())
				{
					if (!Protectables.removeProtection(block))
						event.setCancelled(true);
					else if (!(ownername == null))
						event.getPlayer().sendMessage(Prefix.NEUTRAL + "Hajotit juuri pelaajan �c" + ownername + "�6 palikan");
					return;
				}
				else if (!(ownername == null))
				{
					KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Et omista tätä palikkaa!");
					event.setCancelled(true);
					return;
				}

			}
		}
		else
		{
			
			//Tarkistetaan onko suojattu kyltti palikassa kiinni, jos on niin ei sallita blockin hajotusta!
			Location locations[] = new Location[5];
	    	locations[0] = new Location(block.getWorld(), b_loc.getBlockX()+1, b_loc.getBlockY(), b_loc.getZ());
			locations[1] = new Location(block.getWorld(), b_loc.getBlockX()-1, b_loc.getBlockY(), b_loc.getZ());
			locations[2] = new Location(block.getWorld(), b_loc.getBlockX(), b_loc.getBlockY(), b_loc.getZ()+1);
			locations[3] = new Location(block.getWorld(), b_loc.getBlockX(), b_loc.getBlockY(), b_loc.getZ()-1);
			locations[4] = new Location(block.getWorld(), b_loc.getBlockX(), b_loc.getBlockY()+1, b_loc.getZ());
			
			for (int i = 0; i < locations.length; i++)
			{
				if (BlockType.WALL_SIGNS.contains(locations[i].getBlock().getType()) ||
						BlockType.SIGNS.contains(locations[i].getBlock().getType()))
				{
					Sign s = (Sign) locations[i].getBlock().getState().getData();

					Block attached = locations[i].getBlock().getRelative(s.getRotation());
					Location loc2 = attached.getLocation(); 
					
					//Otetaan vain blocki jossa on kyltti kiinni, ei muita!
					if (loc2.getBlockX() == b_loc.getBlockX() &&
							loc2.getBlockY() == b_loc.getBlockY() &&
							loc2.getBlockZ() == b_loc.getBlockZ())
					{
						boolean owner = TesterMain.getDb().isOwner(player.getUniqueId().toString(), locations[i].getBlockX(), locations[i].getBlockY(), locations[i].getBlockZ(), locations[i].getWorld().getName());
						String ownername = TesterMain.getDb().getOwner(locations[i].getBlockX(), locations[i].getBlockY(), locations[i].getBlockZ(), locations[i].getWorld().getName());
						
						if(!owner)
						{
							if (player.isOp())
							{
								event.getPlayer().sendMessage(Prefix.PLUSNEGATIVE + "Rikoit juuri pelaajan §e" + ownername + "§c palikan!");
								if (!TesterMain.getDb().removeProtection(locations[i].getBlockX(), locations[i].getBlockY(), locations[i].getBlockZ(), locations[i].getWorld().getName())) {
									KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "Suojauksen poistaminen epäonnistui!");
								}
							}
							else
							{
								KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Suojauksen poistaminen epäonnistui!");
								event.setCancelled(true);
								break;
							}
						}
						else
						{
							if (!TesterMain.getDb().removeProtection(locations[i].getBlockX(), locations[i].getBlockY(), locations[i].getBlockZ(), locations[i].getWorld().getName()))
							{
								KaranteeniCore.getMessager().sendMessage(player, Sounds.ERROR.get(), Prefix.ERROR + "Virhe suojauksen poistossa!");
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Maahan laitto
	 * @param event
	 */
	@EventHandler
	public void onPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location b_loc = block.getLocation();
		String world = b_loc.getWorld().getName().toString();
		
		if (Protectables.isProtectable(block.getType()))
		{
			//Pelaaja on joko op tai survissa
			boolean enabled = true;
			Location locations[] = new Location[4];
			locations[0] = new Location(block.getWorld(), b_loc.getBlockX()+1, b_loc.getBlockY(), b_loc.getZ());
			locations[1] = new Location(block.getWorld(), b_loc.getBlockX()-1, b_loc.getBlockY(), b_loc.getZ());
			locations[2] = new Location(block.getWorld(), b_loc.getBlockX(), b_loc.getBlockY(), b_loc.getZ()+1);
			locations[3] = new Location(block.getWorld(), b_loc.getBlockX(), b_loc.getBlockY(), b_loc.getZ()-1);
			
			for(int i = 0; i < locations.length; i++)
			{
				//Chest c = (Chest)(Block)locations[i].getBlock().getState();
				//Chest _c = (Chest)((Block)block).getState();
				
				if(locations[i].getBlock().getType() == block.getType() &&
						(block.getType() == Material.CHEST ||
						block.getType() == Material.TRAPPED_CHEST)/* && c.getFacing().equals(_c.getFacing())*/)
				{
					if (!Protectables.canAlterBlock(locations[i], player) && ((player.isSneaking() && event.getBlockAgainst().equals(locations[i].getBlock())) || (!player.isSneaking() && !event.getBlockAgainst().equals(locations[i].getBlock()))) )
					{
						KaranteeniCore.getMessager().sendMessage(player, Sounds.NO.get(), Prefix.NEGATIVE + "Et omista viereistä palikkaa!");
						
						enabled = false;
						event.setCancelled(true);
						break;
					}
				}
			}
			
			if (enabled && Protectables.addProtection(b_loc, player) && player.isOp())
			{
				KaranteeniCore.getMessager().sendActionBar(player, Sounds.NONE.get(), Prefix.NEUTRAL + "Palikka on suojattu!");
			}
			
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event != null)
		{
			//Klikataan suojattuja palikoita
			if (event.hasBlock() && (BlockType.TRAPDOOR.contains(event.getClickedBlock().getType()) || 
					BlockType.FENCE_GATE.contains(event.getClickedBlock().getType())))
			{
				
				//Pelaaja on op, annetaan tiedot
				String owner = Protectables.getBlockOwner(event.getClickedBlock());
				
				if (!Protectables.canInteractBlock(event.getClickedBlock().getLocation(), event.getPlayer()))
				{
					if (event.getPlayer().isOp())
					{
						if (!(owner == null))
						{
							event.getPlayer().sendMessage(ChatColor.YELLOW + "Palikan omistaa: " + ChatColor.LIGHT_PURPLE + owner);
						}
					}
					else
					{
						if (!(owner == null))
						{
							//Pelaaja ei ole op, ev�t��n avaus
							event.setCancelled(true);
							KaranteeniCore.getMessager().sendMessage(event.getPlayer(), Sounds.NO.get(), Prefix.NEGATIVE + "Et omista tätä palikkaa!");
						}
					}
				}
			}
		}
	}
}
