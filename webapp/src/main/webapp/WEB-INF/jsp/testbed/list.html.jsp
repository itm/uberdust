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
    <title>ÜberDust - List Testbeds</title>
</head>
<body>

<h1>Welcome to ÜberDust</h1>
<c:choose>
    <c:when test="${testbeds !=null && fn:length(testbeds)!=0}">
        <table>
            <tbody>
            <tr>
                <td>Available Testbeds</td>
                <td><c:out value="${fn:length(testbeds)}"/></td>
            </tr>
            </tbody>
        </table>
        <table>
            <tbody>
            <c:forEach items="${testbeds}" var="testbed">
                <tr>
                    <td><c:out value="${testbed.id}"/></td>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}"><c:out
                                value="${testbed.name}"/></a>
                    </td>
                    <td>Nodes Count</td>
                    <td><c:out value="${fn:length(testbed.setup.nodes)}"/></td>
                    <td>Links Count</td>
                    <td><c:out value="${fn:length(testbed.setup.link)}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
         <p style="color : red"> No testbeds available</p>
    </c:otherwise>
</c:choose>
</body>
</html>