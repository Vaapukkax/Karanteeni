package net.karanteeni.currency;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.players.PlayerHandler;
import net.karanteeni.currency.commands.Bal;
import net.karanteeni.currency.commands.Baltop;
import net.karanteeni.currency.commands.Eco;
import net.karanteeni.currency.commands.Pay;
import net.karanteeni.currency.events.JoinEvent;
import net.karanteeni.currency.transactions.Balances;
import net.karanteeni.currency.transactions.TransactionResult;

public class KCurrency extends KaranteeniPlugin {
	
	public KCurrency() {
		super(true);
	}

	protected static String TABLE_NAME;
	protected static String BALANCE = "bal";
	protected static String UUID = "UUID";
	private static Balances balances = new Balances();
	//protected static String moneyUnit = "$";
	private static ConfigHandler configHandler;
	public final static SoundType MONEY_RECEIVED = new SoundType(Sound.BLOCK_IRON_DOOR_OPEN, 0.6f, 0.8f);
	public final static SoundType MONEY_LOST = new SoundType(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.9f, 2f);
	
	@Override
	public void onLoad()
	{
		getLogger().log(Level.INFO, "Loading KCurrency Plugin...");
		
		getLogger().log(Level.INFO, "KCurrency loaded");
	}
	
	@Override
	public void onEnable()
	{
		//Register the TABLE_NAME variable
		TABLE_NAME = String.format("balance_%s", getServerIdentificator());
		
		//Generate the database table and check if we're connected to the database
		if(!generateBalanceTable()/* || !getDatabaseConnector().isConnected()*/)
		{
			//If not successful, disable this plugin
			this.setEnabled(false);
			return;
		}
		
		registerConfig();
		getServer().getPluginManager().registerEvents(new JoinEvent(), this);
		registerCommands();
		Bukkit.getConsoleSender().sendMessage("§aKCurrency has been enabled!");
	}
	
	@Override
	public void onDisable()
	{
		
		
		Bukkit.getConsoleSender().sendMessage("§cKCurrency has been disabled!");
	}
	
	/**
	 * Registers the commands for this balance plugin
	 */
	private void registerCommands()
	{
		(new Baltop("baltop", 
				"/baltop [page]", 
				"Show to richest players on the server", 
				getDefaultMsgs().defaultNoPermission())).register();
		//Arrays.asList("rikkaimmatpelaajatservulla", "rikkaimmat", "balancetop", "topmoney", "moneytop")
		(new Pay("pay", 
				"/pay <player> <mount>", 
				"Send money to player", 
				getDefaultMsgs().defaultNoPermission())).register();
		//Arrays.asList("maksa")
		(new Bal("bal",
				"/bal [player]",
				"Shows balance")).register();
		//Arrays.asList("balance", "pankki", "varat")
		(new Eco("eco",
				"/eco help",
				"Currency commands")).register();
	}
	
	public static String getTableName()
	{
		return TABLE_NAME;
	}
	
	public static String getBalanceName()
	{
		return BALANCE;
	}
	
	public static String getUUIDName()
	{
		return UUID;
	}
	
	/**
	 * Return the balance handler for players
	 * @return
	 */
	public static Balances getBalances()
	{
		return balances;
	}
	
