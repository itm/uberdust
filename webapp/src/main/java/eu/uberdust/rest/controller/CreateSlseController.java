package eu.uberdust.rest.controller;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import eu.uberdust.command.CreateSlseCommand;
import eu.uberdust.rest.exception.InvalidTestbedIdException;
import eu.uberdust.rest.exception.TestbedNotFoundException;
import eu.wisebed.wisedb.controller.TestbedController;
import eu.wisebed.wisedb.model.Testbed;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/23/12
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateSlseController extends AbstractRestController {
    /**
     * Testbed persistence manager.
     */
    private TestbedController testbedManager;

    /**
     * Sets testbed persistence manager.
     *
     * @param testbedManager testbed persistence manager.
     */
    public void setTestbedManager(final TestbedController testbedManager) {
        this.testbedManager = testbedManager;
    }

    @Override
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object commandObj, final BindException errors) throws InvalidTestbedIdException,
            TestbedNotFoundException, IOException {

        // set commandNode object
        final CreateSlseCommand command = (CreateSlseCommand) commandObj;

        // a specific testbed is requested by testbed Id
        int testbedId;
        try {
            testbedId = Integer.parseInt(command.getTestbedId());
        } catch (NumberFormatException nfe) {
            throw new InvalidTestbedIdException("Testbed IDs have number format.", nfe);
        }

        // look up testbed
        final Testbed testbed = testbedManager.getByID(testbedId);
        if (testbed == null) {
            // if no testbed is found throw exception
            throw new TestbedNotFoundException("Cannot find testbed [" + testbedId + "].");
        }
        Map<String, Object> refData = new HashMap<String, Object>();

        if (request.getRequestURL().toString().endsWith("createslse")) {
            return new ModelAndView("slse/create.html", refData);

        } else {
            response.setContentType("text/plain");
            final Writer responseOutput;
            try {
                responseOutput = (response.getWriter());
                responseOutput.append("not implemented yet\n");

                final String decodedQuery = URLDecoder.decode(request.getQueryString());


                final int nameStart = decodedQuery.indexOf("name=") + "name=".length();
                final int nameEnd = decodedQuery.indexOf("&", nameStart);
                final String name = decodedQuery.substring(nameStart, nameEnd);


                responseOutput.append("NAME:\n" + name + "\n");

                final int queryStart = decodedQuery.indexOf("elementsQuery=") + "elementsQuery=".length();
                final String queryString = decodedQuery.substring(queryStart);

                responseOutput.append("QUERY:\n" + queryString + "\n");

                Query query = QueryFactory.create(queryString);



                responseOutput.flush();
                responseOutput.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;

        }

    }
}
