#!/bin/bash

INSTALL_DIR=/var/
INIT_SCRIPT=/etc/init.d/autoReports

servename_def="uberdust.cti.gr"
testbedid_def="3"
netprefix_def="urn:ctinetwork:"

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


	#echo "10,40 * * * * $INSTALL_DIR/autoReports/execute" >> /var/spool/cron/crontabs/root

	#create the autoReports INIT SCRIPT
echo -e " \
#!/bin/bash \n\
### BEGIN INIT INFO \n\
# Provides:          autoReports periodic logging \n\
# Required-Start:    $remote_fs $syslog \n\
# Required-Stop:     $remote_fs $syslog \n\
# Default-Start:     2 3 4 5 \n\
# Default-Stop:      0 1 6 \n\
# Short-Description: Start daemon at boot time \n\
# Description:       Enable service provided by daemon. \n\
### END INIT INFO \n\
name="autoReports" \n\
case \$1 in \n\
\"start\") \n" > $INIT_SCRIPT

	#start the ssTrigger script
	read -p "Please provide the username to use the screenlock : " userLock
	if [ $userLock != "" ]
	then
		echo "su -l $userLock -c '$INSTALL_DIR/autoReports/python/ssTriger/ssTrigger' >/dev/null& " >> $INIT_SCRIPT
		chown $userLock $INSTALL_DIR/autoReports/python/ssTriger -R
	fi

	echo "/var/autoReports/execute > /var/log/autoReports.log &" >> $INIT_SCRIPT
echo -e " \n\
;;\n\
*) \n\
	echo \"error\"  \n\
esac " >> $INIT_SCRIPT

	chmod u+x $INIT_SCRIPT
	update-rc.d autoReports defaults

	#CREATE THE BASH SCRIPT TO RUN CONTINOUSLY
	echo "while true ;" >> $INSTALL_DIR/autoReports/execute
	echo "do" >> $INSTALL_DIR/autoReports/execute

	for item in $(ls autoReports/scripts/)
	do
		echo "$INSTALL_DIR/autoReports/scripts/$item" >> $INSTALL_DIR/autoReports/execute
	done
	echo "sleep 1800" >> $INSTALL_DIR/autoReports/execute
	echo "done" >> $INSTALL_DIR/autoReports/execute
	chmod u+x $INSTALL_DIR/autoReports/execute


	echo "======Completed======"
	echo "To start reporting please run as root"
	echo "/etc/init.d/autoReports start "
else
	echo "Need to execute as root"
fi
