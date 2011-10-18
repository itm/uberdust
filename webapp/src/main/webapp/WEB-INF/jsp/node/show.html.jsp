<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="node" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="readingsCount" scope="request" class="java.lang.Long"/>
<jsp:useBean id="readingCountsPerCapability" scope="request" class="java.util.HashMap"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Node : <c:out value="${node.id}"/></title>
</head>
<body>

<p>
    /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed">testbeds</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}">node</a>
</p>

<table>
    <tbody>
    <tr>
        <td>Node ID</td>
        <td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}"><c:out value="${node.id}"/></a></td>
    </tr>
    <tr>
        <td>Node Description</td>
        <td><c:out value="${node.description}"/></td>
    </tr>
    <tr>
        <td>Capabilities(<c:out value="${fn:length(readingCountsPerCapability)}"/>)</td>
        <td>
            <ul>
                <c:forEach items="${readingCountsPerCapability}" var="thisCap">
                    <li>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/capability/${thisCap.key.name}"><c:out
                                value="${thisCap.key.name}"/></a>(<c:out value="${thisCap.value}"/>)</li>
                </c:forEach>
            </ul>
            <span>Total Readings count : <c:out value="${readingsCount}"/> </span>
        </td>
    </tr>
    <tr>
        <td>GeoRSS Feed</td>
        <td>
            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/georss">GeoRSS
                feed</a>
            <a href="http://maps.google.com/maps?q=http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/georss">View
                On Google Maps</a>
        </td>
    </tr>
    <tr>
        <td>KML Feed</td>
        <td>
            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/kml">KML
                feed</a>
            <a href="http://maps.google.com/maps?q=http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/kml">View
                On Google Maps</a>
            <span style="color : red">not implemented yet</span>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>