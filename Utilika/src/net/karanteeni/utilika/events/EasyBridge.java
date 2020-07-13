package net.karanteeni.utilika.events;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BlockIterator;
import net.karanteeni.core.event.NoActionEvent;
import net.karanteeni.utilika.Utilika;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.EnumDirection;
import net.minecraft.server.v1_16_R1.EnumHand;
import net.minecraft.server.v1_16_R1.EnumInteractionResult;
import net.minecraft.server.v1_16_R1.ItemActionContext;
import net.minecraft.server.v1_16_R1.ItemStack;
import net.minecraft.server.v1_16_R1.MinecraftKey;
import net.minecraft.server.v1_16_R1.MovingObjectPositionBlock;
import net.minecraft.server.v1_16_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_16_R1.SoundEffect;
import net.minecraft.server.v1_16_R1.SoundEffectType;
import net.minecraft.server.v1_16_R1.Vec3D;
import net.minecraft.server.v1_16_R1.World;


public class EasyBridge implements Listener {
	private boolean cancelNext = false;
	private boolean running = false;
	
	@EventHandler
	public void bridgeBlockPlace_(NoActionEvent event) {
		// use this to prevent infinite loop
		if(cancelNext || running || 
				event.getPlayer().getGameMode() == GameMode.CREATIVE || 
				event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
			cancelNext = false;
			running = false;
			return;
		}
		
		// check if player is trying to place a block
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		// check if the item placed is a block
		if(event.getItem() == null || !event.getItem().getType().isBlock() || !event.getItem().getType().isOccluding()) return;
		
		// get block iterator
		BlockIterator iter = new BlockIterator(event.getPlayer().getWorld(), 
				event.getPlayer().getEyeLocation().toVector(), 
				event.getPlayer().getLocation().getDirection(), 0D, 3);
		
		Block placeLocation = null;
		Block placedAgainst = null;
		BlockFace face = null;
		int playerLevel = event.getPlayer().getLocation().getBlockY()-1; // get the blocklevel player is standing on
		
		// get the block player is trying to place to
		while(iter.hasNext() && placeLocation == null) {
			Block block = iter.next();
			
			// if the block can be built upon skip it
			if(block.getType().isSolid())
				continue;
			
			// if the block is not on the level skip it
			if(block.getLocation().getBlockY() != playerLevel)
				continue;
			
			// check if there is any solid blocks next to the block placed
			Block rel = block.getRelative(BlockFace.EAST);
			placeLocation = rel;
			face = BlockFace.WEST;
			if(!rel.getType().isSolid()) {
				rel = block.getRelative(BlockFace.WEST);
				placeLocation = rel;
				face = BlockFace.EAST;
				if(!rel.getType().isSolid()) {
					rel = block.getRelative(BlockFace.NORTH);
					placeLocation = rel;
					face = BlockFace.SOUTH;
					if(!rel.getType().isSolid()) {
						rel = block.getRelative(BlockFace.SOUTH);
						placeLocation = rel;
						face = BlockFace.NORTH;
						if(!rel.getType().isSolid()) {
							placeLocation = null;
							continue;
						}
					}
				}
			}
			
			// relative block is not set
			placeLocation = block;
			placedAgainst = rel;
		}
		
		// check if the location was found
		if(face == null || placeLocation == null)
			return;
		
		// check if player can build here, if not then return
		if(Utilika.getPlugin(Utilika.class).getWorldGuardManager() != null)
		if(!Utilika.getPlugin(Utilika.class).getWorldGuardManager().canBuild(event.getPlayer(), placeLocation.getLocation()))
			return;
		
		EnumDirection direction = EnumDirection.valueOf(face.name());
		EnumHand hand = null;
		if(event.getHand() == EquipmentSlot.HAND)
			hand = EnumHand.MAIN_HAND;
		else if(event.getHand() == EquipmentSlot.OFF_HAND)
			hand = EnumHand.OFF_HAND;
		else
			return;
		
		// try not to change item count in creative
		/*org.bukkit.inventory.ItemStack item = null;
		if(hand == EnumHand.MAIN_HAND)
			item = event.getPlayer().getInventory().getItemInMainHand();
		else
			item = event.getPlayer().getInventory().getItemInOffHand();
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) {			
			item.setAmount(item.getAmount()+1);
			event.getPlayer().updateInventory();
		}*/
		
		// build the actioncontext
		ItemActionContext actionContext = getActionContext(event.getPlayer(), placedAgainst, direction, hand);
		// failed to create actioncontext
		if(actionContext == null)
			return;
		
		// place the block at players hand
		ItemStack nmsItem = null;
		nmsItem = CraftItemStack.asNMSCopy(event.getItem());
		
		running = true;
		EnumInteractionResult result = nmsItem.placeItem(actionContext, null);
		running = false;
		
		// if blockplace failed prevent next place
		if(result != EnumInteractionResult.SUCCESS) {
			cancelNext = true;
		} else {
			playSound(placeLocation);
			event.getPlayer().updateInventory();
		}
	}
	
