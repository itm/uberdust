<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Link <c:forEach items="${links}" var="link">[<c:out value="${link.source}"/>,<c:out value="${link.target}"/>]</c:forEach> </title>
</head>
<body>

<table>
    <tbody>

    <c:forEach items="${links}" var="link">
        <tr>
            <td>
                /<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed">testbeds</a>/<a
                    href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}">testbed</a>/<a
                    href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/link/${link.source}/${link.target}">link</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}link/${link.source}/${link.target}"><c:out
                        value="${link.source},${link.target}"/></a>
            </td>
        </tr>
        <tr>
            <td>Source ID</td>
            <td>
                <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${link.source.id}"><c:out
                        value="${link.source.id}"/></a>
            </td>
        </tr>
        <tr>
            <td>Target ID</td>
            <td>
                <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${link.target.id}"><c:out
                        value="${link.target.id}"/></a>
            </td>
        </tr>
        <tr>
            <td>Capabilities count</td>
            <td><c:out value="${fn:length(link.capabilities)}"/></td>
        </tr>
        <tr>
            <td>Readings count</td>
            <td><c:out value="${fn:length(link.readings)}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>