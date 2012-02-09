#!/usr/bin/python

"""ctiLights.py.

A simple python string for turning on/off lights of a CTI room.
"""

import sys
import getopt
import httplib

def main(argv=None):
	"""Main routine of script"""
	if argv is None:
		argv = sys.argv
	try:
		
		# parse options and args
		opts, args = getopt.getopt(argv[1:], "", ["help","node=","zone=","ON","OFF"])
		print "Uberdust Lights Python Control Script"
		for k,v in opts:
			if k == "--help":
				print "A simple python script for turning on/off lights of a CTI room. \nMust provide all of the parameters listed bellow :"
				print "\t --node={node's URN}, define the controller node."
				print "\t --zone={zone's ID}, define the node's zone."
				print "\t --ON, turn light on."
				print "\t --OFF, turn light off."
			elif k == "--node":
				node = v
			elif k == "--zone":
				zone = v
			elif k == "--ON":
				state = 1
			elif k == "--OFF":
				state = 0
		if(not( vars().has_key("node") and vars().has_key("zone") and vars().has_key("state"))):
			print >>sys.stderr, "You must specify --node and --zone and the desired state (--ON or --OFF)"
			return -1

		# form rest calls from options args 
		rest = "".join(("/rest/sendCommand/destination/",node,"/payload/1,",str(zone),",",str(state)))
		conn = httplib.HTTPConnection("uberdust.cti.gr")
		print "Connecting to http://uberdust.cti.gr"
		conn.request("GET",rest)
		print "GET ",rest
		response = conn.getresponse()
		if(response.status == 200):		
			print response.read()
		else:
			print response.status,response.reason	
		return 0
	except getopt.error, msg:
		print >>sys.stderr, msg
		print >>sys.stderr, "for help use -h or --help"
		return -1

if __name__ == "__main__":
	sys.exit(main())
