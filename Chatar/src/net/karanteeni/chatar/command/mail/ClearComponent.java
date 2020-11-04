package net.karanteeni.chatar.command.mail;

import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.command.CommandComponent;
import net.karanteeni.core.command.CommandResult;

public class ClearComponent extends CommandComponent {
	public static final String IntegerKey = "mailindex";
	@Override
	protected void onRegister() {
	}

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		UUID uuid = ((Player)sender).getUniqueId();
		int removedCount = 0;
		boolean deleteAll = false;
		
		if(this.chainer.hasData(IntegerKey)) {
			MailCommand mailCommand = (MailCommand)this.chainer;
			removedCount = mailCommand.getMailDatabase().removeMail(uuid, mailCommand.<Integer>getObject(IntegerKey));
		} else {
			MailCommand mailCommand = (MailCommand)this.chainer;
			deleteAll = true;
			removedCount = mailCommand.getMailDatabase().removeMail(uuid, Integer.MIN_VALUE);
		}
		
		if(deleteAll)
			Chatar.getTranslator().getTranslation(Chatar.getPlugin(Chatar.class), sender, "mail.delete-all")
				.replace(MailCommand.TAG_MAIL_COUNT, Integer.toString(removedCount));
		else
			Chatar.getTranslator().getTranslation(Chatar.getPlugin(Chatar.class), sender, "mail.delete-one")
				.replace(MailCommand.TAG_MAIL_INDEX, Integer.toString(removedCount));
		
		return CommandResult.SUCCESS;
	}

}
