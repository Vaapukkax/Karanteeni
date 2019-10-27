package net.karanteeni.foxetbungee.ban;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import net.karanteeni.foxetbungee.Foxet;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BanEvent implements Listener {
	private Foxet plugin;
	private static final String REQUEST_BAN_REASONS = "request-reasons";
	
	public BanEvent(Foxet plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Plugin has requested the ban reasons, send them back
	 * @param event
	 */
	@EventHandler
	public void requestReasons(PluginMessageEvent event) {
		if(!event.getTag().equals("foxet:ban"))
			return;
		
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
		try {
			String subChannel = in.readUTF();
			if(!subChannel.equals(REQUEST_BAN_REASONS)) // are we requesting ban reasons
				return;
			
			// get the locale in which the reasons are being requested in
			String loc = in.readUTF();
			Locale locale = null;
			if(loc != null)
				locale = Locale.forLanguageTag(loc);
			
			// get all reason icons, lengths and translations
			HashMap<String, String> reasonTranslations = plugin.getDataManager().getTranslatedReasons(locale);
			HashMap<String, String> reasonIcons = plugin.getDataManager().getReasonIcons();
			HashMap<String, Long> reasonLengths = plugin.getDataManager().getReasonLengths();
			// create stringbuffers to allow conversion to strings
			StringBuffer keys = new StringBuffer();
			StringBuffer translations = new StringBuffer();
			StringBuffer lengths = new StringBuffer();
			StringBuffer icons = new StringBuffer();
			
			// convert the values into string
			for(Entry<String, String> entry : reasonTranslations.entrySet()) {
				keys.append(entry.getKey());
				keys.append(",");
				translations.append(entry.getValue());
				translations.append(",");
				lengths.append(reasonLengths.get(entry.getKey()));
				lengths.append(",");
				icons.append(reasonIcons.get(entry.getKey()));
				icons.append(",");
			}
			
			// remove the comma from strings
			keys.deleteCharAt(keys.length()-1);
			translations.deleteCharAt(translations.length()-1);
			lengths.deleteCharAt(lengths.length()-1);
			icons.deleteCharAt(icons.length()-1);

			// send the reasons to spigot
			sendBanReasonsToBukkit(
					keys.toString(), // keys to use for reasons
					translations.toString(), // translations for keys
					icons.toString(), // icons to display in the menu
					lengths.toString(), // lengths for automatic lengths
					in.readUTF(), // players about to get banned
					(Server)event.getSender());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Sends the ban reasons back to bukkit after receiving a call to the above requestReasons method
	 * @param channel channel to send to
	 * @param keys key in order
	 * @param translations translations in the same order as keys
	 * @param server server to send to
	 */
	private void sendBanReasonsToBukkit( 
			String keys, 
			String translations, 
			String icons, 
			String lengths, 
			String bannablePlayers, 
			Server server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(REQUEST_BAN_REASONS);
            out.writeUTF(keys); // keys to translations
            out.writeUTF(translations); // translations to keys
            out.writeUTF(icons); // icons to keys
            out.writeUTF(lengths); // lengths for ban reasons
            out.writeUTF(bannablePlayers); // players about to be banned
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // send the built ban reasons back to spigot
        server.sendData("foxet:ban", stream.toByteArray());
    }
}
