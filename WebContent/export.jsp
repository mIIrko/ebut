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

    <input type="submit" value="export xml all"/>
</form>

<br>
<br>

<h5>export xml selective by short description</h5>
<br>
<form name="exportSelective" action="controllerservlet?action=download" method="post">
    <label for="searchterm-xml">search term</label>
    <input type="text" id="searchterm-xml" name="searchterm" pattern="[a-zA-Z0-9-\.]{1,}" title="Erlaubte Zeichen sind: a-z A-Z 0-9 -" required="required"/>

    <!--
    <label for="matchExact">match term exact?</label>
    <input type="checkbox" name="matchExact" id="matchExact" value="true"/>
    -->

    <input type="hidden" name="role" value="<%= loginBean.getRole() %>"/>
    <input type="hidden" name="type" value="xml">

    <input type="submit" value="export xml selective"/>
</form>

<hr>

<h3>Product Catalog as XHTML</h3>
<form action="controllerservlet?action=download" method="post">
    <input type="hidden" name="searchterm" value=""/>
    <input type="hidden" name="role" value="<%= loginBean.getRole() %>"/>
    <input type="hidden" name="type" value="xhtml">

    <input type="submit" value="export xhtml all"/>
</form>

<br>
<br>

<h5>export xhtml selective by short description</h5>
<br>
<form name="exportSelective" action="controllerservlet?action=download" method="post">
    <label for="searchterm-xhtml">search term</label>
    <input type="text" id="searchterm-xhtml" name="searchterm" pattern="[a-zA-Z0-9-\.]{1,}" title="Erlaubte Zeichen sind: a-z A-Z 0-9 -" required="required"/>

    <!--
    <label for="matchExact">match term exact?</label>
    <input type="checkbox" name="matchExact" id="matchExact" value="true"/>
    -->

    <input type="hidden" name="role" value="<%= loginBean.getRole() %>"/>
    <input type="hidden" name="type" value="xhtml">

    <input type="submit" value="export xhtml selective"/>
</form>

<div class="space-bottom"></div>
<input id="backButton" type=button name=go-back value=" Back " onclick="javascript:history.back()">

</body>
</html>
