package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.ChildrenAndAdultsDTO;
import com.openclassrooms.safetynet.alert.service.ContactInformationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller for handling alert-related requests. */
@RestController
public class AlertController {

    private final ContactInformationService contactInformationService;

    public AlertController(ContactInformationService contactInformationService) {
        this.contactInformationService = contactInformationService;
    }

    /**
     * Return a list of phone numbers of residents served by the fire station. We will use it to
     * send emergency text messages to specific households.
     *
     * @param firestation the fire station number
     * @return a list of phone numbers of residents served by the fire station
     */
    @RequestMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumberListByFireStationNumber(
            @RequestParam int firestation) {
        List<String> body =
                contactInformationService.getPhoneNumberListByFireStationNumber(firestation);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Return a list of children (any individual aged 18 or under) living at this address. The list
     * must include each child's first and last name, their age, and a list of other members of the
     * household. If there are no children, return an empty string.
     *
     * @param address the address to search for children
     * @return a ChildrenAndAdultsDTO
     */
    @RequestMapping("/childAlert")
    public ResponseEntity<ChildrenAndAdultsDTO> getChildrenListByAddress(
            @RequestParam String address) {
        ChildrenAndAdultsDTO body = contactInformationService.getChildrenListByAddress(address);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
