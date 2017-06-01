<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>eBusiness Framework Demo - Export</title>
    <meta charset="UTF-8">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="default.css">
</head>

<body>

<%@ include file="header.jsp" %>
<%@ include file="authentication.jsp" %>
<%@ include file="navigation.jspfragment" %>

<%@ include file="error.jsp" %>
<%@ include file="info.jsp" %>

<h1>Export</h1>


<h3>Product Catalog as XML</h3>

<form action="controllerservlet?action=download" method="post">
    <input type="hidden" name="searchterm" value=""/>
    <input type="hidden" name="role" value="<%= loginBean.getRole() %>"/>
    <input type="hidden" name="type" value="xml">

    <input type="submit" value="export all"/>
</form>

<br>
<br>

<h5>export selective by short description</h5>
<form name="exportSelective" action="controllerservlet?action=download" method="post">
    <label for="searchterm">search term</label>
    <input type="text" name="searchterm" id="searchterm" pattern="[a-zA-Z0-9-\.]{1,}" title="Erlaubte Zeichen sind: a-z A-Z 0-9 -" required="required"/>

    <label for="matchExact">match term exact?</label>
    <input type="checkbox" name="matchExact" id="matchExact" value="true"/>

    <input type="hidden" name="role" value="<%= loginBean.getRole() %>"/>
    <input type="hidden" name="type" value="xml">

    <input type="submit" value="export selective"/>
</form>

<hr>

not yet implemented!
<h3>Product Catalog as XHTML</h3>
<form action="controllerservlet?action=download" method="post">
    <input type="hidden" name="searchterm" value=""/>
    <input type="hidden" name="role" value="<%= loginBean.getRole() %>"/>
    <input type="hidden" name="type" value="xhtml">

    <input type="submit" value="export all"/>
</form>

<input id="backButton" type=button name=go-back value=" Back " onclick="javascript:history.back()">

</body>
</html>
