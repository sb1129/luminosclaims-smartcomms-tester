package com.gb.smartcomms;

import com.gb.smartcomms.domain.Appliance;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;

/**
 * Utility class to support SmartComms ReSTful operations calls.
 *
 * @author tlizak
 * <p>
 * high jacked by Phil J
 */
public class SmartCommsRESTSupport {
    private static final Logger LOG = Logger.getLogger(SmartCommsRESTSupport.class);

    private static final String POST = "POST";
    private static final String GET = "GET";
    private Properties properties = null;

    public SmartCommsRESTSupport() {
    }

    /**
     * @param clientResponse client response from generateDraft operation.
     * @return draft string
     */
    private static String retrieveDraftFromResponse(
            ClientResponse clientResponse) throws Exception {

        LOG.info("retrieveDraftFromResponse");

        String response = null;
        try {
            String responseString = null;
            if (clientResponse != null && SmartCommsConfig.Numbers.TWO_HUNDRED == clientResponse.getStatus()) {
                responseString = clientResponse.getEntity(String.class);
            } else {
                String status = "";
                if (clientResponse != null) {
                    status = "\n With status: " + clientResponse.getClientResponseStatus();
                }
                throw new Exception("Response received from SmartComms appliance does not contain document draft" + status);
            }

            LOG.debug("draft payload returned from smartcomm [" + responseString + "]");

            final InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseString));

            JAXBContext jContext = JAXBContext.newInstance("com.gb.smartcomms");
            Unmarshaller m = jContext.createUnmarshaller();
            ReviewCase reviewCase = (ReviewCase) m.unmarshal(is);

            response = reviewCase.getData();

