package de.htwg_konstanz.ebus.wholesaler.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.xml.xsom.impl.Const;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.*;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.vo.voa.ArticlenumberVOA;
import de.htwg_konstanz.ebus.framework.wholesaler.vo.voa.ArticlenumbertypeVOA;

/**
 * @author schobast
 *
 *         Implements database export of products as XML document (BMECAT).
 *
 */
public class ExportManagerImpl implements IExportManager {

	/**
	 * Instance of ProductBOA to perform CRUD operations on products
	 */
	private ProductBOA productBoa;

	/**
	 * DocumentBuilder instance
	 */
	private DocumentBuilder builder;

	/**
	 * Role of the export operation
	 */
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
	public Document retriveSelectiveArticles(String selector) throws RuntimeException {
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

			if (role == Role.CUSTOMER_ROLE) {
                supplierAID.setTextContent(boProduct.getOrderNumberCustomer());

            } else {
                supplierAID.setTextContent(boProduct.getOrderNumberSupplier());

            }


			article.appendChild(supplierAID);
			article.appendChild(createArticleDetails(doc, boProduct));
			article.appendChild(createArticleOrderDetails(doc, boProduct));
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
	private Element createSelectiveCatalog(Document doc, String selector) throws RuntimeException {

		List<BOProduct> selection = new ArrayList<>();
		List<BOProduct> productList = productBoa.findAll();
		for (int i = 0; i < productList.size(); i++) {
			if (productList.get(i).getShortDescription().toLowerCase().contains(selector.toLowerCase())) {
				selection.add(productList.get(i));
			}
		}

		if (selection.size() == 0) {
			throw new RuntimeException(ExportError.NO_ARTICLE_FOUND.toString("searched substring " + selector));
		}

		for (int i = 0; i < selection.size(); i++) {
			System.out.println(selection.get(i).getShortDescription());
		}

		return createCatalogArticles(doc, selection);
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
		Element language = doc.createElement("LANGUAGE");
		language.setTextContent(Constants.CATALOG_LANGUAGE);
		catalog.appendChild(language);

		Element id = doc.createElement("CATALOG_ID");
		id.setTextContent(Constants.CATALOG_ID);
		catalog.appendChild(id);

		Element version = doc.createElement("CATALOG_VERSION");
		version.setTextContent(Constants.CATALOG_VERSION);
		catalog.appendChild(version);

		Element name = doc.createElement("CATALOG_NAME");
		name.setTextContent(Constants.CATALOG_NAME);
		catalog.appendChild(name);

		Element supplier = doc.createElement("SUPPLIER");
		Element supplierName = doc.createElement("SUPPLIER_NAME");
		supplierName.setTextContent(Constants.COMPANY_NAME);
		supplier.appendChild(supplierName);

		header.appendChild(catalog);
		header.appendChild(supplier);
		return header;
	}

	/**
	 * Creates article details element with all necessary child elements
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            product list
	 * @return the article details element
	 */
	private Element createArticleDetails(Document doc, BOProduct boProduct) {
		Element articleDetails = doc.createElement("ARTICLE_DETAILS");
		Element shortDesc = doc.createElement("DESCRIPTION_SHORT");
		shortDesc.setTextContent(boProduct.getShortDescription());
		articleDetails.appendChild(shortDesc);
		Element longDesc = doc.createElement("DESCRIPTION_LONG");
		longDesc.setTextContent(boProduct.getLongDescription());
		articleDetails.appendChild(longDesc);

		// Element ean = doc.createElement("EAN");
		// TODO: Check if proper value is selected
		// ean.setTextContent(String.valueOf(boProduct.getMaterialNumber()));
		// articleDetails.appendChild(ean);
		return articleDetails;
	}

	/**
	 * Creates element ARTICLE_ORDER_DETAILS with all mandatory requirements to
	 * be valid against BMECAT specification
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            product list
	 * @return the ARTICLE_ORDER_DETAILS element
	 */
	private Element createArticleOrderDetails(Document doc, BOProduct boProduct) {

		Element articleOrderDetails = doc.createElement("ARTICLE_ORDER_DETAILS");
		Element orderUnit = doc.createElement("ORDER_UNIT");
		orderUnit.setTextContent("PK");
		articleOrderDetails.appendChild(orderUnit);
		// <CONTENT_UNIT>C62</CONTENT_UNIT>
		// <NO_CU_PER_OU>10</NO_CU_PER_OU>
		return articleOrderDetails;
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
			return createPurchasePrices(doc, boProduct);
		} else {
			// the user must have role "customer"
			return createSalesPrices(doc, boProduct);
		}
	}

