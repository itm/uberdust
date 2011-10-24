all: msp430

msp430:
	@echo "----- Building MSP430 target ------"
	@make -f Makefile.msp430
	@echo "----- Done  ------"

push:
        scp  bin/msp430/TelosBCollectorApp.ihex testbedruntime@gold.cti.gr:/home/testbedruntime/dataCollector/

clean: clean-msp430
	
clean-msp430:
	@make -s -f Makefile.msp430 clean
	@echo "----- MSP430 cleaned ------"	
	
.PHONY: all clean msp430 clean-msp430
