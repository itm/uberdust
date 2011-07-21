all: clean JN5139R1

JN5139R1:
	@echo "----- Building for JENNIC 5139R1 target ------"
	@make JENNIC_CHIP=JN5139R1 -s -f Makefile.jennic
	@echo "----- Done  ------"


clean: clean-JN5139R1

clean-JN5139R1:
	@make JENNIC_CHIP=JN5139R1 -s -f Makefile.jennic clean
	@echo "----- JN5139R1 cleaned ------"
	
.PHONY: all clean JN5139R1 clean-JN5139R1
