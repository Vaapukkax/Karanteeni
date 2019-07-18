package net.karanteeni.chatar.command.message;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.chatar.events.custom.PlayerMessageEvent;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.core.players.KPlayer;

public class Reply extends CommandChainer implements TranslationContainer {
	private static char[] COLORS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static char[] FORMATS = {'o', 'n', 'm', 'r', 'l'};
	private static char RANDOM = 'k';
	
	public Reply() {
		super(Chatar.getPlugin(Chatar.class), 
				"reply", 
				"/reply <msg>", 
				"Sends a semi-private message to another player", 
				Chatar.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		// register the formatter
		registerTranslations();
	}

	
	@Override
	protected void autofillEffect(CommandSender sender, String[] args) { }
	
	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		if(args.length < 1)
			return CommandResult.INVALID_ARGUMENTS;
		
		KPlayer mSender = KPlayer.getKPlayer((Player)sender);
		// check if player has messaged anyone or received messages
		if(!mSender.dataExists(plugin, "message")) {
			return new CommandResult(
					Chatar.getTranslator().getTranslation(plugin, sender, "message.not-message"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		}
		
		// get the messaged player
		UUID uuid = mSender.getObject(plugin, "message");
		Player mReceiver = Bukkit.getPlayer(uuid);
		
		// check if the messaged player is still online
		if(mReceiver == null || !mReceiver.isOnline()) {
			return new CommandResult(
					Chatar.getTranslator().getRandomTranslation(plugin, sender, "message.offline"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		
		String message = args[0];
		// format the args into a message
		for(int i = 1; i < args.length; ++i) {
			message += " " + args[i]; 
		}
		
		// set message colors
		if(sender.hasPermission("chatar.message.color"))
			for(char c : COLORS)
				message = message.replace("&"+c, "§"+c);
		// set message format
		if(sender.hasPermission("chatar.message.format"))
			for(char c : FORMATS)
				message = message.replace("&"+c, "§"+c);
		// set message random chars
		if(sender.hasPermission("chatar.message.magic"))
			message = message.replace("&"+RANDOM, "§"+RANDOM);
		
		// convert the message to basecomponent
		/*BaseComponent msg = new TextComponent(TextComponent.fromLegacyText(message));
		
		Chatar pl = (Chatar)plugin;
		// get sender msg
		HashMap<Player, BaseComponent> msg1 = 
				pl.getFormattedMessage(pl.getRawFormat("message.send"), (Player)sender, new HashMap<Player, BaseComponent>(){
					private static final long serialVersionUID = 1L;
				{
					put((Player)sender, msg);
				}});
		// get receiver msg
		HashMap<Player, BaseComponent> msg2 = 
				pl.getFormattedMessage(pl.getRawFormat("message.receive"), (Player)sender, new HashMap<Player, BaseComponent>(){
					private static final long serialVersionUID = 2L;
				{
					put(mReceiver, msg);
				}});
		
		// send the messages
		Chatar.getMessager().sendMessage(sender, Sounds.NONE.get(), msg1.get((Player)sender));
		Chatar.getMessager().sendMessage(mReceiver, Sounds.NOTIFICATION.get(), msg2.get(mReceiver));*/
		PlayerMessageEvent event = new PlayerMessageEvent((Player)sender, message, mReceiver);
		Bukkit.getPluginManager().callEvent(event);
		
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerTranslation(plugin, "message.not-message", "You haven't messaged anyone yet");
		Chatar.getTranslator().registerTranslation(plugin, "message.offline", "The player you're trying to reach is offline");
	}
}