	/**
	 * Creates ARTICLE_PRICE_DETAILS element (of sales prices) with all
	 * mandatory requirements to be valid against BMECAT specification
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boProduct:
	 *            the product list
	 * @return the sales prices element
	 */
	private Element createSalesPrices(Document doc, BOProduct boProduct) {
		Element articlePriceDetails = doc.createElement("ARTICLE_PRICE_DETAILS");
		List<BOSalesPrice> prices = boProduct.getSalesPrices();
		for (BOSalesPrice boSalesPrice : prices) {
			Element articlePrice = createSalesPrice(doc, boSalesPrice);
			articlePriceDetails.appendChild(articlePrice);
		}
		return articlePriceDetails;
	}

	/**
	 * Creates ARTICLE_PRICE_DETAILS element (of purchase prices) with all
	 * mandatory requirements to be valid against BMECAT specification
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

			Element articlePriceCustomer = createSalesPrice(doc, boPurchasePrice);
			articlePriceDetails.appendChild(articlePriceCustomer);
		}
		return articlePriceDetails;
	}

	/**
	 * Creates a single sales price element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @param boSalesPrice:
	 *            the sale price
	 * @return the single sales price element
	 */
	private Element createSalesPrice(Document doc, IBOPrice boSalesPrice) {
		Element articlePrice = doc.createElement("ARTICLE_PRICE");
		articlePrice.setAttribute("price_type", "net_customer");
		//Multiply by MARGE constant's value
		BigDecimal amountNetSale = boSalesPrice.getAmount();
		BigDecimal marge = amountNetSale.multiply(Constants.MARGE).setScale(2, RoundingMode.HALF_UP);
		Element priceAmount = doc.createElement("PRICE_AMOUNT");
		priceAmount.setTextContent(amountNetSale.add(marge).toString());
		System.out.println("ARTICLE PRICE = " + amountNetSale.add(marge).toString());
		articlePrice.appendChild(priceAmount);

		Element priceCurrency = doc.createElement("PRICE_CURRENCY");
		BOCountry country = boSalesPrice.getCountry();
		BOCurrency currency = country.getCurrency();
		priceCurrency.setTextContent(currency.getCode());
		articlePrice.appendChild(priceCurrency);

		Element tax = doc.createElement("TAX");
		tax.setTextContent(String.valueOf(boSalesPrice.getTaxrate()));
		articlePrice.appendChild(tax);

		Element territory = doc.createElement("TERRITORY");
		territory.setTextContent(boSalesPrice.getCountry().getIsocode());
		articlePrice.appendChild(territory);

		return articlePrice;
	}

	/**
	 * Creates a single purchase price element
	 *
	 * @param doc:
	 *            the BMECAT document
	 * @return the single purchase price element
	 */
	private Element createPurchasePrice(Document doc, IBOPrice boPurchasePrice) {
		Element articlePrice = doc.createElement("ARTICLE_PRICE");
		articlePrice.setAttribute("price_type", boPurchasePrice.getPricetype());

		Element priceAmount = doc.createElement("PRICE_AMOUNT");
		priceAmount.setTextContent(String.valueOf(boPurchasePrice.getAmount()));
		articlePrice.appendChild(priceAmount);

		Element priceCurrency = doc.createElement("PRICE_CURRENCY");
		BOCountry country = boPurchasePrice.getCountry();
		BOCurrency currency = country.getCurrency();
		priceCurrency.setTextContent(currency.getCode());
		articlePrice.appendChild(priceCurrency);

		Element tax = doc.createElement("TAX");
		tax.setTextContent(String.valueOf(boPurchasePrice.getTaxrate()));
		articlePrice.appendChild(tax);

		Element territory = doc.createElement("TERRITORY");
		territory.setTextContent(boPurchasePrice.getCountry().getIsocode());
		articlePrice.appendChild(territory);
		return articlePrice;
	}

}