	//@EventHandler
	public void bridgeBlockPlace(NoActionEvent event) {
		// use this to prevent infinite loop
		if(cancelNext || running) {
			cancelNext = false;
			running = false;
			return;
		}
		
		// check if player is trying to place a block
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		// check if the item placed is a block
		if(event.getItem() == null || !event.getItem().getType().isBlock() || !event.getItem().getType().isOccluding()) return;
		
		// get the block at players feet
		Block playerBase = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
		
		// check if the block below players feet is air. if not return
		if(!playerBase.getType().isSolid()) return;
		
		// get block iterator
		BlockIterator iter = new BlockIterator(event.getPlayer().getWorld(), 
				event.getPlayer().getEyeLocation().toVector(), 
				event.getPlayer().getLocation().getDirection(), 0D, 3);
		
		Block placeLocation = null;
		BlockFace face = null;
		
		// loop the blocks player is facing
		while(iter.hasNext()) {
			Block block = iter.next();
			// if the block can be built upon skip it
			if(block.getType().isSolid())
				continue;
			
			face = playerBase.getFace(block);
			// check if the block is next to the block player is on top of
			if(face == BlockFace.EAST || face == BlockFace.NORTH || face == BlockFace.SOUTH || face == BlockFace.WEST) {
				placeLocation = block;
				break;
			} else {
				face = null;
			}
		}
		
		// if no face found return
		if(face == null) return;
		
		// check if player can build here, if not then return
		if(Utilika.getPlugin(Utilika.class).getWorldGuardManager() != null)
		if(!Utilika.getPlugin(Utilika.class).getWorldGuardManager().canBuild(event.getPlayer(), placeLocation.getLocation()))
			return;
		
		// get the correct direction
		EnumDirection direction = EnumDirection.valueOf(face.name());
		
		// get the hand
		EnumHand hand = null;
		if(event.getHand() == EquipmentSlot.HAND)
			hand = EnumHand.MAIN_HAND;
		else if(event.getHand() == EquipmentSlot.OFF_HAND)
			hand = EnumHand.OFF_HAND;
		else
			return;
		
		// try not to change item count in creative
		/*org.bukkit.inventory.ItemStack item = null;
		if(hand == EnumHand.MAIN_HAND)
			item = event.getPlayer().getInventory().getItemInMainHand();
		else
			item = event.getPlayer().getInventory().getItemInOffHand();
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE) {			
			item.setAmount(item.getAmount()+1);
			event.getPlayer().updateInventory();
		}*/
		
		// build the actioncontext
		ItemActionContext actionContext = getActionContext(event.getPlayer(), playerBase, direction, hand);
		// failed to create actioncontext
		if(actionContext == null)
			return;
		
		// place the block at players hand
		ItemStack nmsItem = null;
		nmsItem = CraftItemStack.asNMSCopy(event.getItem());
		
		running = true;
		EnumInteractionResult result = nmsItem.placeItem(actionContext, null);
		running = false;
		
		// if blockplace failed prevent next place
		if(result != EnumInteractionResult.SUCCESS) {
			cancelNext = true;
		} else {
			playSound(placeLocation);
			event.getPlayer().updateInventory();
			swingHand(event.getPlayer(), event.getHand() == EquipmentSlot.HAND);
		}
	}
	
	
	/**
	 * Make the player swing their hand
	 * @param player player whose arm is swung
	 */
	private void swingHand(Player player, boolean mainHand) {
		EntityPlayer p = ((CraftPlayer)player).getHandle();
		for(Player p2 : Bukkit.getOnlinePlayers()) {
			EntityPlayer p_ = ((CraftPlayer)p2).getHandle();
			// check if the other player can see the player who is swinging their hand
			if(!p2.canSee(player)) continue;
			
			p_.playerConnection.sendPacket(new PacketPlayOutAnimation(p, mainHand?0:3));			
		}
	}
	
	
	/**
	 * Play the placement sound for the placement
	 * @param world
	 * @param block
	 */
	private void playSound(Block block) {
		Sound sound = null; 
		
		try {
            World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
            net.minecraft.server.v1_16_R1.Block nmsBlock = nmsWorld.getType(new BlockPosition(block.getX(), block.getY(), block.getZ())).getBlock();
            SoundEffectType soundEffectType = nmsBlock.getStepSound(nmsBlock.getBlockData());

            //Field breakSound = SoundEffectType.class.getDeclaredField("y");
            Field placeSound = SoundEffectType.class.getDeclaredField("A");
            placeSound.setAccessible(true);
            SoundEffect nmsSound = (SoundEffect) placeSound.get(soundEffectType);

            //Field keyField = SoundEffect.class.getDeclaredField("a");
            Field keyField = SoundEffect.class.getDeclaredField("a");
            keyField.setAccessible(true);
            MinecraftKey nmsString = (MinecraftKey) keyField.get(nmsSound);

            sound = Sound.valueOf(nmsString.getKey().replace(".", "_").toUpperCase());
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
		
		if(sound == null)
			return;
		block.getWorld().playSound(block.getLocation(), sound, SoundCategory.BLOCKS, 1f, 1.0f);
	}
	
	
	/**
	 * Builds an ItemActionContext
	 * @param player
	 * @param block
	 * @param direction
	 * @param hand
	 * @return
	 */
	private ItemActionContext getActionContext(Player player, Block block, EnumDirection direction, EnumHand hand) {
		try {
			EntityHuman entityHuman = (EntityHuman)player.getClass().getMethod("getHandle").invoke(player);
			BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
			MovingObjectPositionBlock objectPosition = new MovingObjectPositionBlock(new Vec3D(0,0,0), direction, position, false);
			return new ItemActionContext(entityHuman, hand, objectPosition);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
}