            LOG.debug("draft payload returned from smartcomm (decoded response string) [" + Base64.decode(reviewCase.getData()) + "]");

        } catch (JAXBException e) {
            throw new Exception("Error when parsing response from generateDraft operation", e);
        }
        return response;
    }

    public String createTransactXML(String xmlPayload) {

        LOG.info("createTransactXML");

        String draftData = xmlPayload.replace("&", "&amp;");
        return new String(Base64.encode(draftData));
    }

    public Properties loadProperties(String fileName) {
        properties = new Properties();

        try ( final InputStream input = getClass().getClassLoader().getResourceAsStream(fileName) ) {
            properties.load(input);
            properties.keySet().forEach(key -> LOG.info(key + " = " + properties.getProperty((String) key)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }

    public JSONObject generateDraftRequest(final String transactXML) {

        LOG.info("generateDraftRequest");

        String templateSelectorID = properties.getProperty("TemplateSelectorID");

        JSONObject documentRequest = new JSONObject();

        documentRequest.put("batchConfigResId", templateSelectorID);
        documentRequest.put("transactionData", transactXML);
        documentRequest.put("transactionRange", "1");
        documentRequest.put("properties", new JSONArray());
        return documentRequest;
    }

    /**
     * @param corro
     * @return JSON response.
     */
    public String sendGenerateDraftRequest(final JSONObject corro) throws Exception {

        LOG.info("sendGenerateDraftRequest");

        String smartcommApplianceURL = properties.getProperty("SmartCommsApplianceURL");
        smartcommApplianceURL += properties.getProperty("GenerateDraftURL");

        final ClientResponse clientResponse = sendReSTRequest(POST, corro, smartcommApplianceURL, "Draft Generation", MediaType.APPLICATION_XML, false);

        String response;
        if (clientResponse != null && SmartCommsConfig.Numbers.TWO_HUNDRED == clientResponse.getStatus()) {
            response = retrieveDraftFromResponse(clientResponse);
        } else {
            throw new Exception(getErrorMessageWithResponseStatus("Draft Generation failed for URL " + smartcommApplianceURL, clientResponse));
        }

        return response;
    }

    private Client getOldClient() {
        LOG.info("getOldClient");

        final ClientConfig clientConfig = new DefaultClientConfig();

        final int timeOut = Integer.parseInt(properties.getProperty("ServiceTimeOut")) * SmartCommsConfig.Numbers.THOUSAND;
        clientConfig.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeOut);
        clientConfig.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeOut);

        return Client.create(clientConfig);
    }

    /**
     * @param requestOperation GET or POST
     * @param objectToSend     JSONObject to send - expected null for GET operation
     * @param url              - url specific for the operation
     * @param operationName    operation name
     * @param accept           the type of response expected for request
     * @param failGently       - fails by logging an error instead of throwing stack trace. If
     *                         method is by ajax call it should be set to true as otherwise it
     *                         will kill the session.
     * @return Response from the call.
     */
    private ClientResponse sendReSTRequest(
            String requestOperation,
            JSONObject objectToSend,
            String url,
            String operationName,
            String accept,
            boolean failGently) throws Exception {

        LOG.info("sendReSTRequest");


        Client client = getOldClient();

        WebResource webResource = createWebResourceWithAuthentication(client, url);

        final Builder builder = webResource.getRequestBuilder()
                .type(MediaType.APPLICATION_JSON)
                .accept(accept);

        ClientResponse clientResponse = null;

        try {
            if (POST.equals(requestOperation)) {
                clientResponse = builder.post(ClientResponse.class, objectToSend.toString());
            } else {
                clientResponse = builder.get(ClientResponse.class);
            }
        } catch (ClientHandlerException e) {
            if (failGently) {
                LOG.error("Exception when trying to call " + operationName + " operation on URL: " + url, e);
            } else {
                throw new Exception("Exception when trying to call " + operationName + " operation on URL: " + url, e);
            }
        }

        return clientResponse;

    }

    /**
     * Method to create Web Resource and add OAuth 1.0 authentication to it.
     *
     * @param client - Jersey client.
     * @param url    - the operation URL
     * @return WebResource with OAuth 1.0 authentication added.
     */
    private WebResource createWebResourceWithAuthentication(
            Client client,
            String url) {

        LOG.info("createWebResourceWithAuthentication");

        WebResource webResource = client.resource(url);

        if (LOG.isDebugEnabled()) {
            final LoggingFilter loggingFilter = new LoggingFilter(
                    java.util.logging.Logger.getLogger(SmartCommsRESTSupport.class.getName()));
            webResource.addFilter(loggingFilter);
        }

        String customerKey = properties.getProperty(SmartCommsConfig.SC_OAUTH_CUSTOMER_KEY);
        String customerSecret = properties.getProperty(SmartCommsConfig.SC_OAUTH_CUSTOMER_SECRET);
        String consumerUser = properties.getProperty(SmartCommsConfig.SC_OAUTH_USERID);

        String signatureMethod = "HMAC-SHA256";

        OAuthParameters authParams = new OAuthParameters().signatureMethod(signatureMethod).consumerKey(customerKey + '!' + consumerUser).version("1.0");
        authParams.setNonce();
        authParams.setTimestamp();

        final OAuthSecrets authSecrets = new OAuthSecrets().consumerSecret(customerSecret);

        final OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), authParams, authSecrets);
        webResource.addFilter(filter);
        return webResource;
    }

    /**
     * @param reader - reader storing document draft populated from the HTTPRequest.
     * @return status of completion of the operation
     */
    public String saveAndSubmitDraft(String draft) throws Exception {

        LOG.info("saveAndSubmitDraft");

        System.setProperty("https.proxyHost", "cyamproxy.gbapres.local");
        System.setProperty("https.proxyPort", "3128");


        String resultMessage = "";

        String tenancyURL = properties.getProperty(SmartCommsConfig.SMARTCOMMS_SERVER_URL);
        tenancyURL += properties.getProperty("SubmitDraftURL");
        ClientResponse clientResponse = sendReSTRequest(POST, createSubmissionRequest(draft), tenancyURL, "Submit Draft", MediaType.APPLICATION_JSON, true);

        if (clientResponse != null && SmartCommsConfig.Numbers.TWO_HUNDRED_AND_ONE == clientResponse.getStatus()) {
            if (!clientResponse.getHeaders().isEmpty()) {
                String jobURL = clientResponse.getHeaders().get("location").get(0);
                resultMessage = checkJobStatus(jobURL);
            } else {
                resultMessage = "Unable to check status of the job.";
            }
        } else {
            LOG.error(getErrorMessageWithResponseStatus("Submit Draft operation failed on URL: " + tenancyURL,
                    clientResponse));
            resultMessage = "Failed to submit draft for sending";
        }

        return resultMessage;
    }


    /**
     * @param draft document draft
     * @return JSONObject of the request
     */
    @SuppressWarnings("unchecked")
    public JSONObject createSubmissionRequest(final String draft) {

        LOG.info("createSubmissionRequest");

        JSONObject submitRequest = new JSONObject();
        String submitQueue = properties.getProperty(SmartCommsConfig.SC_SUBMIT_QUEUE);
        submitRequest.put("name", "bob");
        submitRequest.put("queue", submitQueue);
        submitRequest.put("type", "DRAFT_FILE");
        submitRequest.put("input", "/mnt/autofs/input/icare89-qa/draft_review_case.xml");
        submitRequest.put("range", "1");
        submitRequest.put("jobProperties", new JSONArray());
        return submitRequest;
    }


    /**
     * @param reader BufferredReader retrieved from the page request.
     * @return draft as string
     */
    public String retrieveDraft(final BufferedReader reader) throws Exception {
        String draft = "";

        try {
            final StringBuffer draftBuffer = new StringBuffer();
            final char[] charBuff = new char[SmartCommsConfig.Numbers.FOUR_KILO];

            int bytesCount = SmartCommsConfig.Numbers.MINUS_ONE;
            while ((bytesCount = reader.read(charBuff)) != SmartCommsConfig.Numbers.MINUS_ONE) {
                // same as Arrays.toString(charBuff).substring(0,bytesCount) but probably
                // performing better
                draftBuffer.append(Arrays.copyOf(charBuff, bytesCount));
            }
            draft = draftBuffer.toString();

        } catch (IOException e) {
            throw new Exception("Unable to read Document received from Draft Editor. ", e);
        }
        return draft;

    }

    /**
     * @param location job URL in Tenancy
     * @return job status
     */
    public String checkJobStatus(final String location) throws Exception {
        String jobStatus = "";
        final ClientResponse clientResponse = sendReSTRequest(GET, null, location, "Job Status Check", MediaType.APPLICATION_JSON, true);

        if (clientResponse != null && SmartCommsConfig.Numbers.TWO_HUNDRED == clientResponse.getStatus()) {

            String resp = clientResponse.getEntity(String.class);

            if (resp != null) {

                resp = resp.replace("while(1);", "");

                JSONParser jp = new JSONParser();
                StringReader st = new StringReader(resp);
                JSONObject responseObject = (JSONObject) jp.parse(st);
                // String status = null;
                if (responseObject != null) {
                    jobStatus = (String) responseObject.get("status");
                    LOG.info("Status " + jobStatus);
                }
                // TaSessionManager.getSession().putValue("smartcommsJobStatus", status);

            }

        } else {
            LOG.error(getErrorMessageWithResponseStatus(
                    "Verifying status of Submit Draft operation failed on URL: " + location, clientResponse));
            jobStatus = "Could not verify status of the draft submission and email sending.";
        }
        return jobStatus;
    }

    public void callAPIRestEndpoint() throws Exception {
        String jobStatus = "";

        LOG.info("callAPIRestEndpoint");

        System.setProperty("https.proxyHost", "cyamproxy.gbapres.local");
        System.setProperty("https.proxyPort", "3128");

        String tenancyURL = properties.getProperty(SmartCommsConfig.SMARTCOMMS_SERVER_URL);
        tenancyURL += "/one/oauth1/bulkServices/api/v5/resources/158553025";

        final ClientResponse clientResponse = sendReSTRequest(GET, null, tenancyURL, "REST", MediaType.APPLICATION_JSON, true);

        if (clientResponse != null && SmartCommsConfig.Numbers.TWO_HUNDRED == clientResponse.getStatus()) {

            String resp = clientResponse.getEntity(String.class);

            if (resp != null) {

                resp = resp.replace("while(1);", "");

                JSONParser jp = new JSONParser();
                StringReader st = new StringReader(resp);
                JSONObject responseObject = (JSONObject) jp.parse(st);
                // String status = null;
                if (responseObject != null) {
                    jobStatus = (String) responseObject.get("status");
                    LOG.info("Status " + jobStatus);
                }
                // TaSessionManager.getSession().putValue("smartcommsJobStatus", status);

            }

        } else {
            LOG.error("broke");
        }
    }

    /**
     * @param message        error message
     * @param clientResponse client response
     * @return status of the response if response is not null
     */
    private String getErrorMessageWithResponseStatus(final String message, final ClientResponse clientResponse) {
        String status = message;
        if (clientResponse != null) {
            status += "\n With status: " + clientResponse.getClientResponseStatus();
        }
        return status;
    }
}

