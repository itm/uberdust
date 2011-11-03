<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.jar.Manifest" %>
<%@ page import="eu.wisebed.api.controller.RequestStatus" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% if (implVersion != null && !implVersion.isEmpty()) {%>
    <p>Uberdust : <%= implVersion %></p>
<% }%>