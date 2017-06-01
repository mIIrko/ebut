package de.htwg_konstanz.ebus.wholesaler.main;

import org.w3c.dom.Document;


/**
 * @author schobast
 *
 * Interface to decouple implementation of export functionalities
 *
 */
public interface IExportManager {


	/**
	 * Retrieves all articles as Document (BMECAT) instance.
	 *
	 * @return the BMECAT instance containing all articles.
	 */
	public abstract Document retriveAllArticles();

	/**
	 * Retrieves selection of articles depending on selector parameter.
	 * Selector represents a substring of an article's short description field.
	 *
	 * @param selector: Substring of an article's short description.
	 *
	 * @return the Document (BMECAT) instance containing the article selection.
	 */
	public abstract Document retriveSelectiveArticles(String selector);

}
