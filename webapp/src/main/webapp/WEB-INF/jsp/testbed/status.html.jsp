<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/WEB-INF/tag/custom.tld" prefix="myfn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<c:forEach items="${testbed.setups}" var="setup">
    <p>Setup ID : <c:out value="${setup.id}"/></p>

    <p>Nodes</p>
    <table>
        <thead>
        <th>Node Id</th>
        <th>Description</th>
        <th>Last Recorded Date</th>
        <th>Total Readings Count</th>
        </thead>
        <tbody>
        <c:forEach items="${setup.nodes}" var="node">
            <c:if test="${node != null}">
                <tr>
                    <td>
                        <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}"><c:out
                                value="${node.id}"/></a>
                    </td>
                    <td><c:out value="${node.description}"/></td>
                    <td><c:out value="${myfn:lastNodeReadingRecordedDate(node.readings)}"/></td>
                    <td><c:out value="${fn:length(node.readings)}"/></td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>

    <%--<p>Links</p>--%>
    <%--<table>--%>
    <%--<thead>--%>
    <%--<th>Link Source & Target Id</th>--%>
    <%--<th>Last Recorded Date</th>--%>
    <%--<th>Total Readings Count</th>--%>
    <%--</thead>--%>
    <%--<c:forEach items="${setup.link}" var="link">--%>
    <%--<tr>--%>
    <%--<td>--%>
    <%--<a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/{testbedId}/link/${link.source}/${link.target}"><c:out--%>
    <%--value="${link.source},${link.target}"/></a>--%>
    <%--</td>--%>
    <%--</tr>--%>
    <%--</c:forEach>--%>
    <%--</table>--%>
</c:forEach>
</body>
</html>