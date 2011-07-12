#/bin/bash
if [ $# -eq 3 ]
then
	JAR_PATH=motap-wisebed-0.7-SNAPSHOT.one-jar.jar
	BINFILE=$1
	RESKEY=$2
	CHANNEL=$3
	SESENDPOINT=http://hercules.cti.gr:8888/sessions/
	PORT=8089

	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x1b76,0x1b85 \
		-p 52023 \
		--nodeurn=urn:wisebed:ctitestbed:0x6699 \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x1c96,0x1cd6 \
		-p 52023 \
		--nodeurn=urn:wisebed:ctitestbed:0x6699 \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT


	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x14d4 \
		-p 52023 \
		--nodeurn=urn:wisebed:ctitestbed:0x6699 \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

else
	echo "Command : sh flash_otap.sh binfile_path reservation channel"
fi
