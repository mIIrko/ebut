<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
    <title>eBusiness Framework Demo - Import</title>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="default.css">
</head>

<body>

<%@ include file="header.jsp" %>
<%@ include file="error.jsp" %>
<%@ include file="info.jsp" %>
<%@ include file="authentication.jsp" %>
<%@ include file="navigation.jspfragment" %>

<h1>Import</h1>

<form method="post" action="controllerservlet?action=upload" enctype="multipart/form-data">
    Select file to upload:
    <input type="file" name="uploadFile"/>
    <br/><br/>
    <input type="submit" value="upload"/>
</form>


<%@ include file="pageDebugger.jsp"%>
</body>
</html>
