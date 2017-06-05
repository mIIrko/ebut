package de.htwg_konstanz.ebus.wholesaler.main;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSupplier;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.SupplierBOA;

/**
 * @author schobast
 *
 */
public class ImportManagerImpl implements IImportManager{

	/**
	 * 
	 */
	private ProductBOA productBoa;
	/**
	 * 
	 */
	private SupplierBOA supplierBoa;

	/**
	 * Default constructor
	 */
	public ImportManagerImpl() {
		this.productBoa = ProductBOA.getInstance();
		this.supplierBoa = SupplierBOA.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.htwg_konstanz.ebus.wholesaler.data.IImportManager#storeAllArticles(
	 * org.w3c.dom.Document)
	 */
	@Override
	public void storeAllArticles(Document doc) throws RuntimeException {
		doc.getDocumentElement().normalize();
		NodeList supplierList = doc.getElementsByTagName("SUPPLIER");
		Node supplier = supplierList.item(0);
		if (supplier.getNodeType() == Node.ELEMENT_NODE) {
			Element supEl = (Element) supplier;
			String supName = supEl.getTextContent();
			if (!isKnownSupplier(supName)) {
				throw new RuntimeException(ImportError.UNKNOWN_SUPPLIER_ERROR.toString());
			}
		}
		NodeList catalogList = doc.getElementsByTagName("T_NEW_CATALOG");
		Node catalog = catalogList.item(0);
		NodeList articles = catalog.getChildNodes();
		for (int i = 0; i < articles.getLength(); i++) {
			//Initialize product
			BOProduct boProduct = new BOProduct();
			Node article = articles.item(0);
			NodeList articleContent = article.getChildNodes();
			for (int j = 0; j < articleContent.getLength(); j++) {
				if (articleContent.item(j).getNodeName().equals("SUPPLIER_AID")) {
					String supplierAid= articleContent.item(j).getTextContent();
					boProduct.setOrderNumberSupplier(supplierAid);
				} else if(articleContent.item(j).getNodeName().equals("ARTICLE_DETAILS")){
					processArticleDetails(boProduct, articleContent.item(j));
				} else if (articleContent.item(j).getNodeName().equals("ARTICLE_PRICE_DETAILS")) {
					//TODO: Process child nodes
				}
			}
			productBoa.saveOrUpdate(boProduct);
		}
	}
	
	private void processArticleDetails(BOProduct prod, Node articleDetails){
		NodeList list = articleDetails.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals("DESCRIPTION_SHORT")) {
				prod.setShortDescription(list.item(i).getTextContent());
			} else if (list.item(i).getNodeName().equals("DESCRIPTION_LONG")) {
				prod.setLongDescription(list.item(i).getTextContent());
			} else if (list.item(i).getNodeName().equals("EAN")) {
				//TODO: Handle EAN
			}
		}
	}

	/**
	 * Checks if supplier is known
	 * 
	 * @param supplierAid:
	 *            supplier identifier
	 * @return true if supplier is known
	 */
	private boolean isKnownSupplier(String companyName) {
		List<BOSupplier> sup = supplierBoa.findByCompanyName(companyName);
		if (sup == null) {
			return false;
		} else if (sup.size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if article is stored
	 * 
	 * @param matNum:
	 *            product identifier
	 * @return true if article exists
	 */
	private boolean isArticleStored(String matNum) {
		BOProduct prod = productBoa.findByMaterialNumber(Integer.valueOf(matNum));
		if (prod == null) {
			return false;
		}
		return true;
	}



}
