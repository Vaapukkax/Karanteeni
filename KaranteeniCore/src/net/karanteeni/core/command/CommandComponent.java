package net.karanteeni.core.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

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
	private HashMap<String, CommandComponent> components;
	private CommandChainer chainer;
	
	
	/**
	 * Initialize component without next components
	 */
	public CommandComponent() {
		
	}
	
	
	/**
	 * Initialize component with next possible components
	 */
	public CommandComponent(HashMap<String, CommandComponent> components) {
		this.components = components;
	}
	
	
	/**
	 * Sets the command chainer to be used in the command execution
	 * @param chain Chainer from which the data is accessed
	 */
	public void setChainer(CommandChainer chain) {
		this.chainer = chain;
		
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
	 * @return
	 */
	protected Boolean chainComponents(String param, CommandSender sender, Command cmd, String label, String[] args) {
		if(components == null)
			return null;
		
		CommandComponent comp = null;
		
		if(chainer != null)
			comp = components.get(param.toLowerCase());
		else
			comp = components.get(chainer.getRealParam(param));
		
		if(comp == null)
			return null;
		return comp.exec(sender, cmd, label, args);
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
	 * Executes this component and checks all possible subcomponents
	 * and if a match is found runs that component instead
	 * @return true if command exec was a success, false if invalid arguments
	 */
	protected abstract boolean runComponent(CommandSender sender, Command cmd, String label, String[] args);
}
