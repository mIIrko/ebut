package de.htwg_konstanz.ebus.wholesaler.main;

/**
 * @author schobast
 *
 *
 *
 */
public enum Role {

    INTERNAL_ROLE("internal", 1),
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
        if (number == 1){
    return Role.INTERNAL_ROLE;
        } else if (number == 2){
    return Role.CUSTOMER_ROLE;
        }
        return Role.SUPPLIER_ROLE;
    }
}
