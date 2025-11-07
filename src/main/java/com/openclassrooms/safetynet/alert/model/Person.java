package com.openclassrooms.safetynet.alert.model;

import lombok.Data;

/**
 * Model representing a person.
 */
@Data
public class Person {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;

}
