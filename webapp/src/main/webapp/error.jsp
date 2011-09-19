<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>
<body>
    <%--<% String statusCode = (String) request.getAttribute("javax.servlet.error.status_code"); %>--%>
    <%--<% String message = (String) request.getAttribute("javax.servlet.error.message"); %>--%>
    <%--<jsp:forward page="/rest/testbedsetup">--%>
        <%--<jsp:param name="statusCode" value="Kodikos!"/>--%>
        <%--<jsp:param name="message" value="minima!"--%>
    <%--</jsp:forward>--%>
   <table id="error" style="color:#ff6666">
       <thead>
            <th>Error Occured</th>
       </thead>
       <tbody>
       <tr>
           <td>Status</td>
           <td><%=request.getAttribute("javax.servlet.error.status_code") %></td>
       </tr>
       <tr>
           <td>Reason</td>
           <td><%=request.getAttribute("javax.servlet.error.message") %></td>
       </tr>
       </tbody>
   </table>
</body>
</html>