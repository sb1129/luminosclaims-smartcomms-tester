package com.gb.smartcomms.service;

import com.gb.smartcomms.domain.Appliance;
import com.gb.smartcomms.domain.ComplexGetRequest;
import com.gb.smartcomms.domain.ComplexGetRequest.OrganisationRecipient;
import com.gb.smartcomms.domain.ComplexGetRequest.PersonRecipient;
import com.gb.smartcomms.domain.SimpleGetRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.gb.smartcomms.domain.ComplexGetRequest.Address;
import static com.gb.smartcomms.service.SmartCommsAssert.assertHasNode;
import static com.gb.smartcomms.service.SmartCommsAssert.assertMatches;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SmartCommsServiceTest {

    @Test
    public void getDocumentSimpleGetRequest() throws IOException {
        final SimpleGetRequest request = new SimpleGetRequest(Appliance.GEN, IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("payload.to.send.uk.xml")), UTF_8));
        final Document document = SmartCommsService.getDocument(request);

        assertHasNode("/review-case", document);
    }

    @Test
    public void getXmlSimpleGetRequest() throws IOException {
        final SimpleGetRequest request = new SimpleGetRequest(Appliance.GEN, IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("payload.to.send.uk.xml")), UTF_8));
        final String xml = SmartCommsService.getXml(request);

        assertHasNode("/review-case", xml);
    }

    @Test
    public void getDocumentPersonComplexGetRequest() {
        final List<PersonRecipient> personRecipients = new ArrayList<>();
        personRecipients.add(new PersonRecipient("3", "Mr", "Srikanth", "Behra", "04645645676", null, "srikanth_behra@gbtpa.com.au", new Address("Royal &amp; Ancient, Forgan House, The Links, ST. ANDREWS", "FIFE", null, "KY16 9JB", "United Kingdom")));

        final List<OrganisationRecipient> organisationRecipients = new ArrayList<>();

        final ComplexGetRequest request = new ComplexGetRequest(Appliance.GEN, "157909509", personRecipients, organisationRecipients, "Our ref: UK000171-01 Acknowledgement of your complaint", "email_test_gen89-si@gbtpa.com.au", "SIG_BLOCK_AUTO_GENERAL", "UK000171", "UK000171-1", "UK-APOLLO-PROPERTY-2016-COMMERCIAL PROPERTY", "https://process.dms.view:123456@gen89-si-esb.gbau.ajgco.com:443/process/dms/document/view/");

        final Document document = SmartCommsService.getDocument(request);

        assertMatches(request, document);

        assertHasNode("/review-case/review-document/review-channel/content/region/section/section/frag/lcij/p/var[@name='AddresseeTitle']/text()='Mr'", document);
        assertHasNode("/review-case/review-document/review-channel/content/region/section/section/frag/lcij/p/var[@name='AddresseeLastName']/text()='Behra'", document);

        assertHasNode("//section[@name='Footer']/frag/lcij/p/text()='You can call us on 07 3012 3070 or email us at '", document);
        assertHasNode("//section[@name='Footer']/frag/lcij/p/style/text()='autogeneral@claims-travel.com.au'", document);

        assertHasNode("//section[@name='Signature']/frag/lcij/p/b/var[@full-path='Correspondence.Case.CurrentUser.Name']/text()='Generic Claims Consultant'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/b/var[@full-path='Correspondence.Case.CurrentUser.Role']/text()='Claims Consultant'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/text()='Auto &amp; General Holdings Pty Ltd'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/text()='P: 07 3012 3070'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/frag/@id='1320513382'", document);

        assertHasNode("//section[@name='DefaultAttachment']/frag[@name='Auto Attachment Name']/lcij/p/text()='A&amp;G IDR Complaint Dispute Brochure.PDF'", document);
    }

    @Test
    public void getDocumentOrganisationComplexGetRequest() {
        final List<PersonRecipient> personRecipients = new ArrayList<>();

        final List<OrganisationRecipient> organisationRecipients = new ArrayList<>();
        organisationRecipients.add(new OrganisationRecipient("137", "Chubbs", "Chubbs", "ABN100", "ANZ1459789", null, "074553470", "zac_sophios@gbtpa.com.au", new Address("1 Eagle Street", "Brisbane", "QLD", "4000", "Australia")));

        final ComplexGetRequest request = new ComplexGetRequest(Appliance.GEN, "157909509", personRecipients, organisationRecipients, "Our ref: UK000171-01 Acknowledgement of your complaint", "email_test_gen89-si@gbtpa.com.au", "SIG_BLOCK_AUTO_GENERAL", "UK000171", "UK000171-1", "UK-APOLLO-PROPERTY-2016-COMMERCIAL PROPERTY", "https://process.dms.view:123456@gen89-si-esb.gbau.ajgco.com:443/process/dms/document/view/");

        final Document document = SmartCommsService.getDocument(request);

        assertMatches(request, document);

        assertHasNode("//section[@name='Footer']/frag/lcij/p/text()='You can call us on 07 3012 3070 or email us at '", document);
        assertHasNode("//section[@name='Footer']/frag/lcij/p/style/text()='autogeneral@claims-travel.com.au'", document);

        assertHasNode("//section[@name='Signature']/frag/lcij/p/b/var[@full-path='Correspondence.Case.CurrentUser.Name']/text()='Generic Claims Consultant'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/b/var[@full-path='Correspondence.Case.CurrentUser.Role']/text()='Claims Consultant'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/text()='Auto &amp; General Holdings Pty Ltd'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/text()='P: 07 3012 3070'", document);
        assertHasNode("//section[@name='Signature']/frag/lcij/p/frag/@id='1320513382'", document);

        assertHasNode("//section[@name='DefaultAttachment']/frag[@name='Auto Attachment Name']/lcij/p/text()='A&amp;G IDR Complaint Dispute Brochure.PDF'", document);
    }
}
