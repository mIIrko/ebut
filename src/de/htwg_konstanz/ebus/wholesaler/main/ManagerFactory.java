package de.htwg_konstanz.ebus.wholesaler.main;

/**
 * @author schobast
 * 
 * Factory class to create implementations of the the interfaces IImportManager and IExportManager
 *
 */
@Deprecated
public class ManagerFactory {
	/**
	 * Private constructor to avoid instantiation
	 */
	private ManagerFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the ExportManager for the role Role.INTERNAL_ROLE 
	 */
	public IExportManager createInternalExportManager(){
		return new ExportManagerImpl(Role.INTERNAL_ROLE);
	}

	/**
	 * @return the ExportManager for the role Role.CUSTOMER_ROLE 
	 */
	public IExportManager createCustomerExportManager(){
		return new ExportManagerImpl(Role.CUSTOMER_ROLE);
	}
}

