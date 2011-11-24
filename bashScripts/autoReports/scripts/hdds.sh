#!/bin/bash
source /var/autoReports/settings

FILE=temp

sudo hddtemp /dev/sd?>$FILE

awk '{

if (NF==4){
print $2,$4;
}
else if (NF==3){
print $1,$3;
}

}' $FILE > $FILE.2



while read line
do
    echo $line > $FILE
    name=$(cut -d " " -f 1 $FILE)
    value=$(cut -d " " -f 2 $FILE)
    name=${name%":"*}
    name=${name#"/dev/"*}
    value=${value%"°C"*}
	echo $namea"=="$value

    addReading "$name:temperature" $value

done < $FILE.2


for item in $(ls /dev/sd?)
do
        smartctl $item --health | grep result
done


rm $FILE.2 $FILE
