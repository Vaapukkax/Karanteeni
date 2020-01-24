package net.karanteeni.core.command.bare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.command.ChainerInterface;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.sounds.SoundType;

public abstract class BareCommand extends AbstractCommand implements ChainerInterface {
	private String permission;
	
	public BareCommand(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage, List<String> params) {
		super(plugin, command, usage, description, permissionMessage, params);
	}
	
	
	public void setPermission(String permission) {
		this.permission = permission;
	}

	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return permission == null || sender.hasPermission(permission);
	}
	
	
	@Override
	public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// check if sender has permission to this command
		BukkitRunnable commandRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(!hasPermission(sender)) {
					noPermission(sender, 
							CommandResult.NO_PERMISSION.getSound(), 
							CommandResult.NO_PERMISSION.getDisplayFormat(), 
							CommandResult.NO_PERMISSION.getMessage());
					return;
				}
				
				// run possible command if no params given
				CommandResult res = runCommand(sender, cmd, label, args);
				
				// if the command run returned false, run the invalid arguments method
				if(!CommandResult.SUCCESS.equals(res) && !CommandResult.ASYNC_CALLBACK.equals(res)) {
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
				}
			}
		};
		
		// run the command
		commandRunnable.run();
		
		// return true to prevent default "no permission" message
		return true;
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
     * Filters a list of string based on their prefix
     * @param list List to be filtered
     * @param prefix Prefix by which the strings will be filtered
     * @return List of filtered strings
     */
    protected List<String> filterByPrefix(Collection<String> list, String prefix, boolean caseSensitive) {
    	if(prefix == null)
    		return new ArrayList<String>();
    	if(caseSensitive)
    		return list.stream().filter(param -> param.startsWith(prefix)).collect(Collectors.toList());	
    	return list.stream().filter(param -> param.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
    }
	
	
	@Override
	public final List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		// if sender has permission play the autofill sound effects
		autofillEffect(sender, args);

		// run the code of this component
		return autofill(sender, cmd, label, args);
	}
	
	
	/**
	 * Returns the autofill to this component. By default returns the params for next components. Can be overriden
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	public abstract List<String> autofill(CommandSender sender, Command cmd, String label, String[] args);
}
