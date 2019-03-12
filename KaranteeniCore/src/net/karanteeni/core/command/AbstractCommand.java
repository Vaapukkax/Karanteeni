package net.karanteeni.core.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.data.structures.graphs.Digraph;
import net.karanteeni.core.data.structures.graphs.Vertex;

/**
 * For a How-To on how to use AbstractCommand see this post @ http://forums.bukkit.org/threads/195990/
 * 
 * @author Goblom
 */
public abstract class AbstractCommand implements TabExecutor {
    
	protected final KaranteeniPlugin plugin;
    protected final String command;
    protected final String description;
    protected List<String> alias;
    protected final String usage;
    protected final String permMessage;
    //protected final Map<String, String> paramMap;
    protected final Digraph<String,List<String>> params;

    protected static CommandMap cmap;
    
    public AbstractCommand(KaranteeniPlugin plugin, String command) {
        this(plugin, command, null, null, null, null, null);
    }
    
    public AbstractCommand(KaranteeniPlugin plugin, String command, String usage) {
        this(plugin, command, usage, null, null, null, null);
    }
    
    public AbstractCommand(KaranteeniPlugin plugin, String command, String usage, String description) {
        this(plugin, command, usage, description, null, null, null);
    }
    
    public AbstractCommand(KaranteeniPlugin plugin, String command, String usage, String description, String permissionMessage) {
        this(plugin, command, usage, description, permissionMessage, null, null);
    }
    
    /*private AbstractCommand(KaranteeniPlugin plugin, String command, String usage, String description, List<String> aliases) {
        this(plugin, command, usage, description, null, aliases);
    }*/
    
    private AbstractCommand(KaranteeniPlugin plugin, String command, String usage, String description, String permissionMessage, List<String> aliases, Digraph<String,List<String>> parameters) {
        this.plugin = plugin;
    	this.command = command.toLowerCase();
        this.usage = usage;
        this.description = description;
        this.permMessage = permissionMessage;
        this.alias = aliases;

        //Create a map for parameters to get the actual parameter 
        //this.paramMap = new HashMap<String, String>();
        this.params = parameters;
    }
    
    /**
     * 
     * @param plugin
     * @param command
     * @param params Possible command parameters
     */
    public AbstractCommand(KaranteeniPlugin plugin, String command, Digraph<String,List<String>> params) {
        this(plugin, command, null, null, null, null, params);
    }
    
    public AbstractCommand(KaranteeniPlugin plugin, String command, String usage, Digraph<String,List<String>> params) {
        this(plugin, command, usage, null, null, null, params);
    }
    
    public AbstractCommand(KaranteeniPlugin plugin, String command, String usage, String description, Digraph<String,List<String>> params) {
        this(plugin, command, usage, description, null, null, params);
    }
    
    public AbstractCommand(KaranteeniPlugin plugin, String command, String usage, String description, String permissionMessage, Digraph<String,List<String>> params) {
        this(plugin, command, usage, description, permissionMessage, null, params);
    }
    
    public void register() {
    	registerAliases(); //Register aliases
        ReflectCommand cmd = new ReflectCommand(this.command);
        if (this.alias != null) cmd.setAliases(this.alias);
        if (this.description != null) cmd.setDescription(this.description);
        if (this.usage != null) cmd.setUsage(this.usage);
        if (this.permMessage != null) cmd.setPermissionMessage(this.permMessage);
        getCommandMap().register("", cmd);
        cmd.setExecutor(this);
        //plugin.getCommand(this.command).setTabCompleter(this);
    }
    
    /**
     * Returns the real parameter which is defined in the actual command
     * from the users alias to it
     * @param parameter
     * @return
     */
    protected String getRealParam(String parameter)
    {
    	if(parameter == null)
    		return "";
    	
    	//String param = paramMap.get(parameter.toLowerCase());
    	Map<String, Vertex<List<String>>> valuePairs = params.getKeyValuePairs();
    	parameter = parameter.toLowerCase();
    	String realParam = "";
    	
    	//Loop through all the parameters to get the correct one
    	for(Entry<String,Vertex<List<String>>> entry : valuePairs.entrySet())
    	{
    		for(String param : entry.getValue().getValue())
    		{
    			if(parameter.equals(param))
    			{
    				realParam = entry.getKey();
    				break;
    			}
    		}
    		
    		if(realParam.length() > 0) //Real param has been found
    			break;
    	}
    	
    	return realParam;
    }
    
    /**
     * Returns the next parameters for onTabComplete if first params are given
     * @param parameter
     * @return
     */
    protected List<String> getNextParams(String[] parameters)
    {
    	
    }
    
    /**
     * Registers this commands aliases to command and parameters
     */
    private void registerAliases()
    {
    	if(!plugin.getConfig().isSet("Command."+this.command+".aliases"))
    	{
            //Register the command alias list to a map
            plugin.getConfig().set("Command."+this.command+".aliases", new ArrayList<String>());
            plugin.saveConfig();
    	}
    	
    	for(String ali : plugin.getConfig().getStringList("Command."+this.command+".aliases"))
    	{
    		if(alias != null)
    			alias.add(ali);
    		else
    			alias = new ArrayList<String>(Arrays.asList(ali));
    	}
    	
    	//Create and load the parameters if set
    	if(params != null && !params.isEmpty()) //There are parameters in this command
    	{
    		//Loop all parameters
    		for(String param : params.getKeys())
    		{
    			param = param.toLowerCase();
    			//Create placeholders for parameter types
    			if(!plugin.getConfig().isSet("Command."+this.command+".params."+param))
    				plugin.getConfig().set("Command."+this.command+".params."+param, new ArrayList<String>());
    		}
    		plugin.saveConfig();
    		
    		//Loop all parameters
    		for(String param : params.getKeys())
    			for(String paramalias : plugin.getConfig().getStringList("Command."+this.command+".params."+param))
    				paramMap.put(paramalias.toLowerCase(), param);
    	}
    }
    
    /**
     * returns the possible command parameters
     */
    public List<String> getParams()
    {
    	return this.params;
    }
    
    final CommandMap getCommandMap() {
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
    
    private final class ReflectCommand extends Command {
        private AbstractCommand exe = null;
        protected ReflectCommand(String command) { super(command); }
        public void setExecutor(AbstractCommand exe) { this.exe = exe; }
        @Override public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (exe != null) { return exe.onCommand(sender, this, commandLabel, args); }
            return false;
        }
        
        @Override  public List<String> tabComplete(CommandSender sender, String alais, String[] args) {
            if (exe != null) { return exe.onTabComplete(sender, this, alais, args); }
            return null;
        }
    }
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
    
    
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    	return null;
    }
}
