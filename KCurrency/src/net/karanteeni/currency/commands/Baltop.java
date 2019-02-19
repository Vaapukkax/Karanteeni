package net.karanteeni.currency.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.karanteeni.core.database.DatabaseConnector;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.currency.KCurrency;

public class Baltop extends net.karanteeni.core.command.AbstractCommand {

	public Baltop(String command, String usage, String description, String permissionMessage) {
		super(KCurrency.getPlugin(KCurrency.class), command, usage, description, permissionMessage);
	}

	@Deprecated
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		//Does the player have permission for this
		if(!sender.hasPermission("kcurrency.baltop"))
		{
			KCurrency.getMessager().sendMessage(sender, Sounds.NO.get(), KCurrency.getDefaultMsgs().noPermission(sender));
			return true;
		}
		
		DatabaseConnector db = KCurrency.getDatabaseConnector();
		List<UUID> players = new ArrayList<UUID>();
		List<Double> balances = new ArrayList<Double>();
		
		//Get the balances from database
		try {
			/*
			players = db.getUUIDList("SELECT * FROM " + KCurrency.getTableName() + " ORDER BY " + KCurrency.getBalanceName() + ";", 
					KCurrency.getUUIDName());
			balances = db.getDoubleList("SELECT * FROM " + KCurrency.getTableName() + " ORDER BY " + KCurrency.getBalanceName() + ";", 
					KCurrency.getBalanceName());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < players.size(); ++i)
		{
			String name = KCurrency.getPlayerHandler().getOfflineName(players.get(i));
			Bukkit.broadcastMessage("Name: " + name + " Balance: " + balances.get(i) + KCurrency.getPlugin(KCurrency.class).getConfigHandler().getCurrencyUnit());
		}
		
		
		
		return true;
	}

}
