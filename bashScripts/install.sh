#!/bin/bash


if [ $USER == "root" ]
then
	echo -n "Moving scripts to /var/ directorty..."
	cp -r autoReports /var/
	echo "done"
else
	echo "Need to execute as root"
fi
