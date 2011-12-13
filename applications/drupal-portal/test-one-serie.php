<html>

<?php
    $room = "CTI Room 0.I.1";
    $node1Mac = "0x1cd3";
    $node1Type = "iSense";
    $node1 = $node1Type." ".$node1Mac;
    $nodeUrn1 = "urn:wisebed:ctitestbed:0x1cde";
    $capability1 = "Luminosity";
    $capabilityUrn1 = "urn:wisebed:node:capability:light";
    $unit = "(lux)";
    $maxRows = 1000;
?>

<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
    <script type="text/javascript" src="/uberdust/js/highcharts.src.js"></script>
    <script type="text/javascript" src="/uberdust/js/themes/gray.js"></script>
    <script type="text/javascript" src="/uberdust/js/uberdust-chart.js"></script>
    <script type="text/javascript">
        console.log("load readings data");
        var sequence = {
            name :'<?= $capability1 ?> reading <?= $unit ?>',
            data :[ <?php
            $tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/" . $nodeUrn1 . "/capability/" . $capabilityUrn1 . "/tabdelimited/limit/" . $maxRows);
            $lines = explode("\n", $tabdelimited, $maxRows);
            unset($lines[count($lines) - 1]);
            $firstRow = explode("\t", $lines[0]);
            print("[" . $firstRow[0] . " , " . $firstRow[1] . "]");
            unset($lines[0]);
            foreach ($lines as $thisLine) {
                $row = explode("\t", $thisLine);
                print(",\n[" . $row[0] . " , " . $row[1] . "]");
            }
            ?>
            ]
        };
        divContainer = 'container-<?= $node1Mac . '-' . $capability1 ?>';
        titleText = '<?=$capability1?> chart inside <?= $room?>';
        subtitleText = '<?=$node1?>';
        yaxisText = '<?= $capability1 . " " . $unit ?>';
        console.log("readings data loaded")
        uberdustSeries.push(sequence);
    </script>
</head>

<body>
<div id="container-<?= $node1Mac . '-' . $capability1 ?>" style="width: 100%; height: 400px"></div>
</body>

</html>