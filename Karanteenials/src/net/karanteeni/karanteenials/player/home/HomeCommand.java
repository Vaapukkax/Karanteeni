package net.karanteeni.karanteenials.player.home;

import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.Teleporter;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.karanteenials.functionality.Back;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.Group;

public class HomeCommand extends AbstractCommand implements TranslationContainer {
	private boolean 		safeTeleport 	= true;
	protected static String HOME_TAG 		= "%home%";
	protected static String PLAYER_TAG 		= "%player%";
	protected static String DEFAULT_NAME 	= "home";
	
	protected static String	NO_HOMES						= "home.none";
	protected static String	HOMES							= "home.own.homes";
	protected static String	HOMES_OF						= "home.other.homes";
	protected static String	HOME_OWN_TELEPORTED				= "home.own.teleported";
	protected static String HOME_OTHER_TELEPORTED			= "home.other.teleported";
	protected static String HOME_OWN_SET					= "home.own.set";
	protected static String HOME_OTHER_SET					= "home.other.set"; 
	protected static String HOME_OWN_ALREADY_EXISTS			= "home.own.already-exists";
	protected static String HOME_OTHER_ALREADY_EXISTS		= "home.other.already-exists"; 
	protected static String HOME_OWN_MOVED					= "home.own.moved";
	protected static String HOME_OTHER_MOVED				= "home.other.moved"; 
	protected static String HOME_OWN_REMOVED				= "home.own.remove";
	protected static String HOME_OTHER_REMOVED				= "home.other.remove";
	protected static String HOME_OWN_DOES_NOT_EXIST			= "home.own.not-exists";
	protected static String HOME_OTHER_DOES_NOT_EXIST		= "home.other.not-exists";
	protected static String HOME_OWN_DANGEROUS_LOCATION		= "home.own.too-dangerous";
	protected static String HOME_OTHER_DANGEROUS_LOCATION	= "home.own.too-dangerous";
	protected static String HOME_OWN_MAXCOUNT				= "home.own.limit-reached";
	protected static String HOME_OTHER_MAXCOUNT				= "home.other.limit-reached";
	
	
	public HomeCommand(KaranteeniPlugin plugin) {
		super(plugin, "home", 
				"/home [<name>]", 
				"Teleports player to their home", 
				KaranteeniPlugin.getDefaultMsgs().defaultNoPermission());
		registerTranslations();
		createHomeTable();
		initConfig();
		initGroupData();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) 
	{
		if(!(sender instanceof Player)) //Don't allow console to access this command
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		Player player = (Player)sender;
		
		if(args.length != 0 && args.length != 1 && args.length != 2) //Check parameter correctness
			KaranteeniPlugin.getMessager().sendMessage(sender, //Invalid arguments
					Sounds.NO.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
		
		String homeName = DEFAULT_NAME; //name of the home
		//Entry<String,UUID> nameUUID = new SimpleEntry<String,UUID>(homeName, player.getUniqueId()); //uuid of the home owner
		UUID uuid = player.getUniqueId(); //UUID of the player whose home will be searched
		
		if(args.length == 1) //Get the name of the home from arguments
		{
			homeName = args[0];
			//nameUUID = getUUID(nameUUID.getValue(), args[0]); //Seperate uuid from argument
			//homeName = getHomeNameFromArgument(args[0]).toLowerCase(); //Separate homename from argument
		}
		else if(args.length == 2)
		{
			homeName = args[1];
			uuid = KaranteeniPlugin.getPlayerHandler().getUUID(args[0]);
		}
		
		if(uuid == null) { //Player was not found
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().playerNotFound(sender, (args.length>0)?args[0]:""));
			return true;
		}
		
		//Is the home this player's own home
		boolean ownHome = player.getUniqueId().equals(uuid);
		
		//Check that player has the permission to use this command
		if((ownHome && !player.hasPermission("karanteenials.home.own.use")) ||
				(!ownHome && !player.hasPermission("karanteenials.home.other.use"))) {
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
					Prefix.NEGATIVE+KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		//Load home
		Home home = Home.getHome(uuid, homeName);
		
		if(home == null) //Home was not found 
		{
			messageHomeNotFound(player, homeName, (args.length>0)?args[0]:"", ownHome);
			return true;
		}
		
		//Create new Back object from teleportation
		Back back = new Back(player);
		back.setBackLocation(player.getLocation()); //Set the back location
		Teleporter teleporter = new Teleporter(home.getLocation()); //Create a new teleporter
		if(teleporter.teleport(player, 
				(player.getGameMode() == GameMode.SURVIVAL || //Check safe teleport only from survival and adventure players
				player.getGameMode() == GameMode.ADVENTURE), false, true, TeleportCause.PLUGIN) == null && 
				safeTeleport) //Teleport player and check if teleport was successful
		{
			//Teleportation was too unsafe
			if(ownHome)
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HOME_OWN_DANGEROUS_LOCATION)
					.replace(HOME_TAG, homeName));
			else
				KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(plugin, sender, HOME_OTHER_DANGEROUS_LOCATION)
						.replace(HOME_TAG, homeName)
						.replace(PLAYER_TAG, (args.length>0)?args[0]:""));
		}
		else
			messageHomeTeleportation(player, home.getName(), (args.length>0)?args[0]:"", ownHome);
		
		return true;
	}
	
	/**
	 * Messages player that the home they entered was not found
	 * @param player player to whom the message will be sent
	 * @param homeName name of the home not found
	 * @param playername name of the other player if other players home
	 * @param ownHome is this the players own home
	 */
	private void messageHomeNotFound(Player player, String homeName, String playername, boolean ownHome)
	{
		if(ownHome) 
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HOME_OWN_DOES_NOT_EXIST)
					.replace(HOME_TAG, homeName));
		else
			KaranteeniPlugin.getMessager().sendMessage(player, Sounds.NO.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HOME_OTHER_DOES_NOT_EXIST)
					.replace(HOME_TAG, homeName)
					.replace(PLAYER_TAG, playername));
	}
	
	/**
	 * Message player about the teleportation to a specific home
	 * @param player Player to whom the message will be sent
	 * @param homename name of the home
	 * @param playername name of the homes owner (if others home)
	 * @param ownHome is this players own home
	 */
	private void messageHomeTeleportation(Player player, String homename, String playername, boolean ownHome)
	{
		/*if(ownHome) 		//Message player that they were teleported own home
			KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.TELEPORT.get(), 
				Prefix.NEUTRAL + 
				KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HOME_OWN_TELEPORTED)
				.replace(HOME_TAG, homename));
		else		//Message player that they were teleported others home
			KaranteeniPlugin.getMessager().sendActionBar(player, Sounds.TELEPORT.get(), 
					Prefix.NEUTRAL + 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HOME_OTHER_TELEPORTED)
					.replace(HOME_TAG, homename)
					.replace(PLAYER_TAG, playername));*/
		
		//Send player a bossbar showing the home they teleported to
		if(ownHome)
			KaranteeniPlugin.getMessager().sendBossbar(player, Sounds.TELEPORT.get(), 3, 5000, true, 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HOME_OWN_TELEPORTED)
					.replace(HOME_TAG, homename));
		else
			KaranteeniPlugin.getMessager().sendBossbar(player, Sounds.TELEPORT.get(), 3, 5000, true, 
					KaranteeniPlugin.getTranslator().getTranslation(plugin, player, HOME_OTHER_TELEPORTED)
					.replace(HOME_TAG, homename)
					.replace(PLAYER_TAG, playername));
	}
	
	
	/**
	 * Creates a database table for homes into the database
	 */
	private void createHomeTable() {
		try {
			Statement st = KaranteeniPlugin.getDatabaseConnector().getStatement();
			st.execute("CREATE TABLE IF NOT EXISTS home ( "+
				"UUID VARCHAR(128) NOT NULL, "+
				"name VARCHAR(64) NOT NULL, "+
				"location VARCHAR(128) NOT NULL, "+
				"serverID VARCHAR(64) NOT NULL, "+
				"FOREIGN KEY (UUID) REFERENCES player(UUID), "+
				"FOREIGN KEY (serverID) REFERENCES server(ID), "+
				"PRIMARY KEY (UUID,name));");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Initializes the default config values
	 */
	private void initConfig() {
		if(!plugin.getConfig().isSet("Home.defaultname"))  {
			plugin.getConfig().set("Home.defaultname", DEFAULT_NAME);
			plugin.saveConfig();
		}
		//What is the default name for homes
		DEFAULT_NAME = plugin.getConfig().getString("Home.defaultname");
		
		if(!plugin.getConfig().isSet("Home.safe-teleport"))  {
			plugin.getConfig().set("Home.safe-teleport", true);
			plugin.saveConfig();
		}
		//Is the /home teleportation safe
		safeTeleport = plugin.getConfig().getBoolean("Home.safe-teleport");
	}
	
	
	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_TELEPORTED, 
				"You teleported to your home "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_SET, 
				"You set a new home "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_ALREADY_EXISTS, 
				"You already have a home called "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_MOVED, 
				"Your home "+HOME_TAG+" was moved here!");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_REMOVED, 
				"You removed your home "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_DOES_NOT_EXIST, 
				"Home "+HOME_TAG+" does not exist!");
		
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_TELEPORTED, 
				"You teleported to "+PLAYER_TAG+"'s home "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_SET, 
				"You set a new home "+HOME_TAG+" to player "+PLAYER_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_ALREADY_EXISTS, 
				PLAYER_TAG+" already have a home called "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_MOVED, 
				PLAYER_TAG+"'s home "+HOME_TAG+" was moved here!");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_REMOVED, 
				"You removed "+PLAYER_TAG+"'s home "+HOME_TAG);
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_DOES_NOT_EXIST, 
				"Player "+PLAYER_TAG+" does not have a home named "+HOME_TAG+"!");
		
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OWN_DANGEROUS_LOCATION, 
				"Home "+HOME_TAG+" is in a dangerous location!");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOME_OTHER_DANGEROUS_LOCATION, 
				PLAYER_TAG+"'s home "+HOME_TAG+" is in a dangerous location!");
		
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOMES, 
				"Homes");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				HOMES_OF, 
				PLAYER_TAG+"'s homes");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, 
				NO_HOMES, 
				"No homes");
		
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, HOME_OWN_MAXCOUNT, 
				"You have reached the max limit of homes!");
		KaranteeniPlugin.getTranslator().registerTranslation(plugin, HOME_OTHER_MAXCOUNT, 
				PLAYER_TAG+" has reached the max limit of homes!");
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		Player player = (Player)sender;
		if(args.length == 1 && sender.hasPermission("karanteenials.home.own.use")) {
			List<Home> homes = Home.getHomes(player.getUniqueId());
			List<String> params = new LinkedList<String>();
			
			for(Home home : homes)
				params.add(home.getName());
			
			return filterByPrefix(params, args[0]);
		} else if(args.length == 2 && sender.hasPermission("karanteenials.home.other.use")) {
			UUID uuid = KaranteeniPlugin.getPlayerHandler().getUUID(args[0]);
			
			if(uuid == null)
				return null;
			
			List<Home> homes = Home.getHomes(uuid);
			List<String> params = new LinkedList<String>();
			
			for(Home home : homes)
				params.add(home.getName());
			
			return filterByPrefix(params, args[1]);
		}
		
		return null;
	}
	
	
	/**
	 * Adds the home max limit data to each group
	 */
	private void initGroupData() {
		KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		Collection<Group> groups = perms.getGroupModel().getLocalGroupList().getGroups();
		
		//Set 10 homes as the default home count limit
		for(Group group : groups) {
			group.setCustomData(plugin, "limit.home", 10);
			group.saveGroup();
		}
	}
}
