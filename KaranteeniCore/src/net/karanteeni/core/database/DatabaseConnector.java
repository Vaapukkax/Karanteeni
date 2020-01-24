package net.karanteeni.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	//private Connection connection;
	//private Statement statement;
	private String host, database, username, password;
	private int port;
	//private static final String NOT_CONNECTED = "DATABASE NOT CONNECTED!";
	
	/**
	 * Connects to database
	 * @param host
	 * @param database
	 * @param username
	 * @param password
	 * @param port
	 */
	public DatabaseConnector(final String host, final String database, final String username, final String password, final int port) 
			throws SQLException {
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;
		
		openConnection();
		//Statement statement = connection.createStatement();
		System.out.println(String.format(
				"§aSuccessfully connected to database: %1$s, host: %2$s, username: %3$s, port: %4$s!", 
				database, host, username, port));
		
	}
	
	
	/**
	 * Open a connection to database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Connection openConnection() throws SQLException {
		//Jos yhteys on jo olemassa niin ei luoda uutta
		/*if(connection != null && !connection.isClosed())
			return;*/
		
		synchronized(this) {
			/*if(connection != null && !connection.isClosed())
				return;*/
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=true", 
						this.username, 
						this.password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				
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
							this.username, 
							this.password);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			
			return null;
		}
	}
}
