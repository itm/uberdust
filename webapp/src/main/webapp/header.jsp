<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.jar.Manifest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
    Manifest manifest = new Manifest(getServletConfig().getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
    final String implVersion = manifest.getMainAttributes().getValue("Implementation-Version");
    final String jobName = manifest.getMainAttributes().getValue("Job-Name");
    final String hudsonServerUrl = manifest.getMainAttributes().getValue("Hudson-URL");
%>

<c:set var="implVersion" value="<%= implVersion%>" scope="application" />
<c:set var="jobName" value="<%= jobName %>" scope="application" />
<c:set var="hudsonServerUrl" value="<%= hudsonServerUrl %>" scope="application" />

<c:if test="${implVersion != null && fn:length(implVersion) != 0}">
    <c:set var="buildUrl" value="${hudsonServerUrl}job/${jobName}/${implVersion}" scope="application"/>
</c:if>


<c:if test="${buildUrl != null && fn:length(buildUrl) != 0}">
    <p style="font-size:small">Uberdust[<a href="<c:out value="${buildUrl}"/>"><c:out value="${implVersion}"/></a>]</p>
</c:if>

