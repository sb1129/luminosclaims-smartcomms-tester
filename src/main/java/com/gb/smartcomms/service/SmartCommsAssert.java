package com.gb.smartcomms.service;

import com.gb.smartcomms.domain.ComplexGetRequest;
import com.gb.smartcomms.domain.ComplexGetRequest.Address;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class SmartCommsAssert {
    private static final Logger LOGGER = Logger.getLogger(SmartCommsAssert.class);

    public static final String REQUEST_DETAILS_XPATH = "/review-case/transaction/objects/object/property[@name='RequestDetails']/object/";
    private static final String TEMPLATE_ID_XPATH = REQUEST_DETAILS_XPATH + "property[@name='TemplateId']";
    private static final String RECIPIENT_EMAILS_XPATH = REQUEST_DETAILS_XPATH + "property[@name='Recipients']";
    private static final String SENDER_ADDRESS_XPATH = REQUEST_DETAILS_XPATH + "property[@name='SenderAddress']";
    private static final String SUBJECT_XPATH = REQUEST_DETAILS_XPATH + "property[@name='Subject']";
    private static final String ATTACHMENT_SOURCE_XPATH = REQUEST_DETAILS_XPATH + "property[@name='AttachmentSource']";
    private static final String SIGNATURE_TEMPLATE_ID_XPATH = REQUEST_DETAILS_XPATH + "property[@name='SignatureTemplateId']";

    private static final String RECIPIENT_XPATH = REQUEST_DETAILS_XPATH + "property[@name='EmailRecipients']/object/property/object/";
    private static final String RECIPIENT_CUSTOMER_NUMBER_XPATH = RECIPIENT_XPATH + "property[@name='CustomerNumber']";
    private static final String PERSON_RECIPIENT_XPATH = RECIPIENT_XPATH + "property[@name='Person']/object/";
    private static final String ORGANISATION_RECIPIENT_XPATH = RECIPIENT_XPATH + "property[@name='Organisation']/object/";
    private static final String RECIPIENT_ADDRESS_XPATH = RECIPIENT_XPATH + "property[@name='Address']/object/";
    private static final String RECIPIENT_PHONE_NUMBER_XPATH = RECIPIENT_XPATH + "property[@name='PhoneNumber']";
    private static final String RECIPIENT_MOBILE_NUMBER_XPATH = RECIPIENT_XPATH + "property[@name='MobileNumber']";
    private static final String RECIPIENT_EMAIL_XPATH = RECIPIENT_XPATH + "property[@name='Email']";

    private static final String XPATH_VALUE_SUFFIX = "/@value";

    private SmartCommsAssert() {
        // Do nothing.
    }

    public static void assertMatches(final ComplexGetRequest request, final Document document) {
        assertNotNull(document);

        assertEquals(request.getSmartCommsTemplateId(), getValue(TEMPLATE_ID_XPATH, document));

        final String recipientsEmail = getValue(RECIPIENT_EMAILS_XPATH, document);
        request.getRecipientsEmail().forEach(recipient -> Assert.assertTrue(recipientsEmail.contains(recipient)));

        assertEquals(request.getSenderEmail(), getValue(SENDER_ADDRESS_XPATH, document));
        assertEquals(request.getSubject(), getValue(SUBJECT_XPATH, document));
        assertEquals(request.getAttachmentSource(), getValue(ATTACHMENT_SOURCE_XPATH, document));
        assertEquals(request.getSignatureTemplateId(), getValue(SIGNATURE_TEMPLATE_ID_XPATH, document));

        request.getPersonRecipients().forEach(req -> {
            assertEquals(req.getCustomerNumber(), getValue(RECIPIENT_CUSTOMER_NUMBER_XPATH, document));

            assertEquals(req.getTitle(), getValue(PERSON_RECIPIENT_XPATH + "property[@name='Title']", document));
            assertEquals(req.getFirstName(), getValue(PERSON_RECIPIENT_XPATH + "property[@name='FirstName']", document));
            assertEquals(req.getLastName(), getValue(PERSON_RECIPIENT_XPATH + "property[@name='LastName']", document));

            assertAddress(req.getAddress(), document);

            Optional.ofNullable(req.getPhoneNumber()).ifPresent(phoneNumber -> assertEquals(phoneNumber, getValue(RECIPIENT_PHONE_NUMBER_XPATH, document)));
            Optional.ofNullable(req.getMobileNumber()).ifPresent(mobileNumber -> assertEquals(mobileNumber, getValue(RECIPIENT_MOBILE_NUMBER_XPATH, document)));

            assertEquals(req.getEmail(), getValue(RECIPIENT_EMAIL_XPATH, document));
        });

        request.getOrganisationRecipients().forEach(req -> {
            assertEquals(req.getCustomerNumber(), getValue(RECIPIENT_CUSTOMER_NUMBER_XPATH, document));

            assertEquals(req.getShortName(), getValue(ORGANISATION_RECIPIENT_XPATH + "property[@name='ShortName']", document));
            assertEquals(req.getName(), getValue(ORGANISATION_RECIPIENT_XPATH + "property[@name='Name']", document));
            assertEquals(req.getAbn(), getValue(ORGANISATION_RECIPIENT_XPATH + "property[@name='ABN']", document));
            assertEquals(req.getBankAccount(), getValue(ORGANISATION_RECIPIENT_XPATH + "property[@name='BankAccount']", document));

            assertAddress(req.getAddress(), document);

            Optional.ofNullable(req.getPhoneNumber()).ifPresent(phoneNumber -> assertEquals(phoneNumber, getValue(RECIPIENT_PHONE_NUMBER_XPATH, document)));
            Optional.ofNullable(req.getMobileNumber()).ifPresent(mobileNumber -> assertEquals(mobileNumber, getValue(RECIPIENT_MOBILE_NUMBER_XPATH, document)));

            assertEquals(req.getEmail(), getValue(RECIPIENT_EMAIL_XPATH, document));
        });
    }

    private static void assertAddress(final Address address, final Document document) {
        assertEquals(address.getStreetAddress(), getValue(RECIPIENT_ADDRESS_XPATH + "property[@name='StreetAddress']", document));
        assertEquals(address.getSuburb(), getValue(RECIPIENT_ADDRESS_XPATH + "property[@name='Suburb']", document));
        assertEquals(address.getPostCode(), getValue(RECIPIENT_ADDRESS_XPATH + "property[@name='PostCode']", document));
        assertEquals(address.getCountry(), getValue(RECIPIENT_ADDRESS_XPATH + "property[@name='Country']", document));
        Optional.ofNullable(address.getState()).ifPresent(state -> assertEquals(state, getValue(RECIPIENT_ADDRESS_XPATH + "property[@name='State']", document)));
    }

    public static void assertMatches(final ComplexGetRequest request, final String xml) {
        assertNotNull(xml);

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));

            assertMatches(request, document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("An error occurred whilst validating XML", e);
            throw new AssertionError("An error occurred whilst validating XML: " + e.getMessage());
        }
    }

    public static void assertHasNode(final String xpathExpression, final String xml) {
        assertNotNull(xml);

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));

            assertHasNode(xpathExpression, document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("An error occurred whilst validating XML", e);
            throw new AssertionError("An error occurred whilst validating XML: " + e.getMessage());
        }
    }

    public static void assertHasNode(final String xpathExpression, final Document document) {
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();

        try {
            final XPathExpression expr = xpath.compile(xpathExpression);

            assertNotNull(expr.evaluate(document));
        } catch (XPathExpressionException e) {
            final String message = "Unable to find XPath: '" + xpathExpression + "' in document.";

            LOGGER.error(message, e);
            throw new AssertionError(message);
        }
    }

    public static String getValue(final String xpathExpression, final Document document) {
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();

        try {
            final XPathExpression expr = xpath.compile(xpathExpression.endsWith(XPATH_VALUE_SUFFIX) || xpathExpression.endsWith("/text()")
                            ? xpathExpression : xpathExpression + XPATH_VALUE_SUFFIX);

            final Node node = (Node) expr.evaluate(document, XPathConstants.NODE);

            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                return node.getNodeValue();
            } else {
                return node.getTextContent();
            }
        } catch (XPathExpressionException e) {
            final String message = "Unable to find XPath: '" + xpathExpression + "' in document.";

            LOGGER.error(message, e);
            throw new AssertionError(message);
        }
    }
}
