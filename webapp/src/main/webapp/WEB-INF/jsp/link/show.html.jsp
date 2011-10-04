<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="testbedId" scope="request" class="java.lang.String"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<table>
    <tbody>
    <tr>
        <td>Links found</td>
        <td><c:out value="${fn:length(links)}"/></td>
    </tr>
    <c:forEach items="${links}" var="link">
        <tr>
            <td>
                /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}">testbed</a>/<a
                    href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/link/${link.source}/${link.target}">link</a>
            </td>
        </tr>
        <tr>
            <td>Source ID</td>
            <td><c:out value="${link.source}"/></td>
        </tr>
        <tr>
            <td>Target ID</td>
            <td><c:out value="${link.target}"/></td>
        </tr>
        <tr>
            <td>Capabilities(<c:out value="${fn:length(link.capabilities)}"/>)</td>
            <td>
                <ul>
                    <c:forEach items="${link.capabilities}" var="capability">
                        <li>
                            <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbedId}/link/${link.source}/${link.target}/capability/${capability.name}"><c:out
                                    value="${capability.name}"/></a></li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>