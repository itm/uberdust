#!/bin/bash
source ../settings

#=============================================
#KERNEL VERSION

kernel_name=$(uname -r)
echo "kernel""="$kernel_name
addStringReading "kernel" $kernel_name



#=============================================
#UPTIME

cuptime=$(uptime)
cuptime=${cuptime%"users"*}
cuptime=${cuptime%","*}
cuptime=${cuptime#*"up "}
cuptime=$(echo $cuptime|sed 's/ /_/g')

echo "uptime""="$cuptime
addStringReading "uptime" $cuptime









