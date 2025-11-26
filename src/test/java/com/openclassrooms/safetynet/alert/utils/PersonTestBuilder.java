package com.openclassrooms.safetynet.alert.utils;

import com.openclassrooms.safetynet.alert.model.Person;

public class PersonTestBuilder {
    private String firstName = "John";
    private String lastName = "Doe";
    private String address = "1509 Culver St";
    private String city = "Culver";
    private String zip = "97451";
    private String phone = "841-874-6512";
    private String email = "johndoe@email.com";

    public PersonTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonTestBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public PersonTestBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public PersonTestBuilder withZip(String zip) {
        this.zip = zip;
        return this;
    }

    public PersonTestBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public PersonTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public Person build() {
        return new Person(firstName, lastName, address, city, zip, phone, email);
    }
}
