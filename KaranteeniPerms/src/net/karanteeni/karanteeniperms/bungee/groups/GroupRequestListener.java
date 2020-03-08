package net.karanteeni.karanteeniperms.bungee.groups;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.karanteeni.karanteeniperms.bungee.KaranteeniPermsBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GroupRequestListener implements Listener {
	
	@EventHandler
	public void requestGroups(PluginMessageEvent event) {
		System.out.println("Receiving some request: " + event.getTag());
		if(!event.getTag().equals("karanteeniperms:groups"))
			return;
		
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
		KaranteeniPermsBungee plugin = KaranteeniPermsBungee.getInstance();
		
		try {
			String subChannel = in.readUTF();
			if(!subChannel.equals("request-groups")) // are we requesting groups
				return;
			
			// send the requested groups back
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(stream);
	        try {
	        	// build the groups
	        	out.writeByte(plugin.getGroupList().getGroups().size());
	        	int offset = 1;
	        	
	        	// write all of the groups to the byte array
	        	for(Group group : plugin.getGroupList().getGroups()) {
	        		byte[] groupData = group.convertToSpigotBungeeGroup();
	        		out.write(groupData, offset, groupData.length);
	        		offset += groupData.length;
	        	}
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        // send the built groups back to spigot
	        if(event.getSender() instanceof Server)
	        	((Server)event.getSender()).sendData("karanteeniperms:groups", stream.toByteArray());
	        else if (event.getSender() instanceof ProxiedPlayer)
	        	((ProxiedPlayer)event.getSender()).sendData("karanteeniperms:groups", stream.toByteArray());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
