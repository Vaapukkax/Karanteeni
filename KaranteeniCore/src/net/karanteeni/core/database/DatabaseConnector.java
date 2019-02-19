package net.karanteeni.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class DatabaseConnector {
	private Connection connection;
	private Statement statement;
	private String host, database, username, password;
	private int port;
	private static final String NOT_CONNECTED = "DATABASE NOT CONNECTED!";
	
	/**
	 * Connects to database
	 * @param host
	 * @param database
	 * @param username
	 * @param password
	 * @param port
	 */
	public DatabaseConnector(final String host, final String database, final String username, final String password, final int port)
	{
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;
		
		try{
			openConnection();
			statement = connection.createStatement();
			Bukkit.getConsoleSender().sendMessage(String.format(
					"§aSuccessfully connected to database: %1$s, host: %2$s, username: %3$s, port: %4$s!", 
					database, host, username, port));
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "COULD NOT CONNECT TO DATABASE! SOME OPERATIONS WILL NOT WORK!", e);
		}
	}
	
	/**
	 * Check if we're connected to the database
	 * @return
	 */
	public boolean isConnected()
	{
		try {
			if(connection == null)
				return false;
			return !connection.isClosed();
		} catch (SQLException e) {
		}
		
		return false;
	}
	
	/**
	 * Open a connection to database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void openConnection() throws SQLException, ClassNotFoundException
	{
		//Jos yhteys on jo olemassa niin ei luoda uutta
		if(connection != null && !connection.isClosed())
			return;
		
		synchronized(this)
		{
			if(connection != null && !connection.isClosed())
				return;
			
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
		}
	}
	
	/**
	 * Returns a new statement to connect to database
	 * @return
	 */
	public Statement getStatement()
	{
		try {
			if(connection == null || connection.isClosed())
				return null;
			return connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns a preparedstatement with given sql
	 * @param sql Sql used in the statement
	 * @return Prepared statement
	 */
	public PreparedStatement prepareStatement(String sql)
	{
		try {
			if(connection == null || connection.isClosed())
				return null;
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * Gets a list of strings from database
	 * @param query
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	/*public List<String> getStringList(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			List<String> resultList = new ArrayList<String>();
			
			while(result.next())
			{
				resultList.add(result.getString(columnName));
			}
			
			return resultList;
		}
	}*/
	
	/**
	 * Gets a list of UUIDs from database
	 * @param query
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	/*public List<UUID> getUUIDList(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			List<UUID> resultList = new ArrayList<UUID>();
			
			while(result.next())
			{
				resultList.add(UUID.fromString(result.getString(columnName)));
			}
			
			return resultList;
		}
	}*/
	
	/**
	 * Gets a uuid from database
	 * @param query
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	/*public UUID getUUID(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			
			if(result.next())
				 return UUID.fromString(result.getString(columnName));
			
			return null;
		}
	}*/
	
	/**
	 * Gets a list of doubles from database in the given column
	 * @param query
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	/*public List<Double> getDoubleList(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			List<Double> resultList = new ArrayList<Double>();
			
			while(result.next())
			{
				resultList.add(result.getDouble(columnName));
			}
			
			return resultList;
		}
	}*/
	
	/**
	 * Gets a string from database
	 * @param query
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	/*public String getString(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			
			while(result.next())
			{
				return result.getString(columnName);
			}
			
			return null;
		}
	}*/
	
	/**
	 * Gets a long value from database
	 * @param query
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	/*public Long getLong(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			
			while(result.next())
			{
				return result.getLong(columnName);
			}
			
			return null;
		}
	}
	
	public int getInteger(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			
			while(result.next())
			{
				return result.getInt(columnName);
			}
			
			return 0;
		}
	}
	
	public double getDouble(String query, String columnName) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			ResultSet result = statement.executeQuery(query);
			
			while(result.next())
			{
				return result.getDouble(columnName);
			}
			
			return 0;
		}
	}
	
	public void updateDouble(String table, String columnName, String condition, double newValue) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			statement.executeUpdate("UPDATE " + table + " SET " + columnName + " = " + newValue + " WHERE " + condition + ";");
		}
	}*/
	
	/*public String removeSqlInjection(String text)
	{
		return text;
	}*/
	
	/**
	 * Runs any given query, returns only execution status
	 * @param query
	 * @return the resultset of the query
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	@Deprecated
	public int runQuery(String query) throws SQLException, ClassNotFoundException, Exception
	{
		if(connection == null || connection.isClosed())
			throw new Exception(NOT_CONNECTED);
		
		synchronized(this)
		{
			return statement.executeUpdate(query);
		}
	}
	
	/**
	 * Execute a query on a database to return a set of values
	 * @param query
	 * @return
	 */
	@Deprecated
	public ResultSet executeQuery(String query) throws SQLException, ClassNotFoundException, Exception
	{
		synchronized(this)
		{
			if(connection == null || connection.isClosed())
				throw new Exception(NOT_CONNECTED);
			
			return statement.executeQuery(query);
		}
	}
	
	/*public List<Location> getLocationList(String query) throws SQLException, ClassNotFoundException, Exception
	{
		synchronized(this)
		{
			if(connection == null || connection.isClosed())
				throw new Exception(NOT_CONNECTED);
			
			ResultSet result = statement.executeQuery(query);
			
			ArrayList<Location> locs = new ArrayList<Location>();
			
			while(result.next())
			{
				double x = result.getDouble("X");
				double y = result.getDouble("Y");
				double z = result.getDouble("Z");
				String world = result.getString("WORLD");
				float pitch = result.getFloat("PITCH");
				float yaw = result.getFloat("YAW");
				
				locs.add(new Location(Bukkit.getWorld(world), x, y, z, pitch, yaw));
			}
			
			return locs;
		}
	}
	
	public Location getLocation(String query) throws SQLException, ClassNotFoundException, Exception
	{
		synchronized(this)
		{
			if(connection == null || connection.isClosed())
				throw new Exception(NOT_CONNECTED);
			
			ResultSet result = statement.executeQuery(query);
			
			while(result.next())
			{
				double x = result.getDouble("X");
				double y = result.getDouble("Y");
				double z = result.getDouble("Z");
				String world = result.getString("WORLD");
				float pitch = result.getFloat("PITCH");
				float yaw = result.getFloat("YAW");
				
				return new Location(Bukkit.getWorld(world), x, y, z, pitch, yaw);
			}
		}
		return null;
	}*/
	
	/**
	 * Saves a location to database
	 * @param table
	 * @param location
	 * @throws Exception
	 */
	/*public void saveLocation(String table, Location location) throws Exception
	{
		synchronized(this)
		{
			statement.executeQuery("INSERT INTO " + table + "(WORLD, X, Y, Z, PITCH, YAW) VALUES (" + 
					"'" + location.getWorld().getName() + "', " +
					location.getX() + ", " +
					location.getY() + ", " + 
					location.getZ() + ", " + 
					location.getYaw() + ", " +
					location.getPitch() + ");");
		}
	}*/
	
	/**
	 * Shuts down the connection to database
	 */
	public void closeConnection()
	{
		try{connection.close();}
		catch(Exception e){}
	}
}
