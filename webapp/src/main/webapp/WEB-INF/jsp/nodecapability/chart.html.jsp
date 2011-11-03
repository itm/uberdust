<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbed" scope="request" class="eu.wisebed.wisedb.model.Testbed"/>
<jsp:useBean id="node" scope="request" class="eu.wisebed.wiseml.model.setup.Node"/>
<jsp:useBean id="capability" scope="request" class="eu.wisebed.wiseml.model.setup.Capability"/>


<html>
<%@include file="/header.jsp"%>
<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script type="text/javascript" src=<c:url value="/js/highcharts.js"/>></script>
    <script type="text/javascript" src=<c:url value="/js/themes/gray.js"/>></script>
    <script type="text/javascript">
        var chart;
        $(document).ready(function() {
            chart = new Highcharts.Chart({
                chart: {
                    renderTo: 'container',
                    defaultSeriesType: 'spline',
                    zoomType: 'x',
                    spacingRight: 20
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
                plotOptions: {
                    area: {
                        fillColor: {
                            linearGradient: [0, 0, 0, 300],
                            stops: [
                                [0, Highcharts.getOptions().colors[0]],
                                [1, 'rgba(2,0,0,0)']
                            ]
                        },
                        lineWidth: 1,
                        marker: {
                            enabled: false,
                            states: {
                                hover: {
                                    enabled: true,
                                    radius: 5
                                }
                            }
                        },
                        shadow: false,
                        states: {
                            hover: {
                                lineWidth: 1
                            }
                        }
                    }
                },
                series: [
                    {
                        name: 'Reading value (<c:out value="${capability.unit}"/>,<c:out value="${capability.datatype}"/>)',
                        data: []
                    }
                ]
            });


            $.ajax({
                url: '<c:out value="http://${pageContext.request.serverName}:${pageContext.request.serverPort}/uberdust/rest/testbed/${testbed.id}/node/${node.id}/capability/${capability.name}/json"/>',
                success: function(json) {
                    var series = chart.series[0];

                    //get readings
                    var capability = json['capabilityId'];
                    var node = json['nodeId'];
                    var readings = json['readings'];
                    var data = [];
                    var j = 0;
                    for (var i in readings) {
                        data.push({
                            x : readings[i].timestamp,
                            y : readings[i].reading
                        })
                        if (j == 500) break;
                    }
                    chart.series[0].data = data;
                    chart.redraw();
                },
                cache: false
            });

        });
    </script>

    <title>ÜberDust - Readings Chart Testbed: <c:out value="${testbed.name}"/> <c:out value="${node.id}"/> , Capability
        : <c:out value="${capability.name}"/></title>
</head>
<body>
<div id="container" style="width: 100%; height: 400px"></div>
<%@include file="/footer.jsp"%>
</body>
</html>