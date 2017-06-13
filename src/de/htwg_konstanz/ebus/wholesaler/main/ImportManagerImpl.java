package de.htwg_konstanz.ebus.wholesaler.main;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.*;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.PriceBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.SupplierBOA;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import org.w3c.dom.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author schobast
 * 
 *         Implements all database import functionalities
 */
public class ImportManagerImpl implements IImportManager {

	/**
	 * @author schobast
	 * 
	 *         Price container which represents a single price (did not know of
	 *         the strange price interface...expected a superclass)
	 */
	private class PriceContainer {
		/**
		 * The price amount
		 */
		private BigDecimal priceAmount;
		/**
		 * The price currency
		 */
		private String priceCurrency;
		/**
		 * The tax for the price
		 */
		private BigDecimal tax;
		/**
		 * List of territories
		 */
		private List<String> territoryList;

		/**
		 * Empty default constructor
		 */
		public PriceContainer() {
		}

		/**
		 * @param priceAmount:
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
	 * Instance to perform CRUD operations on product related database tables
	 */
	private ProductBOA productBoa;
	/**
	 * Instance to perform CRUD operations on supplier related database tables
	 */
	private SupplierBOA supplierBoa;

	/**
	 * Instance to perform CRUD operations on price related database tables
	 */
	private PriceBOA priceBoa;

	/**
	 * Default constructor to instantiate instance members
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
	public void storeAllArticles(Document doc, List<String> infoList) throws RuntimeException {
		doc.getDocumentElement().normalize();
		BOSupplier boSupplier = null;

		NodeList supplierList = doc.getElementsByTagName("SUPPLIER");
		Node supplier = supplierList.item(0);
		// Check if supplier is stored
		if (supplier.getNodeType() == Node.ELEMENT_NODE) {
			Element supEl = (Element) supplier;
			String supName = supEl.getTextContent();
			boSupplier = isKnownSupplier(supName);
			if (boSupplier == null) {
				// Throw exception in case of an unknown supplier
				throw new RuntimeException(ImportError.UNKNOWN_SUPPLIER_ERROR.toString(supName));
			}
		}

		NodeList catalogList = doc.getElementsByTagName("T_NEW_CATALOG");
		Node catalog = catalogList.item(0);
		NodeList articles = catalog.getChildNodes();
		for (int i = 0; i < articles.getLength(); i++) {
			if (articles.item(i).getNodeType() == Node.ELEMENT_NODE) {
				System.out.println("Found article: " + articles.item(i).getNodeName() + "@" + i);
				// Initialize product
				BOProduct boProduct = new BOProduct();
				boProduct.setSupplier(boSupplier);

				Node article = articles.item(i);
				NodeList articleContent = article.getChildNodes();

				// Prints debug information
				for (int u = 0; u < articleContent.getLength(); u++) {
					if (articleContent.item(i).getNodeType() == Node.ELEMENT_NODE) {
						System.out.println(
								articleContent.item(u).getNodeName() + " | " + articleContent.item(u).getNodeType());
					}
				}
				// List to store purchase prices
				List<BOPurchasePrice> purchasePrices = new ArrayList<>();
				for (int j = 0; j < articleContent.getLength(); j++) {
					if (articleContent.item(j).getNodeType() == Node.ELEMENT_NODE) {
						// Perform operations on ARTICLE elements
						if (articleContent.item(j).getNodeName().equals("SUPPLIER_AID")) {
							String supplierAid = articleContent.item(j).getTextContent();
							// Verify that article is not already stored
							if (isArticleStored(supplierAid)) {
								// Throw exception in case that article is
								// already stored
								throw new RuntimeException(ImportError.ARTICLE_EXISTS_ERROR.toString(supplierAid));
							}
							boProduct.setOrderNumberSupplier(supplierAid);
							boProduct.setOrderNumberCustomer(supplierAid);
						} else if (articleContent.item(j).getNodeName().equals("ARTICLE_DETAILS")) {
							// Perform ARTICLE_DETAILS related operations
							processArticleDetails(boProduct, articleContent.item(j));
						} else if (articleContent.item(j).getNodeName().equals("ARTICLE_PRICE_DETAILS")) {
							// Perform ARTICLE_PRICE_DETAILS related operations
							NodeList list = articleContent.item(j).getChildNodes();
							for (int k = 0; k < list.getLength(); k++) {
								System.out.println("Checking node for price " + list.item(k).getNodeType());
								if (list.item(k).getNodeType() == Node.ELEMENT_NODE) {
									Node articlePrice = list.item(k);
									purchasePrices.add(processPurchasePrice(boProduct, articlePrice));
								}
							}
						}

					} // end if

				} // end for

				// Import products
				productBoa.saveOrUpdate(boProduct);
				infoList.add("Imported article: " + boProduct.getShortDescription() + " ("
						+ boProduct.getOrderNumberSupplier() + ")");
				System.out.println("Saved product: " + boProduct.getShortDescription());
				// create and add the sales prices to the list
				List<BOSalesPrice> salesPrices = new ArrayList<>();
				// Import purchase prices
				for (BOPurchasePrice purchasePrice : purchasePrices) {
					priceBoa.saveOrUpdate(purchasePrice);
					System.out.println("Saved purchase price: " + purchasePrice.getAmount() + " ("
							+ purchasePrice.getPricetype() + ")");
					salesPrices.add(processSalesPrice(purchasePrice));
				}
				// Import sales prices
				for (BOSalesPrice salesPrice : salesPrices) {
					priceBoa.saveOrUpdate(salesPrice);
					System.out.println(
							"Saved sales price: " + salesPrice.getAmount() + " (" + salesPrice.getPricetype() + ")");
				}
			} else {
				// Node is not an element node
				System.out.println("Dismissed node " + articles.item(i).getNodeName() + " - "
						+ articles.item(i).getNodeType() + "@" + i);
			}
		}
	}

	/**
	 * Processes a purchase price element to retrieve it as an instance of
	 * BOPurchasePrice
	 * 
	 * @param prod:
	 *            the involved BOProduct instance
	 * @param articlePrice:
	 *            the involved price node
	 * @return a BOPurchasePrice instance
	 */
	public BOPurchasePrice processPurchasePrice(BOProduct prod, Node articlePrice) {
		BOPurchasePrice price = new BOPurchasePrice();
		PriceContainer container = processPrice(articlePrice);

		price.setAmount(container.getPriceAmount());
		price.setTaxrate(container.getTax());
		price.setPricetype("net_list");

		if (isNetPrice(articlePrice)) {
			price.setAmount(container.getPriceAmount());
		} else {
			// gross price, we must calculate the net price
			BigDecimal priceAmount = price.getAmount();
			BigDecimal divider = container.getTax().add(new BigDecimal(1));
			BigDecimal priceAmountNet = priceAmount.divide(divider, RoundingMode.HALF_UP);
			price.setAmount(priceAmountNet);
		}

		// todo: check if price per unit is set, else set default value (=1) ->
		// no setter available
		// element <ARTICLE_ORDER_DETAILS><PRICE_QUANTITY> ...

		BOCountry country = new BOCountry();
		price.setCountry(country);

		BOCurrency currency = new BOCurrency();
		currency.setCode(container.getPriceCurrency());
		country.setIsocode(container.getTerritoryList().get(0));
		country.setCurrency(currency);

		price.setProduct(prod);
		price.setLowerboundScaledprice(1);
		return price;
	}

