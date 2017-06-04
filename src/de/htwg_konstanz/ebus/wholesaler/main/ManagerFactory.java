package de.htwg_konstanz.ebus.wholesaler.main;

/**
 * @author schobast
 *
 */
public class ManagerFactory {
	/**
	 *
	 */
	private ManagerFactory() {
		// TODO Auto-generated constructor stub
	}

	public IExportManager createInternalExportManager(){
		return new ExportManagerImpl(Role.INTERNAL_ROLE);
	}

	public IExportManager createCustomerExportManager(){
		return new ExportManagerImpl(Role.CUSTOMER_ROLE);
	}
}
