#!/bin/bash
source ../settings

cpuusage=$(mpstat | grep "all" | awk '{print $4;}')

echo "cpu:usage""="$cpuusage
addReading "cpu:usage" $cpuusage
