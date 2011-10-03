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

<p>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node">testbed's nodes</a></p>

<table>
    <tbody>
    <tr>
        <td>Nodes found</td>
        <td><c:out value="${fn:length(testbed.setup.nodes)}"/></td>
    </tr>
    <c:forEach items="${testbed.setup.nodes}" var="node">
        <tr>
            <td>Node ID</td>
            <td><c:out value="${node.id}"/></td>
        </tr>
        <tr>
            <td>Node Description</td>
            <td><c:out value="${node.description}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>