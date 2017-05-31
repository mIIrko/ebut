package de.htwg_konstanz.ebus.wholesaler.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author schobast
 *
 */
public class CATALOG {
	private String language;
	private String catalogId;
	private String catalogVersion;
	private String catalogName;

	@XmlElement(name = "LANGUAGE")
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@XmlElement(name = "CATALOG_ID")
	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	@XmlElement(name = "CATALOG_VERSION")
	public String getCatalogVersion() {
		return catalogVersion;
	}

	public void setCatalogVersion(String catalogVersion) {
		this.catalogVersion = catalogVersion;
	}

	@XmlElement(name = "CATALOG_NAME")
	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

}
