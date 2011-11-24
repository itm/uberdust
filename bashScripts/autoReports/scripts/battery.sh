#!/bin/bash
source /var/autoReports/settings

#execute commands
VAL=$(acpi | cut -d " " -f 4)

#truncate data
charge=${VAL%"%,"*}

addReading "charge" $charge

