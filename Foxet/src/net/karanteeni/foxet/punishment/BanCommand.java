package net.karanteeni.foxet.punishment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import net.karanteeni.core.command.CommandChainer;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.defaultcomponent.PlayerLoader;
import net.karanteeni.foxet.Foxet;

public class BanCommand extends CommandChainer implements PluginMessageListener {

	public BanCommand() {
		super(Foxet.getPlugin(Foxet.class), "autoban", "aaa", "aaa", "aaa", Arrays.asList());
	}

	
	/**
	 * Request ban reasons to be displayed in inventory menu
	 * @param player player to whom the message is translated to
	 */
	public void requestReasons(Player player, List<UUID> uuid){
        // convert the list of uuids to a string
		StringBuilder builder = new StringBuilder();
		for(UUID uid : uuid)
			builder.append(uid.toString()).append(",");
		builder.replace(builder.length()-1, builder.length(), ""); // elem,elem,elem,elem
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("request-reasons"); // subchannel
            //out.writeUTF(player.getUniqueId().toString()); // player who requests the reasons
            out.writeUTF(player.getLocale()); // locale to translate to
            out.writeUTF(builder.toString()); // uuids of players to be banned
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //player.sendPluginMessage(Foxet.getPlugin(Foxet.class), "foxet:ban", b.toByteArray());
        player.sendPluginMessage(Foxet.getPlugin(Foxet.class), "foxet:ban", b.toByteArray());
	}
	
	
	/**
	 * Receives the ban messages from bungee
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String subChannel = in.readUTF();
			if(subChannel.equals("request-reasons")) {
				 
				// convert the keys and translations into a map
				// UUID requester = UUID.fromString(in.readUTF()); // player who requested the ban
				Bukkit.broadcastMessage(p.getName());
				String[] keys_ = in.readUTF().split(",");
				String[] translations_ = in.readUTF().split(",");
				String[] icons_ = in.readUTF().split(",");
				String[] lengths_ = in.readUTF().split(",");
				
				// map the translations to a map
				HashMap<String,String> mappedTranslations = new HashMap<String, String>();
				for(int i = 0; i < keys_.length && i < translations_.length; ++i)
					mappedTranslations.put(keys_[i], translations_[i]);
				 
				// map the materials to a map
				HashMap<String, Material> mappedIcons = new HashMap<String, Material>();
				for(int i = 0; i < keys_.length && i < icons_.length; ++i) {
					Material icon = Material.matchMaterial(icons_[i]);
					// if the material given is invalid use barrier as material
					if(icon == null)
						mappedIcons.put(keys_[i], Material.BARRIER);
					else
						mappedIcons.put(keys_[i], icon);
				}
				 
				// map the translations to a map
				HashMap<String,Long> mappedLengths = new HashMap<String, Long>();
				for(int i = 0; i < keys_.length && i < lengths_.length; ++i) {
					try {
						Long l = Long.parseLong(lengths_[i]);
						mappedLengths.put(keys_[i], l);
					} catch(NumberFormatException e) {
						mappedLengths.put(keys_[i], 86400000l);
					}
				}
				 
				// create a list of the UUIDs received back
				List<UUID> players = new ArrayList<UUID>();
				keys_ = in.readUTF().split(",");
				for(String id : keys_)
					players.add(UUID.fromString(id));
				 
				// open the ban inventory to the command sender
				openBanInventory(p, mappedTranslations, mappedIcons, mappedLengths, players);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Opens the ban inventory to manage the ban for a given player
	 * @param player player to open the ban window to
	 */
	private void openBanInventory(Player player, 
			HashMap<String, String> translations, 
			HashMap<String, Material> icons, 
			HashMap<String, Long> lengths, 
			List<UUID> players) {
		for(Entry<String, String> trans : translations.entrySet())
			Bukkit.broadcastMessage(trans.getKey() + ": " + trans.getValue());
		
		for(Entry<String, Material> trans : icons.entrySet())
			Bukkit.broadcastMessage(trans.getKey() + ": " + trans.getValue());
		
		for(Entry<String, Long> trans : lengths.entrySet())
			Bukkit.broadcastMessage(trans.getKey() + ": " + trans.getValue());
		
		for(UUID uuid : players)
			Bukkit.broadcastMessage(uuid.toString());
	}
	

	@Override
	protected CommandResult runCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return CommandResult.NOT_FOR_CONSOLE;
		
		List<UUID> bannable = null;
		List<Player> multiple = null;
		// get the loaded players
		if(this.hasData(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE)) // load a single offline player
			bannable = Arrays.asList(this.<UUID>getObject(PlayerLoader.PLAYER_KEY_OFFLINE_SINGLE));
		else { // load multiple online players
			multiple = Arrays.asList(this.<Player>getObject(PlayerLoader.PLAYER_KEY_SINGLE));
			bannable = new ArrayList<UUID>();
			for(Player player : multiple)
				bannable.add(player.getUniqueId());
		}
		
		// request ban reasons for the given players
		requestReasons((Player)sender, bannable);
		
		return CommandResult.SUCCESS;
	}
}
