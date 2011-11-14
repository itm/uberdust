<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>
<jsp:useBean id="nodes" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="links" scope="request" class="java.util.ArrayList"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust - Show Capability [<c:out value="${capability.name}"/>]</title>
</head>
<body>
<%@include file="/header.jsp"%>
<p>
    /<a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed">testbeds</a>/
    <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}">testbed</a>/
    <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}">capability</a>
</p>
<table>
    <tbody>
    <tr>
        <td>Capability ID</td>
        <td>
            <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}"><c:out
                    value="${capability.name}"/></a>
        </td>
    </tr>
    <c:if test="${nodes != null && fn:length(nodes) != 0}">
        <tr>
            <td>Nodes(<c:out value="${fn:length(nodes)}"/>)</td>
            <td>
                <table>
                    <tbody>
                    <c:forEach items="${nodes}" var="node">
                        <tr>
                            <td>
                                <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/node/${node.id}"><c:out
                                        value="${node.id}"/></a>
                            </td>
                            <td>
                                <!-- add stuff here ! -->
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </td>
        </tr>
    </c:if>
    <c:if test="${links != null && fn:length(links) != 0}">
        <tr>
            <td>Links(<c:out value="${fn:length(links)}"/>)</td>
            <td>
                <table>
                    <tbody>
                    <c:forEach items="${links}" var="link">
                        <tr>
                            <td>
                                <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/link/${link.source}/${link.target}}"><c:out
                                        value="[${link.source},${link.target}]"/></a>
                            </td>
                            <td>
                                <!-- TODO add stuff from next page -->
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </td>
        </tr>
    </c:if>
    <tr>
        <td>
            <a href="http://${uberdustDeploymentHost}/uberdust/rest/testbed/${testbed.id}/capability/${capability.name}/tabdelimited">Tab
                Delimited Format</a>
        </td>
    </tr>
    </tbody>
</table>
<%@include file="/footer.jsp"%>
</body>
</html>