#!/bin/bash
source /var/autoReports/settings

FILE=temp

df -T | grep -e "tmpfs" -v -e "Filesystem" -v -e "debugfs" -v > $FILE
awk '{print $1" "$6;}' $FILE > $FILE.1


while read line
do
    echo $line > $FILE
    name=$(cut -d " " -f 1 $FILE)
    value=$(cut -d " " -f 2 $FILE)
    name=${name#"/dev/"*}
    value=${value%"%"*}
    #echo $name"=="$value

	#create url
	addReading "$name:usage" $value

done < $FILE.1


#cleanup
rm $FILE.1 $FILE


