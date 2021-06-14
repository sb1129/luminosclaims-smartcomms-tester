package com.gb.smartcomms.domain;

public class SimpleGetRequest implements GetRequest {
    private final Appliance appliance;
    private final String payload;

    public SimpleGetRequest(final Appliance appliance, final String payload) {
        this.appliance = appliance;
        this.payload = payload;
    }

    @Override
    public Appliance getAppliance() {
        return appliance;
    }

    public String getPayload() {
        return payload;
    }
}
