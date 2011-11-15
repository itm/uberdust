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
    <title>ÜberDust - ${pageContext.errorData.statusCode}</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>

<%@include file="/header.jsp" %>
<table class="error">
    <tbody>
    <tr>
        <td>Status</td>
        <td>${pageContext.errorData.statusCode}</td>
    </tr>
    <tr>
        <td>URI</td>
        <td>${pageContext.errorData.requestURI}</td>
    </tr>
    <tr>
        <td>Servlet Name</td>
        <td>${pageContext.errorData.servletName}</td>
    </tr>
    </tbody>
</table>
<%@include file="/footer.jsp" %>
</body>
</html>