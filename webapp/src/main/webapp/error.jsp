<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <title>ÜberDust - Error</title>
</head>
<body>
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