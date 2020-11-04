package net.karanteeni.chatar.command.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;

public class ReadComponent extends CommandComponent implements TranslationContainer {
	
	@Override
	protected void onRegister() {
		registerTranslations();
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		UUID receiver = null;
		
		// Reading mail from other players
		if(this.chainer.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE)) {
			receiver = this.chainer.getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE);
		} else if (this.chainer.hasData(PlayerLoader.PLAYER_KEY_SINGLE)) {
			receiver = this.chainer.<Player>getObject(PlayerLoader.PLAYER_KEY_SINGLE).getUniqueId();
		}
		
		if(receiver != null) {
			if(!sender.hasPermission("chatar.mail.read-others"))
				return CommandResult.NO_PERMISSION;
		}
		
		if(!(sender instanceof Player) && receiver == null) {
			return CommandResult.INVALID_ARGUMENTS;
		} else {
			receiver = ((Player)sender).getUniqueId();
		}
		
		MailCommand mailCommand = (MailCommand)this.chainer;
		List<Mail> mails = mailCommand.getMailDatabase().getMail(receiver);
		
		if(mails == null) {
			return CommandResult.ERROR;
		} else if(mails.size() == 0) {
			Chatar.getMessager().sendMessage(
					sender,
					Sounds.CLICK_PREVIOUS_PAGE.get(),
					Prefix.NEUTRAL +
					Chatar.getTranslator().getTranslation(Chatar.getPlugin(Chatar.class), sender, "mail.no-mail"));
		} else {
			StringBuffer rows = new StringBuffer();
			int index = 0;
			Iterator<Mail> iter = mails.iterator();
			SimpleDateFormat timeFormat =
					new SimpleDateFormat(Chatar.getTranslator().getTranslation(
							Chatar.getPlugin(Chatar.class),
							sender,
							"mail.timestamp"));
			SimpleDateFormat agoTimeFormat =
					new SimpleDateFormat(Chatar.getTranslator().getTranslation(
							Chatar.getPlugin(Chatar.class),
							sender,
							"mail.ago-timestamp"));

			// Format the message rows
			while(iter.hasNext()) {
				Mail mail = iter.next();
				Date ago = new Date(System.currentTimeMillis() - mail.date.getTime());
				rows.append(Chatar.getTranslator().getTranslation(Chatar.getPlugin(Chatar.class), sender, "mail.read-row")
					.replace(MailCommand.TAG_MAIL_INDEX, Integer.toString(++index))
					.replace(MailCommand.TAG_SENDER, mail.senderName)
					.replace(MailCommand.TAG_DATE, timeFormat.format(new Date(mail.date.getTime())))
					.replace(MailCommand.TAG_AGO, agoTimeFormat.format(new Date(ago.getTime())))
					.replace(MailCommand.TAG_MAIL, mail.message));
				if(iter.hasNext())
					rows.append("\n");
			}
			
			Chatar.getMessager().sendMessage(sender,
					Sounds.CLICK_PREVIOUS_PAGE.get(),
					Chatar.getTranslator().getTranslation(Chatar.getPlugin(Chatar.class), sender, "mail.read-format")
						.replace(MailCommand.TAG_MAIL_COUNT, Integer.toString(mails.size()))
						.replace(MailCommand.TAG_MAILS, rows.toString()));
		}
			
		return CommandResult.SUCCESS;
	}

	@Override
	public void registerTranslations() {
		// https://www.journaldev.com/17899/java-simpledateformat-java-date-format
		Chatar.getTranslator().registerTranslation(Chatar.getPlugin(Chatar.class), "mail.timestamp", "yyyy.MM.dd HH:mm:ss");
		Chatar.getTranslator().registerTranslation(Chatar.getPlugin(Chatar.class), "mail.ago-timestamp", "dd HH:mm:ss");
	}
}
