package net.karanteeni.core.database;

/**
 * This interface allows to manage data in the database as if they were objects
 * @author Nuubles
 *
 */
public interface DatabaseObject {
	/**
	 * Save the 
	 * @return true if save was successful
	 */
	public abstract boolean save();
	
	
	/**
	 * Deletes this instance of an object from the database 
	 * @return true if deletion was successful
	 */
	public abstract boolean delete();
}
