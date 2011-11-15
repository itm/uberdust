package eu.uberdust.controller.communication;

import com.google.protobuf.ByteString;
import eu.uberdust.controller.protobuf.CommandProtocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/5/11
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestClient {

    public static void main(final String[] args) throws IOException {

        Socket kkSocket = null;
        PrintWriter out = null;
        String destination = "urn:wisebed:ctitestbed:0x494";
        String payload = "1,FF,1";


        final String macAddress = destination.substring(destination.indexOf("0x") + 2);
        final byte[] macBytes = new byte[2];
        if (macAddress.length() == 4) {
            macBytes[0] = Integer.valueOf(macAddress.substring(0, 2), 16).byteValue();
            macBytes[1] = Integer.valueOf(macAddress.substring(2, 4), 16).byteValue();
        } else if (macAddress.length() == 3) {
            macBytes[0] = Integer.valueOf(macAddress.substring(0, 1), 16).byteValue();
            macBytes[1] = Integer.valueOf(macAddress.substring(1, 3), 16).byteValue();
        }

        System.out.println(Arrays.toString(macBytes));
        System.out.println(ByteString.copyFromUtf8(macAddress).toStringUtf8());
        System.out.println(macAddress);


        byte nn = 94;
        System.out.println(Integer.toHexString(nn));
        System.out.println(Integer.valueOf("94", 16).intValue());


        try {
            kkSocket = new Socket("lime.cti.gr", 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis.");
            System.exit(1);
        }


        CommandProtocol.Command cmd = CommandProtocol.Command.newBuilder()
                .setDestination(destination)
                .setPayload(payload)
                .build();

        System.out.println(cmd.toString());
        cmd.writeTo(kkSocket.getOutputStream());

        out.close();

        kkSocket.close();
    }
}
