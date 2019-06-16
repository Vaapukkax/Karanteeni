package net.karanteeni.core.command;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniCore;

/**
 * Enable shortcuts to premade commands with any plugin
 * @author Nuubles
 *
 */
public class ShortcutCommand {
	private final KaranteeniCore core;
	protected static CommandMap cmap;
	
	/**
	 * Initialize shortcuts using the core plugin
	 * @param core
	 */
	public ShortcutCommand(KaranteeniCore core) {
		this.core = core;
		
		// load settings in which the shortcuts are located
		if(!core.getSettings().isSet("shortcuts")) {
			core.getSettings().set("shortcuts.gms", "/gamemode survival");
			core.getSettings().set("shortcuts.gma", "/gamemode adventure");
			core.getSettings().set("shortcuts.gmc", "/gamemode creative");
			core.getSettings().set("shortcuts.gmsp", "/gamemode spectator");
			core.saveSettings();
		}
		
		// register the shortcut commands made in settings file
		registerShortcuts(core.getSettings().getConfigurationSection("shortcuts").getValues(false));
	}
	
	
	/**
	 * Initializes the shortcuts to spigot
	 * @param core
	 */
	public static void initializeShortcuts(KaranteeniCore core) {
		new ShortcutCommand(core);
	}
	
	
	/**
	 * Returns the command map of plugins
	 * @return command map
	 */
    private final CommandMap getCommandMap() {
        if (cmap == null) {
            try {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
                return getCommandMap();
            } catch (Exception e) { e.printStackTrace(); }
        } else if (cmap != null) { return cmap; }
        return getCommandMap();
    }
    
    
    /**
     * Registers the shortcuts to make them work
     */
    private void registerShortcuts(Map<String, Object> commands) {
    	// loop each shortcut
    	for(Entry<String, Object> entry : commands.entrySet()) {
    		// if the command is null or has no characters skip and log it
    		if(entry.getValue() == null || entry.getValue().toString().length() == 0) {
    			Bukkit.getLogger().log(Level.CONFIG, "Invalid settings! shortcut "+entry.getKey()+" has no value!");
    			continue;
    		}
    		
    		// create a new command reflector
    		ReflectCommand cmd = new ReflectCommand(entry.getKey());

    		// create and set the command executor
    		cmd.setExecutor(new Shortcut(entry.getValue().toString()));
    		
    		// register the command reflector
    		getCommandMap().register(core.getName().toLowerCase(), cmd);
    	}
    }
    
    
    /**
     * Reflects the shortcut to the shortcut executor
     * @author Matti
     *
     */
    private final class ReflectCommand extends Command {
        private Shortcut exe = null;
        protected ReflectCommand(String command) { super(command); }
        
        public void setExecutor(Shortcut exe) { this.exe = exe; }
        
        @Override public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (exe != null) { return exe.onCommand(sender, this, commandLabel, args); }
            return false;
        }
    }
    
    
    /**
     * Runs the shortcut command as the actual command
     * @author Nuubles
     *
     */
    private final class Shortcut implements CommandExecutor {
        // long command to be run
        private String longCMD;
        
        /**
         * 
         * @param command the shortcut which gets executed
         * @param actual The actual command which the shortcut will be run
         */
        protected Shortcut(String actual) { 
        	this.longCMD = actual;
    	}

        
		@Override
		public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
			
			// if it is a command
			if(longCMD.charAt(0) == '/') {
				Bukkit.dispatchCommand(sender, longCMD.substring(1, longCMD.length()));
			} else { // a chat message
				if(sender instanceof Player) {
					((Player)sender).chat(longCMD);
				} else {
					Bukkit.broadcastMessage(longCMD);
				}
			}
			return true;
		}
    }
}
