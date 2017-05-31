/*************************************************************************
 * 
 * CONFIDENTIAL
 * __________________
 * 
 *  [2016] Bastian Schoettle & Tim Schoettle
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Bastian Schoettle & Tim Schoettle and his suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Bastian Schoettle & Tim Schoettle
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Bastian Schoettle & Tim Schoettle.
 *
 */
package de.htwg_konstanz.ebus.wholesaler.xml;

/**
 * @author schobast
 *
 */
public class ARTICLE_DETAILS {

	private String descriptionShort;
	private String descriptionLong;
	private String ean;

	public String getDescriptionShort() {
		return descriptionShort;
	}

	public void setDescriptionShort(String descriptionShort) {
		this.descriptionShort = descriptionShort;
	}

	public String getDescriptionLong() {
		return descriptionLong;
	}

	public void setDescriptionLong(String descriptionLong) {
		this.descriptionLong = descriptionLong;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

}
