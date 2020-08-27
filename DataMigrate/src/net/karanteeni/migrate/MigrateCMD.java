package net.karanteeni.migrate;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import net.karanteeni.core.command.AbstractCommand;

public class MigrateCMD extends AbstractCommand {

	public MigrateCMD() {
		super(TesterMain.getPlugin(TesterMain.class), "migrate", "/migrate", "migrate data from player folders", "wololo");
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// read all files
		File[] files = plugin.getDataFolder().listFiles();
		try {
			Connection con = plugin.getDatabaseConnector().openConnection();
			
			// migrate the player uuids, names, usernames
			for(File file : files) {
				FileConfiguration yml = new YamlConfiguration();
				
				try {
					yml.load(file);
					
					UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
					long playTimeMS = (yml.getLong("PlayTime") * 1000 * 60);
					String name = yml.getString("Name");
					String nick = yml.getString("Nick");
					
					// let only players with 10minutes or more playtime through
					if(playTimeMS < 600000) continue;
					
					// insert the player to the database
					PreparedStatement stmt = con.prepareStatement("INSERT IGNORE INTO player(UUID, name, displayname) VALUES (?,?,?);");
					stmt.setString(1, uuid.toString());
					stmt.setString(2, name!=null?name:"unknown");
					stmt.setString(3, nick!=null?nick:"unknown");
					stmt.execute();
					
					// insert the players playtime to the database
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate("INSERT IGNORE INTO global_playtime(player, time) VALUES ('"+uuid.toString()+"',"+playTimeMS+");");
					
				} catch(Exception e) {
					System.out.println("Could not read file " + file.getName());
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return false;
	}

}
