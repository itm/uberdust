<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="thisLink" scope="request" class="eu.wisebed.wiseml.model.setup.Link"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<h1>Welcome to ÜberDust</h1>

<table id="information">
    <thead>
    <th>Property</th>
    <th>Value</th>
    </thead>
    <tbody>
    <tr>
        <td>Source Node ID</td>
        <td><c:out value="${thisLink.source}"/></td>
    </tr>
    <tr>
        <td>Target Id</td>
        <td><c:out value="${thisLink.target}"/></td>
    </tr>
    <tr>
        <td>Capabilities</td>
        <td>
            <ul>
                <c:forEach items="${thisLink.capabilities}" var="thisCap">
                    <li><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/link/${thisLink.source}/${thisLink.target}/capability/${thisCap.name}"><c:out value="${thisCap.name}"/></a></li>
                </c:forEach>
            </ul>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>