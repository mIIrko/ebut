package de.htwg_konstanz.ebus.wholesaler.main;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.*;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.PriceBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.SupplierBOA;
import de.htwg_konstanz.ebus.wholesaler.demo.util.Constants;
import org.w3c.dom.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author schobast
 */
public class ImportManagerImpl implements IImportManager {

    /**
     * @author schobast
     */
    private class PriceContainer {
        private BigDecimal priceAmount;
        private String priceCurrency;
        private BigDecimal tax;
        private List<String> territoryList;

        public PriceContainer() {
        }

        /**
         * @param priceAmount the priceAmount to set
         */
        public void setPriceAmount(BigDecimal priceAmount) {
            this.priceAmount = priceAmount;
        }

        /**
         * @param priceCurrency the priceCurrency to set
         */
        public void setPriceCurrency(String priceCurrency) {
            this.priceCurrency = priceCurrency;
        }

        /**
         * @param tax the tax to set
         */
        public void setTax(BigDecimal tax) {
            this.tax = tax;
        }

        /**
         * @param territoryList the territoryList to set
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

    /**
     *
     */
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
    public void storeAllArticles(Document doc, List<String> infoList) throws RuntimeException {
        doc.getDocumentElement().normalize();
        BOSupplier boSupplier = null;

        // check if supplier exists
        NodeList supplierList = doc.getElementsByTagName("SUPPLIER");
        Node supplier = supplierList.item(0);
        if (supplier.getNodeType() == Node.ELEMENT_NODE) {
            Element supEl = (Element) supplier;
            String supName = supEl.getTextContent();
            boSupplier = isKnownSupplier(supName);
            if (boSupplier == null) {
                throw new RuntimeException(ImportError.UNKNOWN_SUPPLIER_ERROR.toString(supName));
            }
        }


        NodeList catalogList = doc.getElementsByTagName("T_NEW_CATALOG");
        Node catalog = catalogList.item(0);
        NodeList articles = catalog.getChildNodes();
        for (int i = 0; i < articles.getLength(); i++) {
            System.out.println("Checking node " + i);
            if (articles.item(i).getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Found article: " + articles.item(i).getNodeName() + "@" + i);
                // Initialize product
                BOProduct boProduct = new BOProduct();
                boProduct.setSupplier(boSupplier);

                Node article = articles.item(i);
                NodeList articleContent = article.getChildNodes();

                for (int u = 0; u < articleContent.getLength(); u++) {
                    if (articleContent.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(articleContent.item(u).getNodeName() + " | " + articleContent.item(u).getNodeType());
                    }
                }

                List<BOPurchasePrice> purchasePrices = new ArrayList<>();

                for (int j = 0; j < articleContent.getLength(); j++) {
                    if (articleContent.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        if (articleContent.item(j).getNodeName().equals("SUPPLIER_AID")) {
                            String supplierAid = articleContent.item(j).getTextContent();
                            if (isArticleStored(supplierAid)) {
                                throw new RuntimeException(ImportError.ARTICLE_EXISTS_ERROR.toString(supplierAid));
                            }
                            boProduct.setOrderNumberSupplier(supplierAid);
                            boProduct.setOrderNumberCustomer(supplierAid);
                        } else if (articleContent.item(j).getNodeName().equals("ARTICLE_DETAILS")) {
                            processArticleDetails(boProduct, articleContent.item(j));
                        } else if (articleContent.item(j).getNodeName().equals("ARTICLE_PRICE_DETAILS")) {
                            //processArticlePrices(boProduct, articleContent.item(j));
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

                productBoa.saveOrUpdate(boProduct);
                infoList.add("imported article " + boProduct.getShortDescription() + " (" + boProduct.getOrderNumberSupplier() + ")");
                System.out.println("Saved product: " + boProduct.getShortDescription());

                // create and add the sales prices to the list
                List<BOSalesPrice> salesPrices = new ArrayList<>();

                for (BOPurchasePrice purchasePrice : purchasePrices) {
                    priceBoa.saveOrUpdate(purchasePrice);
                    System.out.println("Saved purchase price: " + purchasePrice.getAmount() + " (" + purchasePrice.getPricetype() + ")");
                    salesPrices.add(processSalesPrice(purchasePrice));
                }

                for (BOSalesPrice salesPrice : salesPrices) {
                    priceBoa.saveOrUpdate(salesPrice);
                    System.out.println("Saved sales price: " + salesPrice.getAmount() + " (" + salesPrice.getPricetype() + ")");
                }

            } else {
                System.out.println("Dismissed node " + articles.item(i).getNodeName() + " - " + articles.item(i).getNodeType() + "@" + i);
            }
        }
    }

    public BOPurchasePrice processPurchasePrice(BOProduct prod, Node articlePrice) {
        BOPurchasePrice price = new BOPurchasePrice();
        PriceContainer container = processPrice(articlePrice);

        price.setAmount(container.getPriceAmount());
        price.setTaxrate(container.getTax());

        if (isNetPrice(articlePrice)) {
            price.setPricetype("net_list");
        } else {
            price.setPricetype("gros_list");
        }

        // todo: check if price per unit is set, else set default value (=1)
        // element <ARTICLE_ORDER_DETAILS><PRICE_QUANTITY> ...


        // todo: set correct country
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
     * creates a sales price with the same values as the given purchase price
     *
     * @param purchasePrice
     * @return
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
     * Processes the purchase price node and it's children
     *
     * @param prod
     * @param articlePrice
     */
    private BOPurchasePrice processNetPrice(BOProduct prod, Node articlePrice) {
        BOPurchasePrice price = new BOPurchasePrice();
        PriceContainer container = processPrice(articlePrice);

        price.setAmount(container.getPriceAmount());
        price.setTaxrate(container.getTax());

        price.setPricetype("net_list");

        // todo: set correct country
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
     * @param price
     * @return
     */
    private boolean isNetPrice(Node price) {
        NamedNodeMap priceMap = price.getAttributes();
        Node type = priceMap.getNamedItem("price_type");
        System.out.println("Name of Node = >" + type.getNodeName() + "<");
        System.out.println("Type of Node = >" + type.getNodeType() + "<");
        System.out.println("Value of Node = >" + type.getNodeValue() + "<");
        if (type.getNodeValue().equals("net_list")) {
            return true;
        }
        return false;
    }

    /**
     * @param prod
     * @param articleDetails
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
     * @param companyName: supplier identifier
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
     * @param aid: product identifier
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
     * Processes the sales price node and it's children
     *
     * @param prod
     * @param articlePrice
     */
    /*
    private BOPurchasePrice processGrosPrice(BOProduct prod, Node articlePrice) {

        BOPurchasePrice price = new BOPurchasePrice();
        PriceContainer container = processPrice(articlePrice);
        price.setAmount(container.getPriceAmount());
        price.setTaxrate(container.getTax());
        price.setPricetype("gros_list");
        price.setProduct(prod);
        price.setLowerboundScaledprice(1);

        BOCountry country = new BOCountry();
        price.setCountry(country);

        BOCurrency currency = new BOCurrency();
        currency.setCode(container.getPriceCurrency());
        country.setIsocode(container.getTerritoryList().get(0));
        country.setCurrency(currency);
        return price;
    }
*/

    /**
     * @param prod
     * @param articlePrices
     */
    /*
    private void processArticlePrices(BOProduct prod, Node articlePrices) {
        NodeList list = articlePrices.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Node articlePrice = list.item(i);
                if (isNetPrice(articlePrice)) {
                    processSalesPrice(prod, articlePrice);
                } else {
                    processPurchasePrice(prod, articlePrice);
                }
            }
        }
    }
    */
}
