package net.karanteeni.simplevote;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.UUID;
import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ReceiveVote implements Listener {
	SimpleVotes plugin;
	
	public ReceiveVote(SimpleVotes plugin) {
		this.plugin = plugin;
		initializeTable();
		registerTranslations();
	}
	
	
	/**
	 * Register the translations used by this plugin
	 */
	private void registerTranslations() {
		SimpleVotes.getTranslator().registerTranslation(plugin, "vote.broadcast", "§ePlayer %player% has voted the server!");
	}
	
	
	@EventHandler
	public void onVote(VotifierEvent event) {
		Vote vote = event.getVote();
		String username = vote.getUsername();
		
		UUID uuid = SimpleVotes.getPlayerHandler().getUUID(username);
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(uuid);
		
		addVote(uuid);
		/*if(player == null)
			addVote(uuid);
		else
			activateVote(player);*/
	}
	
	
	/**
	 * Adds a vote to the database in case the player is offline
	 * @param uuid uuid of the player to whom the vote shall be added
	 */
	public void addVote(UUID uuid) {
		Connection con;
		try {
			con = SimpleVotes.getDatabaseConnector().getConnection();
			Statement stmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO votes (uuid, votes)"+
				"VALUES ('"+uuid.toString()+"', 1)"+
				"ON DUPLICATE KEY UPDATE\n"+
			   "votes = votes + 1;");
			
			// broadcast the vote to all players
			Collection<ProxiedPlayer> players = BungeeCord.getInstance().getPlayers();
			for(ProxiedPlayer player : players) {
				player.sendMessage(
						TextComponent.fromLegacyText(
							SimpleVotes.getTranslator().getTranslation(plugin, player, "vote.broadcast")
							.replace("%player%", player.getName())));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Activates the vote on the server the player is in
	 * @param player player to whom the vote is activated
	 */
	private void activateVote(ProxiedPlayer player) {
		// TODO
	}
	
	
	/**
	 * Initializes the vote table to the database
	 */
	private void initializeTable() {
		try {
			Connection con = SimpleVotes.getDatabaseConnector().getConnection();
			Statement stmt = con.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS votes (\n"+
					"uuid VARCHAR(64) PRIMARY KEY NOT NULL,\n"+
					"votes SMALLINT NOT NULL,\n"+
					"FOREIGN KEY (uuid) REFERENCES player(UUID));");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
