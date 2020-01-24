package net.karanteeni.core.block.executable;

import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.QueryState;

public abstract class ActionBlock {
	private Block block;		// block with the executable function
	private String permission; 	// possible permission needed to use this block
	private UUID uuid;
	public static final String ACTION_BLOCK_ID = "kc_acb"; 
	
	/**
	 * Creates an actionblock with specified UUID and permission.
	 * @param block
	 * @param uuid
	 * @param permission
	 */
	public ActionBlock(Block block, UUID uuid, String permission) throws IllegalArgumentException {
		// check that the block is not already in use
		if(!block.getMetadata(ACTION_BLOCK_ID).isEmpty())
			throw new IllegalArgumentException("Created block at " + block.getLocation().toString() + " already at use");
		this.block = block;
		block.setMetadata(ACTION_BLOCK_ID, new FixedMetadataValue(KaranteeniCore.getPlugin(KaranteeniCore.class), uuid.toString()));
		this.uuid = uuid;
		this.permission = permission;
	}
	
	
	/**
	 * Create a block executable object. No other constructors will be used.
	 * You may override this constructor, BUT NOT MAKE OTHER ONES
	 * @param block block this executable uses
	 */
	public ActionBlock(Block block, String permission) throws IllegalArgumentException {
		if(!block.getMetadata(ACTION_BLOCK_ID).isEmpty())
			throw new IllegalArgumentException("Created block at " + block.getLocation().toString() + " already at use");
		this.block = block;
		this.uuid = UUID.randomUUID();
		block.setMetadata(ACTION_BLOCK_ID, new FixedMetadataValue(KaranteeniCore.getPlugin(KaranteeniCore.class), uuid.toString()));
		this.permission = permission;
	}
	
	
	/**
	 * Creates an actionblock with specified UUID.
	 * @param block
	 * @param uuid
	 */
	public ActionBlock(Block block, UUID uuid) throws IllegalArgumentException {
		if(!block.getMetadata(ACTION_BLOCK_ID).isEmpty())
			throw new IllegalArgumentException("Created block at " + block.getLocation().toString() + " already at use");
		this.block = block;
		block.setMetadata(ACTION_BLOCK_ID, new FixedMetadataValue(KaranteeniCore.getPlugin(KaranteeniCore.class), uuid.toString()));
		this.uuid = uuid;
	}
	
	
	/**
	 * Create a block executable object. No other constructors will be used.
	 * You may override this constructor, BUT NOT MAKE OTHER ONES
	 * @param block block this executable uses
	 */
	public ActionBlock(Block block) throws IllegalArgumentException {
		if(!block.getMetadata(ACTION_BLOCK_ID).isEmpty())
			throw new IllegalArgumentException("Created block at " + block.getLocation().toString() + " already at use");
		this.block = block;
		this.uuid = UUID.randomUUID();
		block.setMetadata(ACTION_BLOCK_ID, new FixedMetadataValue(KaranteeniCore.getPlugin(KaranteeniCore.class), uuid.toString()));
	}
	
	
	/**
	 * Get the UUID of this actionblock
	 * @return uuid of this actionblock
	 */
	public UUID getUUID() {
		return this.uuid;
	}
	
	
	/**
	 * Register this object to begin receiving actions
	 * @return true if registration successful, false otherwise
	 */
	public boolean register() {
		return KaranteeniCore.getActionBlockManager().registerBlock(this);
	}
	
	
	/**
	 * Unregister this object and stop receiving actions
	 * @return true if unregistered, false otherwise
	 */
	public boolean unregister() {
		// remove metadata
		block.removeMetadata(ACTION_BLOCK_ID, KaranteeniCore.getPlugin(KaranteeniCore.class));
		return KaranteeniCore.getActionBlockManager().unregisterBlock(this);
	}
	
	
	/**
	 * Check if this block is already registered
	 * @return true if registered, false otherwise
	 */
	public boolean isRegistered() {
		return KaranteeniCore.getActionBlockManager().isBlockRegistered(this);
	}
	
	
	/**
	 * Saves the block to the database
	 * @return state of the addition
	 */
	public QueryState save() {
		return KaranteeniCore.getActionBlockManager().saveActionBlock(this);
	}
	
	
	/**
	 * Removes this block from the database
	 * @return state of the removal
	 */
	public QueryState destroy() {
		// remove metadata from block
		block.removeMetadata(ACTION_BLOCK_ID, KaranteeniCore.getPlugin(KaranteeniCore.class));
		return KaranteeniCore.getActionBlockManager().destroyActionBlock(this);
	}
	
	
	/**
	 * Get the block which receives the actions
	 * @return block receiving the actions
	 */
	public Block getBlock() {
		return this.block;
	}
	
	
	/**
	 * Player clicked this block
	 * @param player player who clicked the block
	 * @param action click action
	 */
	//public abstract void onClick(PlayerInteractEvent event);
	
	
	/**
	 * Called when the given block is loaded from the database to the server
	 */
	public abstract void onLoad();
	
	
	/**
	 * Check if the given entity has the permission to use this blockExecutable
	 * @param entity entity whose permission will be checked
	 * @return true if can use, false if cannot
	 */
	public final boolean hasPermission(Entity entity) {
		return entity.hasPermission(permission);
	}
	
	
	/**
	 * Returns the permission owned by this class
	 * @return
	 */
	public final String getPermission() {
		return this.permission;
	}
	
	
	/**
	 * List of events that can be used by the actionblock.
	 * The actionblock must not implement any events on its own; to use events, inherit the interfaces provided by this class
	 * @author Nuubles
	 *
	 */
	public static class Events {
		/**
		 * Base interface to inherit to provide easy access to all other interfaces
		 */
		protected static interface Base					{};
		public static interface BlockBreakEvent 		extends Base { public void blockBreakEvent(org.bukkit.event.block.BlockBreakEvent event); };
		public static interface BlockBurnEvent 			extends Base { public void blockBurnEvent(org.bukkit.event.block.BlockBurnEvent event); };
		public static interface BlockCanBuildEvent 		extends Base { public void blockCanBuildEvent(org.bukkit.event.block.BlockCanBuildEvent event); };
		public static interface BlockCookEvent 			extends Base { public void blockCookEvent(org.bukkit.event.block.BlockCookEvent event); };
		public static interface BlockDamageEvent 		extends Base { public void blockDamageEvent(org.bukkit.event.block.BlockDamageEvent event); };
		//public static interface BlockDispenseArmorEvent extends Base { public void blockDispenseArmorEvent(BlockDispenseArmorEvent event); };
		public static interface BlockDispenseEvent 		extends Base { public void blockDispenseEvent(org.bukkit.event.block.BlockDispenseEvent event); };
		public static interface BlockExpEvent 			extends Base { public void blockExpEvent(org.bukkit.event.block.BlockExpEvent event); };
		//public static interface BlockExplodeEvent 		extends Base { public void blockExplodeEvent(BlockExplodeEvent event); };
		public static interface BlockFadeEvent 			extends Base { public void blockFadeEvent(org.bukkit.event.block.BlockFadeEvent event); };
		public static interface BlockFertilizeEvent 	extends Base { public void blockFertilizeEvent(org.bukkit.event.block.BlockFertilizeEvent event); };
		public static interface BlockFormEvent 			extends Base { public void blockFormEvent(org.bukkit.event.block.BlockFormEvent event); };
		public static interface BlockFromToEvent 		extends Base { public void blockFromToEvent(org.bukkit.event.block.BlockFromToEvent event); };
		public static interface BlockGrowEvent 			extends Base { public void blockGrowEvent(org.bukkit.event.block.BlockGrowEvent event); };
		public static interface BlockIgniteEvent 		extends Base { public void blockIgniteEvent(org.bukkit.event.block.BlockIgniteEvent event); };
		public static interface BlockMultiPlaceEvent 	extends Base { public void blockMultiPlaceEvent(org.bukkit.event.block.BlockMultiPlaceEvent event); };
		//public static interface BlockPhysicsEvent 		extends Base { public void blockPhysicsEvent(BlockPhysicsEvent event); };
		//public static interface BlockPistonExtendEvent 	extends Base { public void blockPistonExtendEvent(BlockPistonExtendEvent event); };
		//public static interface BlockPistonRetractEvent extends Base { public void blockPistonRetractEvent(BlockPistonRetractEvent event); };
		public static interface BlockPlaceEvent 		extends Base { public void blockPlaceEvent(org.bukkit.event.block.BlockPlaceEvent event); };
		public static interface BlockRedstoneEvent 		extends Base { public void blockRedstoneEvent(org.bukkit.event.block.BlockRedstoneEvent event); };
		//public static interface BlockShearEntityEvent 	extends Base { public void blockShearEntityEvent(BlockShearEntityEvent event); };
		//public static interface BlockSpreadEvent 		extends Base { public void blockSpreadEvent(BlockSpreadEvent event); };
		public static interface CauldronLevelChangeEvent extends Base { public void cauldronLevelChangeEvent(org.bukkit.event.block.CauldronLevelChangeEvent event); };
		public static interface EntityBlockFormEvent 	extends Base { public void entityBlockFormEvent(org.bukkit.event.block.EntityBlockFormEvent event); };
		//public static interface FluidLevelChangeEvent 	extends Base { public void fluidLevelChangeEvent(FluidLevelChangeEvent event); };
		//public static interface LeavesDecayEvent 		extends Base { public void leavesDecayEvent(LeavesDecayEvent event); };
		//public static interface MoistureChangeEvent 	extends Base { public void moistureChangeEvent(MoistureChangeEvent event); };
		public static interface NotePlayEvent 			extends Base { public void notePlayEvent(org.bukkit.event.block.NotePlayEvent event); };
		//public static interface SignChangeEvent 		extends Base { public void signChangeEvent(SignChangeEvent event); };
		//public static interface SpongeAbsorbEvent 		extends Base { public void spongeAbsorbEvent(SpongeAbsorbEvent event); };
		public static interface PlayerInteractEvent 	extends Base { public void playerInteractEvent(org.bukkit.event.player.PlayerInteractEvent event); };
	}
}
