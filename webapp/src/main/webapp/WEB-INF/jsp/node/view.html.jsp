<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="nodes" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<p style="color :red">path to be set here !</p>

<table id="information">
    <tbody>
    <tr>
        <td>Nodes found</td>
        <td><c:out value="${fn:length(nodes)}"/></td>
    </tr>
    <c:forEach items="${nodes}" var="thisNode">
        <tr>
            <td>Node ID</td>
            <td><c:out value="${thisNode.id}"/></td>
        </tr>
        <tr>
            <td>Node Description</td>
            <td><c:out value="${thisNode.description}"/></td>
        </tr>
        <tr>
            <td>Capabilities(<c:out value="${fn:length(thisNode.capabilities)}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${thisNode.capabilities}" var="thisCap">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/node/${thisNode.id}/capability/${thisCap.name}"><c:out
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