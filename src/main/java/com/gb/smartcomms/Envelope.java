package com.gb.smartcomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Envelope", namespace = "http://www.gallagherbasset.com.au/smartcommsappliance", propOrder = {
        "masterChannel"
})
public class Envelope {

    public MasterChannel getMasterChannel() {
        return masterChannel;
    }

    public void setMasterChannel(MasterChannel masterChannel) {
        this.masterChannel = masterChannel;
    }

    @XmlElement(required = true)
    protected MasterChannel masterChannel;

}
