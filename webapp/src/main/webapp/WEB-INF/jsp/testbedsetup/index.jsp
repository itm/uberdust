<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="thisTesbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="thisSetup" scope="request" class="eu.wisebed.wiseml.model.setup.Setup"/>

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
    <tbody>
    <tr>
        <td>Testbed ID</td>
        <td><c:out value="${thisTesbed.id}"/></td>
    </tr>
    <tr>
        <td>Testbed Description</td>
        <td><c:out value="${thisTesbed.description}"/></td>
    </tr>
    <tr>
        <td>Setup ID</td>
        <td><c:out value="${thisSetup.id}"/></td>
    </tr>
    <tr>
        <td>Nodes</td>
        <td>
            <ul>
                <c:forEach items="${thisSetup.nodes}" var="thisNode">
                    <li><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/node/${thisNode.id}"><c:out value="${thisNode.id}"/></a></li>
                </c:forEach>
            </ul>
        </td>
    </tr>
    <tr>
        <td>Links</td>
        <td>
            <ul>
                <c:forEach items="${thisSetup.link}" var="thisLink">
                    <li><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/link/${thisLink.source}/${thisLink.target}"><c:out value="${thisLink.source},${thisLink.target}"/></a></li>
                </c:forEach>
            </ul>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>