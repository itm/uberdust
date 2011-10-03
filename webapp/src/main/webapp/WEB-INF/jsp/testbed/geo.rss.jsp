<%@ page import="java.util.Date" %>
<%@page contentType="text/xml;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@page session="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/WEB-INF/tag/custom.tld" prefix="myfn" %>
<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<?xml version="1.0" encoding="utf-8"?>
<% Date now = new Date(); %>

<feed xmlns="http://www.w3.org/2005/Atom"
      xmlns:georss="http://www.georss.org/georss">

    <title>Testbed <c:out value="${testbed.name}"/></title>
    <subtitle>GeoRSS data from Uberdust</subtitle>
    <link href="http://150.140.5.11:8080/uberdust/rest/testbed/selected!/georss"/>
    <updated><%= now.toGMTString() %></updated>
    <author>
        <name>Uberdust</name>
        <email>uberdust@cti.gr</email>
    </author>
    <id><c:out value="ID:${testbed.name}${testbed.id}"/></id>
    <entry>
        <title>Testbed <c:out value="${testbed.name}"/></title>
        <link href="http://150.140.5.11:8080/uberdust/rest/testbed/selected!/georss"/>
        <id><c:out value="ID:${testbed.name}${testbed.id}"/></id>
        <updated><%= now.toGMTString() %></updated>
        <summary>Graphing the selected testbed</summary>
        <georss:point><c:out value="${testbed.setup.origin.x} ${testbed.setup.origin.y}"/></georss:point>
    </entry>
</feed>