package de.htwg_konstanz.ebus.wholesaler.main;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCountry;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCurrency;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOPurchasePrice;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSalesPrice;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;

/**
 * @author schobast
 *
 *         Implements database export of products as XML document (BMECAT).
 *
 */
public class ExportManagerImpl implements IExportManager {

	private static final String CATALOG_LANGUAGE = "en";
	private static final String CATALOG_ID = "HTWG-EBUS-17";
	private static final String CATALOG_VERSION = "1.0";
	private static final String CATALOG_NAME = "HTWG-EBUS-CATALOG_EXPORT";

	/**
	 * Instance of ProductBOA to perform CRUD operations on products
	 */
	private ProductBOA productBoa;
	private DocumentBuilder builder;
	private Role role;

	/**
	 * Default constructor of InternalExportManager
	 *
	 */
	public ExportManagerImpl(Role role) {
		this.role = role;
		this.productBoa = ProductBOA.getInstance();
		try {
			this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.htwg_konstanz.ebus.wholesaler.main.IExportManager#retriveAllArticles(
	 * )
	 */
	@Override
	public Document retriveAllArticles() {
		Document doc = builder.newDocument();
		Element bmecat = createRootElement(doc);
		bmecat.appendChild(createCatalog(doc));
		doc.appendChild(bmecat);
		return doc;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.htwg_konstanz.ebus.wholesaler.main.IExportManager#
	 * retriveSelectiveArticles(java.lang.String)
	 */
	@Override
	public Document retriveSelectiveArticles(String selector) {
		Document doc = builder.newDocument();
		Element bmecat = createRootElement(doc);
		bmecat.appendChild(createSelectiveCatalog(doc, selector));
		doc.appendChild(bmecat);
		return doc;
	}

	/**
	 * Creates doc's root element and header
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @return the root element (BMECAT)
	 */
	private Element createRootElement(Document doc) {
		Element bmecat = doc.createElement("BMECAT");
		bmecat.setAttribute("version", "1.2");
		bmecat.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		bmecat.appendChild(createHeader(doc));
		return bmecat;
	}

	/**
	 * Creates instance of Element and fills it with all stored articles
	 *
	 * @return instance of Element containing all articles as child nodes
	 */
	private Element createCatalog(Document doc) {
		return createCatalogArticles(doc, productBoa.findAll());
	}

	/**
	 * Creates catalog articles element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param products:
	 *            the products list
	 * @return the catalog articles element
	 */
	private Element createCatalogArticles(Document doc, List<BOProduct> products) {
		Element catalog = doc.createElement("T_NEW_CATALOG");
		for (BOProduct boProduct : products) {
			Element article = doc.createElement("ARTICLE");
			Element supplierAID = doc.createElement("SUPPLIER_AID");
			supplierAID.setTextContent(boProduct.getSupplier().getSupplierNumber());
			article.appendChild(supplierAID);
			article.appendChild(createArticleDetails(doc, boProduct));
			article.appendChild(createArticlePriceDetails(doc, boProduct));
			catalog.appendChild(article);
		}
		return catalog;
	}

	/**
	 * Creates catalog element of a selection of items. According to substring
	 * matching of the item's short description.
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param selector:
	 *            search string for description mapping
	 * @return the catalog element
	 */
	private Element createSelectiveCatalog(Document doc, String selector) {
		return createCatalogArticles(doc, productBoa.findByShortdescriptionLike(selector));
	}

	/**
	 * Creates BMECAT document's header element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @return the header element
	 */
	private Element createHeader(Document doc) {
		Element header = doc.createElement("HEADER");
		Element catalog = doc.createElement("CATALOG");
		header.appendChild(catalog);
		Element language = doc.createElement("LANGUAGE");
		language.setTextContent(CATALOG_LANGUAGE);
		catalog.appendChild(language);
		Element id = doc.createElement("CATALOG_ID");
		id.setTextContent(CATALOG_ID);
		catalog.appendChild(id);
		Element version = doc.createElement("CATALOG_VERSION");
		version.setTextContent(CATALOG_VERSION);
		catalog.appendChild(version);
		Element name = doc.createElement("CATALOG_NAME");
		name.setTextContent(CATALOG_NAME);
		catalog.appendChild(name);
		catalog.appendChild(version);
		return header;
	}

	/**
	 * Creates article details element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            product list
	 * @return the article details element
	 */
	private Element createArticleDetails(Document doc, BOProduct boProduct) {
		Element articleDetails = doc.createElement("ARTICLE_DETAIS");
		Element shortDesc = doc.createElement("DESCRIPTION_SHORT");
		shortDesc.setTextContent(boProduct.getShortDescription());
		articleDetails.appendChild(shortDesc);
		Element longDesc = doc.createElement("DESCRIPTION_LONG");
		longDesc.setTextContent(boProduct.getLongDescription());
		articleDetails.appendChild(longDesc);
		Element ean = doc.createElement("EAN");
		// TODO: Check if proper value is selected
		ean.setTextContent(String.valueOf(boProduct.getMaterialNumber()));
		articleDetails.appendChild(ean);
		return articleDetails;
	}

	/**
	 * Creates article prices according to value of role member
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            the product list
	 * @return the article
	 */
	private Element createArticlePriceDetails(Document doc, BOProduct boProduct) {
		if (this.role == Role.INTERNAL_ROLE) {
			return createSalesPrices(doc, boProduct);
		} else {
			return createPurchasePrices(doc, boProduct);
		}
	}

	/**
	 * Creates sales prices element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            the product list
	 * @return the sales prices element
	 */
	private Element createSalesPrices(Document doc, BOProduct boProduct) {
		Element artilcePriceDetails = doc.createElement("ARTICLE_PRICE_DETAILS");
		List<BOSalesPrice> prices = boProduct.getSalesPrices();
		for (BOSalesPrice boSalesPrice : prices) {
			Element articlePrice = createSalesPrice(doc, boSalesPrice);
			articlePrice.appendChild(articlePrice);
		}
		return artilcePriceDetails;
	}

	/**
	 * Creates purchase prices element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            the product list
	 * @return the purchase price element
	 */
	private Element createPurchasePrices(Document doc, BOProduct boProduct) {
		Element articlePriceDetails = doc.createElement("ARTICLE_PRICE_DETAILS");
		List<BOPurchasePrice> prices = boProduct.getPurchasePrices();
		for (BOPurchasePrice boPurchasePrice : prices) {
			Element articlePrice = createPurchasePrice(doc, boPurchasePrice);
			articlePriceDetails.appendChild(articlePrice);
		}
		return articlePriceDetails;
	}

	/**
	 * Creates a single sales price
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boSalesPrice:
	 *            the sale price
	 * @return the single sales price element
	 */
	private Element createSalesPrice(Document doc, BOSalesPrice boSalesPrice) {
		Element articlePrice = doc.createElement("ARTICLE_PRICE");
		articlePrice.setAttribute("price_type", "gros_list");
		Element priceAmount = doc.createElement("PRICE_AMOUNT");
		priceAmount.setTextContent(String.valueOf(boSalesPrice.getAmount()));
		Element priceCurrency = doc.createElement("PRICE_CURRENCY");
		BOCountry country = boSalesPrice.getCountry();
		BOCurrency currency = country.getCurrency();
		priceCurrency.setTextContent(currency.getCode());
		Element tax = doc.createElement("TAX");
		tax.setTextContent(String.valueOf(boSalesPrice.getTaxrate()));
		return articlePrice;
	}

	/**
	 * Creates a single purchase price element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boSalesPrice:
	 *            the purchase price
	 * @return the single purchase price element
	 */
	private Element createPurchasePrice(Document doc, BOPurchasePrice boPurchasePrice) {
		Element articlePrice = doc.createElement("ARTICLE_PRICE");
		articlePrice.setAttribute("price_type", "net_list");
		Element priceAmount = doc.createElement("PRICE_AMOUNT");
		priceAmount.setTextContent(String.valueOf(boPurchasePrice.getAmount()));
		Element priceCurrency = doc.createElement("PRICE_CURRENCY");
		BOCountry country = boPurchasePrice.getCountry();
		BOCurrency currency = country.getCurrency();
		priceCurrency.setTextContent(currency.getCode());
		Element tax = doc.createElement("TAX");
		tax.setTextContent(String.valueOf(boPurchasePrice.getTaxrate()));
		return articlePrice;
	}
}
