<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="thisNode" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
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
    <tr>
        <td>Node ID</td>
        <td><c:out value="${thisNode.id}"/></td>
    </tr>
    <tr>
        <td>Node Description</td>
        <td><c:out value="${thisNode.description}"/></td>
    </tr>
    <tr>
        <td>Capabilities</td>
        <td>
            <ul>
                <c:forEach items="${thisNode.capabilities}" var="thisCap">
                    <li><c:out value="${thisCap.name}"/></li>
                </c:forEach>
            </ul>
        </td>
    </tr>
</table>
</body>
</html>