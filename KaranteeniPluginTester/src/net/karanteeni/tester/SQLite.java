package net.karanteeni.tester;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


public class SQLite extends Database{
    String dbname;
    String dbname2;
    String dbname3;
    public SQLite(TesterMain instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "protections"); //Chestisuojaukset
        dbname2 = plugin.getConfig().getString("SQLite.Filename", "protectionmember"); //Chestien oikeudet
        dbname3 = plugin.getConfig().getString("SQLite.Filename", "votes"); //Votet
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS protections (" +
            "`owner` varchar(60) NOT NULL," +
            "`downer` varchar(60) NOT NULL," +
            "`x` int(11) NOT NULL," +
            "`y` int(11) NOT NULL," +
            "`z` int(11) NOT NULL," +
            "`world` varchar(30) NOT NULL" +
            ");";
    
    public String SQLiteCreateProtMembers = "CREATE TABLE IF NOT EXISTS protectionmember (" +
            "`owner` varchar(60) NOT NULL," +
            "`member` varchar(60) NOT NULL," +
            "`membername` varchar(50) NOT NULL," +
            "`x` int(11) NOT NULL," +
            "`y` int(11) NOT NULL," +
            "`z` int(11) NOT NULL," +
            "`world` varchar(30) NOT NULL" +
            ");";
    
    public String Votes = "CREATE TABLE IF NOT EXISTS votes (" +
            "`player` varchar(60) NOT NULL UNIQUE," +
    		"`amount` int(3)" +
            ");";
    
    
 
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
    
    
    public Connection getSQLConnection2() {
        File dataFolder = new File(plugin.getDataFolder(), dbname2+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname2+".db");
            }
        }
        try {
            if(connection2!=null&&!connection2.isClosed()){
                return connection2;
            }
            Class.forName("org.sqlite.JDBC");
            connection2 = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection2;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
    
    public Connection getSQLConnection3() {
        File dataFolder = new File(plugin.getDataFolder(), dbname3+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname3+".db");
            }
        }
        try {
            if(connection3!=null&&!connection3.isClosed()){
                return connection3;
            }
            Class.forName("org.sqlite.JDBC");
            connection3 = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection3;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
    
    
    public void load2() {
        connection2 = getSQLConnection2();
        try {
            Statement s = connection2.createStatement();
            s.executeUpdate(SQLiteCreateProtMembers);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
    
    public void load3() {
        connection3 = getSQLConnection3();
        try {
            Statement s = connection3.createStatement();
            s.executeUpdate(Votes);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}