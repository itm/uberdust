#!/bin/bash
source /var/autoReports/settings
unset names; declare -A names # the -A attributes stands for associative

names["temp1"]="cpu"

for item in "${!names[@]}"
do
	value=$(sensors | grep $item | awk '{print $2}')
	svalue1=${value%"°C"*}
	svalue2=${svalue1#'+'}
	svalue=$svalue2

	addReading "${names[$item]}:temperature" $svalue
done

