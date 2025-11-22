package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller for handling alert-related requests. */
@RestController
public class AlertController {

    private FireStationService fireStationService;
    private PopulationService populationService;

    public AlertController(
            FireStationService fireStationService, PopulationService populationService) {
        this.fireStationService = fireStationService;
        this.populationService = populationService;
    }

    /**
     * Return a list of phone numbers of residents served by the fire station. We will use it to
     * send emergency text messages to specific households.
     *
     * @param firestation
     * @return
     */
    @RequestMapping("/phoneAlert")
    public List<String> getPhoneNumberListByFireStationNumber(@RequestParam int firestation) {
        return populationService
                .getPersonListByAddressList(
                        fireStationService.getFireStationAddressListByFireStationNumber(
                                firestation))
                .stream()
                .map(Person::getPhone)
                .toList();
    }
}
