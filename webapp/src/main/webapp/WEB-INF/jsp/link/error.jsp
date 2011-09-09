<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="sourceId" scope="request" class="java.lang.String"/>
<jsp:useBean id="targetId" scope="request" class="java.lang.String"/>

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
       <c:when test="${empty sourceId || empty targetId }">
           Must Provide source & target node ids.
       </c:when>
       <c:otherwise>
           Cannot find node with node source id <c:out value="${sourceId}"/> and target id <c:out value="${targetId}"/>
       </c:otherwise>
    </c:choose>
</div>
</body>
</html>