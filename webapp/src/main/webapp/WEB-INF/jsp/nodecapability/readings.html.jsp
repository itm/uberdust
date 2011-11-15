<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="readings" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Readings</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>

<body>
<%@include file="/header.jsp" %>
<c:choose>
    <c:when test="${fn:length(readings) == 0}">
        <h2> No readings found </h2>
    </c:when>
    <c:otherwise>
        <table id="information">
            <thead>
            <tr>
            <th>Timestamp</th>
            <th>Readings(<c:out value="${fn:length(readings)}"/>)</th>
            </thead>
            <tbody>
            <c:forEach items="${readings}" var="reading">
                <tr>
                    <td>
                        <c:out value="${reading.timestamp}"/>
                    </td>
                    <td align="center">
                        <c:out value="${reading.reading}"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
<%@include file="/footer.jsp" %>
</body>
</html>