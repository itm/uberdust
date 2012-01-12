import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import eu.uberdust.reading.NodeReading;

import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 12/8/11
 * Time: 1:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketTest {

    public static void main(String[] args) {
        // sample node reading
        NodeReading nodeReading1 = new NodeReading();
        nodeReading1.setTestbedId("1");
        nodeReading1.setNodeId("urn:ctinetwork:carrot_delete_me");
        nodeReading1.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading1.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading1.setReading("1.0");


        NodeReading nodeReading2 = new NodeReading();
        nodeReading2.setTestbedId("1");
        nodeReading2.setNodeId("urn:ctinetwork:carrot_delete_moi");
        nodeReading2.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading2.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading2.setReading("1.0");


        /**
         * WebSocket Call
         */

        final String webSocketUrl = "ws://localhost:8080/uberdust/insertreading.ws";

        // insert node reading using WebSockets

        try {
            InsertReadingWebSocketClient.getInstance().connect(webSocketUrl);

            int counter = 0;
            while (true) {
                System.out.println(counter);
                if (counter % 2 == 0) {
                    nodeReading1.setTestbedId(String.valueOf(counter % 4));
                    InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading1);
                } else {
                    nodeReading2.setTestbedId(String.valueOf(counter % 4));
                    InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading2);
                }
                Thread.sleep(5);
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
