package net.karanteeni.utilika.structure.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import net.karanteeni.core.block.BlockCollection;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.event.NoActionEvent;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.utilika.Utilika;
import net.karanteeni.utilika.external.CoreProtectAccessor;
import net.karanteeni.utilika.worldguard.WorldGuardManager;

public class WandBuilder implements Listener {
	private BuildersWand wand;
	private Utilika plugin;
	private static final String EXTEND_BLOCK_LIMIT = "wand.extend-block-limit";
	private static final String TOWER_BLOCK_LIMIT = "wand.tower-block-limit";
	private static final String ILLEGAL_TYPES = "wand.illegal-types";
	private Set<Material> illegalTypes = new HashSet<Material>();
	private int extendSizeLimit = 5;
	private int towerSizeLimit = 5;
	
	public WandBuilder(Utilika plugin) {
		this.plugin = plugin;
		this.wand = new BuildersWand();
		
		if(!plugin.getConfig().isSet(EXTEND_BLOCK_LIMIT)) {
			plugin.getConfig().set(EXTEND_BLOCK_LIMIT, extendSizeLimit);
			plugin.saveConfig();
		}
		this.extendSizeLimit = plugin.getConfig().getInt(EXTEND_BLOCK_LIMIT);
		
		if(!plugin.getConfig().isSet(TOWER_BLOCK_LIMIT)) {
			plugin.getConfig().set(TOWER_BLOCK_LIMIT, towerSizeLimit);
			plugin.saveConfig();
		}
		this.towerSizeLimit = plugin.getConfig().getInt(TOWER_BLOCK_LIMIT);
		
		if(!plugin.getConfig().isSet(ILLEGAL_TYPES)) {
			plugin.getConfig().set(ILLEGAL_TYPES, Arrays.asList(
					Material.SHULKER_BOX.toString(),
					Material.BLACK_SHULKER_BOX.toString(),
					Material.BLUE_SHULKER_BOX.toString(),
					Material.BROWN_SHULKER_BOX.toString(),
					Material.CYAN_SHULKER_BOX.toString(),
					Material.GRAY_SHULKER_BOX.toString(),
					Material.GREEN_SHULKER_BOX.toString(),
					Material.LIGHT_BLUE_SHULKER_BOX.toString(),
					Material.LIGHT_GRAY_SHULKER_BOX.toString(),
					Material.LIME_SHULKER_BOX.toString(),
					Material.MAGENTA_SHULKER_BOX.toString(),
					Material.ORANGE_SHULKER_BOX.toString(),
					Material.PINK_SHULKER_BOX.toString(),
					Material.PURPLE_SHULKER_BOX.toString(),
					Material.RED_SHULKER_BOX.toString(),
					Material.WHITE_SHULKER_BOX.toString(),
					Material.YELLOW_SHULKER_BOX.toString(),
					Material.BLACK_BED.toString(),
					Material.BLUE_BED.toString(),
					Material.BROWN_BED.toString(),
					Material.CYAN_BED.toString(),
					Material.GRAY_BED.toString(),
					Material.GREEN_BED.toString(),
					Material.LIGHT_BLUE_BED.toString(),
					Material.LIGHT_GRAY_BED.toString(),
					Material.LIME_BED.toString(),
					Material.MAGENTA_BED.toString(),
					Material.ORANGE_BED.toString(),
					Material.PINK_BED.toString(),
					Material.PURPLE_BED.toString(),
					Material.RED_BED.toString(),
					Material.WHITE_BED.toString(),
					Material.YELLOW_BED.toString(),
					Material.CHEST.toString(),
					Material.BARREL.toString(),
					Material.TRAPPED_CHEST.toString(),
					Material.DROPPER.toString(),
					Material.HOPPER.toString(),
					Material.DISPENSER.toString(),
					Material.LECTERN.toString(),
					Material.BREWING_STAND.toString(),
					Material.FURNACE.toString(),
					Material.BLAST_FURNACE.toString(),
					Material.ACACIA_DOOR.toString(),
					Material.BIRCH_DOOR.toString(),
					Material.CRIMSON_DOOR.toString(),
					Material.DARK_OAK_DOOR.toString(),
					Material.IRON_DOOR.toString(),
					Material.JUNGLE_DOOR.toString(),
					Material.OAK_DOOR.toString(),
					Material.SPRUCE_DOOR.toString(),
					Material.WARPED_DOOR.toString(),
					Material.DARK_OAK_DOOR.toString(),
					Material.PLAYER_HEAD.toString(),
					Material.PLAYER_WALL_HEAD.toString()));
			plugin.saveConfig();
		}
		for(String type : plugin.getConfig().getStringList(ILLEGAL_TYPES)) {
			try {
				illegalTypes.add(Material.valueOf(type));
			} catch(Exception e) {
				Bukkit.getLogger().log(Level.CONFIG, "Invalid config value in " + ILLEGAL_TYPES);
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getHand() != EquipmentSlot.HAND)
			return;
		
		ItemStack wandItem = event.getPlayer().getInventory().getItemInOffHand();
		if(wandItem == null)
			return;
		
		if(!event.canBuild())
			return;
		
		if(Utilika.getBlockManager().isBeingProcessed(event.getBlock()))
			return;
		
		if(illegalTypes.contains(event.getBlock().getType()))
			return;
		
		// Get directions
		BlockFace placeDirection = event.getBlockAgainst().getFace(event.getBlock());
		
		if(wand.isTowerWand(wandItem)) { // tower wand place
			if(!event.getPlayer().hasPermission("utilika.builderswand.use")) {
				Utilika.getMessager().sendMessage(event.getPlayer(),
						Sounds.NO.get(),
						Prefix.NEGATIVE + Utilika.getDefaultMsgs().noPermission(event.getPlayer()));
				return;
			}

			
			Collection<Block> blocks = new LinkedList<Block>();
			Block placingAgainst = event.getBlockAgainst();
			for(int i = 0; i < towerSizeLimit; ++i) {
				placingAgainst = placingAgainst.getRelative(placeDirection);
				blocks.add(placingAgainst);
			}
			placeBlocks(event.getBlock().getType(), blocks, event.getPlayer(), true, event.getBlockPlaced().getBlockData());
		} else if(wand.isExtendWand(wandItem)) { // extend wand place
			if(!event.getPlayer().hasPermission("utilika.builderswand.use")) {
				Utilika.getMessager().sendMessage(event.getPlayer(),
						Sounds.NO.get(),
						Prefix.NEGATIVE + Utilika.getDefaultMsgs().noPermission(event.getPlayer()));
				return;
			}

			BlockCollection.Axis axis = BlockCollection.Axis.fromBlockFace(placeDirection);
			BlockCollection shapedAgainst = BlockCollection.scanBlockTypesFlatByDistance(event.getBlockAgainst(), axis, extendSizeLimit);
			
			switch(placeDirection) {
			case NORTH:
				shapedAgainst.shiftSelection(0, 0, -1);
				break;
			case SOUTH:
				shapedAgainst.shiftSelection(0, 0, 1);
				break;
			case EAST:
				shapedAgainst.shiftSelection(1, 0, 0);
				break;
			case WEST:
				shapedAgainst.shiftSelection(-1, 0, 0);
				break;
			case UP:
				shapedAgainst.shiftSelection(0, 1, 0);
				break;
			case DOWN:
				shapedAgainst.shiftSelection(0, -1, 0);
				break;
			default:
				return;
			}
			
			//shapedAgainst = shapedAgainst.removeBlockNotOfType(event.getBlock().getType());
			shapedAgainst.scanAndFilterByUnplaceableFlatByDistance(axis, extendSizeLimit);
			placeBlocks(event.getBlock().getType(), shapedAgainst.getBlocks(), event.getPlayer(), true, event.getBlockPlaced().getBlockData());
		} // Nothingnesswand does nothing
	}
	
	
	@EventHandler
	public void levitationWandPlace(NoActionEvent event) {
		if(event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		ItemStack wandItem = event.getPlayer().getInventory().getItemInOffHand();
		if(wandItem == null)
			return;
		
		// check if player is holding a block
		if(event.getItem() == null || !event.getItem().getType().isBlock())
			return;
		
		
		if(event.getBlock() != null) {
			if(illegalTypes.contains(event.getBlock().getType()))
				return;
		} else {
			ItemStack handItem = event.getItem();
			if(handItem == null)
				return;
			if(illegalTypes.contains(handItem.getType()))
				return;
		}
		
		if(!wand.isLevitationWand(wandItem))
			return;

		if(!event.getPlayer().hasPermission("utilika.builderswand.use")) {
			Utilika.getMessager().sendMessage(event.getPlayer(),
					Sounds.NO.get(),
					Prefix.NEGATIVE + Utilika.getDefaultMsgs().noPermission(event.getPlayer()));
			return;
		}
		
		Location blockLocation = event.getPlayer().getEyeLocation().add(event.getPlayer().getEyeLocation().getDirection().multiply(3));

		if(blockLocation.getBlockY() > 255 && blockLocation.getBlockY() < 0)
			return;
		
		List<Block> blocks = new ArrayList<Block>();
		blocks.add(blockLocation.getBlock());
		placeBlocks(event.getItem().getType(), blocks, event.getPlayer(), false, null);
	}
	
	
	@EventHandler(ignoreCancelled = false)
	public void toggleWand(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND)
			return;
		if(event.getAction() != Action.LEFT_CLICK_AIR)
			return;
		if(!event.getPlayer().isSneaking())
			return;
		
		ItemStack wandItem = event.getPlayer().getInventory().getItemInOffHand();
		if(wandItem == null)
			return;

		if(wand.getWandType(wandItem) == null)
			return;
		
		event.getPlayer().getInventory().setItemInOffHand(wand.toggleWand(wandItem));
		Utilika.getMessager().sendActionBar(event.getPlayer(), Sounds.CLICK_CHANGE.get(), "§6Changed wand type");
	}
	
	
	/**
	 * Makes the player place the specified blocks safely to the ground
	 * @param type
	 * @param blocks
	 * @param player
	 */
	private void placeBlocks(Material type, Collection<Block> blocks, Player player, boolean offSetByOne, BlockData dataToCopy) {
		if(blocks.isEmpty())
			return;
		
		filterBlocks(player, blocks);
		
		KPlayer kp = KPlayer.getKPlayer(player);
		int itemCount = kp.getAmountOfType(type);
		if(player.getGameMode() == GameMode.CREATIVE)
			itemCount = Integer.MAX_VALUE;
		int blocksPlaced = offSetByOne ? 1 : 0;
		
		for(Block block : blocks) {
			if(!BlockType.REPLACEABLE.contains(block.getType()))
				continue;
			if(++blocksPlaced > itemCount)
				break;
			
			block.setType(type);
			if(dataToCopy != null)
				block.setBlockData(dataToCopy);
			block.getState().update();

			// Log to coreprotect
			logBlock(player, block.getType(), block.getLocation(), block.getBlockData());
			displayBlockParticle(block);
		}
		
		if(player.getGameMode() != GameMode.CREATIVE)
			kp.removeItems(type, blocksPlaced);
		
		// sound
		if(blocksPlaced > 0) {
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LODESTONE_PLACE, SoundCategory.BLOCKS, 1f, 0.5f);
			// invtweaks
			ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
			if(item == null || item.getAmount() == 0) {
				plugin.getInventoryUtilities().searchAndReplace(player, player.getInventory().getHeldItemSlot(), type, true);
			}
		}
	}
	
	
	/**
	 * Filters blocks from the collection to which the player can't place anything
	 * according to worldguard
	 * @param player
	 * @param blocks
	 */
	private void filterBlocks(Player player, Collection<Block> blocks) {
		WorldGuardManager wgm = plugin.getWorldGuardManager();
		if(wgm != null) {
			wgm.filterUnbuildable(player, blocks);
		}
	}
	
	
	/**
	 * Logs the placed blocks to coreprotect
	 * @param player
	 * @param blocks
	 */
	private void logBlock(Player player, Material material, Location location, BlockData blockData) {
		CoreProtectAccessor accessor = plugin.getCoreProtectAccessor();
		if(accessor != null) {
			accessor.registerBlockPlacement(player, material, location, blockData);
		}
	}
	
	
	private void displayBlockParticle(Block block) {
		block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 5, block.getBlockData());
	}
}
