package uberdust.controllers;

import com.google.protobuf.ByteString;
import eu.uberdust.controller.protobuf.CommandProtocol;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.DestinationPayloadCommand;
import uberdust.commands.NodeCapabilityCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendCommandController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(SendCommandController.class);

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set commandNode object
        DestinationPayloadCommand command = (DestinationPayloadCommand) commandObj;
        LOGGER.info("command.getDestination() : " + command.getDestination());
        LOGGER.info("command.getPayload() : " + command.getPayload());

        System.out.println("command.getDestination() : " + command.getDestination());
        System.out.println("command.getPayload() : " + command.getPayload());

        Socket kkSocket = null;
        PrintWriter out = null;


        try {
            kkSocket = new Socket("gold.cti.gr", 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);

        } catch (UnknownHostException ex) {
            LOGGER.fatal(ex.getMessage());
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.fatal(ex.getMessage());
            System.out.println(ex.getMessage());
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

        kkSocket.close();

        httpServletResponse.setContentType("text/plain");
        final Writer textOutput = (httpServletResponse.getWriter());
        textOutput.write("Destination : "+ new String(destination) +"\nPayload : "+ new String(payload));

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
