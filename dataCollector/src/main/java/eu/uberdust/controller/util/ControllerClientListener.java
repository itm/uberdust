package eu.uberdust.controller.util;

import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClientListener;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.controller.RequestStatus;

import javax.jws.WebParam;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/3/11
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControllerClientListener implements ProtobufControllerClientListener {

    @Override
    public void onConnectionClosed() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onConnectionEstablished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void experimentEnded() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void receive(@WebParam(name = "msg", targetNamespace = "") List<Message> messages) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void receiveNotification(@WebParam(name = "msg", targetNamespace = "") List<String> strings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void receiveStatus(@WebParam(name = "status", targetNamespace = "") List<RequestStatus> requestStatuses) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

/*

{
           public void receive(List<Message> msg) {
               for (Message message : msg) {

                   final String str = BeanShellHelper.toString(message, true);
                   if (str.contains("iSense::0x1ccd EM_E 1")) {
                       System.out.println(BeanShellHelper.toString(message, true));
                       // send();
                   }
               }
           }

           public void receiveStatus(List requestStatuses) {
               wsn.receive(requestStatuses);
           }

           public void receiveNotification(List<String> msgs) {
               for (String msg : msgs) {
                   System.out.println(msg);
               }
           }

           public void experimentEnded() {
               System.out.println("Experiment ended");
           }

           public void onConnectionEstablished() {
               System.out.println("Connection established.");
           }

           public void onConnectionClosed() {
               System.out.println("Connection closed.");
           }
       });


   }
*/
