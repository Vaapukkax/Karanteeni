package net.karanteeni.core.block.executable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Responsible for events regarding classes inheriting ActionBlock class. If a certain interface is unused, it will
 * be unregistered and if it is used it will be registered.
 * @author Nuubles
 *
 */
public class ActionBlockEventManager implements Listener {
	// interfaces used and a list of 
	private HashMap<Class<? extends ActionBlock.Events.Base>, List<Class<? extends ActionBlock>>> implementingClasses = null;
	private HashMap<ActionBlock, Set<Class<? extends ActionBlock.Events.Base>>> implementedMethods = null;
	private ActionBlockManager manager = null;
	
	/**
	 * Initializes this class
	 * @param core the plugin this class is a part of
	 */
	public ActionBlockEventManager(ActionBlockManager manager) {
		this.manager = manager;
		implementingClasses =	new HashMap<Class<? extends ActionBlock.Events.Base>, List<Class<? extends ActionBlock>>>();
		implementedMethods = new HashMap<ActionBlock, Set<Class<? extends ActionBlock.Events.Base>>>();
					
		// load all of the classes to the hashmap
		implementingClasses.put(ActionBlock.Events.BlockBreakEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockBurnEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockCanBuildEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockCookEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockDamageEvent.class,			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockDispenseArmorEvent.class, 	new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockDispenseEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockExpEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockExplodeEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockFadeEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockFertilizeEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockFormEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockFromToEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockGrowEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockIgniteEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockMultiPlaceEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockPhysicsEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockPistonEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockPistonRetractEvent.class,	new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockPistonExtendEvent.class, 	new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockPlaceEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockRedstoneEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockShearEntityEvent.class, 	new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.BlockSpreadEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.CauldronLevelChangeEvent.class, 	new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.BlockFormEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.FluidLevelChangeEvent.class, 	new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.LeavesDecayEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.MoistureChangeEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.NotePlayEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.SignChangeEvent.class, 			new LinkedList<Class<? extends ActionBlock>>());
		//implementingClasses.put(ActionBlock.Events.SpongeAbsorbEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		implementingClasses.put(ActionBlock.Events.PlayerInteractEvent.class, 		new LinkedList<Class<? extends ActionBlock>>());
		
	}
	
	
	/**
	 * Adds an actionblock to listen to
	 * @param block block to which events should be listened to
	 */
	@SuppressWarnings("unchecked")
	public void addActionBlock(ActionBlock block) {
		if(!ActionBlock.Events.Base.class.isAssignableFrom(block.getClass()))
			return;
		Set<Class<? extends ActionBlock.Events.Base>> interfaces = getAllImplementedEventInterfaces((Class<? extends ActionBlock.Events.Base>)block.getClass());
		
		implementedMethods.put(block, interfaces);
	}
	
	
	/**
	 * Registers the given implemented interface to usage
	 * @param intf
	 */
	/*private void registerEvent(Class<? extends ActionBlock.Events.Base> intf) {
		
	}*/
	
	
	/**
	 * Removes an actionblock of which events should be listened to
	 * @param block
	 */
	public void removeActionBlock(ActionBlock block) {
		implementedMethods.remove(block);
	}
	
	
	/**
	 * Returns all interfaces of class that extend the base event interface
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<Class<? extends ActionBlock.Events.Base>> getAllImplementedEventInterfaces(Class<? extends ActionBlock.Events.Base> clazz) {
	    HashSet<Class<? extends ActionBlock.Events.Base>> res = new HashSet<Class<? extends ActionBlock.Events.Base>>();

	    do {
	        res.add(clazz);

	        // First, add all the interfaces implemented by this class
	        Class<?>[] interfaces = clazz.getInterfaces();
	        if (interfaces.length > 0) {
	            for (Class<?> interfaze : interfaces) {
	            	if(ActionBlock.Events.Base.class.isAssignableFrom(interfaze)) {
	            		res.add((Class<? extends ActionBlock.Events.Base>)interfaze);
	            		res.addAll(getAllImplementedEventInterfaces((Class<? extends ActionBlock.Events.Base>)interfaze));
	            	}
	            }
	        }

	        // Add the super class
	        Class<?> superClass = clazz.getSuperclass();

	        // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
	        if (superClass == null || !ActionBlock.Events.Base.class.isAssignableFrom(superClass)) {
	            break;
	        }

	        // Now inspect the superclass 
	        clazz = (Class<? extends ActionBlock.Events.Base>)superClass;
	    } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

	    return new HashSet<Class<? extends ActionBlock.Events.Base>>(res);
	}
	
	
	@EventHandler
	public void blockBreakEvent(BlockBreakEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockBreakEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockBreakEvent ev = (ActionBlock.Events.BlockBreakEvent)block;
			ev.blockBreakEvent(event);
		}
	}
	
	@EventHandler
	public void blockBurnEvent(BlockBurnEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockBurnEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockBurnEvent ev = (ActionBlock.Events.BlockBurnEvent)block;
			ev.blockBurnEvent(event);
		}
	}
	
	@EventHandler
	public void blockCanBuildEvent(BlockCanBuildEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockCanBuildEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockCanBuildEvent ev = (ActionBlock.Events.BlockCanBuildEvent)block;
			ev.blockCanBuildEvent(event);
		}
	}
	
	@EventHandler
	public void blockCookEvent(BlockCookEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockCookEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockCookEvent ev = (ActionBlock.Events.BlockCookEvent)block;
			ev.blockCookEvent(event);
		}
	}
	
	@EventHandler
	public void blockDamageEvent(BlockDamageEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockDamageEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockDamageEvent ev = (ActionBlock.Events.BlockDamageEvent)block;
			ev.blockDamageEvent(event);
		}
	}
	
	@EventHandler
	public void blockDispenseEvent(BlockDispenseEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockDispenseEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockDispenseEvent ev = (ActionBlock.Events.BlockDispenseEvent)block;
			ev.blockDispenseEvent(event);
		}
	}
	
	@EventHandler
	public void blockExpEvent(BlockExpEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockExpEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockExpEvent ev = (ActionBlock.Events.BlockExpEvent)block;
			ev.blockExpEvent(event);
		}
	}
	
	@EventHandler
	public void blockFadeEvent(BlockFadeEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockFadeEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockFadeEvent ev = (ActionBlock.Events.BlockFadeEvent)block;
			ev.blockFadeEvent(event);
		}
	}
	
	@EventHandler
	public void blockFertilizeEvent(BlockFertilizeEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockFertilizeEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockFertilizeEvent ev = (ActionBlock.Events.BlockFertilizeEvent)block;
			ev.blockFertilizeEvent(event);
		}
	}
	
	@EventHandler
	public void blockFormEvent(BlockFormEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockFormEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockFormEvent ev = (ActionBlock.Events.BlockFormEvent)block;
			ev.blockFormEvent(event);
		}
	}
	
	@EventHandler
	public void blockFromToEvent(BlockFromToEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockFromToEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockFromToEvent ev = (ActionBlock.Events.BlockFromToEvent)block;
			ev.blockFromToEvent(event);
		}
	}
	
	@EventHandler
	public void blockGrowEvent(BlockGrowEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockGrowEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockGrowEvent ev = (ActionBlock.Events.BlockGrowEvent)block;
			ev.blockGrowEvent(event);
		}
	}
	
	@EventHandler
	public void blockIgniteEvent(BlockIgniteEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockIgniteEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockIgniteEvent ev = (ActionBlock.Events.BlockIgniteEvent)block;
			ev.blockIgniteEvent(event);
		}
	}
	
	@EventHandler
	public void blockMultiPlaceEvent(BlockMultiPlaceEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockMultiPlaceEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockMultiPlaceEvent ev = (ActionBlock.Events.BlockMultiPlaceEvent)block;
			ev.blockMultiPlaceEvent(event);
		}
	}
	
	@EventHandler
	public void blockRedstoneEvent(BlockRedstoneEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockRedstoneEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockRedstoneEvent ev = (ActionBlock.Events.BlockRedstoneEvent)block;
			ev.blockRedstoneEvent(event);
		}
	}
	
	/*@EventHandler
	public void blockSpreadEvent(BlockSpreadEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.BlockBurnEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.BlockBurnEvent ev = (ActionBlock.Events.BlockBurnEvent)block;
			ev.blockBurnEvent(event);
		}
	}*/
	
	@EventHandler
	public void cauldronLevelChangeEvent(CauldronLevelChangeEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.CauldronLevelChangeEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.CauldronLevelChangeEvent ev = (ActionBlock.Events.CauldronLevelChangeEvent)block;
			ev.cauldronLevelChangeEvent(event);
		}
	}
	
	@EventHandler
	public void notePlayEvent(NotePlayEvent event) {
		ActionBlock block = manager.getActionBlock(event.getBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.NotePlayEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.NotePlayEvent ev = (ActionBlock.Events.NotePlayEvent)block;
			ev.notePlayEvent(event);
		}
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event) {
		ActionBlock block = manager.getActionBlock(event.getClickedBlock());
		if(block == null)
			return;
		if(ActionBlock.Events.PlayerInteractEvent.class.isAssignableFrom(block.getClass())) {
			ActionBlock.Events.PlayerInteractEvent ev = (ActionBlock.Events.PlayerInteractEvent)block;
			ev.playerInteractEvent(event);
		}
	}
}
