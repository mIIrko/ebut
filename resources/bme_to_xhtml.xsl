<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

    <xsl:template match="/">

        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
            <head>
                <title>Product Catalog</title>
                <style type="text/css">
                    body {
                        font-family: monospace;
                        font-size: 1.1em;
                        font-weight: normal;
                        color: #000000;
                        margin: 0
                    }
                    table {
                        border-collapse: collapse;
                    }
                    table, th, td {
                        border: 1px solid black;
                    }
                    th, td {
                        padding: 0.8em;
                        text-align: left;
                        vertical-align: top;
                    }
                    sup {
                        font-size: 0.5em;
                        padding-left: 0.5em;
                    }
                    #header, #main-table {
                        margin-left: 1em;
                    }
                    .pricetable, .pricetable th, .pricetable td {
                     border: none;
                     padding: 0em;
                    }
                    #artnum {
                        width: 10em;
                    }
                    #shortdesc {
                        width: 13em;
                    }
                    #longdesc {
                        width: 25em;
                    }
                    .price {
                        width: 11em;
                    }
                    .tax {
                        width: 3em;
                    }
                    #supplier-name {
                        background-color: #009b91;
                        color: white;
                        padding: 2em;
                        margin-top: 0;
                        width: 100%;
                        margin-left: 0;
                    }

                </style>
            </head>

            <body>
                <xsl:apply-templates select="BMECAT/HEADER"/>

                <div id="main-table">
                    <table>
                        <thead>
                            <tr>
                                <td id="artnum">
                                    Art-Num
                                </td>
                                <td id="shortdesc">
                                    Short description
                                </td>
                                <td id="longdesc">
                                    Long description
                                </td>
                                <td id="price">
                                    Price net
                                </td>

                            </tr>
                        </thead>

                        <tbody>

                            <xsl:for-each select="BMECAT/T_NEW_CATALOG/ARTICLE">
                                <xsl:apply-templates select="."/>
                            </xsl:for-each>

                        </tbody>
                    </table>
                </div>

            </body>
        </html>

    </xsl:template>

    <xsl:template match="HEADER">
            <p id="supplier-name">
                <xsl:value-of select="SUPPLIER/SUPPLIER_NAME"/>
            </p>
        <div id="header">

        <h1>
                <xsl:value-of select="CATALOG/CATALOG_ID"/>
                <sup>
                    <xsl:value-of select="CATALOG/CATALOG_VERSION"/>
                </sup>
            </h1>
            <p>
                <xsl:value-of select="CATALOG/CATALOG_NAME"/>
            </p>
        </div>
    </xsl:template>


    <xsl:template match="ARTICLE">
        <tr>
            <td>
                <xsl:value-of select="SUPPLIER_AID"/>
            </td>
            <td>
                <xsl:value-of select="ARTICLE_DETAILS/DESCRIPTION_SHORT"/>
            </td>
            <td>
                <xsl:value-of select="ARTICLE_DETAILS/DESCRIPTION_LONG"/>
            </td>

            <td>
                <table class="pricetable">
                    <tbody>
                        <xsl:for-each select="ARTICLE_PRICE_DETAILS/ARTICLE_PRICE">
                            <tr>
                                <td class="price">
                                    <xsl:value-of select="PRICE_AMOUNT"/>&#032;
                                    <xsl:value-of select="PRICE_CURRENCY"/>&#032;
                                    <xsl:choose>
                                        <xsl:when test="@price_type='net_list'">
                                            (list)
                                        </xsl:when>
                                        <xsl:when test="@price_type='net_customer'">
                                            (sale)
                                        </xsl:when>
                                    </xsl:choose>
                                </td>

                                <td class="tax">
                                    <xsl:value-of select="TAX * 100"/>%
                                </td>
                                <td class="territory">
                                    <xsl:value-of select="TERRITORY"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
            </td>

        </tr>
    </xsl:template>

</xsl:stylesheet>
