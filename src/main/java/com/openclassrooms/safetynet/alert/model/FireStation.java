package com.openclassrooms.safetynet.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Model representing a fire station. */
@Data
@AllArgsConstructor
public class FireStation {

    private String address;
    private String station;
}
