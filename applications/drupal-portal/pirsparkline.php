<html>

<?php
    $room = "CTI Room 0.I.1";
    $node = "iSense 0x1cd3";
    $nodeUrn = "urn:wisebed:ctitestbed:0x1cde";
    $capability = "Movement";
    $capabilityUrn = "urn:wisebed:node:capability:pir";
    $unit = "(lux)";
    $maxRows = 1000;
?>

<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
    <script type="text/javascript" src="/uberdust/js/jquery.sparkline.min.js"></script>
    <script type="text/javascript">
    <?php
        $tabdelimited = file_get_contents("http://uberdust.cti.gr/rest/testbed/1/node/".$nodeUrn."/capability/.$capabilityUrn./tabdelimited/limit/".$maxRows);
        $lines = explode("\n", $tabdelimited, $maxRows);
        unset($lines[count($lines)-1]);

        $hours = array();
        for ($j=0;$j<10;$j++) {
            $hourt="0".$j;
            $hours[$hourt]=0;
        }
        for ($j=10;$j<24;$j++)
        {
            $hourt="".$j;
            $hours[$hourt]=0;
        }
        $firstRow = explode("\t", $lines[0]);
        $i = date("H",$firstRow[0]);
        $hours[$i]++;
        unset($lines[0]);

        foreach ($lines as $thisLine) {
            $row = explode("\t", $thisLine);
            $i = date("H",$row[0]/1000);
            print($i."\t".date("c",$row[0]/1000)."\n");
            $hours[$i]++;
        }

	    ksort($hours);
	    foreach ($hours as $key => $value) {
		    echo "Hour: $key; Sum: $value<br />\n";
        }

	    $hstring = "";
            foreach ($hours as $key => $value) {
		        $hstring .= $value .",";
	    }
        $hstring = rtrim($hstring, ",");
    ?>
	</script>
    <script type="text/javascript">
    $(function() {
        /** This code runs when everything has been loaded on the page */
        /* Inline sparklines take their values from the contents of the tag */
        $('.inlinesparkline').sparkline(); 
    });
    </script>
</head>
<body>
<span class="inlinesparkline"><?php echo $hstring ?></span>
</body>
</html>