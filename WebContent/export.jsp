<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
    <title>eBusiness Framework Demo - Export</title>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="default.css">
</head>

<body>

<%@ include file="header.jsp" %>
<%@ include file="error.jsp" %>
<%@ include file="authentication.jsp" %>
<%@ include file="navigation.jspfragment" %>

<h1>Export</h1>

<h5>export selective by short description</h5>
<form action="controllerservlet?action=download" method="post">
    <label for="searchterm">search term</label>
    <input type="text" name="searchterm" id="searchterm"/>

    <label for="matchExact">match term exact?</label>
    <input type="checkbox" name="matchExact" id="matchExact" />

    <input type="hidden" name="role" value="<%= loginBean.getRole() %>" />

    <input type="submit" value="export selective" />
</form>

<hr>

<h5>export all</h5>
<form action="controllerservlet?action=download" method="post">
    <input type="hidden" name="searchterm" value=""/>
    <input type="hidden" name="role" value="<%= loginBean.getRole() %>" />

    <input type="submit" value="export all" />
</form>
</body>
</html>
