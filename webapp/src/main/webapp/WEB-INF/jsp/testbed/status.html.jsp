<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/WEB-INF/tag/custom.tld" prefix="util" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="lastNodeReadings" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="lastLinkReadings" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Testbed <c:out value="${testbed.name}"/> status page</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>
<%@include file="/header.jsp"%>
<h1>Testbed <c:out value="${testbed.name}"/> status page</h1>
<c:choose>
    <c:when test="${lastLinkReadings != null}">
        <h2>Nodes</h2>
        <table>
            <thead>
            <th>Node</th>
            <th>Capability</th>
            <th>Last Reading Timestamp</th>
            <th>Last Reading Value</th>
            <th>Description</th>
            </thead>
            <tbody>
            <c:forEach items="${lastNodeReadings}" var="lnr">
                <c:if test="${lnr != null}">
                    <tr>
                        <td>
                            <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/node/${lnr.node.id}"><c:out value="${lnr.node.id}"/></a>
                        </td>
                        <td>
                            <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/capability/${lnr.capability.name}"><c:out value="${lnr.capability.name}"/></a>
                        </td>
                        <c:choose>
                            <c:when test="${util:checkIfDateIsToday(lnr.timestamp)}">
                                <td>${lnr.timestamp}</td>
                                <td>${lnr.reading}</td>
                            </c:when>
                            <c:otherwise>
                                <td style="color :red">${lnr.timestamp}</td>
                                <td style="color :red">${lnr.reading}</td>
                            </c:otherwise>
                        </c:choose>
                        <td><c:out value="${lnr.node.description}"/></td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color :red"> No node status available</p>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${lastLinkReadings != null}">
        <h2>Links</h2>
        <table>
            <thead>
            <th>Link</th>
            <th>Capability</th>
            <th>Last Reading Timestamp</th>
            <th>Last Reading Value</th>
            </thead>
            <tbody>
            <c:forEach items="${lastLinkReadings}" var="llr">
                <c:if test="${llr != null}">
                    <tr>
                        <td>
                            <a href="http://${uberdustDeploymentHost}/eu.uberdust/rest/testbed/${testbed.id}/link/${llr.link.source}/${llr.link.target}"><c:out
                                    value="[${llr.link.source},${llr.link.target}]"/></a>
                        </td>
                        <td>
                            <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/capability/${llr.capability.name}"><c:out value="${llr.capability.name}"/></a>
                        </td>
                        <c:choose>
                            <c:when test="${util:checkIfDateIsToday(llr.timestamp)}">
                                <td>${llr.timestamp}</td>
                                <td>${llr.reading}</td>
                            </c:when>
                            <c:otherwise>
                                <td style="color :red">${llr.timestamp}</td>
                                <td style="color :red">${llr.reading}</td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color :red"> No link status available</p>
    </c:otherwise>
</c:choose>
<%@include file="/footer.jsp"%>
</body>
</html>