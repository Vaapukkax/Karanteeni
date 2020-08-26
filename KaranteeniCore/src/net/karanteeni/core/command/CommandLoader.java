package net.karanteeni.core.command;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandLoader extends CommandComponent {
	private boolean before;
	
	/**
	 * Initialize component without next components
	 */
	public CommandLoader(boolean before) {
		this.before = before;
	}
	
	
	/**
	 * Initialize component with next possible components
	 */
	public CommandLoader(HashMap<String, CommandComponent> components) {
		this.components = components;
		this.before = true;
		if(components != null)
			for(Entry<String, CommandComponent> entry : components.entrySet())
				entry.getValue().chainer = this.chainer;
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
		if(component != null)
			component.chainer = this.chainer;
	}
	
	
	/**
	 * Initializes a command component with always executable component
	 * @param component component to run always if no other matches are found
	 * @param before should the loader be run before the next actual parameter
	 */
	public CommandLoader(CommandLoader component, boolean before) {
		this.execComponent = component;
		this.before = before;
		if(component != null)
			component.chainer = this.chainer;
	}
	
	
	/**
	 * Checks if this loader should be run before or after the param this is assigned to
	 * @return true if this should be run before the container, false if after
	 */
	public final boolean isBefore() {
		return this.before;
	}
	
	
	/**
	 * Chain and always executes this component.
	 * The loader component of this component is run either before this AND chain or after this AND chain
	 * @param sender command sender
	 * @param cmd command
	 * @param label command label
	 * @param args command args
	 * @return true if success, false if invalid args
	 */
	public final CommandResult exec(CommandSender sender, Command cmd, String label, String[] args) {
		if(!hasPermission(sender)) {
			noPermission(sender, 
					CommandResult.NO_PERMISSION.getSound(), 
					CommandResult.NO_PERMISSION.getDisplayFormat(), 
					CommandResult.NO_PERMISSION.getMessage());
			return CommandResult.NO_PERMISSION;
		}
		
		// if there is a loader running before this, run it
		if(this.execComponent != null && this.execComponent.isBefore() && args != null && args.length > 0 && (components == null || !components.containsKey(args[0]))) {
			CommandResult result = this.execComponent.exec(sender, cmd, label, cutArgs(args, parameterLength));
			// if the command execution a success
			if(!CommandResult.SUCCESS.equals(result) && !CommandResult.ASYNC_CALLBACK.equals(result)) 
				return result; // if incorrect result, return loader result
		}
			
		// run the loader before continuing forward
		CommandResult retValue = runComponent(sender, cmd, label, args);
		
		// if the execution is false, the given argument was invalid
		// if errors in execution handle them
		if(!CommandResult.SUCCESS.equals(retValue) && !CommandResult.ASYNC_CALLBACK.equals(retValue)) {
			if(CommandResult.INVALID_ARGUMENTS.equals(retValue)) {
				invalidArguments(sender, retValue.getSound(), retValue.getDisplayFormat(), retValue.getMessage());
			} else if(CommandResult.NO_PERMISSION.equals(retValue)) {
				noPermission(sender, retValue.getSound(), retValue.getDisplayFormat(), retValue.getMessage());
			} else if(CommandResult.OTHER.equals(retValue)) {
				other(sender, retValue.getSound(), retValue.getDisplayFormat(), retValue.getMessage());
			} else if(CommandResult.ERROR.equals(retValue)) {
				error(sender, retValue.getSound(), retValue.getDisplayFormat(), retValue.getMessage());
			} else if(CommandResult.NOT_FOR_CONSOLE.equals(retValue)) {
				notForConsole(sender, retValue.getSound(), retValue.getDisplayFormat(), retValue.getMessage());
			} else if(CommandResult.ONLY_CONSOLE.equals(retValue)) {
				onlyConsole(sender, retValue.getSound(), retValue.getDisplayFormat(), retValue.getMessage());
			}
			
			return retValue;
		}
		
		// run the possible chain if there is anything to chain
		if(args != null && args.length > parameterLength) {
			CommandResult chainResult = chainComponents(args[parameterLength], sender, cmd, label, cutArgs(args, parameterLength+1));
			if(chainResult != null)
				retValue = chainResult; // retValue is ALWAYS true here so no need to combine both
		}
		
		// run the loader component after this chain and this
		if((CommandResult.SUCCESS.equals(retValue) || CommandResult.ASYNC_CALLBACK.equals(retValue)) && 
				this.execComponent != null && !this.execComponent.isBefore()) {
			CommandResult res = this.execComponent.exec(sender, cmd, label, cutArgs(args, parameterLength));
			if(!CommandResult.SUCCESS.equals(res) && !CommandResult.ASYNC_CALLBACK.equals(res)) return res;
		}
		
		// run the code of this component
		return retValue;
	}
}
