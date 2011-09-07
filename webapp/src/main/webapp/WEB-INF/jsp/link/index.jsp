<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="thisLink" scope="request" class="eu.wisebed.wiseml.model.setup.Link"/>
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
        <td>Source Node ID</td>
        <td><c:out value="${thisLink.source}"/></td>
        <td>Target Id</td>
        <td><c:out value="${thisLink.target}"/></td>
    </tr>
    <tr>
        <td>RSSI</td>
        <td><c:out value="${thisLink.rssi}"/></td>
    </tr>
    <tr>
        <td>Capabilities</td>
        <td>
            <ul>
                <c:forEach items="${thisLink.capabilities}" var="thisCap">
                    <li><c:out value="${thisCap.name}"/></li>
                </c:forEach>
            </ul>
        </td>
    </tr>
</table>
</body>
</html>