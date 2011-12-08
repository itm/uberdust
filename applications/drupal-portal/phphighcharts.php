<html>
<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
    <script type="text/javascript" src="/uberdust/js/highcharts.js"></script>
    <script type="text/javascript" src="/uberdust/js/themes/gray.js"></script>
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
                    text: '<?=$capability?> chart inside <?= $room?>'

                },
                subtitle: {
                    text: '<?=$node?>'
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
                    title: {
                        text: '<?= $capability." ".$unit ?>'
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
                series: [
                    {
                        name: '<?= $capability ?> reading <?= $unit ?>',
                        data: [
<?
$tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/".$nodeUrn."/capability/".$capabilityUrn."/tabdelimited/limit/".$maxRows);
$lines = explode("\n", $tabdelimited, $maxRows);
unset($lines[count($lines)-1]);

$firstRow = explode("\t", $lines[0]);
print("                                 [".$firstRow[0]." , ".$firstRow[1]."]");
unset($lines[0]);

foreach ($lines as $thisLine) {
  $row = explode("\t", $thisLine);
  print(",\n                                 [".$row[0]." , ".$row[1]."]");
}
?>
			      ]
                    }
                ]
            });
        });
    </script>
</head>
<body>
<div id="container" style="width: 100%; height: 400px"></div>

</body>
</html>