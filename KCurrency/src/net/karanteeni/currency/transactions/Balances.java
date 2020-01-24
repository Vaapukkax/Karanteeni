package net.karanteeni.currency.transactions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.bukkit.Bukkit;
import net.karanteeni.currency.KCurrency;

public class Balances {

	/**
	 * Returns the balance of a player or Double.NaN if not found
	 * @param uuid
	 * @return
	 */
	public Double getBalance(UUID uuid) {
		Connection conn = null;
		Double result = null;
		try {
			if(confirmAccountExistance(uuid)) {
				conn = KCurrency.getDatabaseConnector().openConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT "+
						KCurrency.getBalanceName()+" FROM "+
						KCurrency.getTableName()+" WHERE "+
						KCurrency.getUUIDName()+"='"+uuid+"'");
				
				if(rs.first()) {
					double bal = rs.getDouble(1);
					st.close();
					result = bal;
				} else {				
					result = Double.NaN;
				}				
			}
		} catch (SQLException e) {
			result = Double.NaN;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Returns the balance of a player
	 * @param name
	 * @return
	 */
	public Double getBalance(String name) {
		UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
		
		if(!confirmAccountExistance(uuid))
			return null;
		
		return getBalance(uuid);
	}
	
	
	/**
	 * Set the balance of a player to a value
	 * @param uuid
	 * @param amount
	 * @return
	 */
	public boolean setBalance(UUID uuid, double amount) {
		Connection conn = null;
		boolean result = false;
		try {
			if(confirmAccountExistance(uuid)) {
				conn = KCurrency.getDatabaseConnector().openConnection();
				Statement st = conn.createStatement();
				int c = st.executeUpdate("UPDATE " + KCurrency.getTableName() +
						" SET " + KCurrency.getBalanceName() + " = " + amount +
						" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'");
				st.close();
				result = c > 0;				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Set the balance of a player to a value
	 * @param name
	 * @param amount
	 * @return
	 */
	public boolean setBalance(String name, double amount) {
		UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
		
		if(!confirmAccountExistance(uuid))
			return false;
		
		return setBalance(uuid, amount);
	}
	
	
	/**
	 * Adds money for player
	 * @param uuid
	 * @param amount
	 * @return NaN if balance not found/error adding and null if player does not exist on global server
	 */
	public Double addToBalance(UUID uuid, double amount) {
		Connection conn = null;
		Double result = null;
		
		try {
			if(confirmAccountExistance(uuid)) {
				double balance = getBalance(uuid);
				conn = KCurrency.getDatabaseConnector().openConnection();
				Statement st = conn.createStatement();
				int c = st.executeUpdate("UPDATE " + KCurrency.getTableName() +
						" SET " + KCurrency.getBalanceName() + " = " + (amount+balance) +
						" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'");
				st.close();
				if(c != 0)
					result = balance+amount;
			}			
		} catch (SQLException e) {
			result = Double.NaN;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Adds money for player
	 * @param name
	 * @param amount
	 * @return NaN if balance not found/error adding and null if player does not exist on global server
	 */
	public Double addToBalance(String name, double amount) {
		UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
		
		if(!confirmAccountExistance(uuid))
			return null;
		
		return addToBalance(uuid, amount);
	}
	
	
	/**
	 * Adds money for player
	 * @param uuid
	 * @param amount
	 * @return NaN if balance not found/error adding
	 */
	public Double removeFromBalance(UUID uuid, double amount) {
		Connection conn = null;
		Double result = null;
		
		try {
			if(confirmAccountExistance(uuid)) {
				double balance = getBalance(uuid);
				conn = KCurrency.getDatabaseConnector().openConnection();
				Statement st = conn.createStatement();
				int c = st.executeUpdate("UPDATE " + KCurrency.getTableName() +
						" SET " + KCurrency.getBalanceName() + " = " + (balance-amount) +
						" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'");
				st.close();
				if(c != 0)
					result = balance-amount;				
			}
			
		} catch (SQLException e) {
			result = Double.NaN;
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Adds money for player
	 * @param name
	 * @param amount
	 * @return NaN if balance not found/error adding
	 */
	public Double removeFromBalance(String name, double amount) {
		UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
		if(!confirmAccountExistance(uuid))
			return null;
		
		return removeFromBalance(uuid, amount);
	}
	
	
	/**
	 * Creates an account for player on this server if it does not yet exist
	 * @param uuid
	 */
	private boolean confirmAccountExistance(UUID uuid) {
		Connection conn = null;
		boolean result = false;
		
		//Add player to database
		try {
			conn = KCurrency.getDatabaseConnector().openConnection();
			//Insert player to database with UUID and default balance amount
			Statement st = conn.createStatement();
			st.executeUpdate("INSERT IGNORE INTO " + KCurrency.getTableName() + " (" + 
					KCurrency.getUUIDName() + ", " + KCurrency.getBalanceName() + ") VALUES ('" + uuid + "', " + 
					KCurrency.getPlugin(KCurrency.class).getConfigHandler().getStartBalance() + ");");
			result = true;
		} catch (Exception e) {
			Bukkit.broadcastMessage("§4Failed to generate balance information to database, please contanct server staff IMMEDIATELY!");
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
