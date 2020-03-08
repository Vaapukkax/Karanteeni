package net.karanteeni.karanteeniperms;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.LanguageLoader;
import net.karanteeni.karanteeniperms.command.PermissionsCommand;
import net.karanteeni.karanteeniperms.command.group.AddComponent;
import net.karanteeni.karanteeniperms.command.group.GroupComponent;
import net.karanteeni.karanteeniperms.command.group.GroupLoader;
import net.karanteeni.karanteeniperms.command.group.LongComponent;
import net.karanteeni.karanteeniperms.command.group.PrefixComponent;
import net.karanteeni.karanteeniperms.command.group.Rankname;
import net.karanteeni.karanteeniperms.command.group.RemoveComponent;
import net.karanteeni.karanteeniperms.command.group.SetComponent;
import net.karanteeni.karanteeniperms.command.group.ShortComponent;
import net.karanteeni.karanteeniperms.command.group.SuffixComponent;
import net.karanteeni.karanteeniperms.command.player.PermissionPlayerLoader;
import net.karanteeni.karanteeniperms.command.player.PlayerComponent;
import net.karanteeni.karanteeniperms.command.player.RanknameComponent;
import net.karanteeni.karanteeniperms.command.player.ResetComponent;
import net.karanteeni.karanteeniperms.command.player.ResetLongRanknameComponent;
import net.karanteeni.karanteeniperms.command.player.ResetPrefixComponent;
import net.karanteeni.karanteeniperms.command.player.ResetShortRanknameComponent;
import net.karanteeni.karanteeniperms.command.player.ResetSuffixComponent;
import net.karanteeni.karanteeniperms.command.player.SetGroupComponent;
import net.karanteeni.karanteeniperms.command.player.SetLongRanknameComponent;
import net.karanteeni.karanteeniperms.command.player.SetPrefixComponent;
import net.karanteeni.karanteeniperms.command.player.SetShortRanknameComponent;
import net.karanteeni.karanteeniperms.command.player.SetSuffixComponent;
import net.karanteeni.karanteeniperms.command.player.TestComponent;
import net.karanteeni.karanteeniperms.groups.player.BungeeGroupBuilder;
import net.karanteeni.karanteeniperms.groups.player.GroupDatabase;
import net.karanteeni.karanteeniperms.groups.player.GroupList;
import net.karanteeni.karanteeniperms.groups.player.JoinEvent;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;
import net.karanteeni.karanteeniperms.groups.player.PlayerModel;

public class KaranteeniPerms extends KaranteeniPlugin {
	
	/** Contains all the groups player has */
	private GroupList groupList;
	/** Contains cache of all players groups and gets them from database */
	private PlayerModel playerModel;
	
