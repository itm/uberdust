<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="nodes" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<p style="color :red">path to be set here !</p>

<table>
    <tbody>
    <tr>
        <td>Nodes found</td>
        <td><c:out value="${fn:length(nodes)}"/></td>
    </tr>
    <c:forEach items="${nodes}" var="node">
        <tr>
            <td>Node ID</td>
            <td><c:out value="${node.id}"/></td>
        </tr>
        <tr>
            <td>Node Description</td>
            <td><c:out value="${node.description}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>