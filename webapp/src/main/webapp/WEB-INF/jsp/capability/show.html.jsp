<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>
<jsp:useBean id="nodes" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>

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
<table>
    <tbody>
    <tr>
        <td>Capability ID</td>
        <td>
            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}"><c:out
                    value="${capability.name}"/></a>
        </td>
    </tr>
    <c:if test="${fn:length(nodes) != 0}">
        <tr>
            <td>Nodes(<c:out value="${fn:length(nodes)}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${nodes}" var="node">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}"><c:out
                                    value="${node.id}"/></a>
                        </li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
    </c:if>
    <c:if test="${fn:length(links) != 0}">
        <tr>
            <td>Links(<c:out value="${fn:length(links)}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${links}" var="link">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/link/${link.source}/${link.target}/capability/${capability.name}"><c:out
                                    value="[${link.source},${link.target}]"/></a>
                        </li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
    </c:if>
    </tbody>
</table>
</body>
</html>