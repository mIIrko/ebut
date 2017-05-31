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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author schobast
 *
 */
public class T_NEW_CATALOG {
	

	private List<ARTICLE> article;

	@XmlElement(name = "ARTICLE")
	public List<ARTICLE> getArticle() {
		return article;
	}

	public void setArticle(List<ARTICLE> article) {
		this.article = article;
	}

}
