<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="thisNode" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="nodeId" scope="request" type="java.lang.String"/>
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
        Node ID <c:out value="${nodeId}"/>
    </tr>
    <tr>
        <c:out value="${thisNode.description}"/>
    </tr>
    <tr>
        <h3>Capabilities</h3>
        <ul>
            <c:forEach items="${thisNode.capabilities}" var="thisCap">
                <li><c:out value="${thisCap.name}"/></li>
            </c:forEach>
        </ul>
    </tr>
</table>
</body>
</html>