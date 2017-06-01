package de.htwg_konstanz.ebus.wholesaler.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author schobast
 *
 */
public class ARTICLE_ORDER_DETAILS {
	private String orderUnit;
	private String contentUnit;
	private int noCuPerOu;
	
	private List<ARTICLE_PRICE> articlePrices;

	public String getOrderUnit() {
		return orderUnit;
	}

	public void setOrderUnit(String orderUnit) {
		this.orderUnit = orderUnit;
	}

	public String getContentUnit() {
		return contentUnit;
	}

	public void setContentUnit(String contentUnit) {
		this.contentUnit = contentUnit;
	}

	public int getNoCuPerOu() {
		return noCuPerOu;
	}

	public void setNoCuPerOu(int noCuPerOu) {
		this.noCuPerOu = noCuPerOu;
	}
	@XmlElementWrapper(name="ARTICLE_PRICE_DETAILS")
	@XmlElement(name="ARTICLE_PRICE")
	public List<ARTICLE_PRICE> getArticlePrices() {
		return articlePrices;
	}

	public void setArticlePrices(List<ARTICLE_PRICE> articlePrices) {
		this.articlePrices = articlePrices;
	}
}
