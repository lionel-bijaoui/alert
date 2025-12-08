package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller for handling population-related requests. */
@RestController
public class PopulationController {

    private final PopulationService populationService;

    public PopulationController(PopulationService populationService) {
        this.populationService = populationService;
    }

    /**
     * Return the name, address, age, email address, and medical history (medications, dosage, and
     * allergies) of each resident. If several people have the same name, they must all appear.
     *
     * @param lastName the last name of the person
     * @return a list of persons with medical infos
     */
    @RequestMapping("/personInfolastName")
    public ResponseEntity<List<PersonWithMedicalInfosDTO>> getPersonListByLastName(
            @RequestParam String lastName) {
        List<PersonWithMedicalInfosDTO> body = populationService.getPersonListByLastName(lastName);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Return the email addresses of all residents of the city.
     *
     * @param city the city name
     * @return a list of email addresses
     */
    @RequestMapping("/communityEmail")
    public ResponseEntity<List<String>> getAllEmailFromCity(@RequestParam String city) {
        List<String> body = populationService.getPersonEmailListByCity(city);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
