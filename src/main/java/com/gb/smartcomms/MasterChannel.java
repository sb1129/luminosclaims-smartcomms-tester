package com.gb.smartcomms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MasterChannel", namespace = "http://www.gallagherbasset.com.au/smartcommsappliance", propOrder = {
        "data"
})
public class MasterChannel {

    @XmlElement(required = true)
    protected String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
