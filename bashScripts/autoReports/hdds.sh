#!/bin/bash
source /var/autoReports/settings

FILE=temp

sudo hddtemp /dev/sd?>$FILE

awk '{

if (NF==4){
print $3,$4;
}
else if (NF==3){
print $2,$3;
}

}' $FILE > $FILE.2



while read line
do
    echo $line > $FILE
    name=$(cut -d " " -f 1 $FILE)
    value=$(cut -d " " -f 2 $FILE)
    name=${name%":"*}
    value=${value%"°C"*}
	#echo $name"=="$value

    addReading "$name:temperature" $value

done < $FILE.2


rm $FILE.2 $FILE
