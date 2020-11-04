package net.karanteeni.chatar.command.mail;

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.chatar.events.custom.MailSendEvent;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.DisplayFormat;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.command.defaultcomponent.StringCombinerLoader;
import net.karanteeni.core.information.text.Prefix;

public class SendComponent extends CommandComponent {
	private static int maxMailCount;
	
	@Override
	protected void onRegister() {
		Chatar plugin = Chatar.getPlugin(Chatar.class);
		if(!plugin.getConfig().isSet("mailbox-size")) {
			plugin.getConfig().set("mailbox-size", 25);
			plugin.saveConfig();
		}
		maxMailCount = plugin.getConfig().getInt("mailbox-size");
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		UUID senderUuid = null;
		UUID receiver = null;
		
		// Get parsed receiver
		if(this.chainer.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE)) {
			receiver = this.chainer.getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE);
		} else if (this.chainer.hasData(PlayerLoader.PLAYER_KEY_SINGLE)) {
			receiver = this.chainer.<Player>getObject(PlayerLoader.PLAYER_KEY_SINGLE).getUniqueId();
		} else {
			return CommandResult.INVALID_ARGUMENTS;
		}
		
		if(sender instanceof Player)
			senderUuid = ((Player)sender).getUniqueId();
		
		if(!this.chainer.hasData(StringCombinerLoader.STRING_COMBINER_RESULT))
			return CommandResult.INVALID_ARGUMENTS;
		String message = this.chainer.getObject(StringCombinerLoader.STRING_COMBINER_RESULT);
		
		/*MailCommand mailCommand = (MailCommand)this.chainer;
		List<Mail> existingMails = mailCommand.getMailDatabase().getMail(receiver);
		
		// Verify that the players inbox is not full
		if(existingMails == null)
			return CommandResult.ERROR;
		else if(existingMails.size() >= maxMailCount) {
			String receiverName = Chatar.getPlayerHandler().getName(receiver);
			return new CommandResult(Prefix.NEGATIVE +
					Chatar.getTranslator().getTranslation(Chatar.getPlugin(Chatar.class), sender, "mail.inbox-full")
						.replace(MailCommand.TAG_RECEIVER, receiverName),
					ResultType.INVALID_ARGUMENTS,
					DisplayFormat.MESSAGE);
		}*/
		
		Mail mail = new Mail(senderUuid, receiver, message);
		Bukkit.broadcastMessage(message);
		Bukkit.getServer().getPluginManager().callEvent(new MailSendEvent(mail));
		return CommandResult.SUCCESS;
	}
}
