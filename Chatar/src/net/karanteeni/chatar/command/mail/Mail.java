package net.karanteeni.chatar.command.mail;

import java.sql.Date;
import java.util.UUID;
import org.bukkit.command.CommandSender;

public class Mail {
	public CommandSender trueSender;
	public UUID sender;
	public UUID receiver;
	public String message;
	public String senderName;
	public int id;
	public Date date;
	
	public Mail(UUID sender, String senderName, UUID receiver, int id, String message, Date date) {
		this.sender = sender;
		this.senderName = senderName;
		this.receiver = receiver;
		this.message = message;
		this.id = id;
		this.date = date;
	}
	
	
	public Mail(UUID sender, UUID receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.id = -1;
		this.date = new Date(System.currentTimeMillis());
	}
}
