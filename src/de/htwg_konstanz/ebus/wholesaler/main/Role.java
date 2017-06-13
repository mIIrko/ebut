package de.htwg_konstanz.ebus.wholesaler.main;

/**
 * @author schobast
 *
 *         Enumeration containing the three different roles which can perform
 *         import and export operations
 */
public enum Role {

	INTERNAL_ROLE("internal", 1), CUSTOMER_ROLE("customer", 2), SUPPLIER_ROLE("supplier", 3);

	/**
	 * The role's name
	 */
	private String role;
	/**
	 * The role's number (id)
	 */
	private int numb;

	/**
	 * Private enum constructor which takes the role's name and id
	 */
	private Role(String role, int numb) {
		this.role = role;
		this.numb = numb;
	}

	/**
	 * @return the role's name
	 */
	public String getRole() {
		return this.role;
	}

	/**
	 * @return he role's number (id)
	 */
	public int getRoleNumb() {
		return this.numb;
	}

	/**
	 * Returns the role object depending on given number (id)
	 * 
	 * @param number:
	 *            the role's identifier
	 * @return to role according to the given number
	 */
	public static Role getRoleByNumber(int number) {
		if (number == 1) {
			return Role.INTERNAL_ROLE;
		} else if (number == 2) {
			return Role.CUSTOMER_ROLE;
		}
		return Role.SUPPLIER_ROLE;
	}
}
