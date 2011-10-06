package eu.uberdust.controller.communication;

import com.google.protobuf.ByteString;
import eu.uberdust.controller.protobuf.CommandProtocol;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/5/11
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestClient {

    public static void main(String[] args) throws IOException {

        Socket kkSocket = null;
        PrintWriter out = null;

        byte nn = 94;
        System.out.println(Integer.toHexString(nn));
        System.out.println(Integer.valueOf("94", 16).intValue());


     /*   try {
            kkSocket = new Socket("lime.cti.gr", 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis.");
            System.exit(1);
        }


        byte[] destination = new byte[]{0x4, (byte) 0x94};
        byte[] payload = new byte[]{1, 1, 1};
        CommandProtocol.Command cmd = CommandProtocol.Command.newBuilder()
                .setDestination(ByteString.copyFrom(destination))
                .setPayload(ByteString.copyFrom(payload))
                .build();

        System.out.println(cmd.toString());
        cmd.writeTo(kkSocket.getOutputStream());

        out.close();

        kkSocket.close();*/
    }
}
