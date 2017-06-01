package com.htwg_konstanz.ebus.wholesaler.data;

/**
 * @author schobast
 *
 * 
 *
 */
public enum Role {
	
	CUSTOMER_ROLE("customer"), INTERNAL_ROLE("interal");
	
	private String role;
	
	/**
	 * 
	 */
	private Role(String role) {
		this.role = role;
	}
	
	public String getRole(){
		return this.role;
	}
}
