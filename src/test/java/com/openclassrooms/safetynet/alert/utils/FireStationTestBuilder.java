package com.openclassrooms.safetynet.alert.utils;

import com.openclassrooms.safetynet.alert.model.FireStation;

public class FireStationTestBuilder {
    private String address = "1509 Culver St";
    private Integer station = 3;

    public FireStationTestBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public FireStationTestBuilder withStation(Integer station) {
        this.station = station;
        return this;
    }

    public FireStation build() {
        return new FireStation(address, station);
    }
}
