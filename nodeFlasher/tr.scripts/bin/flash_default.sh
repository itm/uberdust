ISERAERIAL=iSerAerial_JN5139R1_16bit.bin
DEFAULT_IMAGE_ISENSE=iSenseCollectorApp.bin
DEFAULT_IMAGE_TELOSB=TelosBCollectorApp.ihex
PROPFILE=../cti.properties
ISENSE_NODES=$(cat nodes_isense)
TELOSB_NODES=$(cat nodes_telosb)
CHANNEL=12

echo "===============FLASHING... ISENSE"
./reserve $PROPFILE 3 0 $ISENSE_NODES
./flash-protobuf $PROPFILE $DEFAULT_IMAGE_ISENSE >> log_isense_wired
echo "===============FLASHING... TELOSB"
./reserve $PROPFILE 3 0 $TELOSB_NODES
./flash-protobuf $PROPFILE $DEFAULT_IMAGE_TELOSB >> log_telosb_wired

echo "===============RESERVING..."
sh reserve_flasher
echo "===============FLASHING SERARIAL"
#sh flash-protobuf $PROPFILE $ISERAERIAL

#sleep 40
FLASHER_KEY=$(cat ../flasher_key)
echo "not flashing wirelessly - not reliable yet"
echo "===============USING MOTAP-21"
#./flash_floor21.sh $DEFAULT_IMAGE_ISENSE $FLASHER_KEY $CHANNEL >> log_otap_21 &
echo "===============USING MOTAP-23"
#./flash_floor23.sh $DEFAULT_IMAGE_ISENSE $FLASHER_KEY $CHANNEL >> log_otap_23 &
echo "===============USING MOTAP-31"
#./flash_floor31.sh $DEFAULT_IMAGE_ISENSE $FLASHER_KEY $CHANNEL >> log_otap_31 &
echo "===============USING MOTAP-33"
#./flash_floor33.sh $DEFAULT_IMAGE_ISENSE $FLASHER_KEY $CHANNEL >> log_otap_33 &
#sleep 600
echo "===============FLASHING COLLECTOR"
sh flash-protobuf $PROPFILE $DEFAULT_IMAGE_ISENSE
