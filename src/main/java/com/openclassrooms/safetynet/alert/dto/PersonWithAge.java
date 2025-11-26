package com.openclassrooms.safetynet.alert.dto;

import com.openclassrooms.safetynet.alert.model.Person;

public record PersonWithAge(Person person, int age) {}
