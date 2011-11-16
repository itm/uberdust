<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="capabilities" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - List Capabilities</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>
<%@include file="/header.jsp" %>
<p>
    /<a href="<c:url value="/rest/testbed"/>">testbeds</a>/
    <a href="<c:url value="/rest/testbed/${testbed.id}"/>">testbed</a>/
    <a href="<c:url value="/rest/testbed/${testbed.id}/capability"/>">testbed capabilities</a>
</p>
<c:choose>
    <c:when test="${capabilities != null || fn:length(capabilities) != 0}">
        <table>
            <tr>
                <td>Capabilities</td>
                <td>(<c:out value="${fn:length(capabilities)}"/>)</td>
            </tr>
        </table>
        <table>
            <c:forEach items="${capabilities}" var="capability">
                <tr>
                    <td>
                        <a href="<c:url value="/rest/testbed/${testbed.id}/capability/${capability.name}"/>"><c:out
                                value="${capability.name}"/></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color : red">No capabilities found for testbed <c:out value="${testbed.name}"/></p>
    </c:otherwise>
</c:choose>
<%@include file="/footer.jsp" %>
</body>
</html>