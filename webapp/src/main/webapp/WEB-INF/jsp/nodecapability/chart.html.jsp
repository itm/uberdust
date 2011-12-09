<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<spring:message code="uberdust.deployment.host" var="uberdustDeploymentHost"/>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="node" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>
<jsp:useBean id="limit" scope="request" type="java.lang.Integer"/>

<html>
<%@include file="/header.jsp" %>
<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
    <script type="text/javascript" src="<c:url value="/js/highcharts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/themes/gray.js"/>"></script>
    <script type="text/javascript">

        var chart;

        function requestData() {
            $.ajax({
                url: 'http://${pageContext.request.serverName}:${pageContext.request.serverPort}'
                    .concat('<c:url value="/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/json"/>')
                    <c:if test="${limit != null}">.concat('/limit/<c:out value="${limit}"/>')</c:if>,
                success: function(json, textStatus, xhr) {
                    var readings = json['readings'];
                    for (var i in readings) {
                        var point = [readings[i].timestamp,readings[i].reading];
                        chart.series[0].addPoint(point);
                    }
                },
                complete: function(json, textStatus, xhr) {
                },
                cache : false
            });
        }

        $(document).ready(function() {
            chart = new Highcharts.Chart({
                chart: {
                    renderTo: 'container',
                    defaultSeriesType: 'spline',
                    zoomType: 'x',
                    spacingRight: 20,
                    events: {
                        load: function(event) {
                            console.log('chart loaded requesting data');
                            requestData();
                        }
                    }
                },
                title: {
                    text: 'Readings Chart Testbed : '
                            .concat('<c:out value="${testbed.name}"/>')
                            .concat(' Node : ')
                            .concat('<c:out value="${node.id}"/>')
                            .concat(' Capability : ')
                            .concat('<c:out value="${capability.name}"/>')
                },
                subtitle: {
                    text: document.ontouchstart === undefined ?
                            'Click and drag in the plot area to zoom in' :
                            'Drag your cursor over the plot to zoom in'
                },
                xAxis: {
                    type: 'datetime',
                    tickPixelInterval: 150,
                    maxZoom: 1000
                },
                yAxis: {
                    title: {
                        text: 'Reading'
                    },
                    min: 0.6,
                    startOnTick: false,
                    showFirstLabel: false
                },
                tooltip: {
                    shared: true
                },
                legend: {
                    enabled: false
                },
                series: [
                    {
                        name: 'Reading value (<c:out value="${capability.unit}"/>,<c:out value="${capability.datatype}"/>)',
                        data: []
                    }
                ]
            });
        });
    </script>

    <title>ÜberDust - Readings Chart Testbed: <c:out value="${testbed.name}"/> <c:out value="${node.id}"/> , Capability
        : <c:out value="${capability.name}"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>
</head>
<body>
<div id="container" style="width: 100%; height: 400px"></div>
<%@include file="/footer.jsp" %>
</body>
</html>