package net.karanteeni.teleportal.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.teleportal.Teleportal;

/**
 * Component for editwarp command. This component is responsible for editing the warp permission
 * @author Nuubles
 *
 */
public class EditWarpPermission extends CommandComponent implements TranslationContainer {

	/**
	 * Initialize the warp permission editor component
	 * @param loader warp loader
	 */
	public EditWarpPermission(CommandLoader loader) {
		super(loader);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerTranslations() {
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"warp.permission.set", 
				"Warp %warpname% (%warp%) now requires permission %permission%");
		Teleportal.getTranslator().registerTranslation(this.chainer.getPlugin(), 
				"warp.permission.removed", 
				"Warp %warpname% (%warp%) no longer requires permission");
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean runComponent(CommandSender sender, Command arg1, String arg2, String[] args) {
		Warp warp = this.chainer.getObject("warp");
		
		// set the permission to this warp
		if(args.length == 1) // no permission set = clear perm
			warp.setPermission(null);
		else if(args.length == 2) // permission set = set permission to given permission (only one!)
			warp.setPermission(args[1].toLowerCase());
		else
			return false;
		
		// save the modified warp
		if(!warp.save()) {
			Teleportal.getMessager().sendMessage(sender, 
					Sounds.ERROR.get(),
					Prefix.ERROR +
					Teleportal.getDefaultMsgs().databaseError(sender));
			return true;
		}
		
		// saving was successful, return result
		if(args.length == 1) // permission cleared
			Teleportal.getMessager().sendMessage(sender, 
					Sounds.SETTINGS.get(), 
					Prefix.POSITIVE +
					Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), 
						sender, 
						"warp.permission.removed")
						.replace("%warp%", warp.getName())
						.replace("%warpname%", warp.getDisplayName()));
		else // permission set
			Teleportal.getMessager().sendMessage(sender, 
				Sounds.SETTINGS.get(), 
				Prefix.POSITIVE +
				Teleportal.getTranslator().getTranslation(this.chainer.getPlugin(), 
					sender, 
					"warp.permission.set")
					.replace("%warp%", warp.getName())
					.replace("%warpname%", warp.getDisplayName())
					.replace("%permission%", warp.getPermission()));
		return true;
	}
}
