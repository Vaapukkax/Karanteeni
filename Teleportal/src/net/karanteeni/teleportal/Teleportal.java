package net.karanteeni.teleportal;

import org.bukkit.Bukkit;
import net.karanteeni.core.KaranteeniPlugin;
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

	public Teleportal() {
		super(true);
	}

	
	@Override
	public void onEnable() {
		registerCommands();
		registerEvents();
		Bukkit.getConsoleSender().sendMessage("§Teleportal has been enabled!");
	}
	
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§Teleportal has been disabled!");
	}
	
	
	/**
	 * Registers the commands of the teleportal plugin
	 */
	private void registerCommands() {
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
	
	
	/**
	 * Registers the events of the teleportal plugin
	 */
	private void registerEvents() {
		
	}
}
