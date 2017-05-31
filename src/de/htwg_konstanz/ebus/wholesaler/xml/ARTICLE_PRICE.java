package de.htwg_konstanz.ebus.wholesaler.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author schobast
 *
 */
public class ARTICLE_PRICE {


	private String priceType;

	private double priceAmount;

	private String priceCurrency;
	private double tax;

	private List<String> territory;

	@XmlElement(name="TERRITORY")
	public List<String> getTerritory() {
		return territory;
	}

	public void setTerritory(List<String> territory) {
		this.territory = territory;
	}

	@XmlElement(name="PRICE_AMOUNT")
	public double getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(double priceAmount) {
		this.priceAmount = priceAmount;
	}

	@XmlElement(name="PRICE_CURRENCY")
	public String getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(String priceCurrency) {
		this.priceCurrency = priceCurrency;
	}
	
	@XmlElement(name="TAX")
	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}
	
	@XmlAttribute(name = "price_type")
	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

}
