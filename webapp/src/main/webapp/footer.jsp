<jsp:useBean id="time" scope="request" class="String"/>
<c:if test="${time != ''}">
 page loaded in <c:out value="${time}"/> milliseconds
</c:if>
