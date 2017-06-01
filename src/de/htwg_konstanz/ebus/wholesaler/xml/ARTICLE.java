package de.htwg_konstanz.ebus.wholesaler.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author schobast
 *
 */
public class ARTICLE {

	private String supplierAid;
	private ARTICLE_DETAILS articleDetails;
	private List<ARTICLE_PRICE_DETAILS> articlePriceDetails;
	private ARTICLE_ORDER_DETAILS orderDetails;

	
	
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
	@XmlElement(name="ARTICLE_PRICE_DETAILS")
	public List<ARTICLE_PRICE_DETAILS> getArticlePriceDetails() {
		return articlePriceDetails;
	}

	public void setArticlePriceDetails(List<ARTICLE_PRICE_DETAILS> articlePriceDetails) {
		this.articlePriceDetails = articlePriceDetails;
	}

	public ARTICLE_ORDER_DETAILS getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(ARTICLE_ORDER_DETAILS orderDetails) {
		this.orderDetails = orderDetails;
	}

}
