package net.karanteeni.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.SoundType;

/**
 * Chains a given command using it's arguments as parameters
 * @author Nuubles
 *
 */
public abstract class CommandChainer extends AbstractCommand implements ChainerInterface {
	private HashMap<String, CommandComponent> components;
	private CommandLoader execComponent;
	private HashMap<String, Object> data;
	private String permission;
	protected int parameterLength = 0;
	
	public CommandChainer(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage, List<String> params) {
		super(plugin, command, usage, description, permissionMessage, params);
	}

	
	/**
	 * Checks if player has the permission to use this component
	 * @param sender command sender
	 * @return true if has permission, false otherwise
	 */
	public boolean hasPermission(CommandSender sender) {
		if(permission == null)
			return true;
		return sender.hasPermission(permission);
	}
	
	
	/**
	 * Sets the permission which is required to use this chainer
	 * @param permission permission to check when using this chainer
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	
    /**
     * Returns the superclass plugin
     * @return the plugin using this command
     */
    public final KaranteeniPlugin getPlugin() {
    	return this.plugin;
    }
	
	
	/**
	 * Initializes the data map
	 */
	private final void initDataMap() {
		if(data == null)
			data = new HashMap<String, Object>();
	}
	
	
	/**
	 * Sets data to keep in memory while running command
	 * @param key
	 * @param data
	 */
	public final void setObject(String key, Object data) {
		initDataMap();
		this.data.put(key, data);
	}
	
	
	/**
	 * Returns the possible object from memory
	 * @param key with which the data was stored in
	 * @return the found data or null if not found
	 */
	public final Object getData(String key) {
		if(data == null)
			return null;
		return data.get(key);
	}
	
	
	/**
	 * Checks if data is set to the given key.
	 * Does not perform null checks
	 * @param key key to search the data with
	 * @return true if data found from hashmap
	 */
	public final boolean isSet(String key) {
		if(data != null)
			return data.containsKey(key);
		return false;
	}
	
	
	/**
	 * Returns the possible object from memory
	 * @param key key with which the data was stored in
	 * @return the found data or null if not found
	 */
	@SuppressWarnings("unchecked")
	public final <T> T getObject(String key) throws ClassCastException {
		if(data == null)
			return null;
		return (T)data.get(key);
	}
	
	
	/**
	 * Checks if the chainer has data with the given key
	 * @param key key the possible data has been associated with
	 * @return true if some key found, false otherwise
	 */
	public boolean hasData(String key) {
		if(this.data == null) return false;
		return this.data.containsKey(key);
	}
	
	
	/**
	 * Sets a player to the chainer memory
	 * @param key key to access the player data
	 * @param player player to add
	 */
	public final void setPlayer(String key, Player player) {
		initDataMap();
		this.data.put(key, player);
	}
	
	
	/**
	 * Returns a given player from data map
	 * @param key key to access the data with
	 * @return player if one is found
	 */
	public final Player getPlayer(String key) {
		if(data == null)
			return null;
		return (Player)data.get(key);
	}
	
	
	/**
	 * Returns a list of type from data map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T> List<T> getList(String key) throws ClassCastException {
		if(data == null)
			return null;
		return (List<T>)data.get(key);
	}
	

	/**
	 * Adds the given component to the command parameters
	 * @param arg
	 * @param component
	 */
	public final void addComponent(String arg, CommandComponent component) {
		if(components == null)
			components = new HashMap<String, CommandComponent>();
		components.put(arg, component);
		if(component != null) {
			component.setChainer(this);
			component.onRegister();
		}
	}
	
	
	/**
	 * Adds the given loader to the command parameters. Run always if no match in parameters
	 * @param component
	 */
	public final void setLoader(CommandLoader component) {
		this.execComponent = component;
		component.setChainer(this);
		component.onRegister();
	}
	
	
	/**
	 * Plays the sound effect etc. effects when using autofill
	 * @param sender autofill user
	 * @param args arguments in command
	 */
	protected void autofillEffect(CommandSender sender, String[] args) {
		if(!(sender instanceof Player))
			return;
		
		//SoundType sound = new SoundType(Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 2f);
		SoundType sound = new SoundType(Sound.ENTITY_SHULKER_HURT_CLOSED, 0.1f, (float)(Math.random()/2)+0.8f);
		KaranteeniCore.getSoundHandler().playSound((Player)sender, sound, SoundCategory.NEUTRAL);
	}
	
	
	/**
	 * Removes the first parameter from the given array
	 * @param args args where the first param is removed
	 * @return shortened args
	 */
	protected String[] cutArgs(String[] args, int argLength) {
		if(args == null || args.length == 0 || args.length <= argLength)
			return new String[0];
		return Arrays.copyOfRange(args, argLength, args.length);
	}
	
	
	@Override
	public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Bukkit.broadcastMessage("" + this.components);
		//Bukkit.broadcastMessage(""+ this.execComponent);
		System.out.println(("Beginning chainer " + label + " having " + (this.components!=null?this.components.size():"0") + " components and " + (this.execComponent!=null) + " loader with class id " + this));
		System.out.println("Using arguments: ");
		for(String arg : args)
			System.out.println(arg);
		// make runnable to prevent commands from running in main thread
		/*BukkitRunnable commandRunnable = new BukkitRunnable() {
			@Override
			public void run() {*/
				// check if sender has permission to this command
				if(!hasPermission(sender)) {
					noPermission(sender, 
							CommandResult.NO_PERMISSION.getSound(), 
							CommandResult.NO_PERMISSION.getDisplayFormat(), 
							CommandResult.NO_PERMISSION.getMessage());
					return true;
				}
				
