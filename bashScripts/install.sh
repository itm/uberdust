#!/bin/bash

NAME=autoReports
INSTALL_PATH=/var/
INIT_SCRIPT=/etc/init.d/autoReports

servename_def="uberdust.cti.gr"
testbedid_def="3"
netprefix_def="urn:ctinetwork:"

if [ $USER == "root" ]
then

	#===================================================================
	#INSTALL REQUIRED APPLICATIONS
	apt-get install sensors hddtemp sysstat

	#===================================================================
	#CHECK FOR PREVIOUS INSTALLATION
	if [ -d "$INSTALL_PATH$NAME" ]; then		
		read -p "Directory exists overwritte? (yes/NO) " dooverwrite
		if [ $dooverwrite != "yes" ]
		then
			echo "Exiting..."
			exit
		fi
	fi

	#===================================================================
	#CLEANUP PREVIOUS INSTALLATION
	rm -rf $INSTALL_PATH/autoReports/
	echo -n "Moving scripts to /var/ directorty..."
	cp -r autoReports $INSTALL_PATH
	echo "done"
	echo -n "Changing permissions..."
	chmod u+x $INSTALL_PATH/autoReports/scripts/*
	echo "done"
	read -p "Please provide the server hostname:port (ie uberdust.cti.gr ) : " servername
	read -p "Please provide the testbed id (ie 1 ) : " testbedid
	read -p "Please provide the nerwork prefix (ie urn:ctinetwork: ): " netprefix
	sed "s/host.name/$servername/g" $INSTALL_PATH/autoReports/settings > /tmp/file
	sed "s/testbed.id/$testbedid/g" /tmp/file > /tmp/file2
	sed "s/urn:network:/$netprefix/g" /tmp/file2 > $INSTALL_PATH/autoReports/settings
	
	#===================================================================
	#CREATE THE INIT SCRIPT TO START REPORTING
	EXECUTE_PATH=$INSTALL_PATH/autoReports/execute
	sed -e 's#path.to.execute#$EXECUTE_PATH#' ./autoReports.init > $INIT_SCRIPT
	chmod u+x $INIT_SCRIPT
	update-rc.d autoReports defaults

	#===================================================================
	#CREATE THE BASH SCRIPT TO RUN CONTINOUSLY		
	echo "while true ;" >> $EXECUTE_PATH
	echo "do" >> $EXECUTE_PATH
	for item in $(ls autoReports/scripts/)
	do
		echo "./scripts/$item" >> $EXECUTE_PATH
	done
	echo "sleep 1800" >> $EXECUTE_PATH
	echo "done" >> $EXECUTE_PATH
	chmod u+x $EXECUTE_PATH

	#===================================================================
	#USAGE INFORMATION
	echo "======Completed======"
	echo "To start reporting please run as root"
	echo "$INIT_SCRIPT start "
else
	echo "Need to execute as root"
fi
