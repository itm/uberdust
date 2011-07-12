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
		--devices=0x997a,0x1538 \
		-p 52031 \
		--nodeurn=urn:wisebed:ctitestbed:0x99ad \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x997f,0x152f \
		-p 52031 \
		--nodeurn=urn:wisebed:ctitestbed:0x99ad \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT


	java -jar \
		$JAR_PATH \
		--binary=$BINFILE \
		-c $CHANNEL \
		--devices=0x995d,0x997c \
		-p 52031 \
		--nodeurn=urn:wisebed:ctitestbed:0x99ad \
		--reservationkeys=urn:wisebed:ctitestbed:,$RESKEY \
		--sessionmanagement=$SESENDPOINT

else
	echo "Command : sh flash_otap.sh binfile_path reservation channel"
fi
