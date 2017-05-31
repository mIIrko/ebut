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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author schobast
 *
 */
public class BMECAT {
	
	private String version;
	
	private HEADER header;
	
	private T_NEW_CATALOG tNewCatalog;

	@XmlElement(name="HEADER")
	public HEADER getHeader() {
		return header;
	}

	public void setHeader(HEADER header) {
		this.header = header;
	}

	@XmlElement(name="T_NEW_CATALOG")
	public T_NEW_CATALOG gettNewCatalog() {
		return tNewCatalog;
	}

	public void settNewCatalog(T_NEW_CATALOG tNewCatalog) {
		this.tNewCatalog = tNewCatalog;
	}

	@XmlAttribute(name="version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
