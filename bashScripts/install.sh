#!/bin/bash

INSTALL_DIR=/var/

if [ $USER == "root" ]
then
	rm -rf $INSTALL_DIR/autoReports/
	echo -n "Moving scripts to /var/ directorty..."
	cp -r autoReports $INSTALL_DIR
	echo "done"
	echo -n "Changing permissions..."
	chmod u+x $INSTALL_DIR/autoReports/scripts/*
	echo "done"
	read -p "Please provide the server hostname:port (ie uberdust.cti.gr ) : " servername
	read -p "Please provide the testbed id (ie 1 ) : " testbedid
	read -p "Please provide the nerwork prefix (ie urn:ctinetwork: ): " netprefix
	sed "s/host.name/$servername/g" $INSTALL_DIR/autoReports/settings > /tmp/file
	sed "s/testbed.id/$testbedid/g" /tmp/file > /tmp/file2
	sed "s/urn:network:/$netprefix/g" /tmp/file2 > $INSTALL_DIR/autoReports/settings

	echo "10,40 * * * * $INSTALL_DIR/autoReports/execute" >> /var/spool/cron/crontabs/root

	for item in $(ls autoReports/scripts/)
	do
		echo "$INSTALL_DIR/autoReports/scripts/$item" >> $INSTALL_DIR/autoReports/execute
	done

	chmod u+x $INSTALL_DIR/autoReports/execute

else
	echo "Need to execute as root"
fi
