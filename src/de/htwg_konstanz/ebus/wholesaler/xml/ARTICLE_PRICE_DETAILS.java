package de.htwg_konstanz.ebus.wholesaler.xml;

import java.util.List;

/**
 * @author schobast
 *
 */
public class ARTICLE_PRICE_DETAILS {
	
	private List<ARTICLE_PRICE_DETAILS> articlePriceDetails;

	public List<ARTICLE_PRICE_DETAILS> getArticlePriceDetails() {
		return articlePriceDetails;
	}

	public void setArticlePriceDetails(List<ARTICLE_PRICE_DETAILS> articlePriceDetails) {
		this.articlePriceDetails = articlePriceDetails;
	}
}
