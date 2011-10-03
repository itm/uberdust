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

<p>
    /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>
</p>

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
        <td>Testbed URN prefix</td>
        <td><c:out value="${testbed.urnPrefix}"/></td>
    </tr>
    <tr>
        <td>Testbed URL</td>
        <td><a href="<c:out value="${testbed.url}"/>"><c:out value="${testbed.url}"/></a></td>
    </tr>
    <tr>
        <td>Testbed SNAA URL</td>
        <td><a href="<c:out value="${testbed.snaaUrl}"/>"><c:out value="${testbed.snaaUrl}"/></a></td>
    </tr>
    <tr>
        <td>Testbed RS URL</td>
        <td><a href="<c:out value="${testbed.rsUrl}"/>"><c:out value="${testbed.rsUrl}"/></a></td>
    </tr>
    <tr>
        <td>Testbed Session Management URL</td>
        <td><a href="<c:out value="${testbed.sessionUrl}"/>"><c:out value="${testbed.sessionUrl}"/></a></td>
    </tr>
    <tr>
        <td>Federated Testbed</td>
        <td><c:out value="${testbed.federated}"/></td>
    </tr>
    <tr>
        <td>Testbed Status Page</td>
        <td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/status">Status page</a></td>
    </tr>
    <tr>
        <td>Testbed GeoRSS feed</td>
        <td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/georss">GeoRSS feed</a></td>
        <td><a href="http://maps.google.com/maps?q=${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/georss">View On Google</a></td>
    </tr>
    <tr>
        <td>Testbed KML feed</td>
        <td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/kml">KML feed</a></td>
        <td><a href="http://maps.google.com/maps?q=${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/georss">View On Google</a></td>
        <td><p style="color : red">not implemented yet</p></td>
    </tr>
    <tr>
        <td>Testbed WiseML</td>
        <td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/wiseml">WiseML</a></td>
        <td><p style="color : red">not implemented yet</p></td>
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