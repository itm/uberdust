import java.util.*;
import java.nio.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.wisebed.testbed.api.rs.RSServiceHelper;
import eu.wisebed.api.rs.PublicReservationData;
import eu.wisebed.api.rs.RS;

import eu.wisebed.api.snaa.SNAA;
import eu.wisebed.api.snaa.AuthenticationTriple;
import eu.wisebed.api.snaa.SecretAuthenticationKey;
import eu.wisebed.testbed.api.snaa.helpers.SNAAServiceHelper;

import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import eu.wisebed.api.controller.*;
import eu.wisebed.api.common.*;
import eu.wisebed.api.sm.*;
import eu.wisebed.api.wsn.*;

import de.uniluebeck.itm.tr.util.*;
import de.itm.uniluebeck.tr.wiseml.WiseMLHelper;

import de.uniluebeck.itm.wisebed.cmdlineclient.*;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.*;
import java.util.concurrent.Future;
import com.google.common.collect.*;

import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.*;


//--------------------------------------------------------------------------
// Configuration
//--------------------------------------------------------------------------

	String secretReservationKeys = System.getProperty("testbed.secretreservationkeys");
	String imageToFlashPath = System.getProperty("testbed.image");
	String nodeUrnsToFlash = System.getProperty("testbed.nodeurns");

	String pccHost = System.getProperty("testbed.protobuf.hostname");
	Integer pccPort = Integer.parseInt(System.getProperty("testbed.protobuf.port"));

	String sessionManagementEndpointURL	= System.getProperty("testbed.sm.endpointurl");
	
	SessionManagement sessionManagement = WSNServiceHelper.getSessionManagementService(sessionManagementEndpointURL);


//--------------------------------------------------------------------------
// Application logic
//--------------------------------------------------------------------------

	String wsnEndpointURL = null;
	try {
		wsnEndpointURL = sessionManagement.getInstance(
				helper.parseSecretReservationKeys(secretReservationKeys),
				"NONE"
		);
	} catch (UnknownReservationIdException_Exception e) {
		log.warn("There was not reservation found with the given secret reservation key. Exiting.");
		System.exit(1);
	}

	log.info("Got a WSN instance URL, endpoint is: {}", wsnEndpointURL);
	WSN wsnService = WSNServiceHelper.getWSNService(wsnEndpointURL);
	final WSNAsyncWrapper wsn = WSNAsyncWrapper.of(wsnService);

	ProtobufControllerClient pcc = ProtobufControllerClient.create(pccHost, pccPort, helper.parseSecretReservationKeys(secretReservationKeys));
	pcc.addListener(new ProtobufControllerClientListener() {
		public void receive(List msg) {
			// nothing to do
		}
		public void receiveStatus(List requestStatuses) {
			wsn.receive(requestStatuses);
		}
		public void receiveNotification(List msgs) {
			for (int i=0; i<msgs.size(); i++) {
				log.info(msgs.get(i));
			}
		}
		public void experimentEnded() {
			log.info("Experiment ended");
			System.exit(0);
		}
		public void onConnectionEstablished() {
			log.debug("Connection established.");
		}
		public void onConnectionClosed() {
			log.debug("Connection closed.");
		}
	});
	pcc.connect();

	// retrieve reserved node URNs from testbed
	List nodeURNs;
	if (nodeUrnsToFlash != null && !"".equals(nodeUrnsToFlash)) {
		nodeURNs = Lists.newArrayList(nodeUrnsToFlash.split(","));
	} else {
		nodeURNs = WiseMLHelper.getNodeUrns(wsn.getNetwork().get(), new String[]{});
	}
	log.info("Selected the following node URNs: {}", nodeURNs);

    log.info("Flashing nodes...");

	List programIndices;
	List programs;

	// flash isense nodes
    programIndices = new ArrayList();
    programs = new ArrayList();
    for (int i=0; i<nodeURNs.size(); i++) {
        programIndices.add(0);
    }
    programs.add(helper.readProgram(
            imageToFlashPath,
            "",
            "",
            "iSense",
            "1.0"
    ));

	Future flashFuture = wsn.flashPrograms(nodeURNs, programIndices, programs, 3, TimeUnit.MINUTES);
	JobResult flashJobResult = flashFuture.get();
	log.info("{}", flashJobResult);
	if (flashJobResult.getSuccessPercent() < 100) {
		System.out.println("Not all nodes could be flashed. Exiting");
		log.info("{}", flashFuture.get());
		System.exit(1);
	}
	
	log.info("Closing connection...");
	pcc.disconnect();
	
	log.info("Shutting down...");
	System.exit(0);
