package eu.uberdust.util;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import eu.wisebed.wisedb.controller.LastNodeReadingController;
import eu.wisebed.wisedb.model.LastNodeReading;
import eu.wisebed.wisedb.model.Semantic;
import eu.wisebed.wisedb.model.Slse;
import eu.wisebed.wiseml.model.setup.Capability;
import eu.wisebed.wiseml.model.setup.Node;
import org.apache.log4j.Logger;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/24/12
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class RdfConverter {
    static RdfConverter instance = null;
    private static LastNodeReadingController lastNodeReadingManager;
    protected Map<String, Map<String, Double>> sensorValues;

    public static RdfConverter getInstance() {

        if (instance == null) {
            instance = new RdfConverter();
        }
        return instance;
    }

    private static Map rdfMapping = new HashMap<String, String>();
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RdfConverter.class);

    public RdfConverter() {

        rdfMapping.put("urn:wisebed:node:capability:light", "luminance");
        rdfMapping.put("urn:wisebed:node:capability:temperature", "Temperature");
        rdfMapping.put("urn:wisebed:node:capability:pir", "pir");
        rdfMapping.put("urn:wisebed:node:capability:humidity", "relativeHumidity");
        rdfMapping.put("urn:wisebed:node:capability:light1", "Lamp1");
        rdfMapping.put("urn:wisebed:node:capability:light2", "Lamp2");
        rdfMapping.put("urn:wisebed:node:capability:light3", "Lamp3");
        rdfMapping.put("urn:wisebed:node:capability:light4", "Lamp4");
        rdfMapping.put("urn:wisebed:node:capability:co", "carbonMonoxide");
        rdfMapping.put("urn:wisebed:node:capability:co2", "catbonDioxide");
        rdfMapping.put("urn:wisebed:node:capability:ch4", "methane");
        rdfMapping.put("urn:wisebed:node:capability:barometricpressure", "barometricPressure");
        rdfMapping.put("urn:wisebed:node:capability:ir", "infraredLuminosity");
        rdfMapping.put("urn:wisebed:node:capability:batterycharge", "batteryCharge");

    }

    public static void setLastNodeReadingManager(LastNodeReadingController lastNodeReadingManager) {
        RdfConverter.lastNodeReadingManager = lastNodeReadingManager;
    }

    public static String map(String key) {
        return (String) rdfMapping.get(key);
    }

    public static String getRdf(final Node node, final String uri, final List<Semantic> semanticList) {

        final StringBuilder rdfOutput = new StringBuilder();
        //headers
        rdfOutput.append("@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .\n" +
                "@prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> .\n" +
                "@prefix dc: <http://purl.org/dc/terms/> . \n" +
                "@prefix spitfire: <http://spitfire-project.eu/cc/spitfireCC_n3.owl#> . \n" +
                "\n" +
                "<#>\n" +
                "\n");
//        rdfOutput.append("@prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> .\n" +
//                "@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .\n" +
//                "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
//                "@prefix sweet: <http://sweet.jpl.nasa.gov/2.2/sweetAll.owl#> .\n" +
//                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
//                "@prefix : <http://spitfire.ibr.cs.tu-bs.de/static/ontology.owl#> .\n" +
//                "\n" +
//                "<#>\n" +
//                "\n");

        final Model m = ModelFactory.createDefaultModel();
//        rdfOutput.append("@prefix ").append("\n");
//        rdfOutput.append("@prefix ns0: <http://purl.org/rss/1.0/modules/dcterms/>.").append("\n");
//        rdfOutput.append("@prefix ns1: <http://xmlns.com/foaf/0.1/>.").append("\n");
//        rdfOutput.append("@prefix ns2: <http://spitfire.ibr.cs.tu-bs.de/static/descriptions#>.").append("\n");
//        rdfOutput.append("@prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>.").append("\n");
//        rdfOutput.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.").append("\n");
//        rdfOutput.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.").append("\n");
//        rdfOutput.append("@prefix ns4: <http://sweet.jpl.nasa.gov/2.2/reprSciUnits.owl#>.").append("\n");
//        rdfOutput.append("@prefix ns5: <http://purl.oclc.org/NET/ssnx/ssn#>.").append("\n");
//        rdfOutput.append("\n").append("<#>").append("\n").append("\n");

        Semantic locatedin = null;
        for (final Semantic semantic : semanticList) {
            rdfOutput.append("\t " + semantic.getSemantic() + " :" + semantic.getValue() + " ;\n");
            if (semantic.getSemantic().equals(":locatedIn")) {
                locatedin = semantic;
            }

        }


        //capabilities
        boolean first = true;
        for (final Capability capability : node.getCapabilities()) {
            if (!first) {
                rdfOutput.append(";\n");
            }
            first = false;
            rdfOutput.append("" +
                    " ssn:attachedSystem [\n" +
                    "  a ssn:Sensor ;\n" +
                    "  ssn:observedProperty <" + capability.getDescription() + "> ; \n" +
                    "  dul:hasValue \"" + lastNodeReadingManager.getByID(node, capability).getReading() + "\";\n"
            );
            if (locatedin != null) {
                // rdfOutput.append("\t\tssn:featureOfInterest :" + locatedin.getValue() + " ;\n");
            }
            rdfOutput.append("\t\t]");
        }
        rdfOutput.append(".");

        StringWriter output;
        try {
            final StringReader stringReader = new StringReader(rdfOutput.toString());

            m.read(stringReader, uri, "N3");
            output = new StringWriter();
            m.write(output);
        } catch (Exception e) {
            e.printStackTrace();
            return rdfOutput.toString();
        }
        return output.toString();  //To change body of created methods use File | Settings | File Templates.
    }

    public static String encode(String in) {
        in = in.replaceAll("_", "\\_U");
        in = in.replaceAll("/", "\\_S");
        in = in.replaceAll(":", "\\_C");
        return in;
    }

    public static String getSlseRdf(final Slse slse, final String uri) {

        //map to host all latest sensor readings for the slse
        final Map<String, Double> sensorValues = new HashMap<String, Double>();

        final StringBuilder rdfOutput = new StringBuilder();
        //headers
        rdfOutput.append("@prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> .\n" +
                "@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .\n" +
                "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix sweet: <http://sweet.jpl.nasa.gov/2.2/sweetAll.owl#> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix : <http://spitfire.ibr.cs.tu-bs.de/static/ontology.owl#> .\n" +
                "\n" +
                "<#>\n" +
                "\n");


        //Get the nodes that compose the slse
        final List<Node> nodes = slse.getNodes();

        //iterate the nodes for their readings
        for (final Node node : nodes) {
            //get all the capabilities for the node
            final List<Capability> capabilities = node.getCapabilities();

            if (capabilities != null) {
                //iterate all capabilities
                for (final Capability capability : capabilities) {
                    //get the latest reading for each capability
                    final LastNodeReading lastReading = lastNodeReadingManager.getByID(node, capability);
                    if (lastReading == null) {
                        continue;
                    }
                    //aggregate if not the only one
                    if (sensorValues.containsKey(lastReading.getCapability().getName())) {
                        Double reading = (sensorValues.get(lastReading.getCapability().getName()) + lastReading.getReading()) / 2;
                        sensorValues.put(lastReading.getCapability().getName(), reading);
                    } else {
                        sensorValues.put(lastReading.getCapability().getName(), lastReading.getReading());
                    }
                }
            }
        }

        //Add all aggregated sensor readings to the rdf description
        boolean first = true;
        for (final String sensor : sensorValues.keySet()) {
            //for formating
            if (!first) {
                rdfOutput.append(";\n");
            }
            first = false;
            //check if the sensor has a value
            if (sensorValues.get(sensor) != null) {
                rdfOutput.append("" +
                        "\tssn:attachedSystem [\n" +
                        "\t\ta ssn:Sensor ;\n" +
                        "\t\tssn:observedProperty :" + RdfConverter.getInstance().map(sensor) + " ;\n" +
                        "\t\tdul:hasValue \"" + sensorValues.get(sensor) + "\";\n");
                rdfOutput.append("\t\t]");
            }
        }
        rdfOutput.append(".");


        //JENA modeling
        final Model m = ModelFactory.createDefaultModel();
        final StringReader stringReader = new StringReader(rdfOutput.toString());
        m.read(stringReader, uri, "N3");
        final StringWriter output = new StringWriter();
        m.write(output);

        return output.toString();  //To change body of created methods u
    }
}
