<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="node" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>


<html>
<head>
    <META NAME="Description" CONTENT="ÜberDust"/>
    <META http-equiv="Content-Language" content="en"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>ÜberDust</title>
</head>

<body>
<h1>Welcome to ÜberDust</h1>
<table>
    <tbody>
    <tr>
        <td>
            Number of Records
        </td>
        <td>
            !!!!!!!!!
        </td>
    </tr>
    <tr>
        <td>
            First recording date
        </td>
        <td>
            !!!!!!!!!
        </td>
    </tr>
    <tr>
        <td>
            Last recording date
        </td>
        <td>
            !!!!!!!!
        </td>
    </tr>
    <tr>
        <td>
            Minimum recorded value
        </td>
        <td>
            !!!!!!!!
        </td>
    </tr>
        <tr>
        <td>
            Max recorded value
        </td>
        <td>
            !!!!!!!!
        </td>
    </tr>
    <tr>
        <td>
            Reading Formats
        </td>
        <td>
            <ul>
                <li>
                    <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/node/${node.id}/capability/${capability.name}/html"/>HTML
                    format</a>
                </li>
                <li>
                    <a href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/node/${node.id}/capability/${capability.name}/tabdelimited"/>Tab
                    Delimited format</a>
                </li>
                <li>
                    WiseML
                </li>
            </ul>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>