    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
    <script type="text/javascript" src="../js/highcharts.js"></script>
    <script type="text/javascript" src="../js/themes/gray.js"></script>
    <script type="text/javascript">
    	var chart;		
    	$(document).ready(function() {
    			console.log("ready");
            chart = new Highcharts.Chart({
                chart: {
                    renderTo: 'container',
                    defaultSeriesType: 'spline',
                    zoomType: 'x',
                    spacingRight: 20,
                    events: {
                        load: function(event) {
                        	console.log('chart loaded requesting data');
                        }
                    }
                },
                title: {
                	text: ''
                },
                subtitle: {
                	text: ''
                },
                xAxis: {
                    type: 'datetime',
                    dateTimeLabelFormats: {
                        day: '%e %b',
                        month: '%e %b'
                    },
                    tickPixelInterval: 400,
                    maxZoom: 1000
                },
                yAxis: {
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
		 series: {
		    lineWidth: 1,
		    marker: {
		       enabled: false,
		       states: {
		          hover: {
		             enabled: true,
		             radius: 5
		          }
		       }
		    }
		 }
	      },
	      series: [{}]
            });
            loadData();
        });
    </script>
