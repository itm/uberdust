<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/WEB-INF/tag/custom.tld" prefix="util" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="nodestats" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="linkstats" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Testbed <c:out value="${testbed.name}"/> status page</title>
</head>
<body>
<h1>Testbed <c:out value="${testbed.name}"/> status page</h1>
<c:choose>
    <c:when test="${nodestats != null}">
        <h2>Nodes</h2>
        <table>
            <thead>
            <th>Node</th>
            <th>Description</th>
            <th>Last Recorded Date</th>
            <th>Max Reading</th>
            <th>Min Reading</th>
            <th>Total Readings Count</th>
            </thead>
            <tbody>
            <c:forEach items="${nodestats}" var="stat">
                <tr>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${stat.node.id}"><c:out
                                value="${stat.node.id}"/></a>
                    </td>
                    <td><c:out value="${stat.node.description}"/></td>
                    <c:choose>
                        <c:when test="${util:checkIfDateIsToday(stat.latestTimestamp)}">
                            <td>${stat.latestTimestamp}</td>
                        </c:when>
                        <c:otherwise>
                            <td style="color :red">${stat.latestTimestamp}</td>
                        </c:otherwise>
                    </c:choose>
                    <td>${stat.maxReading}</td>
                    <td>${stat.minReading}</td>
                    <td>${stat.totalCount}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color :red"> No node status available</p>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${linkstats != null}">
        <h2>Links</h2>
        <table>
            <thead>
            <th>Link</th>
            <th>Last Recorded Date</th>
            <th>Max Reading</th>
            <th>Min Reading</th>
            <th>Total Readings Count</th>
            </thead>
            <tbody>
            <c:forEach items="${linkstats}" var="stat">
                <tr>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/eu.uberdust/rest/testbed/${testbed.id}/node/${stat.link.source}/${stat.link.target}"><c:out
                                value="[${stat.link.source},${stat.link.target}]"/></a>
                    </td>
                    <c:choose>
                        <c:when test="${util:checkIfDateIsToday(stat.latestTimestamp)}">
                            <td>${stat.latestTimestamp}</td>
                        </c:when>
                        <c:otherwise>
                            <td style="color :red">${stat.latestTimestamp}</td>
                        </c:otherwise>
                    </c:choose>
                    <td>${stat.maxReading}</td>
                    <td>${stat.minReading}</td>
                    <td>${stat.totalCount}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p style="color :red"> No node status available</p>
    </c:otherwise>
</c:choose>
</body>
</html>