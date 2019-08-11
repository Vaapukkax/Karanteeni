package net.karanteeni.tester;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Database {
    TesterMain plugin;
    Connection connection;
    Connection connection2;
    Connection connection3;
    // The name of the table we created back in SQLite class.
    public String table = "protections";
    public String allowedmember = "protectionmember";
    public String votes = "votes";
    public int tokens = 0;
    public Database(TesterMain instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();
    public abstract Connection getSQLConnection2();
    public abstract Connection getSQLConnection3();

    public abstract void load();
    public abstract void load2();
    public abstract void load3();

    public void initialize(){
        
    	connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
        
        
        connection2 = getSQLConnection2();
        try{
            PreparedStatement ps = connection2.prepareStatement("SELECT * FROM " + allowedmember);
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
        
        connection3 = getSQLConnection3();
        try{
            PreparedStatement ps = connection3.prepareStatement("SELECT * FROM " + votes);
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    /*public Integer getTokens(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '"+string+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(string.toLowerCase())){ // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return rs.getInt("kills"); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }*/
    
 // Tarkistetaan, onko chestin j�sen
    public boolean isMember(String uuid, int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection2();
            //Tee COUNT() lauseella!!!
            ps = conn.prepareStatement("SELECT * FROM " + allowedmember + " WHERE x = '"+x+"' AND y = '"+y+"' AND z = '"+z+"' AND world = '"+world+"';");
            
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("member").equalsIgnoreCase(uuid.toLowerCase())){
                    //return rs.getInt("total");
                	return true;
                }
            }
        	return false;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                {
                	ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }
    
    
    // Tarkistetaan, onko chestin omistaja
    public boolean isOwner(String uuid, int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            //Tee COUNT() lauseella!!!
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE x = '"+x+"' AND y = '"+y+"' AND z = '"+z+"' AND world = '"+world+"';");
            
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("owner").equalsIgnoreCase(uuid.toLowerCase())){
                    //return rs.getInt("total");
                	return true;
                }
            }
        	return false;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                {
                	ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }
    
    //Tarkistetaan, onko chesti auki kaikille
    public boolean isOpenForAll(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection2();
            //Tee COUNT() lauseella!!!
            ps = conn.prepareStatement("SELECT * FROM " + allowedmember + " WHERE x = '"+x+"' AND y = '"+y+"' AND z = '"+z+"' AND world = '"+world+"';");
            
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("member").equalsIgnoreCase("kaikki")){
                    //return rs.getInt("total");
                	return true;
                }
            }
        	return false;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                {
                	ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }
    
  //Laske votet pelaajalta
    public int getVotes(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection3();
            //Tee COUNT() lauseella!!!
            ps = conn.prepareStatement("SELECT * FROM " + votes + " WHERE player = '" + uuid + "';");
            
            rs = ps.executeQuery();
            while(rs.next()){
                	return rs.getInt(2);
            }
        	return 0;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                {
                	ps.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return -1;
    }
    
    //Poista vote pelaajan ��nestyksist�
    public boolean removeVote(UUID uuid, int amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection3();
            amount--;
            ps = conn.prepareStatement("REPLACE INTO " + votes + " (player, amount) VALUES('"+uuid+"',"+amount+");"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            //ps = conn.prepareStatement("INSERT INTO " + votes + " (player) VALUES(?)");
            
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;      
    }
   
    
    // Lis�� vote pelaajan ��nestyksiin
    public boolean addVote(String uuid, int amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection3();
            amount++;
            ps = conn.prepareStatement("REPLACE INTO " + votes + " (player, amount) VALUES('"+uuid+"',"+amount+");"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            //ps = conn.prepareStatement("INSERT INTO " + votes + " (player) VALUES(?)");
            
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;      
    }
    
    // Lis�t��n k�ytt�j� suojaukseen
    public boolean addMember(String uuid, String memberuuid, String membername, int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection2();
            
            ps = conn.prepareStatement("INSERT INTO " + allowedmember + " (owner,member,membername,x,y,z,world) VALUES(?,?,?,?,?,?,?)");
            ps.setString(1, uuid);
            ps.setString(2, memberuuid);
            ps.setString(3, membername);
            ps.setInt(4, x);
            ps.setInt(5, y);
            ps.setInt(6, z);
            ps.setString(7, world);
            
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;      
    }
    
 // Poistetaan k�ytt�j� suojauksesta
    public ArrayList<String> showMember(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getSQLConnection2();
            
            ps = conn.prepareStatement("SELECT * FROM "+allowedmember+" WHERE x = '"+x+"' AND y = '"+y+"' AND z = '"+z+"' AND world = '"+world+"';");
            
            rs = ps.executeQuery();
            ArrayList<String> m = new ArrayList<String>();
            
            while(rs.next())
            {
            	if(rs.getString("membername") != null)
            		m.add(rs.getString("membername"));
            }
            
            if(m.size() > 0)
            {
            	return m;
            }
            else
            {
            	return null;
            }
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;      
    }
    
    // Lisaa suojaus
    public boolean addProtection(String uuid, String displayname,int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection();
            //ps = conn.prepareStatement("REPLACE INTO " + table + " (x,y,z) VALUES(?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            
            ps = conn.prepareStatement("INSERT INTO " + table + " (owner,downer,x,y,z,world) VALUES(?,?,?,?,?,?)");
            ps.setString(1, uuid);                                             // YOU MUST put these into this line!! And depending on how many
            ps.setString(2, displayname);                                                                                             // colums you put (say you made 5) All 5 need to be in the brackets
                                                                                                         // Seperated with comma's (,) AND there needs to be the same amount of
                                                                                                         // question marks in the VALUES brackets. Right now i only have 3 colums
                                                                                                         // So VALUES (?,?,?) If you had 5 colums VALUES(?,?,?,?,?)                                                                                                
            ps.setInt(3, x); // This sets the value in the database. The colums go in order. Player is ID 1, kills is ID 2, Total would be 3 and so on. you can use
                                  // setInt, setString and so on. tokens and total are just variables sent in, You can manually send values in as well. p.setInt(2, 10) <-
                                  // This would set the players kills instantly to 10. Sorry about the variable names, It sets their kills to 10 i just have the variable called
                                  // Tokens from another plugin :/
            ps.setInt(4, y);
            ps.setInt(5, z);
            ps.setString(6, world);
            
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;    
    }
    

    // Haetaan chestin omistajan uuid
    public String getOwner(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE x = '"+x+"' AND y = '"+y+"' AND z = '"+z+"' AND world = '"+world+"';");
            
            rs = ps.executeQuery();
            
            int owners = 0;
            
            while(rs.next()){
            	owners++;
                return rs.getString("downer");
            }
            
            if (owners == 0)
            {
            	return null;
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "VIRHE!";
    }
    
    // Poistetaan k�ytt�j� suojauksesta
    public boolean deleteMember(String uuid, String memberuuid, int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection2();
            
            ps = conn.prepareStatement("DELETE FROM " + allowedmember + " WHERE owner = '"+uuid+"' AND member = '"+memberuuid+"' AND x = "+x+" AND y = "+y+" AND z = "+z+" AND world = '"+world+"';");
            //ps = conn.prepareStatement("DELETE FROM " + allowedmember + " WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' AND owner = '" + uuid + "' AND member = '" + memberuuid + "' AND world = '"+world+"';");
            
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;      
    }
    
 // Poistetaan k�ytt�j� suojauksesta
    public boolean deleteAllMembers(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getSQLConnection2();
            
            ps = conn.prepareStatement("DELETE FROM " + allowedmember + " WHERE x = " + x + " AND y = " + y + " AND z = " + z + " AND world = '"+world+"';");
            
            ps.executeUpdate();
            
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;      
    }
    
    // Now we need methods to save things to the database
    public boolean removeProtection(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            //ps = conn.prepareStatement("REPLACE INTO " + table + " (x,y,z) VALUES(?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            
            ps = conn.prepareStatement("DELETE FROM " + table + " WHERE x = " + x + " AND y = " + y + " AND z = " + z + " AND world = '"+world+"';");
            
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;       
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}