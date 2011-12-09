<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<spring:message code="uberdust.deployment.host" var="uberdustDeploymentHost" />

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="node" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="capabilities" scope="request" class="org.hibernate.collection.PersistentList"/>

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
        <td>Node ID</td>
        <td>
            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}"/>"><c:out value="${node.id}"/></a>
        </td>
    </tr>
    <tr>
        <td>Node Description</td>
        <td><c:out value="${node.description}"/></td>
    </tr>
    <tr>
        <td>Capabilities(<c:out value="${fn:length(capabilities)}"/>)</td>
        <td>
            <table class="readings">
                <tbody>
                <c:forEach items="${capabilities}" var="capability">
                    <tr>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/capability/${capability.name}"/>"><c:out
                                    value="${capability.name}"/></a>
                        </td>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/html/limit/10"/>">HTML</a>
                        </td>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/tabdelimited/limit/1000"/>">Tab
                                Delimited</a>
                        </td>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/json/limit/1000"/>">JSON</a>
                        </td>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/wiseml"/>">WiseML</a>
                            <span style="color : red">Not implemented yet</span>
                        </td>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/latestreading"/>">Latest
                                Reading</a>(<a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/latestreading/json"/>">JSON</a>)
                        </td>
                        <td>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/chart/limit/10"/>">Chart(10)</a>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/chart/limit/100"/>">Chart(100)</a>
                            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/chart/limit/1000"/>">Chart(1000)</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td>GeoRSS Feed</td>
        <td>
            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/georss"/>">GeoRSS Feed</a>
            (<a href="http://maps.google.com/maps?q=http://${uberdustDeploymentHost}<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/georss"/>">View
            On Google Maps</a>)
        </td>
    </tr>
    <tr>
        <td>KML Feed</td>
        <td>
            <a href="<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/kml"/>">KML feed</a>
            (<a href="http://maps.google.com/maps?q=http://${uberdustDeploymentHost}<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/kml"/>">View
            On Google Maps</a>)
            <span style="color : red">Not implemented yet</span>
        </td>
    </tr>
    </tbody>
</table>
<%@include file="/footer.jsp" %>
</body>
</html>