package de.htwg_konstanz.ebus.wholesaler.xml;

/**
 * @author schobast
 *
 */
public class ARTICLE {

	private String supplierAid;
	private ARTICLE_DETAILS articleDetails;

	
	
	public ARTICLE_DETAILS getArticleDetails() {
		return articleDetails;
	}

	public void setArticleDetails(ARTICLE_DETAILS articleDetails) {
		this.articleDetails = articleDetails;
	}

	public String getSupplierAid() {
		return supplierAid;
	}

	public void setSupplierAid(String supplierAid) {
		this.supplierAid = supplierAid;
	}

}
