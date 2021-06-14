package com.gb.smartcomms.service;

import com.gb.smartcomms.SmartCommsRESTSupport;
import com.gb.smartcomms.domain.GetRequest;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SmartCommsService {
    private static final Logger LOGGER = Logger.getLogger(SmartCommsService.class);

    private SmartCommsService() {
        // Do nothing.
    }

    public static Document getDocument(final GetRequest request) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(get(request)));
        } catch (Exception e) {
            throw new SmartCommsServiceException("Unable to get SmartComms document", e);
        }
    }

    public static String getXml(final GetRequest request) {
        try {
            return new String(get(request), UTF_8);
        } catch (Exception e) {
            throw new SmartCommsServiceException("Unable to get SmartComms XML", e);
        }
    }

    private static byte[] get(final GetRequest request) throws Exception {
        final SmartCommsRESTSupport restSupport = getRestSupport(request);
        final String xml = GetRequestPayloadBuilder.getPayload(request);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Request XML Payload - " + xml);
        }

        final JSONObject jsonObject = restSupport.generateDraftRequest(restSupport.createTransactXML(xml));

        return convert(restSupport.sendGenerateDraftRequest(jsonObject));
    }

    private static SmartCommsRESTSupport getRestSupport(final GetRequest request) {
        final SmartCommsRESTSupport restSupport = new SmartCommsRESTSupport();

        switch (request.getAppliance()) {
            case GEN:
                restSupport.loadProperties("config-gen.properties");
                break;
            case ICARE:
                restSupport.loadProperties("config-icare.properties");
                break;
            default:
                throw new IllegalArgumentException("Invalid Appliance supplied");
        }

        return restSupport;
    }

    private static byte[] convert(final String encodedResponse) {
        final byte[] decode = Base64.getDecoder().decode(encodedResponse);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Response XML Payload - " + new String(decode, UTF_8));
        }

        return decode;
    }
}
