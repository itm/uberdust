<%@ page isErrorPage="true" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <title>ÜberDust - Exception Occured</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>
<%@include file="/header.jsp" %>
<p>Exception Occured</p>
<table class="error">
    <tbody>
    <tr>
        <td>Exception's Message</td>
        <td>${pageContext.exception.message}</td>
    </tr>
    <tr>
        <td>Exception's Stacktrace</td>
        <td>
            <c:forEach var="stacktraceElement" items="${pageContext.exception.stackTrace}">
                    ${stacktraceElement}</br>
            </c:forEach>
        </td>
    </tr>
    </tbody>
</table>
<%@include file="/footer.jsp" %>
</body>
</html>