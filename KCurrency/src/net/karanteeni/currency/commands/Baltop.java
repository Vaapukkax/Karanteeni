package net.karanteeni.currency.commands;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.currency.KCurrency;

/**
 * Returns top N players from page M from the database
 * @author Nuubles
 *
 */
public class Baltop extends AbstractCommand implements TranslationContainer{

	/**
	 * Initializes the baltop class for commands
	 * @param command
	 * @param usage
	 * @param description
	 * @param permissionMessage
	 */
	public Baltop(String command, String usage, String description, String permissionMessage) {
		super(KCurrency.getPlugin(KCurrency.class), command, usage, description, permissionMessage);
		registerTranslations();
		getMessageRowCount();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		//Does the player have permission for this
		if(!sender.hasPermission("kcurrency.baltop")) {
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), KCurrency.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		//Get the page of baltop
		int page = 1;
		if(args.length != 0) {
			try {
				page = Integer.parseInt(args[0]);
			} catch(Exception e) {
				//Incorrect data given, return
				KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), 
						Prefix.NEGATIVE + KCurrency.getTranslator().getTranslation(
								plugin, 
								sender, 
								"invalid-arguments"));
				return true;
			}
		}
		
		int maxPages = this.getPageCount();
		//Check that page count retrieval was successful
		if(maxPages == -1) {
			KCurrency.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE + KCurrency.getDefaultMsgs().databaseError(sender));
			return true;
		}
		
		//Check that player gave correct page within range
		if(page < 1 || page > maxPages) {
			//Show user the min-max page range
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), 
					Prefix.NEGATIVE + KCurrency.getTranslator().getTranslation(
							plugin, sender, "invalid-page").replace("%max%", Integer.toString(this.getPageCount())));
			return true;
		}
		
		//Show the baltop to the player
		showBaltop(sender, page);
		
		return true;
	}

	/**
	 * Returns the top N players from the baltop 
	 * @param page
	 * @param rowCount
	 * @return
	 */
	private List<Entry<String,Double>> getBaltop(int offset, int rowCount) {
		DatabaseConnector db = KCurrency.getDatabaseConnector();
		List<Entry<String,Double>> balances = new ArrayList<Entry<String,Double>>();
		
		//Get the balances from database
		try {
			Statement st = db.getStatement();
			//Select from N the next 10 rows
			//NORMAL SQL
			/*ResultSet rs = st.executeQuery("select player.name, "+KCurrency.getBalanceName()+
							" from "+ KCurrency.getTableName() + ", player" +
							" order by "+ KCurrency.getBalanceName() +
							" offset "+(offset-1)+" rows" +
							" fetch next "+rowCount+" rows only;");*/
			
			//MySQL
			ResultSet rs = st.executeQuery("select player.name, "+KCurrency.getBalanceName()+
					" from "+ KCurrency.getTableName() + ", player" +
					" where player.UUID = "+KCurrency.getTableName()+"."+KCurrency.getUUIDName()+
					" order by "+ KCurrency.getBalanceName() + " desc "+
					" limit "+rowCount+ 
					" offset "+offset+";");
			
			//Add the top rowCount rows to the list
			while(rs.next())
				balances.add(new SimpleEntry<String,Double>(rs.getString(1), rs.getDouble(2)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return balances;
	}
	
	/**
	 * Shows the baltop to given commandsender
	 * @param sender to who will the baltop be shown
	 * @param page which page of baltop is shown
	 */
	private void showBaltop(CommandSender sender, int page) {
		int messageRows = this.getMessageRowCount(); //Amount of rows in one baltop message
		//Get the amount of baltop rows specified in the config
		List<Entry<String,Double>> balances = getBaltop((page-1)*messageRows, messageRows);
		
		String balTopText = KCurrency.getTranslator().getTranslation(
				plugin, sender, "toprow").replace("%page%", Integer.toString(page));

		String content = "";
		String baltopRow = KCurrency.getTranslator().getTranslation(plugin, sender, "baltop-entry");
		int row = 0;
		
		//Loop all balances to one string
		for(Entry<String,Double> pair : balances) {
			//Add a row of balances to the message
			if(row == 0)
				content +=  baltopRow.replace("%row%", Integer.toString((page-1)*messageRows + ++row))
						.replace("%player%", pair.getKey())
						.replace("%amount%", String.format("%.2f", pair.getValue()));
			else
				content += "\n"+ baltopRow.replace("%row%", Integer.toString((page-1)*messageRows + ++row))
					.replace("%player%", pair.getKey())
					.replace("%amount%", String.format("%.2f", pair.getValue()));
		}
		
		//Send the message to the player
		KCurrency.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), balTopText.replace("%content%", content));
	}
	
	
	/**
	 * Get the maximum page that can be used or -1 on error
	 * @return
	 */
	private int getPageCount() {
		//Get the balances from database
		DatabaseConnector db = KCurrency.getDatabaseConnector();
		
		try {
			Statement st = db.getStatement();
			//Count the amount of balances
			ResultSet rs = st.executeQuery("select COUNT(*)"+
							"from "+ KCurrency.getTableName()+";");
			//Get the bigger number if there are less rows than one page requires 
			if(rs.next())
				return (int) Math.max(Math.ceil(rs.getInt(1)/getMessageRowCount()),1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	/**
	 * How many rows of baltop will be shown
	 * @return Amount of rows to show baltop rows
	 */
	private int getMessageRowCount() {
		if(!plugin.getConfig().isSet("rows-in-baltop-page") || plugin.getConfig().getInt("rows-in-baltop-page") < 1) {
			plugin.getConfig().set("rows-in-baltop-page", 10);
			plugin.saveConfig();
		}
		return plugin.getConfig().getInt("rows-in-baltop-page");
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerTranslations() {
		KCurrency.getTranslator().registerTranslation(plugin, "invalid-arguments", "Incorrect page given! Please use /baltop [<page>]");
		KCurrency.getTranslator().registerTranslation(plugin, "invalid-page", "Page does not exist, please select from range 1-%max%");
		KCurrency.getTranslator().registerTranslation(plugin, "toprow", "=========[ KCurrency - Baltop %page% ]========\n%content%\n=========[ KCurrency - Baltop %page% ]========");
		KCurrency.getTranslator().registerTranslation(plugin, "baltop-entry", ">%row%> %player% > %amount%");
	}

	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return null;
	}
}
