package eu.uberdust.restfullapiclient;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class RestClient {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RestClient.class);
    private static final String endpoint = "http://spitfire-project.eu/incontextsensing/link";

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static RestClient ourInstance = null;


    /**
     * UberLogger is loaded on the first execution of UberLogger.getInstance()
     * or the first access to UberLogger.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static RestClient getInstance() {
        synchronized (RestClient.class) {
            if (ourInstance == null) {
                ourInstance = new RestClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private RestClient() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));
    }


    public static void main(final String[] args) {
        RestClient.getInstance().convert("observed_property", "temperature");
    }

    public String convert(final String key, final String value) {


        // Construct data
        String data = null;
        try {
            data = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");

        BufferedReader br = null;

        PostMethod method = new PostMethod(endpoint);
        method.addParameter(key, value);

        try {
            int returnCode = client.executeMethod(method);

            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else {
                br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                String readLine;
                while (((readLine = br.readLine()) != null)) {
                    return readLine;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            method.releaseConnection();
            if (br != null) try {
                br.close();
            } catch (Exception fe) {
            }
        }


        return "error";
    }
}

