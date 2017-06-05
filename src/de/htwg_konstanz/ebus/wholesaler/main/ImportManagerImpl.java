package de.htwg_konstanz.ebus.wholesaler.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.AttributeList;

import com.mysql.fabric.xmlrpc.base.Array;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCountry;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCurrency;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOPurchasePrice;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSalesPrice;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSupplier;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.PriceBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.SupplierBOA;

/**
 * @author schobast
 *
 */
public class ImportManagerImpl implements IImportManager {

	/**
	 * @author schobast
	 *
	 */
	private class PriceContainer {
		private BigDecimal priceAmount;
		private String priceCurrency;
		private BigDecimal tax;
		private List<String> territoryList;

		public PriceContainer() {
		}

		/**
		 * @param priceAmount
		 *            the priceAmount to set
		 */
		public void setPriceAmount(BigDecimal priceAmount) {
			this.priceAmount = priceAmount;
		}

		/**
		 * @param priceCurrency
		 *            the priceCurrency to set
		 */
		public void setPriceCurrency(String priceCurrency) {
			this.priceCurrency = priceCurrency;
		}

		/**
		 * @param tax
		 *            the tax to set
		 */
		public void setTax(BigDecimal tax) {
			this.tax = tax;
		}

		/**
		 * @param territoryList
		 *            the territoryList to set
		 */
		public void setTerritoryList(List<String> territoryList) {
			this.territoryList = territoryList;
		}

		/**
		 * @return the priceAmount
		 */
		public BigDecimal getPriceAmount() {
			return priceAmount;
		}

		/**
		 * @return the priceCurrency
		 */
		public String getPriceCurrency() {
			return priceCurrency;
		}

		/**
		 * @return the tax
		 */
		public BigDecimal getTax() {
			return tax;
		}

		/**
		 * @return the territoryList
		 */
		public List<String> getTerritoryList() {
			return territoryList;
		}

	}

	/**
	 * 
	 */
	private ProductBOA productBoa;
	/**
	 * 
	 */
	private SupplierBOA supplierBoa;

	private PriceBOA priceBoa;

	/**
	 * Default constructor
	 */
	public ImportManagerImpl() {
		this.productBoa = ProductBOA.getInstance();
		this.supplierBoa = SupplierBOA.getInstance();
		this.priceBoa = PriceBOA.getInstance();
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
			// Initialize product
			BOProduct boProduct = new BOProduct();
			Node article = articles.item(0);
			NodeList articleContent = article.getChildNodes();
			for (int j = 0; j < articleContent.getLength(); j++) {
				if (articleContent.item(j).getNodeName().equals("SUPPLIER_AID")) {
					String supplierAid = articleContent.item(j).getTextContent();
					boProduct.setOrderNumberSupplier(supplierAid);
				} else if (articleContent.item(j).getNodeName().equals("ARTICLE_DETAILS")) {
					processArticleDetails(boProduct, articleContent.item(j));
				} else if (articleContent.item(j).getNodeName().equals("ARTICLE_PRICE_DETAILS")) {
					processArticlePrices(boProduct, articleContent.item(j));
				}
			}
			productBoa.saveOrUpdate(boProduct);
		}
	}

	/**
	 * @param prod
	 * @param articlePrices
	 */
	private void processArticlePrices(BOProduct prod, Node articlePrices) {
		NodeList list = articlePrices.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node articlePrice = list.item(i);
			if (isNetPrice(articlePrice)) {
				processSalesPrice(prod, articlePrice);
			} else {
				processPurchasePrice(prod, articlePrice);
			}
		}
	}

	/**
	 * Processes the purchase price node and it's children
	 * 
	 * @param prod
	 * @param articlePrice
	 */
	private void processPurchasePrice(BOProduct prod, Node articlePrice) {
		BOPurchasePrice price = new BOPurchasePrice();
		PriceContainer container = processPrice(articlePrice);
		price.setAmount(container.getPriceAmount());
		price.setTaxrate(container.getTax());
		price.setPricetype("net_list");
		BOCountry country = new BOCountry();
		BOCurrency currency = new BOCurrency();
		currency.setCode(container.getPriceCurrency());
		country.setIsocode(container.getTerritoryList().get(0));
		country.setCurrency(currency);
		price.setCountry(country);
		price.setProduct(prod);
		priceBoa.saveOrUpdate(price);
	}

	/**
	 * Processes the sales price node and it's children
	 * 
	 * @param prod
	 * @param articlePrice
	 */
	private void processSalesPrice(BOProduct prod, Node articlePrice) {
		BOSalesPrice price = new BOSalesPrice();
		PriceContainer container = processPrice(articlePrice);
		price.setAmount(container.getPriceAmount());
		price.setTaxrate(container.getTax());
		price.setPricetype("gross_list");
		BOCountry country = new BOCountry();
		BOCurrency currency = new BOCurrency();
		currency.setCode(container.getPriceCurrency());
		country.setIsocode(container.getTerritoryList().get(0));
		country.setCurrency(currency);
		price.setCountry(country);
		priceBoa.saveOrUpdate(price);
	}

	private PriceContainer processPrice(Node articlePrice) {
		NodeList list = articlePrice.getChildNodes();
		PriceContainer container = new PriceContainer();
		List<String> territoryList = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals("PRICE_AMOUNT")) {
				container.setPriceAmount(new BigDecimal(list.item(i).getTextContent()));
			} else if (list.item(i).getNodeName().equals("PRICE_CURRENCY")) {
				container.setPriceCurrency(list.item(i).getTextContent());
			} else if (list.item(i).getNodeName().equals("TAX")) {
				container.setTax(new BigDecimal(list.item(i).getTextContent()));
			} else if (list.item(i).getNodeName().equals("TERRITORY")) {
				territoryList.add(list.item(i).getTextContent());
			}
		}
		container.setTerritoryList(territoryList);
		return container;
	}

	/**
	 * @param price
	 * @return
	 */
	private boolean isNetPrice(Node price) {
		NamedNodeMap priceMap = price.getAttributes();
		Node type = priceMap.getNamedItem("price_type");
		if (type.getNodeName().equalsIgnoreCase("net_list")) {
			return true;
		}
		return false;
	}

	/**
	 * @param prod
	 * @param articleDetails
	 */
	private void processArticleDetails(BOProduct prod, Node articleDetails) {
		NodeList list = articleDetails.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals("DESCRIPTION_SHORT")) {
				prod.setShortDescription(list.item(i).getTextContent());
			} else if (list.item(i).getNodeName().equals("DESCRIPTION_LONG")) {
				prod.setLongDescription(list.item(i).getTextContent());
			} else if (list.item(i).getNodeName().equals("EAN")) {
				// TODO: Handle EAN
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
