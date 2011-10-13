all: clean JN5139R1

JN5139R1:
	@echo "----- Building for JENNIC 5139R1 target ------"
	@make JENNIC_CHIP=JN5139R1 -s -f Makefile.jennic
	@echo "----- Done  ------"


clean: clean-JN5139R1

clean-JN5139R1:
	@make JENNIC_CHIP=JN5139R1 -s -f Makefile.jennic clean
	@echo "----- JN5139R1 cleaned ------"
	

push:
	scp  bin/JN5139R1/iSenseCollectorApp.bin  testbedruntime@150.140.5.51:/home/testbedruntime/
	scp  bin/JN5139R1/iSenseCollectorApp.bin  testbedruntime@150.140.5.50:/home/testbedruntime/
	scp  bin/JN5139R1/iSenseCollectorApp.bin  testbedruntime@150.140.5.49:/home/testbedruntime/
	scp  bin/JN5139R1/iSenseCollectorApp.bin  testbedruntime@150.140.5.53:/home/testbedruntime/

.PHONY: all clean JN5139R1 clean-JN5139R1
