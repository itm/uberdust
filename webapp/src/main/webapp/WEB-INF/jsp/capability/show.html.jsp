<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Capability [<c:out value="${capability.name}"/>]</title>
</head>
<body>
<p>
    /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed">testbeds</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}">capabilities</a>
</p>
<c:choose>
    <c:when test="${capability != null}">
        <table>
            <tbody>
            <tr>
                <td>Capability name</td>
                <td>
                    <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}"><c:out
                            value="${capability.name}"/></a>
                </td>
            </tr>
            <tr>
                <td>Number of nodes having this capability</td>
                <td><c:out value="${fn:length(capability.nodes)}"/></td>
            </tr>
            <tr>
                <td>Number of node Readings having this capability</td>
                <td><c:out value="${fn:length(capability.nodeReadings)}"/></td>
            </tr>
            <tr>
                <td>Number of links having this capability</td>
                <td><c:out value="${fn:length(capability.links)}"/></td>
            </tr>
            <tr>
                <td>Number of link Readings having this capability</td>
                <td><c:out value="${fn:length(capability.linkReadings)}"/></td>
            </tr>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p>Cannot show capability</p>
    </c:otherwise>
</c:choose>
</body>
</html>