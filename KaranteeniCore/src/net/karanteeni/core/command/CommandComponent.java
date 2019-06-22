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
public abstract class CommandComponent {
	// a map of new components
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
	}
	
	
	/**
	 * Initialize component with one next component
	 * @param name param for the component
	 * @param component component to add
	 */
	protected CommandComponent(String name, CommandComponent component) {
		this.components = new HashMap<String, CommandComponent>();
		this.components.put(name.toLowerCase(), component);
	}
	
	
	/**
	 * Initializes a commandcomponent with always executable component
	 * @param component component to run always if no other matches are found
	 */
	protected CommandComponent(CommandLoader component, boolean before) {
		this.execComponent = component;
	}
	
	
	/**
	 * Sets the command chainer to be used in the command execution
	 * @param chain Chainer from which the data is accessed
	 */
	public void setChainer(CommandChainer chain) {
		this.chainer = chain;
		if(this.components == null)
			return;
		
		// set the chainer to all child chains too
		for(Entry<String, CommandComponent> entry : components.entrySet()) {
			entry.getValue().setChainer(chain);
		}
	}
	
	
	/**
	 * Adds the given command component to the chain
	 * @param parameter parameter this component is accessed with
	 * @param component component to be run with the given parameter
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 * (A null return can also indicate that the map previously associated null with key.)
	 */
	public CommandComponent addComponent(String parameter, CommandComponent component) {
		return components.put(parameter.toLowerCase(), component);
	}
	
	
	/**
	 * Adds a component of which all of them are executed
	 * @param component component to run when no matches are found for param if one is given
	 * @return this
	 */
	public CommandComponent setComponent(CommandLoader component) {
		this.execComponent = component;
		return this;
	}
	
	
	/**
	 * Removes a given component from the command
	 * @param parameter parameter the component has
	 * @return removed command component
	 */
	public CommandComponent removeComponent(String parameter) {
		return components.remove(parameter.toLowerCase());
	}
	
	
	/**
	 * Chains components using the given param
	 * @param param param to chain the components with
	 * @return null if no components were found, true if successful run, false if incorrect parameters
	 */
	protected Boolean chainComponents(String param, CommandSender sender, Command cmd, String label, String[] args) {
		if(components == null)
			return null;
		
		CommandComponent comp = null;
		
		if(chainer != null)
			comp = components.get(param.toLowerCase());
		else
			comp = components.get(chainer.getRealParam(param));
		
		// if components were not found, run all of the executable components until true
		if(comp == null) {
			if(this.execComponent == null)
				return null;
			
			// run all consecutive components
			return this.execComponent.exec(sender, cmd, label, args);
		}
		
		return comp.exec(sender, cmd, label, args);
	}
	
	
	/**
	 * Chains components using the given param
	 * @param param param to chain the components with
	 * @return null if no components were found, true if successful run, false if incorrect parameters
	 */
	protected List<String> chainAutofill(String param, CommandSender sender, Command cmd, String label, String[] args) {
		if(components == null)
			return null;
		
		CommandComponent comp = null;
		
		if(chainer != null)
			comp = components.get(param.toLowerCase());
		else
			comp = components.get(chainer.getRealParam(param));
		
		// if components were not found, run all of the executable components until true
		if(comp == null) {
			if(this.execComponent == null)
				return null;
			
			// run the consecutive component
			return this.execAutofill(sender, cmd, label, args);
		}
		
		return comp.execAutofill(sender, cmd, label, args);
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
	protected String[] removeNth(String[] args, int index) {
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
	 * @return true if success, false if invalid args
	 */
	public boolean exec(CommandSender sender, Command cmd, String label, String[] args) {
		// run the possible chain if there is anything to chain
		if(args != null && args.length > 0) {
			Boolean chainResult = chainComponents(args[0], sender, cmd, label, cutArgs(args));
			if(chainResult != null)
				return chainResult;
		}
		
		// run the code of this component
		return runComponent(sender, cmd, label, args);
	}
	
	
    /**
     * Filters a list of string based on their prefix
     * @param list List to be filtered
     * @param prefix Prefix by which the strings will be filtered
     * @return List of filtered strings
     */
    protected List<String> filterByPrefix(Collection<String> list, String prefix)
    {
    	if(prefix == null)
    		return new ArrayList<String>();
    	return list.stream().filter(param -> param.startsWith(prefix)).collect(Collectors.toList());
    }
	
	
	/**
	 * Executes this component and checks all possible subcomponents
	 * and if a match is found runs that component instead
	 * @return true if command exec was a success, false if invalid arguments
	 */
	protected abstract boolean runComponent(CommandSender sender, Command cmd, String label, String[] args);
	
	
	/**
	 * Returns the parameters for tab completion. By default returns the next params if there are any
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	public List<String> execAutofill(CommandSender sender, Command cmd, String label, String[] args) {
		// run the possible chain if there is anything to chain
		if(args != null && args.length > 0) {
			List<String> chainResult = chainAutofill(args[0], sender, cmd, label, cutArgs(args));
			return chainResult;
		}
		
		// run the code of this component
		if(this.components == null || this.components.isEmpty())
			return null;
		
		return this.filterByPrefix(new ArrayList<String>(this.components.keySet()), args[args.length-1]);
	}
}
