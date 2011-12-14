<html>

<?php
    // room
    $room = "CTI Room 0.I.1";
    $ctiPrefix = "urn:wisebed:ctitestbed:";

    // node 1
    $node1Mac = "0x84";
    $node1type = "iSense";
    $node1 = $node1type . " " . $node1Mac;
    $nodeUrn1 = $ctiPrefix.$node1Mac;

    // node 2
    $node2Mac = "0xddba";
    $node2type = "iSense";
    $node2 = $node2type . " " . $node2Mac;
    $nodeUrn2 = $ctiPrefix.$node2Mac;

    $capability = "Luminosity";
    $capabilityUrn = "urn:wisebed:node:capability:light";
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
            name :'<?= $capability ?> reading <?= $unit ?>',
            data :[ <?php
            $tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/" . $nodeUrn1 . "/capability/" . $capabilityUrn . "/tabdelimited/limit/" . $maxRows);
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
        divContainer = 'container-chart';
        titleText = '<?=$capability?> chart inside <?= $room?>';
        subtitleText = '<?=$node1?>';
        yaxisText = '<?= $capability . " " . $unit ?>';
        console.log("readings data loaded")
        uberdustSeries.push(sequence);
        var sequence = {
            name :'<?= $capabilityUrn ?> reading <?= $unit ?>',
            data :[ <?php
            $tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/" . $nodeUrn2 . "/capability/" . $capabilityUrn . "/tabdelimited/limit/" . $maxRows);
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
        uberdustSeries.push(sequence);
    </script>
</head>

<body>
<div id="container-chart" style="width: 100%; height: 400px"></div>
</body>

</html>