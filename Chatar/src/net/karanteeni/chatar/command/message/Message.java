package net.karanteeni.chatar.command.message;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.chatar.events.custom.PlayerMessageEvent;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.ChatColor;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;

public class Message extends CommandChainer implements TranslationContainer {
	public Message() {
		super(Chatar.getPlugin(Chatar.class), 
				"message", 
				"/message <player> <msg>", 
				"Sends a semi-private message to another player", 
				Chatar.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		// register the formatter
		registerTranslations();
		/*Chatar pl = ((Chatar)plugin);
		pl.registerFormat("message.send", "§7YOU§6 > %player%§e : §r%msg%");
		pl.registerFormat("message.receive", "%player%§6 >§7 YOU§e : §r%msg%");*/
	}

	
	@Override
	protected void autofillEffect(CommandSender sender, String[] args) { }
	
	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		if(args.length < 2)
			return CommandResult.INVALID_ARGUMENTS;
		
		Player receiver = this.getObject("core.player");
		
		if(receiver.getUniqueId().equals(((Player)sender).getUniqueId())) {
			return new CommandResult(
					Chatar.getTranslator().getRandomTranslation(plugin, sender, "message.self"), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		}
		
		
		String message;
		StringBuilder builder = new StringBuilder(args[1]);
		// format the args into a message
		for(int i = 2; i < args.length; ++i) {
			builder.append(" ");
			builder.append(args[i]); 
		}
		message = builder.toString();
		
		// translate color codes
		if(sender.hasPermission("chatar.message.color"))
			message = ChatColor.translateColor(message);
		if(sender.hasPermission("chatar.message.format"))
			message = ChatColor.translateFormat(message);
		if(sender.hasPermission("chatar.message.magic"))
			message = ChatColor.translateMagic(message);
		if(sender.hasPermission("chatar.message.rgb"))
			message = ChatColor.translateHexColorCodes(message);
		
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
					put(receiver, msg);
				}});
		
		// send the messages
		Chatar.getMessager().sendMessage(sender, Sounds.NONE.get(), msg1.get((Player)sender));
		Chatar.getMessager().sendMessage(receiver, Sounds.NOTIFICATION.get(), msg2.get(receiver));
		
		// store the receiver and sender to KPlayers to allow /reply usage
		KPlayer msender = KPlayer.getKPlayer((Player)sender);
		KPlayer mreceiver = KPlayer.getKPlayer(receiver);
		msender.setData(plugin, "message", mreceiver.getPlayer().getUniqueId());
		mreceiver.setData(plugin, "message", msender.getPlayer().getUniqueId());*/
		
		// create a new formattable message event
		PlayerMessageEvent event = new PlayerMessageEvent((Player)sender, message, receiver);
		Bukkit.getPluginManager().callEvent(event);
		
		return CommandResult.SUCCESS;
	}


	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerRandomTranslation(plugin, "message.self", "You cannot message yourself!");
	}
}
