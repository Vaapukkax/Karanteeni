package net.karanteeni.chatar.events.custom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.karanteeni.chatar.Chatar;
import net.karanteeni.chatar.command.mail.Mail;
import net.karanteeni.chatar.command.mail.MailCommand;
import net.karanteeni.chatar.command.mail.MailDatabase;
import net.karanteeni.core.information.sounds.Sounds;

public class MailSendEvent extends Event implements Cancellable {
	/* Event handlers */
	private static final HandlerList handlers = new HandlerList();
	private static final PlayerMailEventListener listener = new PlayerMailEventListener();
	private boolean cancelled = false;
	
	/* Event data */
	private final Mail mail;
	
	public MailSendEvent(Mail mail) {
		this.mail = mail;
	}
	
	
	/**
	 * Returns the message of this event
	 * @return
	 */
	public Mail getMail() {
		return this.mail;
	}
	
		
	/**
	 * Registers this event to be used
	 * @param plugin plugin to which this will be registered
	 */
	public static void register(JavaPlugin plugin) {
		listener.init();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	
	/**
	 * This class will fire the PlayerChatEvent with the necessary information
	 * @author Nuubles
	 */
	private static class PlayerMailEventListener implements Listener {
		private MailDatabase db;
		private Chatar plugin;
		private int maxMailCount;
		
		public void init() {
			db = new MailDatabase();
			
			plugin = Chatar.getPlugin(Chatar.class);
			if(!plugin.getConfig().isSet("mail.max-count")) {
				plugin.getConfig().set("mail.max-count", 64);
				plugin.saveConfig();
			}
			maxMailCount = plugin.getConfig().getInt("mail.max-count");
		}
		
		
		@EventHandler(priority = EventPriority.MONITOR)
		private void onPlayerMailSend(MailSendEvent event) {
			// return if event is cancelled
			if(event.cancelled)
				return;

			int mailCount = db.getMail(event.mail.receiver).size() + 1;
			if(mailCount > maxMailCount) { // receivers mail box is full
				String receiverName = Chatar.getPlayerHandler().getName(event.mail.receiver);
				
				Chatar.getMessager().sendMessage(event.mail.trueSender, Sounds.NO.get(), 
						Chatar.getTranslator().getTranslation(plugin, event.mail.trueSender, "mail.inbox-full")
						.replace(MailCommand.TAG_RECEIVER, receiverName));
			} else { // send mail normally
				if(db.sendMail(event.mail)) {					
					Player receiver = Bukkit.getPlayer(event.mail.receiver);

					String receiverName = Chatar.getPlayerHandler().getName(event.mail.receiver);
					
					Chatar.getMessager().sendMessage(event.mail.trueSender, Sounds.CLICK_NEXT_PAGE.get(), 
							Chatar.getTranslator().getTranslation(plugin, event.mail.trueSender, "mail.sent")
							.replace(MailCommand.TAG_SENDER, event.mail.senderName)
							.replace(MailCommand.TAG_RECEIVER, receiverName)
							.replace(MailCommand.TAG_MAIL, event.mail.message));
					
					if(receiver != null && receiver.isOnline()) {
						Chatar.getMessager().sendActionBar(receiver, Sounds.NOTIFICATION.get(), 
								Chatar.getTranslator().getTranslation(plugin, receiver, "mail.unread-mails")
								.replace(MailCommand.TAG_MAIL_COUNT, Integer.toString(mailCount)));
					}
				} else {
					if(event.mail.trueSender != null) {
						Chatar.getMessager().sendMessage(event.mail.trueSender,
								Sounds.ERROR.get(),
								Chatar.getDefaultMsgs().databaseError(event.mail.trueSender));
					}
				}
			}
		}
	}
}
