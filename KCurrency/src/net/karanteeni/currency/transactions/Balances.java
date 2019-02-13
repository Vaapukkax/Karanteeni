package net.karanteeni.currency.transactions;

import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.currency.KCurrency;

public class Balances {

	/**
	 * Returns the balance of a player
	 * @param uuid
	 * @return
	 */
	public Double getBalance(UUID uuid)
	{
		try {
			if(!confirmAccountExistance(uuid))
				return null;
			
			return KCurrency.getDatabaseConnector().getDouble(
					"SELECT "+KCurrency.getBalanceName()+" FROM "+KCurrency.getTableName()+" WHERE " + KCurrency.getUUIDName() + "='"+uuid+"'", 
					KCurrency.getBalanceName());
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
		try {
			UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
			
			if(!confirmAccountExistance(uuid))
				return null;
			
			return KCurrency.getDatabaseConnector().getDouble(
					"SELECT "+KCurrency.getBalanceName()+" FROM "+KCurrency.getTableName()+" WHERE " + KCurrency.getUUIDName() + "='"+
					uuid+"'", 
					KCurrency.getBalanceName());
		} catch (Exception e) {
			return Double.NaN;
		}
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
			
			KCurrency.getDatabaseConnector().updateDouble(KCurrency.getTableName(), KCurrency.getBalanceName(), KCurrency.getUUIDName()+"='"+uuid+"'", 
					amount);
			return true;
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
		try {
			UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
			
			if(!confirmAccountExistance(uuid))
				return false;
			
			KCurrency.getDatabaseConnector().updateDouble(KCurrency.getTableName(), KCurrency.getBalanceName(), KCurrency.getUUIDName()+"='"+
					uuid+"'", 
					amount);
			return true;
		} catch (Exception e) {
			return false;
		}
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
			
			KCurrency.getDatabaseConnector().updateDouble(KCurrency.getTableName(), KCurrency.getBalanceName(), KCurrency.getUUIDName()+"='"+
					uuid+"'", 
					balance+amount);
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
		try {
			UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
			
			if(!confirmAccountExistance(uuid))
				return null;
			
			double balance = getBalance(uuid);
			KCurrency.getDatabaseConnector().updateDouble(KCurrency.getTableName(), KCurrency.getBalanceName(), KCurrency.getUUIDName()+"='"+
					uuid+"'", 
					balance+amount);
			return balance+amount;
		} catch (Exception e) {
			return Double.NaN;
		}
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
			
			KCurrency.getDatabaseConnector().updateDouble(KCurrency.getTableName(), KCurrency.getBalanceName(), KCurrency.getUUIDName()+"='"+
					uuid+"'", 
					balance-amount);
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
		try {
			UUID uuid = KCurrency.getPlayerHandler().getUUID(name);
			if(!confirmAccountExistance(uuid))
				return null;
			
			double balance = getBalance(uuid);
			KCurrency.getDatabaseConnector().updateDouble(KCurrency.getTableName(), KCurrency.getBalanceName(), KCurrency.getUUIDName()+"='"+
					uuid+"'", 
					balance-amount);
			return balance-amount;
		} catch (Exception e) {
			return Double.NaN;
		}
	}
	
	/**
	 * Creates an account for player on this server if it does not yet exist
	 * @param uuid
	 */
	private boolean confirmAccountExistance(UUID uuid)
	{
		DatabaseConnector db = KaranteeniCore.getDatabaseConnector();
		if(!db.isConnected()) return false;
		
		//check if this player is already in the database
		try {
			ResultSet set = db.executeQuery("SELECT * FROM " + KCurrency.getTableName() + 
					" WHERE "+KCurrency.getUUIDName()+"='"+uuid+"';");
			
			if(set.next())
				return true;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//Add player to database
		try {
			//Insert player to database with UUID and default balance amount
			KCurrency.getDatabaseConnector().runQuery("INSERT INTO " + KCurrency.getTableName() + " (" + 
					KCurrency.getUUIDName() + ", " + KCurrency.getBalanceName() + ") VALUES ('" + uuid + "', " + 
					KCurrency.getPlugin(KCurrency.class).getConfigHandler().getStartBalance() + ");");
			return true;
		} catch (Exception e) {
			Bukkit.broadcastMessage("§4Failed to generate balance information to database, please contanct server staff IMMEDIATELY!");
			Bukkit.broadcastMessage("§4Virhe tilin luomisessa tietokantaan, otathan yhteyttä palvelimen ylläpitoon VÄLITTÖMÄSTI!");
			e.printStackTrace();
		}
		
		return false;
	}
}
