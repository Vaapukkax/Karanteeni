package net.karanteeni.chatar.command.mail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.karanteeni.chatar.Chatar;

public class MailDatabase {
	
	public MailDatabase() {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS mail ("+
				"sender VARCHAR(64) NOT NULL,"+
				"receiver VARCHAR(64) NOT NULL,"+
				"sent DATE NOT NULL,"+
				"msg VARCHAR(300) NOT NULL,"+
				"FOREIGN KEY (sender) REFERENCES player(UUID),"+
				"FOREIGN KEY (receiver) REFERENCES player(UUID));");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(SQLException e) {
				// ignore
			}

			try {
				conn.close();
			} catch(SQLException e) {
				// ignore
			}
		}
	}
	
	
	public List<Mail> getMail(UUID uuid) {
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Mail> mail = new LinkedList<Mail>();
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			stmt = conn.prepareStatement("SELECT player.UUID, player.name, mail.msg, mail.sent "+
					"FROM player JOIN mail ON mail.sender = player.UUID "+
					"WHERE mail.receiver = ? "+
					"ORDER BY sent;");
			stmt.setString(1, uuid.toString());
			ResultSet set = stmt.executeQuery();
			
			int index = 0;
			while(set.next()) {
				UUID sender = null;
				String senderUuid = set.getString(1);
				if(!set.wasNull()) {
					sender = UUID.fromString(senderUuid);
				}
				String senderName = "server";
				if(sender != null)
					senderName = set.getString(2);

				String message = set.getString(3);
				Date date = set.getDate(4);
				int id = ++index;
				
				mail.add(new Mail(sender, senderName, uuid, id, message, date));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(SQLException e) {
				// ignore
			}

			try {
				conn.close();
			} catch(SQLException e) {
				// ignore
			}
		}
		
		return mail;
	}
	
	
	public boolean sendMail(Mail mail) {
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean success = false;
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			stmt = conn.prepareStatement("INSERT INTO mail VALUES (?,?,?,?);");
			stmt.setString(1, mail.sender.toString());
			stmt.setString(2, mail.receiver.toString());
			stmt.setDate(3, mail.date);
			stmt.setString(4, mail.message);
			success = stmt.executeUpdate() != 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(SQLException e) {
				// ignore
			}

			try {
				conn.close();
			} catch(SQLException e) {
				// ignore
			}
		}
		
		return success;
	}
	
	
	public int removeMail(UUID remover, int index) {
		Connection conn = null;
		PreparedStatement stmt = null;
		int removed = 0;
		
		try {
			conn = Chatar.getDatabaseConnector().openConnection();
			
			if(index == Integer.MIN_VALUE) {
				stmt = conn.prepareStatement("DELETE FROM mail WHERE receiver = ?;");
				stmt.setString(1, remover.toString());
			} else {
				stmt = conn.prepareStatement("DELETE FROM mail WHERE receiver = ? ORDER BY sent LIMIT ? - 1, 1;");
				stmt.setString(1, remover.toString());
				stmt.setInt(2, index);
			}
			
			removed = stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(SQLException e) {
				// ignore
			}

			try {
				conn.close();
			} catch(SQLException e) {
				// ignore
			}
		}
		
		return removed;
	}
}
