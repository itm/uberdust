<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="nodes" scope="request" class="java.util.ArrayList"/>


<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Testbed Nodes : <c:out value="${testbed.name}"/></title>
</head>
<body>
<%@include file="/header.jsp"%>

<p>
    /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed">testbeds</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node">testbed
    nodes</a>
</p>


<c:choose>
    <c:when test="${nodes != null || fn:length(nodes) != 0}">
        <table>
            <tbody>
            <tr>
                <td>Nodes</td>
                <td>(<c:out value="${fn:length(nodes)}"/>)</td>
            </tr>
            </tbody>
        </table>
        <table>
            <tbody>
            <c:forEach items="${nodes}" var="node">
                <tr>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}"><c:out
                                value="${node.id}"/></a>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color : red">No nodes found for testbed <c:out value="${testbed.name}"/></p>
    </c:otherwise>
</c:choose>

<%@include file="/footer.jsp"%>
</body>
</html>