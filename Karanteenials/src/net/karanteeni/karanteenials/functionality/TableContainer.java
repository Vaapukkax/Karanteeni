package net.karanteeni.karanteenials.functionality;

/**
 * Tablecontainer abstract class to enforce database table creation
 * @author Nuubles
 *
 */
public abstract class TableContainer {
	TableContainer() {
		initTable();
	}
	
	/**
	 * Initializes database table
	 */
	protected abstract void initTable();
}