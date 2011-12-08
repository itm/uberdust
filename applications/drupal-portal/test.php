<html>
<head>



<?php
  $room = "CTI Room 0.I.1";
  $node = "iSense 0x1cd3";
  $nodeUrn = "urn:wisebed:ctitestbed:0x1cde";
  $capability = "Luminosity";
  $capabilityUrn = "urn:wisebed:node:capability:light";
  $unit = "(lux)";
  $maxRows = 1000;
?>

<?php
include_once 'testhighcharts.php';
?>
<script type="text/javascript">
	function loadData(chart){
	console.log("loadData");
	window.chart.series[0].data=[
	<?php
		$tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/".$nodeUrn."/capability/".$capabilityUrn."/tabdelimited/limit/".$maxRows);
		$lines = explode("\n", $tabdelimited, $maxRows);
		unset($lines[count($lines)-1]);
		$firstRow = explode("\t", $lines[0]);
		print("[".$firstRow[0]." , ".$firstRow[1]."]");
		unset($lines[0]);

		foreach ($lines as $thisLine) {
			$row = explode("\t", $thisLine);
			print(",\n[".$row[0]." , ".$row[1]."]");
		}
	?>
	];
	window.chart.series[0].name= '<?= $capability ?> reading <?= $unit ?>';
	window.chart.title.text = '<?=$capability?> chart inside <?= $room?>';
	window.chart.subtitle.text = '<?=$node?>';
	window.chart.yAxis.title = '<?= $capability." ".$unit ?>';
	};
</script>
</head>
<body>
<div id="container" style="width: 100%; height: 400px"></div>
</body>


</html>           