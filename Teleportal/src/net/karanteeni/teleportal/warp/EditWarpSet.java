package net.karanteeni.teleportal.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

public class EditWarpSet extends CommandComponent implements TranslationContainer {
	private static final String WARP = "%warp%";
	private static final String WARP_NAME = "%warp-name%";
	private static final String PERMISSION = "%permission%";
	
	
	@Override
	protected boolean runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			Teleportal.getMessager().sendMessage(sender, Sounds.NO.get(),
					Prefix.NEGATIVE +
					Teleportal.getDefaultMsgs().defaultNotForConsole());
			return true;
		}
		
		// check if there's correct amount of parameters
		if(args.length != 1 && args.length != 2 && args.length != 3)
			return false;
		
		String name = null;
		String displayname = null;
		String permission = null;
		
		// get the warp displayname
		if(args.length == 1) {
			name = args[0].toLowerCase();
			displayname = args[0].replace('_', ' ');
		}
		else if(args.length > 1) {
			name = args[1].toLowerCase();
			displayname = args[0].replace('_', ' ');
			
			if(args.length == 3)
				permission = args[2].toLowerCase();
		}
		
		
		Warp warp = Warp.loadWarp(name, sender);
		
		// the warp with name already exists
		if(warp != null) {
			Teleportal.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE +
					Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "warp.exists")
					.replace(WARP, warp.getName()));
			return true;
		}
			
			
		// create a new warp
		warp = new Warp(name, displayname, ((Player)sender).getLocation());
		
		if(permission == null) {
			if(!warp.save()) {
				Teleportal.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
						Prefix.ERROR + Teleportal.getDefaultMsgs().databaseError(sender));
				return true;
			}
			Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL + 
					Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "warp.created")
					.replace(WARP, name).replace(WARP_NAME, displayname));
		} else {
			warp.setPermission(permission);
			if(!warp.save()) {
				Teleportal.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
						Prefix.ERROR + Teleportal.getDefaultMsgs().databaseError(sender));
				return true;
			}
			Teleportal.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL + 
					Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), sender, "warp.created-permission")
					.replace(WARP, name).replace(WARP_NAME, displayname).replace(PERMISSION, permission));
		}
		
		return true;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"warp.exists", 
				"Warp with name '" + WARP + "' already exists");
		Teleportal.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"warp.created", 
				"Created a new warp '" + WARP + "' with displayname '" + WARP_NAME + "'");
		Teleportal.getTranslator().registerTranslation(
				this.chainer.getPlugin(), 
				"warp.created-permission", 
				"Created a new warp '" + WARP + "' with displayname '" + WARP_NAME + "' that requires permission '"
				+ PERMISSION + "' to be used");
	}


	@Override
	protected void onRegister() {
		this.registerTranslations();
	}
}
