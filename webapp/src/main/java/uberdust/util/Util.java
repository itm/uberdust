package uberdust.util;

import eu.wisebed.wisedb.model.LinkReading;
import eu.wisebed.wisedb.model.NodeReading;

import java.util.Date;
import java.util.Set;

public class Util {

    public static Date getLastNodeReadingRecordedDate(final Set<NodeReading> nodeReadings) {
        if (nodeReadings.isEmpty()) return null;
        Date latestDate = nodeReadings.iterator().next().getTimestamp();
        for (NodeReading nodeReading : nodeReadings) {
            if (nodeReading.getTimestamp().after(latestDate)) {
                latestDate = nodeReading.getTimestamp();
            }
        }
        return latestDate;
    }

    public static Date getLastLinkReadingRecordedDate(final Set<LinkReading> linkReadings) {
        if (linkReadings.isEmpty()) return null;
        Date latestDate = linkReadings.iterator().next().getTimestamp();
        for (LinkReading linkReading : linkReadings) {
            if (linkReading.getTimestamp().after(latestDate)) {
                latestDate = linkReading.getTimestamp();
            }
        }
        return latestDate;
    }

//    public static String getUberdustUrl(final String testbedId, final String nodeId,final String capablityId) {
//        // set basic url
//        StringBuilder builder = new StringBuilder();
//        builder.append("/rest/testbed");
//
//        if (testbedId != null && !testbedId.isEmpty()) {
//            builder.append("/" + testbedId);
//        }
//
//        if (nodeId != null && !nodeId.isEmpty()) {
//            builder.append("/node");
//            builder.append("/" + nodeId);
//        }
//
//        if (capablityId != null && !capablityId.isEmpty()) {
//            builder.append("/capability");
//            builder.append("/" + capablityId);
//        }
//
//        // return string builder
//        return builder.toString();
//    }
}
