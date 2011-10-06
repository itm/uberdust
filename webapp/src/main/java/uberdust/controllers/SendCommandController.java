package uberdust.controllers;

import com.google.protobuf.ByteString;
import eu.uberdust.controller.protobuf.CommandProtocol;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;
import uberdust.commands.DestinationPayloadCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.Socket;


public class SendCommandController extends AbstractRestController {

    private static final Logger LOGGER = Logger.getLogger(SendCommandController.class);

    @Override
    protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  Object commandObj, BindException e) throws Exception {

        // set commandNode object
        DestinationPayloadCommand command = (DestinationPayloadCommand) commandObj;
        LOGGER.info("command.getDestination() : " + command.getDestination());
        LOGGER.info("command.getPayload() : " + command.getPayload());

        try {

            // prepare socket for connection and writer
            Socket kkSocket = new Socket("gold.cti.gr", 4444);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);

            // parse destination and payload from url and split them
            final String[] destination = command.getDestination().split(",");
            final String[] payload = command.getPayload().split(",");
            if (destination.length > 2 || payload.length > 3) {
                throw new Exception("Invalid destination or payload arguments count");
            }


            // get bytes from destination and payload
            final byte[] payloadBytes = new byte[payload.length];
            for (int i = 0; i < destination.length; i++) {
                int destIntValue = Integer.valueOf(destination[i], 16);
                LOGGER.info(i + ".destIntValue" + destIntValue);
                logger.info(i + ".destIntValue" + destIntValue);
            }
            for (int i = 0; i < payload.length; i++) {
                payloadBytes[i] = (byte) Integer.valueOf(payload[i], 16).intValue();
            }

            // build command and send it through the socket stream
            CommandProtocol.Command cmd = CommandProtocol.Command.newBuilder()
                    .setDestination(command.getDestination())
                    .setPayload(ByteString.copyFrom(payloadBytes))
                    .build();
            cmd.writeTo(kkSocket.getOutputStream());

            // close stream after command execution
            out.close();
            kkSocket.close();

            httpServletResponse.setContentType("text/plain");
            final Writer textOutput = (httpServletResponse.getWriter());
            textOutput.write("OK . Destination : " + command.getDestination() + "\nPayload : " + command.getPayload());

            return null;
        } catch (Exception ex) {
            LOGGER.fatal(ex.getMessage());
            throw new Exception(ex.getMessage());
        }

    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