				// if the loader should be run before this and parameters have been given, run it
				if (execComponent != null && execComponent.isBefore() && args.length > 0) {
					CommandResult result = execComponent.exec(sender, cmd, label, cutArgs(args, parameterLength));
					//args = cutArgs(args, parameterLength);
					// if no component was found, run the excess components which run with any parameters
					if(!CommandResult.SUCCESS.equals(result)) {
						// clear data after cmd execution
						data = null;
						return true;
					}
				} 
				
				// before execution check if there is anything to chain
				if(components != null && args.length > parameterLength) {
					// get the stored component with parameter
					CommandComponent component = components.get(args[0].toLowerCase());
					// execute component if one is found
					if(component != null) {
						CommandResult execResult = component.exec(sender, cmd, label, cutArgs(args, parameterLength+1));
						// execute the found component
						if(!CommandResult.SUCCESS.equals(execResult)) {
							// clear data after cmd execution
							data = null;
							return true;
						}
						
						// if the loader has not been run, run it
						if (execComponent != null && !execComponent.isBefore()){
							execResult = execComponent.exec(sender, cmd, label, cutArgs(args, parameterLength));  ///////////////////////////////////////////////////////////
							// if no component was found, run the excess components which run with any parameters
							if(!CommandResult.SUCCESS.equals(execResult)) {
								data = null;
								return true;
							}
						}
						
						// clear data after cmd execution
						data = null;
						
						return true;
					}
				}
				
				// run possible command if no params given
				CommandResult res = runCommand(sender, cmd, label, args);
				
				// if the command run returned false, run the invalid arguments method
				if(!CommandResult.SUCCESS.equals(res)) {
					if(CommandResult.INVALID_ARGUMENTS.equals(res)) {
						invalidArguments(sender, res.getSound(), res.getDisplayFormat(), res.getMessage());
					} else if(CommandResult.NO_PERMISSION.equals(res)) {
						noPermission(sender, res.getSound(), res.getDisplayFormat(), res.getMessage());
					} else if(CommandResult.OTHER.equals(res)) {
						other(sender, res.getSound(), res.getDisplayFormat(), res.getMessage());
					} else if(CommandResult.ERROR.equals(res)) {
						error(sender, res.getSound(), res.getDisplayFormat(), res.getMessage());
					} else if(CommandResult.NOT_FOR_CONSOLE.equals(res)) {
						notForConsole(sender, res.getSound(), res.getDisplayFormat(), res.getMessage());
					} else if(CommandResult.ONLY_CONSOLE.equals(res)) {
						onlyConsole(sender, res.getSound(), res.getDisplayFormat(), res.getMessage());
					}
					
					data = null;
					return true;
				}
				
				// if the loader has not been run, run it if there are parameters to run
				if (execComponent != null && !execComponent.isBefore() && args.length > 0) {
					CommandResult result = execComponent.exec(sender, cmd, label, args);
					// if no component was found, run the excess components which run with any parameters
					if(!CommandResult.SUCCESS.equals(result)) {
						// clear data after cmd execution
						data = null;
						return true;
					}
				}
				
				// clear data after cmd execution
				data = null;
		/*	}
		};
		
		// run the command
		commandRunnable.run();*/
		
