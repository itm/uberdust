<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<p>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/link">testbed links</a></p>

<table>
    <tbody>
    <tr>
        <td>Links found</td>
        <td><c:out value="${fn:length(testbed.setup.link)}"/></td>
    </tr>
    <c:forEach items="${testbed.setup.link}" var="link">
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


