<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<spring:message code="uberdust.hudson.url" var="hudsonUrl" scope="application" text=""/>
<spring:message code="uberdust.hudson.build" var="hudsonBuild" scope="application" text=""/>
<spring:message code="uberdust.hudson.jobname" var="hudsonJobName" scope="application" text=""/>
<spring:message code="uberdust.webapp.version" var="uberdustWebappVersion" scope="application" text=""/>

<p style="font-size:small">
    Überdust [<a href="${hudsonUrl}job/${hudsonJobName}/${hudsonBuild}">v${uberdustWebappVersion}b${hudsonBuild}</a>]
</p>


