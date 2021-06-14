package com.gb.smartcomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Envelopes", namespace = "http://www.gallagherbasset.com.au/smartcommsappliance", propOrder = {
        "envelope"
})
public class Envelopes {

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    @XmlElement(required = true)
    protected Envelope envelope;


}
