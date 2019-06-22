package net.karanteeni.core.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandLoader extends CommandComponent {
	private boolean before;
	
	/**
	 * Initialize component without next components
	 */
	public CommandLoader() {
		this.before = true;
	}
	
	
	/**
	 * Initialize component with next possible components
	 */
	public CommandLoader(HashMap<String, CommandComponent> components) {
		this.components = components;
		this.before = true;
	}
	
	
	/**
	 * Initialize component with one next component
	 * @param name param for the component
	 * @param component component to add
	 */
	public CommandLoader(String name, CommandComponent component) {
		this.components = new HashMap<String, CommandComponent>();
		this.components.put(name.toLowerCase(), component);
		this.before = true;
	}
	
	
	/**
	 * Initializes a commandcomponent with always executable component
	 * @param component component to run always if no other matches are found
	 * @param before should the loader be run before the next actual parameter
	 */
	public CommandLoader(CommandComponent component, boolean before) {
		this.execComponent = component;
		this.before = before;
	}
	
	/**
	 * Chain and always execute this component
	 * @param sender command sender
	 * @param cmd command
	 * @param label command label
	 * @param args command args
	 * @return true if success, false if invalid args
	 */
	public boolean exec(CommandSender sender, Command cmd, String label, String[] args) {
		// run the loader before continuing forward
		boolean retValue = runComponent(sender, cmd, label, args);
		
		// run the possible chain if there is anything to chain
		if(args != null && args.length > 0) {
			Boolean chainResult = chainComponents(args[0], sender, cmd, label, cutArgs(args));
			if(chainResult != null)
				return chainResult;
		}
		
		// run the code of this component
		return retValue;
	}
}