	public KaranteeniPerms() {
		super(true);
	}
	
	
	@Override
	public void onLoad() {
		// initialize the database
		try {
			GroupDatabase.initialize();
		} catch (SQLException e) {
			// error on database initialization, disable plugin
			this.setEnabled(false);
			Bukkit.getLogger().log(Level.SEVERE, "Failed to initialize database, shutting plugin");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onEnable() {
		try {
			this.playerModel = new PlayerModel(this);
			BungeeGroupBuilder builder = new BungeeGroupBuilder();
			getServer().getMessenger().registerOutgoingPluginChannel(this, "karanteeniperms:groups");
			getServer().getMessenger().registerIncomingPluginChannel(this, "karanteeniperms:groups", builder);
			this.groupList = new GroupList(this, builder);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, 
					"Error happened trying to launch KaranteeniPerms, closing plugin...:", e);
			
			this.getPluginLoader().disablePlugin(this); //Disable this plugin to prevent further damage!
			return;
		}
		
		registerEvents();
		registerCommands();
	}
	
	
	@Override
	public void onDisable() {
		playerModel.clearAllPermissionPlayers();
	}
	
	
	/**
	 * Get the permission player of given players uuid
	 * @param uuid uuid of the permissionPlayer
	 * @return loaded PermissionPlayer or null
	 */
	public PermissionPlayer getPermissionPlayer(UUID uuid) {
		return playerModel.getPermissionPlayer(uuid);
	}
	
	
	/**
	 * Registers the events this plugin uses
	 */
	private final void registerEvents() {
		//Low level joinevent! Loads cache.
		getServer().getPluginManager().registerEvents(new JoinEvent(), this);
	}
	
	
	/**
	 * Registers the commands this plugin uses
	 */
	private final void registerCommands() {
		PermissionsCommand permCommand = new PermissionsCommand(this);
		GroupLoader groupLoader = new GroupLoader(true);
		groupLoader.setChainer(permCommand);
		
		// GROUP
		AddComponent gAddC = new AddComponent();
		gAddC.setChainer(permCommand);
		gAddC.setPermission("karanteeniperms.group.permission.add");
		gAddC.setLoader(groupLoader);
		RemoveComponent gRemC = new RemoveComponent();
		gRemC.setChainer(permCommand);
		gRemC.setPermission("karanteeniperms.group.permission.remove");
		gRemC.setLoader(groupLoader);
		
		// ---- SET
		PrefixComponent gPrefix = new PrefixComponent();
		gPrefix.setChainer(permCommand);
		gPrefix.setPermission("karanteeniperms.group.prefix");
		gPrefix.setLoader(groupLoader);
		SuffixComponent gSuffix = new SuffixComponent();
		gSuffix.setChainer(permCommand);
		gSuffix.setPermission("karanteeniperms.group.suffix");
		gSuffix.setLoader(groupLoader);
		// -------- Rankname
		LanguageLoader ll = new LanguageLoader(true);
		ll.setChainer(permCommand);
		ll.setLoader(groupLoader);
		LongComponent glc = new LongComponent();
		glc.setChainer(permCommand);
		glc.setPermission("karanteeniperms.group.rankname.long");
		glc.setLoader(ll);
		ShortComponent gsc = new ShortComponent();
		gsc.setChainer(permCommand);
		gsc.setPermission("karanteeniperms.group.rankname.short");
		gsc.setLoader(ll);
		Rankname gRankname = new Rankname();
		gRankname.setChainer(permCommand);
		gRankname.addComponent("long", glc);
		gRankname.addComponent("short", gsc);
		// ---------
		SetComponent gSetC = new SetComponent();
		gSetC.setChainer(permCommand);
		gSetC.addComponent("prefix", gPrefix);
		gSetC.addComponent("suffix", gSuffix);
		gSetC.addComponent("rankname", gRankname);
		// ----
		GroupComponent gComponent = new GroupComponent();
		gComponent.setChainer(permCommand);
		gComponent.addComponent("add", gAddC);
		gComponent.addComponent("remove", gRemC);
		gComponent.addComponent("set", gSetC);
		
		PermissionPlayerLoader permissionPlayerLoader = new PermissionPlayerLoader(true);
		permissionPlayerLoader.setChainer(permCommand);
		// PLAYER
		net.karanteeni.karanteeniperms.command.player.AddComponent pAddC = 
				new net.karanteeni.karanteeniperms.command.player.AddComponent();
		pAddC.setChainer(permCommand);
		pAddC.setPermission("karanteeniperms.player.permission.add");
		pAddC.setLoader(permissionPlayerLoader);
		net.karanteeni.karanteeniperms.command.player.RemoveComponent pRemC = 
				new net.karanteeni.karanteeniperms.command.player.RemoveComponent();
		pRemC.setChainer(permCommand);
		pRemC.setPermission("karanteeniperms.player.permission.add");
		pRemC.setLoader(permissionPlayerLoader);
		TestComponent tComponent = new TestComponent();
		tComponent.setChainer(permCommand);
		tComponent.setPermission("karanteeniperms.player.test");
		tComponent.setLoader(permissionPlayerLoader);
		// ---- SET
		SetPrefixComponent spc = new SetPrefixComponent();
		spc.setChainer(permCommand);
		spc.setPermission("karanteeniperms.player.prefix");
		spc.setLoader(permissionPlayerLoader);
		SetSuffixComponent ssc = new SetSuffixComponent();
		ssc.setChainer(permCommand);
		ssc.setPermission("karanteeniperms.player.suffix");
		ssc.setLoader(permissionPlayerLoader);
		SetGroupComponent sgc = new SetGroupComponent();
		sgc.setChainer(permCommand);
		sgc.setPermission("karanteeniperms.player.group");
		PermissionPlayerLoader permissionPlayerLoader_ = new PermissionPlayerLoader(true);
		permissionPlayerLoader_.setChainer(permCommand);
		permissionPlayerLoader_.setLoader(groupLoader);
		sgc.setLoader(permissionPlayerLoader_);
		// -------- Rankname
		SetLongRanknameComponent slrc = new SetLongRanknameComponent();
		slrc.setChainer(permCommand);
		slrc.setPermission("karanteeniperms.player.rankname.long");
		slrc.setLoader(permissionPlayerLoader);
		SetShortRanknameComponent ssrc = new SetShortRanknameComponent();
		ssrc.setChainer(permCommand);
		ssrc.setPermission("karanteeniperms.player.rankname.short");
		ssrc.setLoader(permissionPlayerLoader);
		RanknameComponent pRankC = new RanknameComponent();
		pRankC.setChainer(permCommand);
		pRankC.addComponent("long", slrc);
		pRankC.addComponent("short", ssrc);
		// --------
		net.karanteeni.karanteeniperms.command.player.SetComponent pSetC = 
				new net.karanteeni.karanteeniperms.command.player.SetComponent();
		pSetC.setChainer(permCommand);
		pSetC.addComponent("prefix", spc);
		pSetC.addComponent("suffix", ssc);
		pSetC.addComponent("group", sgc);
		pSetC.addComponent("rankname", pRankC);
		// ----
		
		// ---- RESET
		ResetPrefixComponent rspc = new ResetPrefixComponent();
		rspc.setChainer(permCommand);
		rspc.setPermission("karanteeniperms.player.prefix");
		rspc.setLoader(permissionPlayerLoader);
		ResetSuffixComponent rssc = new ResetSuffixComponent();
		rssc.setChainer(permCommand);
		rssc.setPermission("karanteeniperms.player.suffix");
		rssc.setLoader(permissionPlayerLoader);
		// -------- Rankname
		ResetLongRanknameComponent rlrc = new ResetLongRanknameComponent();
		rlrc.setChainer(permCommand);
		rlrc.setPermission("karanteeniperms.player.rankname.long");
		rlrc.setLoader(permissionPlayerLoader);
		ResetShortRanknameComponent rsrc = new ResetShortRanknameComponent();
		rsrc.setChainer(permCommand);
		rsrc.setPermission("karanteeniperms.player.rankname.short");
		rsrc.setLoader(permissionPlayerLoader);
		RanknameComponent pRankComponent = new RanknameComponent();
		pRankComponent.setChainer(permCommand);
		pRankComponent.addComponent("long", rlrc);
		pRankComponent.addComponent("short", rsrc);
		// --------
		ResetComponent resetC = new ResetComponent();
		resetC.setChainer(permCommand);
		resetC.addComponent("prefix", rspc);
		resetC.addComponent("suffix", rssc);
		resetC.addComponent("rankname", pRankComponent);
		// ----
		
		PlayerComponent pComponent = new PlayerComponent();
		pComponent.setChainer(permCommand);
		pComponent.addComponent("add", pAddC);
		pComponent.addComponent("remove", pRemC);
		pComponent.addComponent("set", pSetC);
		pComponent.addComponent("reset", resetC);
		pComponent.addComponent("test", tComponent);
		
		// COMMON
		permCommand.addComponent("group", gComponent);
		permCommand.addComponent("player", pComponent);
		
		permCommand.register();
		/*(new CommandPerms(this,
			"permissions", "/permissions", "Manages the permissions of groups and players",
			getDefaultMsgs().defaultNoPermission())).register();*/
	}
	
	
	/**
	 * Returns the current groupmodel
	 * @return
	 */
	public GroupList getGroupList()
	{ return this.groupList; }
	
	
	/**
	 * Returns the playermodel of this plugin
	 * playermodel is used to access players group (database/cache)
	 * @return Class to access the group of player
	 */
	public PlayerModel getPlayerModel()
	{ return this.playerModel; }
}