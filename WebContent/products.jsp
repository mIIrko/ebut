<%@ page session="true"
         import="de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOInventory,de.htwg_konstanz.ebus.framework.wholesaler.api.boa.InventoryBOA,java.math.BigDecimal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
    <title>eBusiness Framework Demo - Product List</title>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="default.css">
</head>

<body>

<%@ include file="header.jsp" %>
<%@ include file="error.jsp" %>
<%@ include file="authentication.jsp" %>
<%@ include file="navigation.jspfragment" %>

<h1>Product Catalog</h1>

<table class="dataTable">
    <thead>
    <tr>
        <th><b>Order No.</b></th>
        <th><b>Title</b></th>
        <th><b>Description</b></th>
        <th><b>Price net</b></th>
        <th><b>Price gross</b></th>
        <th><b>Inventory</b></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="product" items="${sessionScope.productList}">
        <jsp:useBean id="product" type="de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct"/>
        <tr valign="top">
            <td>
                <a href="<%= response.encodeURL("controllerservlet?action=showProductDetail&orderNumberCustomer="+product.getOrderNumberCustomer()) %>"><%= product.getOrderNumberCustomer() %>
                </a></td>
            <td><%= product.getShortDescription() %>
            </td>
            <td width="400px"><%= product.getLongDescription() %>
            </td>

            <%if (product.getDefaultSalesPrice() != null) {%>
            <% BigDecimal netPrice = product.getDefaultSalesPrice().getAmount(); %>
            <% BigDecimal grossPrice = product.getDefaultSalesPrice().getAmountGross(); %>
            <td align="right"><fmt:formatNumber value="<%= netPrice %>" type="currency"
                                                currencySymbol="<%= product.getDefaultSalesPrice().getCountry().getCurrency().getCode() %>"/></td>
            <td align="right"><fmt:formatNumber value="<%= grossPrice %>" type="currency"
                                                currencySymbol="<%= product.getDefaultSalesPrice().getCountry().getCurrency().getCode() %>"/></td>
            <%} else { %>
            <td align="right">N/A</td>
            <td align="right">N/A</td>
            <%} %>

            <% BOInventory inventory = InventoryBOA.getInstance().findByProduct(product); %>
            <td align="right"><% if (inventory != null) { %>
                <%= inventory.getInventoryNumber() %>
                <% } else { %>
                N/A
                <% } %>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
    <input id="backButton" type=button name=go-back value=" Back " onclick="javascript:history.back()">
</body>
</html>
