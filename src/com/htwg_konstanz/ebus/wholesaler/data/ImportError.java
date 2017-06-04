package com.htwg_konstanz.ebus.wholesaler.data;

/**
 * @author schobast
 * 
 * Enumeration covers all errors related to import and export operations
 *
 */
public enum ImportError {
	UNKNOWN_SUPPLIER_ERROR(1, "Unknown supplier"), ARTICLE_EXISTS_ERROR(1, "Article already exists") ;
	
	/**
	 * The error code
	 */
	private int code;
	/**
	 * The error description
	 */
	private String desc;
	
	/**
	 * Default enumeration constructor
	 */
	private ImportError(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return code + " - " + desc;
	}
}
