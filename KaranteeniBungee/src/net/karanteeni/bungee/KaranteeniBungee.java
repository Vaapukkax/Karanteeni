package net.karanteeni.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import net.karanteeni.bungee.command.Ping;
import net.karanteeni.bungee.event.PostLogin;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class KaranteeniBungee extends Plugin implements Listener {
	private static KaranteeniBungee instance;
	
	public KaranteeniBungee()
	{
		instance = this;
	}
	
	
	public static KaranteeniBungee getInstance()
	{
		return instance;
	}
	
	
	@Override
	public void onEnable()
	{
		enableCommands();
		enableEvents();
		getLogger().info("�2KaranteeniBungee has loaded!");
	}
	
	
	/**
	 * Register plugin commands
	 */
	private void enableCommands() {
		getProxy().getPluginManager().registerCommand(this, new Ping());
	}
	
	
	/**
	 * Register plugin events
	 */
	private void enableEvents() {
		getProxy().getPluginManager().registerListener(this, new PostLogin());
		this.getProxy().getPluginManager().registerListener(this, this);
		this.getProxy().registerChannel("Return");
	}
	
	
	@EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
		System.out.println("aAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
            	String channel = in.readUTF();
            	if(channel.equals("get")) {
            		String message = in.readUTF();
            		System.out.println(message);
            		
            		Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();
                    for (Map.Entry<String, ServerInfo> en : servers.entrySet()) {
                        String name = en.getKey();
                        ServerInfo all = BungeeCord.getInstance().getServerInfo(name);
                        sendToBukkit("TellMods", message, all);
                    }
            	}
            } catch(Exception e1) {
            	
            }
     
        }
    }
	
	private void sendToBukkit(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("Return", stream.toByteArray());
    }
}
