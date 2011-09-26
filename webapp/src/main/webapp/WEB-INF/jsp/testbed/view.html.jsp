<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbeds" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<h1>Welcome to ÜberDust</h1>

<table id="information">
    <tbody>
    <tr>
        <td>Testbeds found</td>
        <td><c:out value="${fn:length(testbeds)}"/></td>
    </tr>
    <c:forEach items="${testbeds}" var="thisTestbed">
        <tr>
            <td>Testbed ID</td>
            <td><c:out value="${thisTestbed.id}"/></td>
        </tr>
        <tr>
            <td>Testbed Description</td>
            <td><c:out value="${thisTestbed.description}"/></td>
        </tr>
        <tr>
            <td>Available Setups on Testbed(<c:out value="${thisTestbed.id}"/>)</td>
            <td><c:out value="${fn:length(thisTestbed.setups)}"/></td>
        </tr>
        <c:forEach items="${thisTestbed.setups}" var="thisSetup">
            <tr>
                <td>Setup ID</td>
                <td><c:out value="${thisSetup.id}"/></td>
            </tr>
            <tr>
                <td>Nodes</td>
                <td>
                    <ul>
                        <c:forEach items="${thisSetup.nodes}" var="thisNode">
                            <li>
                                <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/node/${thisNode.id}"><c:out
                                        value="${thisNode.id}"/></a></li>
                        </c:forEach>
                    </ul>
                </td>
            </tr>
            <tr>
                <td>Links</td>
                <td>
                    <ul>
                        <c:forEach items="${thisSetup.link}" var="thisLink">
                            <li>
                                <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/link/${thisLink.source}/${thisLink.target}"><c:out
                                        value="${thisLink.source},${thisLink.target}"/></a></li>
                        </c:forEach>
                    </ul>
                </td>
            </tr>
        </c:forEach>
    </c:forEach>
    </tbody>
</table>
</body>
</html>