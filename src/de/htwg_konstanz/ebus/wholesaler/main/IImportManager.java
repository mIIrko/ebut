package de.htwg_konstanz.ebus.wholesaler.main;

import org.w3c.dom.Document;


/**
 * @author schobast
 *
 * Interface to decouple implementation of import functionalities
 *
 */
public interface IImportManager {

	/**
	 * Stores all articles of a given Document (BMECAT) to the database
	 *
	 * @param articles as Document instance
	 */
	public abstract void storeAllArticles(Document articles) throws RuntimeException;

}
