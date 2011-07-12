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

import com.google.common.base.*;
import com.google.common.collect.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Process;



//--------------------------------------------------------------------------
// Configuration
//--------------------------------------------------------------------------

    // Authentication credentials and other relevant information used again and again as method parameters

	Splitter csvSplitter                = Splitter.on(",").trimResults().omitEmptyStrings();

	List urnPrefixes					= Lists.newArrayList(csvSplitter.split(System.getProperty("testbed.urnprefixes")));
    List usernames						= Lists.newArrayList(csvSplitter.split(System.getProperty("testbed.usernames")));
    List passwords						= Lists.newArrayList(csvSplitter.split(System.getProperty("testbed.passwords")));

	Preconditions.checkArgument(
		urnPrefixes.size() == usernames.size() && usernames.size() == passwords.size(),
		"The list of URN prefixes must have the same length as the list of usernames and the list of passwords"
	);

    // Endpoint URLs of Authentication (SNAA), Reservation (RS) and Experimentation (iWSN) services
    String snaaEndpointURL 				= System.getProperty("testbed.snaa.endpointurl");
    String rsEndpointURL				= System.getProperty("testbed.rs.endpointurl");
	String sessionManagementEndpointURL	= System.getProperty("testbed.sm.endpointurl");


	// Retrieve Java proxies of the endpoint URLs above
	SNAA authenticationSystem 			= SNAAServiceHelper.getSNAAService(snaaEndpointURL);
	RS reservationSystem				= RSServiceHelper.getRSService(rsEndpointURL);
	SessionManagement sessionManagement = WSNServiceHelper.getSessionManagementService(sessionManagementEndpointURL); 

//--------------------------------------------------------------------------
// Application logic
//--------------------------------------------------------------------------

	//--------------------------------------------------------------------------
	// 1st step: authenticate with the system
	//--------------------------------------------------------------------------

	// build argument types
	List credentialsList = new ArrayList();
	for (int i=0; i<urnPrefixes.size(); i++) {
		
		AuthenticationTriple credentials = new AuthenticationTriple();
		
		credentials.setUrnPrefix(urnPrefixes.get(i));
		credentials.setUsername(usernames.get(i));
		credentials.setPassword(passwords.get(i));
		
		credentialsList.add(credentials);
	}

	// do the authentication
	log.info("Authenticating...");
	List secretAuthenticationKeys = authenticationSystem.authenticate(credentialsList);
	log.info("Successfully authenticated!");




    //--------------------------------------------------------------------------
    // 2nd step: reserve some nodes (here: all nodes)
    //--------------------------------------------------------------------------
    GregorianCalendar cal = new GregorianCalendar();
	long from = (new Date()).getTime();
	cal.setTimeInMillis(from);
        XMLGregorianCalendar timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
	from = (new Date()).getTime() + 30*60*1000;
	cal.setTimeInMillis(from);
        XMLGregorianCalendar timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
	List futurereservationlist = reservationSystem.getReservations(timeFrom,timeTo);


    GregorianCalendar cal = new GregorianCalendar();
	long from = (new Date()).getTime() - 30*60*1000;
	cal.setTimeInMillis(from);
        XMLGregorianCalendar timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
	from = (new Date()).getTime();
	cal.setTimeInMillis(from);
        XMLGregorianCalendar timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
	List previousreservationlist = reservationSystem.getReservations(timeFrom,timeTo);


	if ( futurereservationlist.size() == 0 ){
		if (previousreservationlist.size() !=0 ){
			log.info("Need to flash!");
			Process p  = Runtime.getRuntime().exec("sh flash_default.sh");
			BufferedReader in = new BufferedReader(  
                                new InputStreamReader(p.getInputStream()));
			String line = null;  
            while ((line = in.readLine()) != null) {  
            	System.out.println(line);  
            }
		}
		else {
			log.info("No need to reflash nodes");
		}
	}
	else {
		log.info("Cannot flash, Pending reservations");
	}

