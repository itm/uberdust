<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="slse" scope="request" class="eu.wisebed.wisedb.model.Slse"/>
<jsp:useBean id="nodes" scope="request" class="org.hibernate.collection.PersistentList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Node : <c:out value="${node.id}"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>
<%@include file="/header.jsp" %>
<p>
    /<a href="<c:url value="/rest/testbed"/>">testbeds</a>/
    <a href="<c:url value="/rest/testbed/${testbed.id}"/>">testbed</a>/
    <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}"/>">node</a>
</p>

<table>
    <tbody>
    <tr>
        <td>Slse ID</td>
        <td>
            <a href="<c:url value="/rest/testbed/${testbed.id}/slse/${slse.name}"/>"><c:out value="${slse.name}"/></a>
        </td>


    </tr>
    <tr>
        <td>
            <a href="<c:url value="/rest/testbed/${testbed.id}/slse/${slse.name}/rdf"/>">
                <img src="http://www.mkbergman.com/wp-content/themes/ai3/images/2009Posts/090326_rdf_200.png" width="20px">
                Rdf description
            </a>
        </td>
    </tr>
    <tr>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>Nodes(<c:out value="${fn:length(nodes)}"/>)</td>
        <td>
            <table class="nodes">
                <tbody>
                <c:forEach items="${nodes}" var="node">
                    <tr>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}"/>">
                            <c:out value="${node.id}"/>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>
<%@include file="/footer.jsp" %>
</body>
</html>