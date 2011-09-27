<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>

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
        <td>Links found</td>
        <td><c:out value="${fn:length(links)}"/></td>
    </tr>
    <c:forEach items="${links}" var="thisLink">
        <tr>
            <td>Source ID</td>
            <td><c:out value="${thisLink.source}"/></td>
        </tr>
        <tr>
            <td>Target ID</td>
            <td><c:out value="${thisLink.target}"/></td>
        </tr>
        <tr>
            <td>Link Description</td>
        </tr>
        <tr>
            <td>Capabilities(<c:out value="${fn:length(thisLink.capabilities)}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${thisLink.capabilities}" var="thisCap">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/link/${thisLink.source}/${thisLink.target}/capability/${thisCap.name}"><c:out
                                    value="${thisCap.name}"/></a></li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>