	/**
	 * Generates a balance table for player balances
	 */
	private boolean generateBalanceTable()
	{
		DatabaseConnector db = KCurrency.getDatabaseConnector();

		try {
			Statement st = db.getStatement();
			st.executeUpdate(
					"CREATE TABLE IF NOT EXISTS "+getTableName()+" ("+
					getUUIDName()+" VARCHAR(60) NOT NULL, " + 
					getBalanceName() + " DECIMAL(20, 2) NOT NULL DEFAULT 0," + 
					"PRIMARY KEY("+UUID+"), " + 
					"FOREIGN KEY ("+UUID+") REFERENCES "+
						PlayerHandler.PlayerDataKeys.PLAYER_TABLE
						+"("+PlayerHandler.PlayerDataKeys.UUID+")"+
						" ON DELETE CASCADE ON UPDATE CASCADE "+");");
			st.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Reduces money from player
	 * @param uuid
	 * @param amount
	 * @return
	 */
	public static TransactionResult reduceMoney(UUID uuid, double amount, boolean showBar)
	{
		Double ret = balances.removeFromBalance(uuid, amount);
		
		if(ret == null)
			return TransactionResult.UNSUCCESSFUL;
		else if(Double.isNaN(ret))
			return TransactionResult.UNSUCCESSFUL;
		else
		{
			//Show the bossbar to player
			if(showBar && Bukkit.getPlayer(uuid).isOnline())
			{
				BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getTranslation(
						KCurrency.getPlugin(KCurrency.class), 
						Bukkit.getPlayer(uuid), "you-lost")
						.replace("%amount%", Double.toString(amount))
						.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit())
						, BarColor.GREEN, BarStyle.SOLID);
				
				KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(uuid), KCurrency.MONEY_LOST, 3, 1, true, bar);
			}
			
			return TransactionResult.SUCCESSFUL;
		}
	}
	
	public static TransactionResult setBalance(UUID uuid, double amount, boolean showBar)
	{
		boolean ret = balances.setBalance(uuid, amount);
		
		if(ret)
		{
			//Show the bossbar to player
			if(showBar && Bukkit.getPlayer(uuid).isOnline())
			{
				BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getTranslation(
						KCurrency.getPlugin(KCurrency.class), 
						Bukkit.getPlayer(uuid), "your-balance-set")
						.replace("%amount%", Double.toString(amount))
						.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit())
						, BarColor.GREEN, BarStyle.SOLID);
				
				KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(uuid), KCurrency.MONEY_RECEIVED, 3, 1, true, bar);
			}
			
			return TransactionResult.SUCCESSFUL;
		}
		else
			return TransactionResult.UNSUCCESSFUL;
	}
	
	/**
	 * Adds money to player
	 * @param uuid
	 * @param amount
	 * @return
	 */
	public static TransactionResult addMoney(UUID uuid, double amount, boolean showBar)
	{
		Double ret = balances.addToBalance(uuid, amount);
		
		if(ret == null)
			return TransactionResult.UNSUCCESSFUL;
		else if(Double.isNaN(ret))
			return TransactionResult.UNSUCCESSFUL;
		else
		{
			//Show the bossbar to player
			if(showBar && Bukkit.getPlayer(uuid).isOnline())
			{
				BossBar bar = Bukkit.getServer().createBossBar(KCurrency.getTranslator().getTranslation(
						KCurrency.getPlugin(KCurrency.class), 
						Bukkit.getPlayer(uuid), "you-received")
						.replace("%amount%", Double.toString(amount))
						.replace("%unit%", KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit())
						, BarColor.GREEN, BarStyle.SOLID);
				
				KCurrency.getMessager().sendBossbar(Bukkit.getPlayer(uuid), KCurrency.MONEY_RECEIVED, 3, 1, true, bar);
			}
			
			return TransactionResult.SUCCESSFUL;
		}
	}
	
	/**
	 * Reduces money from player
	 * @param uuid
	 * @param amount
	 * @return
	 */
	public static TransactionResult reduceMoney(String name, double amount)
	{
		Double ret = balances.removeFromBalance(name, amount);
		
		if(ret == null)
			return TransactionResult.UNSUCCESSFUL;
		else if(Double.isNaN(ret))
			return TransactionResult.UNSUCCESSFUL;
		else
			return TransactionResult.SUCCESSFUL;
	}
	
	public static TransactionResult setBalance(String name, double amount)
	{
		boolean ret = balances.setBalance(name, amount);
		
		if(ret)
			return TransactionResult.SUCCESSFUL;
		else
			return TransactionResult.UNSUCCESSFUL;
	}
	
	/**
	 * Adds money to player
	 * @param uuid
	 * @param amount
	 * @return
	 */
	public static TransactionResult addMoney(String name, double amount)
	{
		Double ret = balances.addToBalance(name, amount);
		
		if(ret == null)
			return TransactionResult.UNSUCCESSFUL;
		else if(Double.isNaN(ret))
			return TransactionResult.UNSUCCESSFUL;
		else
			return TransactionResult.SUCCESSFUL;
	}
	
	/**
	 * Returns this plugins confighandler
	 * @return
	 */
	public ConfigHandler getConfigHandler()
	{
		return configHandler;
	}
	
	/**
	 * Registers the configuration file values
	 */
	private void registerConfig()
	{
		configHandler = new ConfigHandler(this);
	}
	
	/**
	 * Is responsible for the value units and texts
	 * @author Nuubles
	 */
	public class ConfigHandler
	{
		private static final String CURRENCY_NAME = "display-char";
		private String currencyName;
		private static final String START_BALANCE = "start-balance";
		private double startBalance;
		private static final String PREFIX = "message-prefix";
		private String prefix;
		
		/**
		 * Register the config data that this plugin uses
		 */
		public ConfigHandler(Plugin plugin)
		{
			FileConfiguration yml = plugin.getConfig();
			Bukkit.getLogger().log(Level.CONFIG, yml.getString(CURRENCY_NAME));
			if(!yml.isSet(CURRENCY_NAME))
			{
				yml.set(CURRENCY_NAME, "$");
				currencyName = "$";
				plugin.saveConfig();
			}
			else
				currencyName = yml.getString(CURRENCY_NAME);
			
			if(!yml.isSet(START_BALANCE))
			{
				yml.set(START_BALANCE, 100.0);
				startBalance = 100.0;
				plugin.saveConfig();
			}
			else
				startBalance = yml.getDouble(START_BALANCE);
			
			if(!yml.isSet(PREFIX))
			{
				yml.set(PREFIX, "§a> ");
				prefix = "§a> ";
				plugin.saveConfig();
			}
			else
				prefix = yml.getString(PREFIX);
		}
		
		/**
		 * Gets the displayname of the currency
		 * @return
		 */
		public String getCurrencyUnit()
		{
			return currencyName;
		}
		
		/**
		 * Get the balance which player should have when he join the server the first time
		 * @return
		 */
		public double getStartBalance()
		{
			return startBalance;
		}
		
		/**
		 * Returns the prefix for messages
		 * @return
		 */
		public String getPrefix()
		{
			return this.prefix;
		}
	}
}
