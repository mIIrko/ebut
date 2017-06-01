<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

    <xsl:template match="/">

        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>Product Catalog</title>
            </head>

            <body>
                <xsl:apply-templates select="BMECAT/HEADER"/>

                <div>
                    <table>
                        <thead>
                            <tr>
                                <td>
                                    Description short
                                </td>

                                <td>
                                    Description long
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

        <div>
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

        <xsl:apply-templates select="ARTICLE_DETAILS/DESCRIPTION_SHORT"/>
        <xsl:apply-templates select="ARTICLE_DETAILS/DESCRIPTION_LONG"/>

    </xsl:template>

    <xsl:template match="DESCRIPTION_SHORT">
        <tr>
            <xsl:value-of select="DESCRIPTION_SHORT"/>
        </tr>
    </xsl:template>

    <xsl:template match="DESCRIPTION_LONG">
        <!-- long description ist optional -->
        <tr>
            <xsl:choose>
                <xsl:when test=".">
                    <xsl:value-of select="."/>
                </xsl:when>
            </xsl:choose>
        </tr>
    </xsl:template>


    <xsl:template match="SUPPLIER_AID">
        <artnum>
            <xsl:value-of select="SUPPLIER_AID"/>
        </artnum>
    </xsl:template>


    <xsl:template match="MANUFACTURER_NAME">
        <!-- manufacturer ist optional -->
        <xsl:choose>
            <xsl:when test="MANUFACTURER_NAME">
                <manufacturer>
                    <xsl:value-of select="MANUFACTURER_NAME"/>
                </manufacturer>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="ARTICLE_DETAILS">
        <!-- alle zusätzlichen Artikelnummern sind optional -->
        <xsl:choose>
            <xsl:when test="EAN">
                <additionalartnum type="EAN">
                    <xsl:value-of select="EAN"/>
                </additionalartnum>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="SUPPLIER_ALT_AID">
                <additionalartnum type="SUPPLIER_ALT_AID">
                    <xsl:value-of select="SUPPLIER_ALT_AID"/>
                </additionalartnum>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="BUYER_AID">
                <additionalartnum type="BUYER_AID">
                    <xsl:value-of select="BUYER_AID"/>
                </additionalartnum>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="MANUFACTURER_AID">
                <additionalartnum type="MANUFACTURER_AID">
                    <xsl:value-of select="MANUFACTURER_AID"/>
                </additionalartnum>
            </xsl:when>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="ARTICLE_PRICE">
        <pricenet>
            <xsl:attribute name="currency">
                <xsl:value-of select="PRICE_CURRENCY"/>
            </xsl:attribute>
            <xsl:attribute name="country">
                <xsl:value-of select="TERRITORY"/>
            </xsl:attribute>
            <xsl:attribute name="tax">
                <xsl:value-of select="TAX"/>
            </xsl:attribute>
            <sum>
                <xsl:value-of select="PRICE_AMOUNT"/>
            </sum>
        </pricenet>
    </xsl:template>

    <xsl:template match="ART_ID_TO">
        <refart>
            <xsl:value-of select="."/>
        </refart>
    </xsl:template>

</xsl:stylesheet>
