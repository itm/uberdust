<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>
<jsp:useBean id="nodeReadingsCount" scope="request" class="java.lang.Long"/>
<jsp:useBean id="linkReadingsCount" scope="request" class="java.lang.Long"/>
<jsp:useBean id="readingCountsPerNode" scope="request" class="java.util.HashMap"/>
<jsp:useBean id="readingCountsPerLink" scope="request" class="java.util.HashMap"/>

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
        href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}">capability</a>
</p>
<table>
    <tbody>
    <tr>
        <td>Capability ID</td>
        <td>
            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}"><c:out
                    value="${capability.name}"/></a>
        </td>
    </tr>
    <c:if test="${nodeReadingsCount != 0}">
        <tr>
            <td>Nodes(<c:out value="${nodeReadingsCount}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${readingCountsPerNode}" var="node">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.key.id}/capability/${capability.name}"><c:out
                                    value="${node.key.id}"/></a>(<c:out value="${node.value}"/>)
                        </li>
                    </c:forEach>
                </ul>
                <span>Total Readings count : <c:out value="${nodeReadingsCount}"/> </span>
            </td>
        </tr>
    </c:if>
    <c:if test="${linkReadingsCount != 0}">
        <tr>
            <td>Links(<c:out value="${linkReadingsCount}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${readingCountsPerLink}" var="link">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/link/${link.key.source}/${link.key.target}/capability/${capability.name}"><c:out
                                    value="[${link.key.source},${link.key.target}]"/></a>(<c:out value="${link.value}"/>)
                        </li>
                    </c:forEach>
                </ul>
                <span>Total Readings count : <c:out value="${linkReadingsCount}"/> </span>
            </td>
        </tr>
    </c:if>
    </tbody>
</table>
</body>
</html>