#!/bin/bash
source ../settings
unset hosts; declare -A hosts # the -A attributes stands for associative

hosts["hosta"]="192.168.1.1"
hosts["hostb"]="192.168.1.2"
hosts["hostc"]="192.168.1.3"

for host in "${!hosts[@]}"
do
	if [ $HOSTNAME != $host ]
	then
		ip=${hosts[$host]};
		echo $ip

		#get the ping results
		stats=$(ping -c 10 $ip | tail -1 | cut -d " " -f 4)
		avg=$(echo $stats|cut -d "/" -f 2)

		if [ $stats != "" ];then
			echo $stats
			#template : min/avg/max/mdev
			avg=$(echo $stats|cut -d "/" -f 2)

			#echo "$host - $ip - $avg"
			addLinkReading $host "rtt" $avg
		fi
	fi
done
