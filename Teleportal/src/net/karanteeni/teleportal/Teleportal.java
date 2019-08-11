package net.karanteeni.teleportal;

import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.teleportal.noobspawn.JoinEvent;
import net.karanteeni.teleportal.noobspawn.NoobSpawnDelete;
import net.karanteeni.teleportal.noobspawn.NoobSpawnSet;
import net.karanteeni.teleportal.respawn.RespawnCommand;
import net.karanteeni.teleportal.respawn.RespawnDelete;
import net.karanteeni.teleportal.respawn.RespawnEvent;
import net.karanteeni.teleportal.respawn.RespawnSet;
import net.karanteeni.teleportal.spawn.SpawnCommand;
import net.karanteeni.teleportal.spawn.SpawnDelete;
import net.karanteeni.teleportal.spawn.SpawnSet;
import net.karanteeni.teleportal.warp.CommandWarpLoader;
import net.karanteeni.teleportal.warp.EditWarpCommand;
import net.karanteeni.teleportal.warp.EditWarpDelete;
import net.karanteeni.teleportal.warp.EditWarpMove;
import net.karanteeni.teleportal.warp.EditWarpPermission;
import net.karanteeni.teleportal.warp.EditWarpRename;
import net.karanteeni.teleportal.warp.EditWarpSet;
import net.karanteeni.teleportal.warp.WarpCommand;
import net.karanteeni.teleportal.warp.WarpsCommand;

/**
 * Main class of the Teleportal plugin. Manages command and event registrations
 * @author Nuubles
 *
 */
public class Teleportal extends KaranteeniPlugin {
	private static String KEY_PREFIX = "Plugin-functionality.";
	
	public Teleportal() {
		super(true);
	}

	
	@Override
	public void onEnable() {
		registerConfig();
		registerCommands();
		registerEvents();
		Bukkit.getConsoleSender().sendMessage("§eTeleportal has been enabled!");
	}
	
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§Teleportal has been disabled!");
	}
	
	
	private void registerConfig() {
		boolean save = false;
		//Check that all possible values are set in the config
		for(KEYS key : KEYS.values()) {
			if(!getSettings().isSet(KEY_PREFIX+key.toString())) {
				getSettings().set(KEY_PREFIX+key.toString(), true);
				save = true;
			}
		}
		
		if(save)
			saveSettings();
	}
	
	
	/**
	 * Registers the commands of the teleportal plugin
	 */
	private void registerCommands() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.WARP.toString())) {
			// editwarp command - edit warp
			CommandWarpLoader cwLoader = new CommandWarpLoader(true); // run before the previous param
			EditWarpDelete ewdelete = new EditWarpDelete(cwLoader);
			ewdelete.setPermission("teleportal.warp.edit.delete"); // set the delete warp permission
			EditWarpRename ewrename = new EditWarpRename(cwLoader);
			ewrename.setPermission("teleportal.warp.edit.rename"); // set the rename section permission
			EditWarpMove ewmove = new EditWarpMove(cwLoader);
			ewmove.setPermission("teleportal.warp.edit.move");
			EditWarpPermission ewpermission = new EditWarpPermission(cwLoader);
			ewpermission.setPermission("teleportal.warp.edit.permission");
			EditWarpSet ewset = new EditWarpSet();
			ewset.setPermission("teleportal.warp.edit.create");
			// EditWarpPermission ewp = new EditWarpPermission(cwLoader);
			EditWarpCommand ewc = new EditWarpCommand();
			ewc.addComponent("delete", ewdelete);
			ewc.addComponent("create", ewset);
			ewc.addComponent("move", ewmove);
			ewc.addComponent("rename", ewrename);
			ewc.addComponent("permission", ewpermission);
			ewc.register();
			
			// warp command - teleport to a warp
			WarpCommand wc = new WarpCommand(new CommandWarpLoader(true));
			wc.register();
			
			// warps command - show all warps
			WarpsCommand wsc = new WarpsCommand();
			wsc.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.SPAWN.toString())) {
			SpawnCommand sp = new SpawnCommand();
			sp.setPermission("teleportal.spawn.teleport.self");
			PlayerLoader pc = new PlayerLoader(true, false, false, false);
			sp.setLoader(pc);
			sp.register();
			
			SpawnDelete sd = new SpawnDelete();
			sd.setPermission("teleportal.spawn.delete");
			sd.register();
			
			SpawnSet ss = new SpawnSet();
			ss.setPermission("teleportal.spawn.set");
			ss.register();
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.NOOBSPAWN.toString())) {
			NoobSpawnDelete nsd = new NoobSpawnDelete();
			nsd.setPermission("teleportal.noobspawn.delete");
			nsd.register();
			
			NoobSpawnSet nss = new NoobSpawnSet();
			nss.setPermission("teleportal.noobspawn.set");
			nss.register();
		}
		
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.RESPAWN.toString())) {
			RespawnCommand rc = new RespawnCommand();
			
			RespawnDelete nsd = new RespawnDelete();
			nsd.setPermission("teleportal.respawn.delete");
			
			RespawnSet nss = new RespawnSet();
			nss.setPermission("teleportal.respawn.set");
			
			rc.addComponent("delete", nsd);
			rc.addComponent("set", nss);
			rc.register();
		}
	}
	
	
	/**
	 * Registers the events of the teleportal plugin
	 */
	private void registerEvents() {
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.NOOBSPAWN.toString())) {
			getServer().getPluginManager().registerEvents(new JoinEvent(), this);
		}
		
		if(getSettings().getBoolean(KEY_PREFIX+KEYS.RESPAWN.toString())) {
			getServer().getPluginManager().registerEvents(new RespawnEvent(this), this);
		}
	}
	
	/**
	 * Keys to access data in config. Which features of the plugin are enabled
	 * @author Nuubles
	 */
	private static enum KEYS {
		WARP, 
		SPAWN,
		NOOBSPAWN,
		RESPAWN
	}
}
