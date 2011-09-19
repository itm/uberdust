<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="thisTesbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="thisSetup" scope="request" class="eu.wisebed.wiseml.model.setup.Setup"/>

<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>

<h1>Welcome to ÜberDust</h1>

<%--<% String statusCode = (String) request.getParameter("statusCode"); %>--%>
<%--<% String message = (String) request.getParameter("message"); %>--%>
<%--<% if(statusCode != null && statusCode.isEmpty() == false) { %>--%>
   <%--<table id="error" style="color:#ff6666">--%>
       <%--<thead>--%>
            <%--<th>Error Occured</th>--%>
       <%--</thead>--%>
       <%--<tbody>--%>
       <%--<tr>--%>
           <%--<td>Status</td>--%>
           <%--<td><%=statusCode %></td>--%>
       <%--</tr>--%>
       <%--<tr>--%>
           <%--<td>Reason</td>--%>
           <%--<td><%=message %></td>--%>
       <%--</tr>--%>
       <%--</tbody>--%>
   <%--</table>--%>
<%--<% } %>--%>

<table id="information">
    <tbody>
    <tr>
        <td>Testbed ID</td>
        <td><c:out value="${thisTesbed.id}"/></td>
    </tr>
    <tr>
        <td>Testbed Description</td>
        <td><c:out value="${thisTesbed.description}"/></td>
    </tr>
    <tr>
        <td>Setup ID</td>
        <td><c:out value="${thisSetup.id}"/></td>
    </tr>
    <tr>
        <td>Nodes</td>
        <td>
            <ul>
                <c:forEach items="${thisSetup.nodes}" var="thisNode">
                    <li><c:out value="${thisNode.id}"/></li>
                </c:forEach>
            </ul>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>