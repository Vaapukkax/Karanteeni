package net.karanteeni.bungee.core.communication;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

@Deprecated
public abstract class BungeeRequester<T> implements PluginMessageListener {
	private String channel;
	private String subChannel;
	
	public BungeeRequester(String channel, String subchannel) {
		this.channel = channel;
		this.subChannel = subchannel;
	}
	
	
	public abstract void makeDataRequest();
	
	
	public abstract T getLoadedData();
	
	
	/**
	 * Requests group data from bungee
	 * @throws IOException
	 */
	public synchronized T requestData(JavaPlugin plugin) {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
		makeDataRequest();
		
		// wait until response
		try {
			wait();
		} catch(InterruptedException e) {}
		
		plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, channel);
		plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, channel, this);
		return getLoadedData();
	}
	
	
	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		
		try {
			String subChannel = in.readUTF();
			if(subChannel.equals(subChannel)) {
				dataReceived(channel, player, message);
				notifyAll();
			}			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected abstract void dataReceived(String channel, Player player, byte[] message);
}
