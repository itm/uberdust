<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.jar.Manifest" %>
<%@ page import="eu.wisebed.api.controller.RequestStatus" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    InputStream inputStream =  getServletConfig().getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
    Manifest manifest = new Manifest(inputStream);
    String implVersion = manifest.getMainAttributes().getValue("Implementation-Version");
%>
<% if (implVersion != null && !implVersion.isEmpty()) {%>
    <p>Hudson Implementation Version : <%= implVersion %></p>
<% }%>