	/**
	 * Processes a purchase price element to retrieve it as an instance of
	 * BOSalesPrice
	 * 
	 * @param purchasePrice:
	 *            the involved purchase price
	 * @return the instance of BOSalesPrice
	 */
	public BOSalesPrice processSalesPrice(BOPurchasePrice purchasePrice) {
		BOSalesPrice salesPrice = new BOSalesPrice();
		salesPrice.setAmount(purchasePrice.getAmount());
		salesPrice.setCountry(purchasePrice.getCountry());
		salesPrice.setLowerboundScaledprice(purchasePrice.getLowerboundScaledprice());
		salesPrice.setPricetype(purchasePrice.getPricetype());
		salesPrice.setProduct(purchasePrice.getProduct());
		salesPrice.setTaxrate(purchasePrice.getTaxrate());
		return salesPrice;
	}

	/**
	 * Processes an article price and returns a PriceContainer instance (because
	 * I didn't know about the strange interface)
	 * 
	 * @param articlePrice: the price node
	 * @return PriceContainer instance containing the prices's information
	 */
	private PriceContainer processPrice(Node articlePrice) {
		NodeList list = articlePrice.getChildNodes();
		PriceContainer container = new PriceContainer();
		List<String> territoryList = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
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
		}
		container.setTerritoryList(territoryList);
		return container;
	}

	/**
	 * Checks if price is a net price
	 * 
	 * @param price: the price node 
	 * @return true in case that the given price is a net price
	 */
	private boolean isNetPrice(Node price) {
		NamedNodeMap priceMap = price.getAttributes();
		Node type = priceMap.getNamedItem("price_type");
		if (type.getNodeValue().equals("net_list")) {
			return true;
		}
		return false;
	}

