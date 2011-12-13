#!/bin/bash
source ../settings

FILE=temp
PREFIX="MemFree"

#execute commands
cat /proc/meminfo | grep $PREFIX > $FILE

#truncate data
VAL=$(awk '{print $2;}' $FILE)

#echo "$PREFIX==$VAL"
addReading $PREFIX $VAL
