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
		--devices=0x9978,0x1539 \
		-p 52033 \
		--nodeurn=urn:wisebed:ctitestbed:0x1b7f \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x997d,0x9977 \
		-p 52033 \
		--nodeurn=urn:wisebed:ctitestbed:0x1b7f \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x1b8a,0x14ea \
		-p 52033 \
		--nodeurn=urn:wisebed:ctitestbed:0x1b7f \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT
		
	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x14d9 \
		-p 52033 \
		--nodeurn=urn:wisebed:ctitestbed:0x1b7f \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

else
	echo "Command : sh flash_otap.sh binfile_path reservation channel"
fi
