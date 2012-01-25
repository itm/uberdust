#!/usr/bin/python

"""node-capability-ws-consumer.py

Message-based WebSockets client that consumes node/capability updates from 
Uberdust server on http://uberdust.cti.gr.
"""

import sys
import getopt
from twisted.internet import reactor
from autobahn.websocket import connectWS,WebSocketClientFactory, WebSocketClientProtocol

WS_URL = 'ws://uberdust.cti.gr:80/lastreading.ws'
PROTOCOL = []

class NodeCapabilityConsumerProtocol(WebSocketClientProtocol):
	"""
	Node/Capability consumer protocol class.
	"""
	def onOpen(self):
		# on connection establish
		print 'WebSocket Connection to ',WS_URL,' established.'
	
	def onMessage(self, message, binary):
		# on received message
		print 'Message received [',message,'] [',binary,'].'

	def onClose(self,wasClean, code, reason):
		# on close connection
		if(reactor.running):
			print 'Closing connection to ',WS_URL,' ',code,' ',reason
			reactor.stop()
		
def main(argv=None):
	"""Main routine of script"""
	if argv is None:
		argv = sys.argv
	try:

		# parse options and args
		opts, args = getopt.getopt(argv[1:], "", ["help","node=","capability="])
		print "Node/Capability WebSocket consumer."
		for k,v in opts:
			if k == "--help":
				print "A simple python script for consuming readings for a specific Node/Capability pair.\nHit CTRL-C to stop script at any time.\nMust provide all of the parameters listed bellow :"
				print "\t --node={node's URN}, define the  node."
				print "\t --capability={zone's ID}, define the node's zone."
			elif k == "--node":
				node = v
			elif k == "--capability":
				capability = v
		if(not( vars().has_key("node") and vars().has_key("capability"))):
			print >>sys.stderr, "You must specify --node and --capability"
			return -1

		# initialize WebSocketClientFactory object and make connection
		PROTOCOL =  [''.join([str(node),'@',str(capability)])]
		factory = WebSocketClientFactory(WS_URL,None,PROTOCOL)
		factory.protocol = NodeCapabilityConsumerProtocol
		factory.setProtocolOptions(13)
		connectWS(factory)
		reactor.run()		
	except getopt.error, msg:
		print >>sys.stderr, msg
		print >>sys.stderr, "for help use -h or --help"
		return -1

if __name__ == '__main__':
	sys.exit(main())
