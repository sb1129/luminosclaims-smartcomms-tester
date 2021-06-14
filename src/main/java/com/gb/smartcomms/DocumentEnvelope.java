package com.gb.smartcomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "DocumentEnvelope", namespace = "http://www.gallagherbasset.com.au/smartcommsappliance", propOrder = {
            "envelopes"
    })
    @XmlRootElement
    public class DocumentEnvelope {

        @XmlElement(required = true)
        protected Envelopes envelopes;


        /**
         * Gets the value of the data property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public Envelopes getEnvelopes() {
            return envelopes;
        }

        /**
         * Sets the value of the data property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setEnvelopes(Envelopes value) {
            this.envelopes = value;
        }


    }

