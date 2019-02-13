package net.karanteeni.currency.transactions;

import java.util.UUID;

import net.karanteeni.currency.KCurrency;

public class Transaction {
	private TransactionResult result = TransactionResult.UNPROCESSED;
	private final UUID from;
	private final UUID to;
	private final double amount;

	
	/**
	 * Creates a new transaction class
	 * @param from
	 * @param to
	 * @param amount
	 */
	public Transaction(final UUID from, final UUID to, final double amount)
	{
		this.from = from;
		this.to = to;
		this.amount = amount;
	}	
	
	/**
	 * Get the result of this transaction
	 * @return
	 */
	public TransactionResult getResult()
	{
		return result;
	}
	
	/**
	 * Performs a transaction from a player to another player
	 * @return
	 */
	public TransactionResult performTransaction()
	{
		if(from == null || to == null)
			return TransactionResult.UNSUCCESSFUL;
		
		Balances bal = KCurrency.getBalances();
		
		Double balanceFrom = bal.getBalance(from);
		
		if(balanceFrom == null || Double.isNaN(balanceFrom))
			return TransactionResult.UNSUCCESSFUL;
		
		Double balanceTo = bal.getBalance(to);
		
		if(balanceTo == null || Double.isNaN(balanceTo))
			return TransactionResult.UNSUCCESSFUL;
		
		if(balanceFrom >= amount)
		{
			bal.removeFromBalance(from, amount);
			bal.addToBalance(to, amount);
			
			return result = TransactionResult.SUCCESSFUL;
		}
		else
			return result = TransactionResult.INSUFFICIENT_CREDITS;
	}
}
