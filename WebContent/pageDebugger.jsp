<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
</head>
<body>
<!-- https://stackoverflow.com/a/6623332 -->
<table id="pageDebugger" width="100%" border="1" cellpadding="0" cellspacing="0"
       style="table-layout: fixed;">
    <colgroup>
        <col width="500">
    </colgroup>
    <tr>
        <th colspan="2">
            <h3>attributes in $paramValues</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${paramValues}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><c:forEach var="item" items="${entry.value}"
                           varStatus="status">
                <pre><c:out value="${item}" /></pre>
                <c:if test="${not status.last}">
                    <br />
                </c:if>
            </c:forEach></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
            <h3>attributes in $requestScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${requestScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
            <h3>attributes in $sessionScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${sessionScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
            <h3>attributes in $pageScope</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${pageScope}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><pre><c:out value="${entry.value}" /></pre></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">
            <h3>attributes in $headerValues</h3>
        </th>
    </tr>
    <c:forEach var="entry" items="${headerValues}">
        <tr>
            <td><c:out value="${entry.key}" /></td>
            <td><c:forEach var="item" items="${entry.value}"
                           varStatus="status">
                <pre><c:out value="${item}" /></pre>
                <c:if test="${not status.last}">
                    <br />
                </c:if>
            </c:forEach></td>
        </tr>
    </c:forEach>

</table>
</body>
</html>
