package de.htwg_konstanz.ebus.wholesaler.main;

/**
 * @author schobast
 *
 * Enumeration covers all errors related to import
 *
 */
public enum ExportError {
	NO_ARTICLE_FOUND(1, "No articles found"),
    EXPORTED_FILE_NOT_VALID(2, "Exported xml file is not valid");

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
	private ExportError(int code, String desc) {
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

	/**
	 * toString method with additional message
	 *
	 * @param message: message to display
	 * @return the string containing the message and error information
	 */
	public String toString(String message){
		return desc + ": " + message + " (" + code + ")";
	}
}
