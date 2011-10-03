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

<p>/<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a></p>

<table>
    <tbody>
    <tr>
        <td>Testbed ID</td>
        <td><c:out value="${testbed.id}"/></td>
    </tr>
    <tr>
        <td>Testbed Description</td>
        <td><c:out value="${testbed.description}"/></td>
    </tr>
    <tr>
        <td>Testbed Name</td>
        <td><c:out value="${testbed.name}"/></td>
    </tr>
    <tr>
        <td>...</td>
    </tr>
    <tr>
        <td>...</td>
    </tr>
    <tr>
        <td>...</td>
    </tr>
    </tbody>
</table>
<p>Nodes</p>
<c:choose>
    <c:when test="${testbed.setup.nodes == null || fn:length(testbed.setup.nodes) == 0}">
        <p style="color : red">No nodes available</p>
    </c:when>
    <c:otherwise>
        <table>
            <tbody>
            <c:forEach items="${testbed.setup.nodes}" var="node">
                <tr>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}"><c:out
                                value="${node.id}"/></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>


<p>Links</p>
<c:choose>
    <c:when test="${testbed.setup.link == null || fn:length(testbed.setup.link) == 0 }">
        <p style="color : red">No links available</p>
    </c:when>
    <c:otherwise>
        <table>
            <c:forEach items="${testbed.setup.link}" var="link">
                <tr>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/link/${link.source}/${link.target}"><c:out
                                value="${link.source},${link.target}"/></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>