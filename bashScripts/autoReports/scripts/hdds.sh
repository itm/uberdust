#!/bin/bash
source /var/autoReports/settings

FILE=/tmp/temp

sudo hddtemp /dev/sd?>$FILE

if [ $(grep -e "°" $FILE | wc -l) -eq "0" ]
then

awk '{

if (NF==5){
print $1,$4;
}
else if (NF==4){
print $1,$3;
}

}' $FILE > $FILE.2

else


awk '{

if (NF==4){
print $1,$4;
}
else if (NF==3){
print $1,$3;
}

}' $FILE > $FILE.2


fi


while read line
do
    echo $line > $FILE
    name=$(cut -d " " -f 1 $FILE)
    value=$(cut -d " " -f 2 $FILE)
    name=${name%":"*}
    name=${name#"/dev/"*}
    value=${value%"°C"*}
	#echo $name"=="$value

    addReading "$name:temperature" $value

done < $FILE.2


#for item in $(ls /dev/sd?)
#do
#        smartctl $item --health | grep result
#done


#rm $FILE.2 $FILE
