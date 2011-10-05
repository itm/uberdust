<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="testbedId" scope="request" class="java.lang.String"/>
<jsp:useBean id="nodeId" scope="request" class="java.lang.String"/>
<jsp:useBean id="capabilityId" scope="request" class="java.lang.String"/>


<html>

<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script type="text/javascript" src="/js/highcharts.js"></script>
    <script type="text/javascript" src="/js/themes/gray.js"></script>
    <script type="text/javascript">

        var chart1; // globally available

        $(document).ready(function() {

            chart1 = new Highcharts.Chart({

                chart: {

                    renderTo: 'container',

                    type: 'bar'

                },

                title: {

                    text: 'Fruit Consumption'

                },

                xAxis: {

                    categories: ['Apples', 'Bananas', 'Oranges']

                },

                yAxis: {

                    title: {

                        text: 'Fruit eaten'

                    }

                },

                series: [
                    {

                        name: 'Jane',

                        data: [1, 0, 4]

                    },
                    {

                        name: 'John',

                        data: [5, 7, 3]

                    }
                ]

            });

        });
    </script>

    <title>
        Node :<c:out value="${nodeId}"/> , Capability : <c:out value="${capabilityId}"/>, Testbed : <c:out
            value="${testbedId}"/>
    </title>
</head>
<body>
<p>
    Node :<c:out value="${nodeId}"/> , Capability : <c:out value="${capabilityId}"/>, Testbed : <c:out
        value="${testbedId}"/>
</p>

<div id="container" style="width: 100%; height: 400px"></div>
</body>
</html>