	/**
	 * Process a node containing the acrticle's details
	 * 
	 * @param prod: the involved product
	 * @param articleDetails: the article details node
	 */
	private void processArticleDetails(BOProduct prod, Node articleDetails) {
		System.out.println("artDetails = " + articleDetails.getNodeName() + " (" + articleDetails.getNodeType() + ")");
		NodeList list = articleDetails.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {

			System.out.println(list.item(i).getNodeName() + " (" + list.item(i).getNodeType() + ")");

			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (list.item(i).getNodeName().equals("DESCRIPTION_SHORT")) {
					prod.setShortDescription(list.item(i).getTextContent());
					prod.setShortDescriptionCustomer(list.item(i).getTextContent());
				} else if (list.item(i).getNodeName().equals("DESCRIPTION_LONG")) {
					prod.setLongDescription(list.item(i).getTextContent());
					prod.setLongDescriptionCustomer(list.item(i).getTextContent());
				} else if (list.item(i).getNodeName().equals("EAN")) {
					// TODO: handle EAN
				}
			}
		}
	}

	/**
	 * Checks if supplier is known
	 *
	 * @param companyName:
	 *            supplier identifier
	 * @return true if supplier is known
	 */
	private BOSupplier isKnownSupplier(String companyName) {
		// todo: improve: findbycompanyname
		List<BOSupplier> supAll = supplierBoa.findAll();

		for (BOSupplier supplier : supAll) {
			if (companyName.trim().equals(supplier.getCompanyname().trim())) {
				return supplier;
			}
		}
		return null;
	}

	/**
	 * Checks if article is stored
	 *
	 * @param aid:
	 *            product identifier
	 * @return true if article exists
	 */
	private boolean isArticleStored(String aid) {
		BOProduct prod = productBoa.findByOrderNumberSupplier(aid);
		if (prod == null) {
			return false;
		}
		return true;
	}

	/**
	 * Processes the purchase price node and it's children
	 *
	 * @param prod
	 * @param articlePrice
	 */
	/*
	 * private BOPurchasePrice processNetPrice(BOProduct prod, Node
	 * articlePrice) { BOPurchasePrice price = new BOPurchasePrice();
	 * PriceContainer container = processPrice(articlePrice);
	 * 
	 * price.setAmount(container.getPriceAmount());
	 * price.setTaxrate(container.getTax());
	 * 
	 * price.setPricetype("net_list");
	 * 
	 * // todo: set correct country BOCountry country = new BOCountry();
	 * price.setCountry(country);
	 * 
	 * BOCurrency currency = new BOCurrency();
	 * currency.setCode(container.getPriceCurrency());
	 * country.setIsocode(container.getTerritoryList().get(0));
	 * country.setCurrency(currency);
	 * 
	 * price.setProduct(prod); price.setLowerboundScaledprice(1); return price;
	 * }
	 */

	/**
	 * Processes the sales price node and it's children
	 *
	 * @param prod
	 * @param articlePrice
	 */
	/*
	 * private BOPurchasePrice processGrosPrice(BOProduct prod, Node
	 * articlePrice) {
	 * 
	 * BOPurchasePrice price = new BOPurchasePrice(); PriceContainer container =
	 * processPrice(articlePrice); price.setAmount(container.getPriceAmount());
	 * price.setTaxrate(container.getTax()); price.setPricetype("gros_list");
	 * price.setProduct(prod); price.setLowerboundScaledprice(1);
	 * 
	 * BOCountry country = new BOCountry(); price.setCountry(country);
	 * 
	 * BOCurrency currency = new BOCurrency();
	 * currency.setCode(container.getPriceCurrency());
	 * country.setIsocode(container.getTerritoryList().get(0));
	 * country.setCurrency(currency); return price; }
	 */

	/**
	 * @param prod
	 * @param articlePrices
	 */
	/*
	 * private void processArticlePrices(BOProduct prod, Node articlePrices) {
	 * NodeList list = articlePrices.getChildNodes(); for (int i = 0; i <
	 * list.getLength(); i++) { if (list.item(i).getNodeType() ==
	 * Node.ELEMENT_NODE) { Node articlePrice = list.item(i); if
	 * (isNetPrice(articlePrice)) { processSalesPrice(prod, articlePrice); }
	 * else { processPurchasePrice(prod, articlePrice); } } } }
	 */
}
