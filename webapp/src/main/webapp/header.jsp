<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:message code="uberdust.hudson.url" var="hudsonUrl" scope="application" text=""/>
<spring:message code="uberdust.hudson.build" var="hudsonBuild" scope="application" text=""/>
<spring:message code="uberdust.hudson.jobname" var="hudsonJobName" scope="application" text=""/>
<spring:message code="uberdust.webapp.version" var="uberdustWebappVersion" scope="application" text=""/>

<c:if test="${fn:length(hudsonUrl) !=0 && fn:length(hudsonBuild) !=0 && fn:length(hudsonJobName) !=0 && fn:length(uberdustWebappVersion) !=0}">
    <p style="font-size:small">
        Überdust [<a href="${hudsonUrl}job/${hudsonJobName}/${hudsonBuild}">v${uberdustWebappVersion}b${hudsonBuild}</a>]
    </p>
</c:if>


