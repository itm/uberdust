<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<spring:message code="uberdust.hudson.url" var="hudsonUrl" scope="application"/>
<spring:message code="uberdust.hudson.build" var="hudsonBuild" scope="application"/>
<spring:message code="uberdust.hudson.jobname" var="hudsonJobName" scope="application"/>
<spring:message code="uberdust.webapp.version" var="uberdustWebappVersion" scope="application"/>
<c:if test="${hudsonUrl != null && hudsonJobName != null && hudsonBuild != null && uberdustWebappVersion != null}">
    <p style="font-size:small">
        Überdust [<a href="${hudsonUrl}job/${hudsonJobName}/${hudsonBuild}">v${uberdustWebappVersion}b${hudsonBuild}</a>]
    </p>
</c:if>


