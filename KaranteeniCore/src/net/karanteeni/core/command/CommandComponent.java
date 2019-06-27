package net.karanteeni.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * A component to allow chaining in commands
 * @author Nuubles
 *
 */
public abstract class CommandComponent implements ChainerInterface {
	// a map of new components
	protected String permission;
	protected HashMap<String, CommandComponent> components;
	protected CommandLoader execComponent;
	protected CommandChainer chainer;
	
	
	/**
	 * Initialize component without next components
	 */
	protected CommandComponent() {
		
	}
	
	
	/**
	 * Initialize component with next possible components
	 */
	protected CommandComponent(HashMap<String, CommandComponent> components) {
		this.components = components;
		if(components != null)
			for(Entry<String, CommandComponent> entry : components.entrySet())
				entry.getValue().chainer = this.chainer;
	}
	
	
	/**
	 * Initialize component with one next component
	 * @param name param for the component
	 * @param component component to add
	 */
	protected CommandComponent(String name, CommandComponent component) {
		this.components = new HashMap<String, CommandComponent>();
		this.components.put(name.toLowerCase(), component);
		if(component != null)
			component.chainer = this.chainer;
	}
	
	
	/**
	 * Initializes a commandcomponent with always executable component
	 * @param component component to run always if no other matches are found
	 */
	protected CommandComponent(CommandLoader component) {
		this.execComponent = component;
		if(component != null)
			component.chainer = this.chainer;
	}
	
	
	/**
	 * Sets the command chainer to be used in the command execution
	 * @param chain Chainer from which the data is accessed
	 */
	public final void setChainer(CommandChainer chain) {
		this.chainer = chain;
		
		// set the chainer to all child chains too
		if(this.components != null)
		for(Entry<String, CommandComponent> entry : components.entrySet()) {
			entry.getValue().setChainer(chain);
		}
		
		// set the chainer to loader
		if(this.execComponent != null)
			this.execComponent.setChainer(chain);
	}
	
	
	/**
	 * Run after this component has been registered
	 */
	protected abstract void onRegister();
	
	
	/**
	 * Set the required permission for this node
	 * @param permission permission required to use this component
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	
	/**
	 * Adds the given command component to the chain
	 * @param parameter parameter this component is accessed with
	 * @param component component to be run with the given parameter
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 * (A null return can also indicate that the map previously associated null with key.)
	 */
	public final CommandComponent addComponent(String parameter, CommandComponent component) {
		if(component != null)
			component.chainer = this.chainer;
		component.onRegister();
		return components.put(parameter.toLowerCase(), component);
	}
	
	
	/**
	 * Adds a component of which all of them are executed
	 * @param component component to run when no matches are found for param if one is given
	 * @return this
	 */
	public final CommandComponent setLoader(CommandLoader component) {
		this.execComponent = component;
		if(component != null) {
			component.chainer = this.chainer;
			component.onRegister();
		}
		return this;
	}
	
	
	/**
	 * Removes a given component from the command
	 * @param parameter parameter the component has
	 * @return removed command component
	 */
	public final CommandComponent removeComponent(String parameter) {
		return components.remove(parameter.toLowerCase());
	}
	
	
	/**
	 * Chains components using the given param
	 * @param param param to chain the components with
	 * @return null if no components were found, true if successful run, false if incorrect parameters
	 */
	protected final Boolean chainComponents(String param, CommandSender sender, Command cmd, String label, String[] args) {
		if(components == null)
			return null;
		
		CommandComponent comp = null;
		
		if(chainer != null)
			comp = components.get(param.toLowerCase());
		else
			comp = components.get(chainer.getRealParam(param));
		
		// if components were not found, run all of the executable components until true
		/*if(comp == null) {
			if(this.execComponent == null)
				return null;
			
			// run all consecutive components
			return this.execComponent.exec(sender, cmd, label, args);
		}*/
		
		return comp.exec(sender, cmd, label, args);
	}
	
	
	/**
	 * Chains autofill components using the given param. Does not run this autofill
	 * @param param param to chain the components with
	 * @return null if no components were found, true if successful run, false if incorrect parameters
	 */
	protected final List<String> chainAutofill(String param, CommandSender sender, Command cmd, String label, String[] args) {
		if(components == null && execComponent == null)
			return this.autofill(sender, cmd, label, args);
		
		CommandComponent comp = null;
		
		// if there are components, check them first
		if(components != null) {
			if(chainer != null)
				comp = components.get(param.toLowerCase());
			else
				comp = components.get(chainer.getRealParam(param));
		}
		
		// run the autofill of the component if there is a component found
		if(comp != null) {
			List<String> componentAutofill = comp.execAutofill(sender, cmd, label, args);
			
			// if autofill fillings was found, return them
			if(componentAutofill != null && !componentAutofill.isEmpty())
				return componentAutofill;
		}
		
		// run the executable component if no return from the previous components
		// no executable component, return null
		if(this.execComponent == null)
			return null;
		
		// run the loader component autofill and return its results as the final result
		return this.execComponent.execAutofill(sender, cmd, label, args);
	}
	
	
	/**
	 * Removes the first parameter from the given array
	 * @param args args where the first param is removed
	 * @return shortened args
	 */
	protected String[] cutArgs(String[] args) {
		if(args == null || args.length == 0 || args.length == 1)
			return new String[0];
		return Arrays.copyOfRange(args, 1, args.length);
	}
	
	
	/**
	 * Removes the first parameter from the given array
	 * @param args args where the first param is removed
	 * @return shortened args
	 */
	protected final String[] removeNth(String[] args, int index) {
		if(args == null || args.length == 0 || args.length == 1)
			return new String[0];
		return (String[]) ArrayUtils.remove(args, index);
	}
	
	
	/**
	 * Chain and execute this component
	 * @param sender command sender
	 * @param cmd command
	 * @param label command label
	 * @param args command args
	 * @return true if success, false if command could not be executed
	 */
	public boolean exec(CommandSender sender, Command cmd, String label, String[] args) {
		// check if sender has the permission to execute this parameter
		if(!hasPermission(sender)) {
			noPermission(sender);
			return false;
		}
		
		// if there is a loader running before this, run it
		if(this.execComponent != null && this.execComponent.isBefore())
			if(!this.execComponent.exec(sender, cmd, label, args)) return false;
		
		// run the possible chain if there is anything to chain
		if(args != null && args.length > 0) {
			Boolean chainResult = chainComponents(args[0], sender, cmd, label, cutArgs(args));
			if(chainResult != null)
				return chainResult;
		}
		
		// run the code of this component
		boolean executed = runComponent(sender, cmd, label, args);
		
		// if the execution if false, the given argument was invalid
		if(!executed)
			invalidArguments(sender);
		
		// if the loader is supposed to run after this, run it after this
		if(executed && this.execComponent != null && !this.execComponent.isBefore())
			if(!this.execComponent.exec(sender, cmd, label, args)) return false;
		return executed;
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
	 * Executes this component and checks all possible subcomponents
	 * and if a match is found runs that component instead
	 * @return true if command exec was a success, false if invalid arguments
	 */
	protected abstract boolean runComponent(CommandSender sender, Command cmd, String label, String[] args);
	
	
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
	 * Returns the parameters for tab completion. By default returns the next params if there are any
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	public final List<String> execAutofill(CommandSender sender, Command cmd, String label, String[] args) {
		// check the permission
		if(!hasPermission(sender))
			return null;
		
		// run the possible chain if there is anything to chain
		if(((components != null && !components.isEmpty()) || (execComponent != null)) 
				&& args != null 
				&& args.length > 1
				&& chainer != null) {
			
			List<String> chainResult = chainAutofill(args[0], sender, cmd, label, cutArgs(args));
			return chainResult;
		}
		
		// run the code of this component
		return autofill(sender, cmd, label, args);
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
		if(this.components == null || this.components.isEmpty())
			return null;
		
		return this.filterByPrefix(new ArrayList<String>(this.components.keySet()), args[args.length-1], false);
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
}
