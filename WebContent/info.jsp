<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<br>
<c:forEach var="info" items="${sessionScope.infoList}">
    <jsp:useBean id="info" type="java.lang.String"/>
    <b>
        <font color="black">
            <%= info %>
        </font>
    </b>
    <br><br>
</c:forEach>
