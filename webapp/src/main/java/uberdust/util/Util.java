package uberdust.util;

import eu.wisebed.wisedb.model.NodeReading;
import eu.wisebed.wiseml.model.setup.Node;

import java.util.Date;
import java.util.Set;

public class Util {

  public static Date lastNodeReadingRecordedDate(final Set<NodeReading> nodeReadings) {
      if(nodeReadings.isEmpty()) return null;
      Date latestDate = nodeReadings.iterator().next().getTimestamp();
      for(NodeReading nodeReading : nodeReadings) {
          if(nodeReading.getTimestamp().after(latestDate)){
              latestDate = nodeReading.getTimestamp();
          }
      }
      return latestDate;
  }

    // TODO Make a URL construction method

}
