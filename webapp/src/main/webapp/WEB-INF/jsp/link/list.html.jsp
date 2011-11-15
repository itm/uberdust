<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>


<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Testbed Links : <c:out value="${testbed.name}"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>
<%@include file="/header.jsp" %>

<p>
    /<a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed">testbeds</a>/
    <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}">testbed</a>/
    <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/link">testbed
        links</a>
</p>
<c:choose>
    <c:when test="${links != null || fn:length(links) != 0}">
        <table>
            <tbody>
            <tr>
                <td>Links</td>
                <td>(<c:out value="${fn:length(links)}"/>)</td>
            </tr>
            </tbody>
        </table>
        <table>
            <tbody>
            <c:forEach items="${links}" var="link">
                <tr>
                    <td>
                        <a href="http://${uberdustDeploymentHost}/uberdust/rest/link/${link.source}/${link.target}"><c:out
                                value="${link.source},${link.target}"/></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color : red">No links found for testbed <c:out value="${testbed.name}"/></p>
    </c:otherwise>
</c:choose>
<%@include file="/footer.jsp" %>
</body>
</html>


