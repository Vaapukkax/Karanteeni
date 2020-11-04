package net.karanteeni.chatar.command.mail;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.information.translation.TranslationContainer;

public class MailCommand extends CommandChainer implements TranslationContainer {
	public static final String TAG_SENDER = "%sender%";
	public static final String TAG_RECEIVER = "%receiver%";
	public static final String TAG_MAIL = "%mail%";
	public static final String TAG_DATE = "%date%";
	public static final String TAG_AGO = "%ago%";
	public static final String TAG_MAIL_COUNT = "%count%";
	public static final String TAG_MAILS = "%mails%";
	public static final String TAG_MAIL_INDEX = "%index%";
	
	MailDatabase mailDatabase;
	
	public MailCommand(KaranteeniPlugin plugin) {
		super(plugin, "mail", "/mail", "read and send mails", Chatar.getDefaultMsgs().defaultNoPermission(), Arrays.asList());
		mailDatabase = new MailDatabase();
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.INVALID_ARGUMENTS;
		
		return CommandResult.SUCCESS;
	}
	
	
	public MailDatabase getMailDatabase() {
		return this.mailDatabase;
	}


	@Override
	public void registerTranslations() {
		Chatar.getTranslator().registerTranslation(plugin, "mail.sent", "[Mail] ["+TAG_SENDER+" > "+TAG_RECEIVER+"]: "
				+TAG_MAIL);
		Chatar.getTranslator().registerTranslation(plugin, "mail.read-row", "["+TAG_MAIL_INDEX+"] Sender: "+TAG_SENDER+" At: "
				+TAG_DATE+" which was "+TAG_AGO+" ago: "+TAG_MAIL);
		Chatar.getTranslator().registerTranslation(plugin, "mail.read-format", "==========["+TAG_MAIL_COUNT+" mail(s)]==========\n"
				+TAG_MAILS+"\n==========["+TAG_MAIL_COUNT+" mail(s)]==========");
		Chatar.getTranslator().registerTranslation(plugin, "mail.delete-one", "Deleted mail " + TAG_MAIL_INDEX);
		Chatar.getTranslator().registerTranslation(plugin, "mail.delete-all", "Deleted "+TAG_MAIL_COUNT+" mails");
		Chatar.getTranslator().registerTranslation(plugin, "mail.unread-mails", "You have "+TAG_MAIL_COUNT+" unread mails");
		Chatar.getTranslator().registerTranslation(plugin, "mail.no-mail", "You have no mail");
		Chatar.getTranslator().registerTranslation(plugin, "mail.inbox-full", "Mail box of " + TAG_RECEIVER + " is full");
	}
}
