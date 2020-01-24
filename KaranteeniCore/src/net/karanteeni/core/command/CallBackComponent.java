package net.karanteeni.core.command;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import net.karanteeni.core.KaranteeniPlugin;

/**
 * This component is used to easy the use of slow operations and take them off from the main thread.
 * Uses a runner and a callback of which both are automatically used.
 * @author Nuubles
 *
 */
public abstract class CallBackComponent implements ChainerInterface {
	private boolean callBackOnMain = false;
	private KaranteeniPlugin plugin = null;
	private CommandSender sender = null;
	
	public CallBackComponent(KaranteeniPlugin plugin, CommandSender sender, boolean callbackInSync) {
		this.callBackOnMain = callbackInSync;
		this.sender = sender;
		this.plugin = plugin;
	}
	
	
	public CallBackComponent(KaranteeniPlugin plugin, CommandSender sender) {
		this.plugin = plugin;
		this.sender = sender;
	}
	
	
	/**
	 * Method to call first before others
	 */
	public abstract void run();
	
	
	/**
	 * Method called back
	 * @return
	 */
	public abstract CommandResult callback();
	
	
	/**
	 * Ignore the hasPermission method from the interface
	 */
	@Override
	public boolean hasPermission(CommandSender sender) {
		Bukkit.getLogger().log(Level.INFO, 
				"This method is here only to fulfill the interface requirements. DO NOT USE CALLBACK hasPermission(#CommandSender)!");
		return true;
	}
	
	
	/**
	 * Method which is run when callback method is requested
	 * @param plugin
	 */
	public final void execute() {
		new BukkitRunnable() {
			private boolean onMain = callBackOnMain;
			
			@Override
			public void run() {
				// run the method first
				CallBackComponent.this.run();

				// run callback
				if(onMain) {
					// run on main thread
					new BukkitRunnable() {
						@Override
						public void run() {
							CommandResult result = callback();
							
							if(CommandResult.INVALID_ARGUMENTS.equals(result)) {
								invalidArguments(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
							} else if(CommandResult.NO_PERMISSION.equals(result)) {
								noPermission(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
							} else if(CommandResult.OTHER.equals(result)) {
								other(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
							} else if(CommandResult.ERROR.equals(result)) {
								error(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
							} else if(CommandResult.NOT_FOR_CONSOLE.equals(result)) {
								notForConsole(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
							} else if(CommandResult.ONLY_CONSOLE.equals(result)) {
								onlyConsole(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
							}
						}
					}.runTask(plugin);
				} else {
					// run async
					CommandResult result = callback();
					// if errors in execution handle them
					if(CommandResult.INVALID_ARGUMENTS.equals(result)) {
						invalidArguments(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
					} else if(CommandResult.NO_PERMISSION.equals(result)) {
						noPermission(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
					} else if(CommandResult.OTHER.equals(result)) {
						other(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
					} else if(CommandResult.ERROR.equals(result)) {
						error(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
					} else if(CommandResult.NOT_FOR_CONSOLE.equals(result)) {
						notForConsole(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
					} else if(CommandResult.ONLY_CONSOLE.equals(result)) {
						onlyConsole(sender, result.getSound(), result.getDisplayFormat(), result.getMessage());
					}
				}
			}
			
		}.runTaskAsynchronously(plugin);
	}
}
