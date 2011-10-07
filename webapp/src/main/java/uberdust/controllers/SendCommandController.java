package uberdust.controllers;

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
            final Socket kkSocket = new Socket("gold.cti.gr", 4444);
            final PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);

            // build command and send it through the socket stream
            final CommandProtocol.Command cmd = CommandProtocol.Command.newBuilder()
                    .setDestination(command.getDestination())
                    .setPayload(command.getPayload())
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
            LOGGER.fatal(ex);
            throw new Exception(ex.getMessage());
        }

    }

    @ExceptionHandler(Exception.class)
    public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        String formattedErrorForFrontEnd = exception.getCause().getMessage();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, formattedErrorForFrontEnd);
    }
}
