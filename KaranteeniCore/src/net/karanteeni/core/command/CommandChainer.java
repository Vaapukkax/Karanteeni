package net.karanteeni.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;

/**
 * Chains a given command using it's arguments as parameters
 * @author Nuubles
 *
 */
public abstract class CommandChainer extends AbstractCommand {
	private HashMap<String, CommandComponent> components;
	private CommandComponent execComponent;
	private HashMap<String, Object> data;
	
	public CommandChainer(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage, List<String> params) {
		super(plugin, command, usage, description, permissionMessage, params);
	}

	
	/**
	 * Initializes the data map
	 */
	private void initDataMap() {
		if(data == null)
			data = new HashMap<String, Object>();
	}
	
	
	/**
	 * Sets data to keep in memory while running command
	 * @param key
	 * @param data
	 */
	public void setObject(String key, Object data) {
		initDataMap();
		this.data.put(key, data);
	}
	
	
	/**
	 * Returns the possible object from memory
	 * @param key with which the data was stored in
	 * @return the found data or null if not found
	 */
	public Object getObject(String key) {
		if(data == null)
			return null;
		return data.get(key);
	}
	
	
	/**
	 * Returns the possible object from memory
	 * @param key key with which the data was stored in
	 * @return the found data or null if not found
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(String key) throws ClassCastException {
		if(data == null)
			return null;
		return (T)data.get(key);
	}
	
	
	/**
	 * Sets a player to the chainer memory
	 * @param key key to access the player data
	 * @param player player to add
	 */
	public void setPlayer(String key, Player player) {
		initDataMap();
		this.data.put(key, player);
	}
	
	
	/**
	 * Returns a given player from data map
	 * @param key key to access the data with
	 * @return player if one is found
	 */
	public Player getPlayer(String key) {
		if(data == null)
			return null;
		return (Player)data.get(key);
	}
	
	
	/**
	 * Returns a list of type from data map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key) throws ClassCastException {
		if(data == null)
			return null;
		return (List<T>)data.get(key);
	}
	

	/**
	 * Adds the given component to the command parameters
	 * @param arg
	 * @param component
	 */
	public void addComponent(String arg, CommandComponent component) {
		if(components == null)
			components = new HashMap<String, CommandComponent>();
		components.put(arg, component);
		component.setChainer(this);
	}
	
	
	/**
	 * Adds the given loader to the command parameters. Run always if no match in parameters
	 * @param component
	 */
	public void addComponent(CommandComponent component) {
		this.execComponent = component;
		component.setChainer(this);
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// before execution check if there is anything to chain
		if(components != null && args.length > 0) {
			// get the stored component with parameter
			CommandComponent component = components.get(args[0]);
			// execute component if one is found
			if(component != null) {
				if(!component.exec(sender, cmd, label, args)) {
					KaranteeniPlugin.getMessager().sendMessage(sender, 
							Sounds.NO.get(),
							Prefix.NEGATIVE + 
							KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
				}
				
				// clear data after cmd execution
				data = null;
				return true;
			} else if (this.execComponent != null){
				// if no component was found, run the excess components which run with any parameters
				if(!this.execComponent.exec(sender, cmd, label, args)) {
					KaranteeniPlugin.getMessager().sendMessage(sender, 
							Sounds.NO.get(),
							Prefix.NEGATIVE + 
							KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
				}
			}
		}
		
		// run possible command if no params given
		boolean res = runCommand(sender, cmd, label, args);
		
		// clear data after cmd execution
		data = null;
		
		// return true to prevent "no permission" message
		return res;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
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
		if((autoFill == null || autoFill.isEmpty()) && this.execComponent != null)
			return this.execComponent.execAutofill(sender, cmd, label, args);
		
		// if no return value from random parameter components, return autofill for the params in the chainer
		if(args.length == 1)
			return filterByPrefix(new ArrayList<String>(this.components.keySet()), args[0]);
		return null;
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
	 * The actual command to be run.
	 * This command will be run only and only if no components to chain were found
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	protected abstract boolean runCommand(CommandSender sender, Command cmd, String label, String[] args);
}
