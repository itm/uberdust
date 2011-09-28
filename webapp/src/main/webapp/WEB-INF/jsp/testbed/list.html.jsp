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

<table>
<tbody>
    <tr>
        <td>Testbeds found</td>
        <td><c:out value="${fn:length(testbeds)}"/></td>
    </tr>
    <c:forEach items="${testbeds}" var="thisTestbed">
        <tr>
            <td>Testbed ID</td>
            <td><c:out value="${thisTestbed.id}"/></td>
            <td>Testbed Name</td>
            <td><c:out value="${thisTestbed.name}"/></td>
            <td><a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/testbed/${thisTestbed.id}">setup information</a></td>
            <td>...</td>
            <td>...</td>
    </c:forEach>
    </tbody>
</table>
</body>
</html>