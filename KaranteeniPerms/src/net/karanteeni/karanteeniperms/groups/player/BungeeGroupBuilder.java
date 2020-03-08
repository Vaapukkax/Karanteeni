package net.karanteeni.karanteeniperms.groups.player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.karanteeni.core.data.ObjectPair;
import net.karanteeni.karanteeniperms.KaranteeniPerms;

public class BungeeGroupBuilder implements PluginMessageListener {
	private List<BungeeGroup> loadedBungeeGroups = null;
	
	/**
	 * Requests group data from bungee
	 * @throws IOException
	 */
	public synchronized List<BungeeGroup> requestBungeeGroupData() throws IOException {
		KaranteeniPerms plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		out.writeUTF("request-groups");
		
		plugin.getServer().sendPluginMessage(plugin, "karanteeniperms:groups", b.toByteArray());
		
		// wait until response
		long requestStartTime = System.currentTimeMillis();
		try {
			while((loadedBungeeGroups == null || loadedBungeeGroups.isEmpty()) && 
					System.currentTimeMillis() - requestStartTime < 10000) {
				wait(10000l - (System.currentTimeMillis() - requestStartTime)); // wait up to 10 seconds for response
				
				if(loadedBungeeGroups == null || loadedBungeeGroups.isEmpty())
					throw new IOException("Failed to retrieve data from bungee");
			}
		} catch(InterruptedException e) {}
		
		// send the loaded groups and clear memory
		List<BungeeGroup> loadedGroups = loadedBungeeGroups;
		loadedBungeeGroups = null;
		return loadedGroups;
	}
	
	
	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("karanteeniperms:groups")) return;
		
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String subChannel = in.readUTF();
			int amountOfGroups = in.readByte();
			
			if(subChannel.equals("request-groups")) {
				Map<String, ObjectPair<BungeeGroup, String[]>> readGroups = new TreeMap<String, ObjectPair<BungeeGroup, String[]>>();
				
				// read all the groups from the data
				for(int i = 0; i < amountOfGroups; ++i) {
					BungeeGroup group = readGroup(in);
					// read the amount of groups this group inherits
					String[] inheritedGroups = new String[in.readByte()];
					
					
					// read the groups inherited and store them to an array
					for(int l = 0; l < inheritedGroups.length; ++l)
						inheritedGroups[l] = in.readUTF();
					
					// store the read group and inheritances
					readGroups.put(group.getID(), new ObjectPair<BungeeGroup, String[]>(group, inheritedGroups));
				}
				
				// build the inheritance for all groups
				for(ObjectPair<BungeeGroup, String[]> inheritance : readGroups.values()) {
					// inherit all inherited groups
					for(int l = 0; l < inheritance.second.length; ++l) {
						ObjectPair<BungeeGroup, String[]> inheritedGroup = readGroups.get(inheritance.second[l]);
						// check if the group being inherited exists
						if(inheritedGroup == null) {
							Bukkit.getLogger().log(Level.WARNING, "BungeeGroup " + inheritance.first.getID() + 
									" tried to inherit nonexistent group " + inheritance.second[l]);
							continue;
						}
						
						inheritance.first.addInheritance(inheritedGroup.first);
					}
				}
				
				this.loadedBungeeGroups = new LinkedList<BungeeGroup>();
				// store all of the groups to send them back
				for(ObjectPair<BungeeGroup, String[]> group : readGroups.values())
					this.loadedBungeeGroups.add(group.first);
				
				// continue the code execution in requestBungeeGroupData
				notifyAll();
			}			
		} catch(IOException | IllegalArgumentException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to read group data from bungee");
			e.printStackTrace();
		} finally {
			try {
				if(in != null)
					in.close();
			} catch(IOException e) { e.printStackTrace(); }
		}
		
	}
	
	
	/**
	 * Reads a group from the given data stream and converts it into a bungeegroup
	 * @param data
	 * @return
	 */
	private BungeeGroup readGroup(DataInputStream data) throws IOException, IllegalArgumentException {
		String Id = data.readUTF();
		String prefix = data.readUTF();
		String suffix = data.readUTF();
		String defaultLongName = data.readUTF();
		String defaultShortName = data.readUTF();
		int localeCount = data.readByte();
		Locale[] locales = new Locale[localeCount];
		String[] longNames = new String[localeCount];
		String[] shortNames = new String[localeCount];
		String[] permissions = null;
		
		// read locales
		for(int i = 0; i < localeCount; ++i)
			locales[i] = Locale.forLanguageTag(data.readUTF());
		
		// read long rank names for each locale
		for(int i = 0; i < localeCount; ++i)
			longNames[i] = data.readUTF();
		
		// read short rank names for each locale
		for(int i = 0; i < localeCount; ++i)
			shortNames[i] = data.readUTF();
		
		// read the amount of permissions
		permissions = new String[data.readInt()];
		// read the permissions
		for(int i = 0; i < permissions.length; ++i)
			permissions[i] = data.readUTF();
		
		// build the bungeegroup
		return new BungeeGroup(Id, 
				prefix, 
				suffix, 
				defaultLongName, 
				defaultShortName, 
				locales, 
				longNames, 
				shortNames, 
				permissions);
	}
	
	
	public void create(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext context, Object object) throws Exception {
				super.channelRead(context, object);
			}
			
			
			@Override
			public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
				super.write(context, packet, channelPromise);
			}
		};
		
		
	}
}
