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
	public boolean save();
	
	
	/**
	 * Initialize the object (create tables)
	 * @return true if initialization was successful
	 */
	public boolean initialize();
	
	
	/**
	 * Deletes this instance of an object from the database 
	 * @return true if deletion was successful
	 */
	public boolean delete();
}
