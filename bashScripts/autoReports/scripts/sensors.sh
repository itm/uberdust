#!/bin/bash
source ../settings
unset names; declare -A names # the -A attributes stands for associative
unset types; declare -A types # the -A attributes stands for associative

names["temp3"]="cpu"
names["fan1"]="fan1"
names["fan2"]="fan2"
names["fan3"]="fan3"

types["temp3"]="temperature"
types["fan1"]="rpm"
types["fan2"]="rpm"
types["fan3"]="rpm"

for item in "${!names[@]}"
do
        value=$(sensors | grep $item | awk '{print $2}')
        echo $value
        svalue1=${value%"°C"*}
        svalue2=${svalue1#'+'}
        svalue=$svalue2

        echo "${names[$item]}:${types[$item]}=$svalue"
        addReading "${names[$item]}:temperature" $svalue
done



