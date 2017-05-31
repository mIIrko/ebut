package de.htwg_konstanz.ebus.wholesaler.xml;

/**
 * @author schobast
 *
 */
public class ARTICLE_ORDER_DETAILS {
	private String orderUnit;
	private String contentUnit;
	private int noCuPerOu;

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
}
