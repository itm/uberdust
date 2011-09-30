<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="testbedId" scope="request" class="java.lang.String"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<p>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}"><c:out value="${testbedId}"/></a>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/link">testbed links</a></p>

<table>
    <tbody>
    <tr>
        <td>Links found</td>
        <td><c:out value="${fn:length(links)}"/></td>
    </tr>
    <c:forEach items="${links}" var="link">
        <tr>
            <td>Source ID</td>
            <td><c:out value="${link.source}"/></td>
        </tr>
        <tr>
            <td>Target ID</td>
            <td><c:out value="${link.target}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>


