<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="capabilities" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - List Capabilities</title>
</head>
<body>
<p>
    /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed">testbeds</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/capability">testbed
    capabilities</a>
</p>
<c:choose>
    <c:when test="${capabilities != null || fn:length(capabilities) != 0}">
        <table>
            <tbody>
            <tr>
                <td>Capabilities found</td>
                <td><c:out value="${fn:length(capabilities)}"/></td>
            </tr>

            <c:forEach items="${capabilities}" var="capability">
                <tr>
                    <td>Capability name</td>
                    <td><c:out value="${capability.name}"/></td>
                </tr>
                <tr>
                    <td>Capability unit</td>
                    <td><c:out value="${capability.unit}"/></td>
                </tr>
                <tr>
                    <td>Capability datatype</td>
                    <td><c:out value="${capability.datatype}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color : red">No capabilities found for testbed <c:out value="${testbed.name}"/></p>
    </c:otherwise>
</c:choose>
</body>
</html>