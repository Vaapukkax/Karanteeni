package net.karanteeni.currency.transactions;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.currency.KCurrency;

public class Balances {

	/**
	 * Returns the balance of a player or Double.NaN if not found
	 * @param uuid
	 * @return
	 */
	public Double getBalance(UUID uuid)
	{
		try {
			if(!confirmAccountExistance(uuid))
				return null;
			
			Statement st = KCurrency.getDatabaseConnector().getStatement();
			ResultSet rs = st.executeQuery("SELECT "+
					KCurrency.getBalanceName()+" FROM "+
					KCurrency.getTableName()+" WHERE "+
					KCurrency.getUUIDName()+"='"+uuid+"'");
			
			if(rs.first())
			{
				double bal = rs.getDouble(1);
				st.close();
				return bal;
			}
			return Double.NaN;
		} catch (Exception e) {
			return Double.NaN;
		}
	}
	
	/**
	 * Returns the balance of a player
	 * @param name
	 * @return
	 */
	public Double getBalance(String name)
	{
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
	public boolean setBalance(UUID uuid, double amount)
	{
		try {
			if(!confirmAccountExistance(uuid))
				return false;
			Statement st = KCurrency.getDatabaseConnector().getStatement();
			int c = st.executeUpdate("UPDATE " + KCurrency.getTableName() +
					" SET " + KCurrency.getBalanceName() + " = " + amount +
					" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'");
			st.close();
			return c > 0;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Set the balance of a player to a value
	 * @param name
	 * @param amount
	 * @return
	 */
	public boolean setBalance(String name, double amount)
	{
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
	public Double addToBalance(UUID uuid, double amount)
	{
		try {
			if(!confirmAccountExistance(uuid))
				return null;
			
			double balance = getBalance(uuid);
			
			Statement st = KCurrency.getDatabaseConnector().getStatement();
			int c = st.executeUpdate("UPDATE " + KCurrency.getTableName() +
					" SET " + KCurrency.getBalanceName() + " = " + (amount+balance) +
					" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'");
			st.close();
			if(c==0)
				return null;
			return balance+amount;
		} catch (Exception e) {
			return Double.NaN;
		}
	}
	
	/**
	 * Adds money for player
	 * @param name
	 * @param amount
	 * @return NaN if balance not found/error adding and null if player does not exist on global server
	 */
	public Double addToBalance(String name, double amount)
	{
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
	public Double removeFromBalance(UUID uuid, double amount)
	{
		try {
			if(!confirmAccountExistance(uuid))
				return null;
			
			double balance = getBalance(uuid);
			
			Statement st = KCurrency.getDatabaseConnector().getStatement();
			int c = st.executeUpdate("UPDATE " + KCurrency.getTableName() +
					" SET " + KCurrency.getBalanceName() + " = " + (balance-amount) +
					" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'");
			st.close();
			if(c==0)
				return null;

			return balance-amount;
		} catch (Exception e) {
			return Double.NaN;
		}
	}
	
	/**
	 * Adds money for player
	 * @param name
	 * @param amount
	 * @return NaN if balance not found/error adding
	 */
	public Double removeFromBalance(String name, double amount)
	{
		UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
		if(!confirmAccountExistance(uuid))
			return null;
		
		return removeFromBalance(uuid, amount);
	}
	
	/**
	 * Creates an account for player on this server if it does not yet exist
	 * @param uuid
	 */
	private boolean confirmAccountExistance(UUID uuid)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return false;
		
		//Add player to database
		try {
			//Insert player to database with UUID and default balance amount
			Statement st = db.getStatement();
			st.executeUpdate("INSERT IGNORE INTO " + KCurrency.getTableName() + " (" + 
					KCurrency.getUUIDName() + ", " + KCurrency.getBalanceName() + ") VALUES ('" + uuid + "', " + 
					KCurrency.getPlugin(KCurrency.class).getConfigHandler().getStartBalance() + ");");
			/*KCurrency.getDatabaseConnector().runQuery("INSERT INTO " + KCurrency.getTableName() + " (" + 
					KCurrency.getUUIDName() + ", " + KCurrency.getBalanceName() + ") VALUES ('" + uuid + "', " + 
					KCurrency.getPlugin(KCurrency.class).getConfigHandler().getStartBalance() + ");");*/
			return true;
		} catch (Exception e) {
			Bukkit.broadcastMessage("§4Failed to generate balance information to database, please contanct server staff IMMEDIATELY!");
			e.printStackTrace();
		}
		
		return false;
	}
}
