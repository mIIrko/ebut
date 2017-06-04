package com.htwg_konstanz.ebus.wholesaler.data;

/**
 * @author schobast
 *
 *
 *
 */
public enum Role {

    INTERNAL_ROLE("interal", 1),
    CUSTOMER_ROLE("customer", 2),
    SUPPLIER_ROLE("supplier", 3);

	private String role;
    private int numb;

	/**
	 *
	 */
	private Role(String role, int numb) {
		this.role = role;
		this.numb = numb;
	}

	public String getRole(){
		return this.role;
	}

	public int getRoleNumb() {return this.numb; }

    public static Role getRoleByNumber(int number) {

        return Role.values()[number];

    }
}
