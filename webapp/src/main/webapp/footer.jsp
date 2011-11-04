<c:if test="${buildUrl != null && fn:length(buildUrl) != 0}">
    <p>Uberdust[<a href="<c:out value="${buildUrl}"/>"><c:out value="${implVersion}"/></a>]</p>
</c:if>