		// return true to prevent default "no permission" message
		return true;
	}
	
	
	/**
	 * Chains autofill components using the given param. Does not run this autofill
	 * @param param param to chain the components with
	 * @return null if no components were found, true if successful run, false if incorrect parameters
	 */
	protected final List<String> chainAutofill(String param, CommandSender sender, Command cmd, String label, String[] args) {
		// request own autofill if no components or not enough arguments to fulfill own autofill
		if((components == null && execComponent == null) || args.length <= parameterLength)
			return this.autofill(sender, cmd, label, args);
		
		// if we're typing the next component, return the next possible components
		if(args.length == parameterLength+1)
			return this.defaultAutofill(sender, cmd, label, args);
		
		CommandComponent comp = null;
		
		// if there are components, check them first
		if(components != null) {
			// null check is here in the component
			comp = components.get(param.toLowerCase());
		}
		
		// run the autofill of the component if there is a component found
		if(comp != null) {
			List<String> componentAutofill = comp.execAutofill(sender, cmd, label, cutArgs(args, parameterLength));
			
			// if autofill fillings was found, return them
			if(componentAutofill != null && !componentAutofill.isEmpty()) {
				return componentAutofill;
			}
		}
		
		// run the executable component if no return from the previous components
		// no executable component, return null
		if(this.execComponent == null)
			return null;
		
		//==========================================================================================================
		// IF DOES NOT WORK ADD -1 AFTER parameterLength!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//==========================================================================================================
		// run the loader component autofill and return its results as the final result
		return this.execComponent.execAutofill(sender, cmd, label, cutArgs(args, parameterLength));
	}
	
	
	/**
	 * Returns the default autofill fill
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	public final List<String> defaultAutofill(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> fill = null;
		if((this.components != null && !this.components.isEmpty()))
			fill = this.filterByPrefix(new ArrayList<String>(this.components.keySet()), args[args.length-1], false);
		
		if((fill == null || fill.isEmpty()) && this.execComponent != null)
			fill = this.execComponent.execAutofill(sender, cmd, label, cutArgs(args, Math.max(parameterLength, 0)));
		
		if(fill != null && fill.isEmpty())
			return null;
		
		return fill;
	}
	
	
	/**
	 * Returns the autofill to this component. By default returns the params for next components. Can be overriden
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		return defaultAutofill(sender, cmd, label, args);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		// check the permission
		if(!hasPermission(sender))
			return null;
		
		// if sender has permission play the autofill sound effects
		autofillEffect(sender, args);
		
		// run the possible chain if there is anything to chain
		if(((components != null && !components.isEmpty()) || (execComponent != null)) 
				&& args != null) {
			
			List<String> chainResult = chainAutofill(args[0], sender, cmd, label, args);
			return chainResult;
		}

		// run the code of this component
		return autofill(sender, cmd, label, args);
		
		
		// check if sender has permission to this command
		/*if(!hasPermission(sender))
			return null;
		
		if(args.length == 0 || (this.components == null && this.execComponent == null))
			return null;
		
		List<String> autoFill = null;
		
		// run autofill which includes a parameter
		if(this.components != null) {
			CommandComponent cc = this.components.get(args[0].toLowerCase());
			if(cc != null)
				autoFill = cc.execAutofill(sender, cmd, label, args);
		}
		
		// if no found matches, get autofill from random parameter components
		if(autoFill == null || autoFill.isEmpty()) {
			if(this.execComponent != null)
				return this.execComponent.execAutofill(sender, cmd, label, args);
		} else {
			// autofill found stuff to return to autofill
			return autoFill;
		}
		
		// if no return value from random parameter components, return autofill for the params in the chainer
		if(args.length == 1)
			return filterByPrefix(new ArrayList<String>(this.components.keySet()), args[0]);
		return null;*/
	}
	
	
    /**
     * Filters a list of string based on their prefix
     * @param list List to be filtered
     * @param prefix Prefix by which the strings will be filtered
     * @return List of filtered strings
     */
    protected List<String> filterByPrefix(Collection<String> list, String prefix, boolean caseSensitive)
    {
    	if(prefix == null)
    		return new ArrayList<String>();
    	if(caseSensitive)
    		return list.stream().filter(param -> param.startsWith(prefix)).collect(Collectors.toList());	
    	return list.stream().filter(param -> param.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
    }
	
	
	/**
	 * The actual command to be run.
	 * This command will be run only and only if no components to chain were found
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	protected abstract CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args);
}
