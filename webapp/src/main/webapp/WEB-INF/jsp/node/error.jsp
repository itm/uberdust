<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="nodeId" scope="request" class="java.lang.String"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>
<div id="warning">
    <c:choose>
       <c:when test="${empty nodeId}">
           Must Provide Node
       </c:when>
       <c:otherwise>
           Cannot find node with id <c:out value="${nodeId}"/>
       </c:otherwise>
    </c:choose>
</div>
</body>
</html>