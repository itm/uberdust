<%@ page import="eu.wisebed.wisedb.model.NodeReading" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="node" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>
<jsp:useBean id="readings" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="testbedId" scope="request" class="java.lang.String"/>

<%
    if (readings.isEmpty() == false) {
        // TODO this is ugly use guava or commons and make it beautifull o.o
        Date firstDate = ((NodeReading) readings.get(0)).getTimestamp();
        Date lastDate = ((NodeReading) readings.get(readings.size() - 1)).getTimestamp();
        double minReading = ((NodeReading) readings.get(0)).getReading();
        double maxReading = ((NodeReading) readings.get(0)).getReading();
        for (NodeReading reading : (ArrayList<NodeReading>) readings) {
            if (reading.getReading() < minReading) minReading = reading.getReading();
            if (reading.getReading() > maxReading) maxReading = reading.getReading();
        }
        pageContext.setAttribute("firstDate", firstDate);
        pageContext.setAttribute("lastDate", lastDate);
        pageContext.setAttribute("minReading", minReading);
        pageContext.setAttribute("maxReading", maxReading);
    }
%>


<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>

<body>

<p>
    /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}">testbed</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}">node</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}/capability/${capability.name}">capability</a>
</p>

<c:choose>
    <c:when test="${fn:length(readings) != 0}">
        <table>
            <tbody>
            <tr>
                <td>
                    Number of Records
                </td>
                <td>
                    <c:out value="${fn:length(readings)}"/>
                </td>
            </tr>
            <tr>
                <td>
                    First recording date
                </td>
                <td>
                    <c:out value="${firstDate}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Last recording date
                </td>
                <td>
                    <c:out value="${lastDate}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Min recorded value
                </td>
                <td>
                    <c:out value="${minReading}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Max recorded value
                </td>
                <td>
                    <c:out value="${maxReading}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Reading Formats
                </td>
                <td>
                    <ul>
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}/capability/${capability.name}/html"/>HTML
                            format</a>
                        </li>
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}/capability/${capability.name}/tabdelimited"/>Tab
                            Delimited format</a>
                        </li>
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}/capability/${capability.name}/wiseml"/>WiseML</a>
                            <span style="color : red">Not implemented</span>
                        </li>
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}/capability/${capability.name}/json"/>JSON</a>
                        </li>
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/node/${node.id}/capability/${capability.name}/chart"/>Readings Chart</a>
                            <span style="color : red">Not implemented</span>
                        </li>
                    </ul>
                </td>
            </tr>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        No readings found for Node with id <c:out value="${node.id}"/> and Capability with name <c:out
            value="${capability.name}"/>
    </c:otherwise>
</c:choose>
</body>
</html>