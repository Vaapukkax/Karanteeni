package net.karanteeni.bungee.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {
	private String host, database, user, password;
	private int port;
	
	
	public DatabaseConnector(String host, String database, String user, String password, int port) {
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		this.port = port;
	}
	
	
	/**
	 * Returns a new connection to the database
	 * @return a NEW connection to the database
	 */
	public Connection getConnection() {
		synchronized(this) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=true", 
						this.user, 
						this.password);
				return con;
			} catch(Exception e) {
				// try again in 50ms
				try {
					wait(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				// try to connect again
				try {
					Class.forName("com.mysql.jdbc.Driver");
					return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=true", 
							this.user, 
							this.password);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			return null;
		}
	}
}
