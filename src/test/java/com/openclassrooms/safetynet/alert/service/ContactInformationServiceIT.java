package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.openclassrooms.safetynet.alert.dto.ChildrenAndAdultsDTO;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonMedicalRecordRepository;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class ContactInformationServiceIT extends IntegrationTestBase {

    @Autowired JsonPersonRepository jsonPersonRepository;

    @Autowired JsonMedicalRecordRepository jsonMedicalRecordRepository;

    @Autowired ContactInformationService contactInformationService;

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists() {
        Person person = new PersonTestBuilder().build();
        FireStation fireStation = new FireStationTestBuilder().build();

        List<String> result =
                contactInformationService.getPhoneNumberListByFireStationNumber(
                        fireStation.getStation());

        assertEquals(1, result.size());
        assertEquals(List.of(person.getPhone()), result);
    }

    @Test
    void getChildrenListByAddress_shouldReturnChildrenList_whenChildrenExist() {
        Person child =
                new PersonTestBuilder()
                        .withFirstName("Jojo")
                        .withEmail("jojodoe@email.com")
                        .build();
        MedicalRecord childMedicalRecord =
                new MedicalRecordTestBuilder()
                        .withFirstName("Jojo")
                        .withLastName("Doe")
                        .withBirthdate(LocalDate.now().minusYears(1))
                        .build();

        jsonPersonRepository.save(child);
        jsonMedicalRecordRepository.save(childMedicalRecord);

        ChildrenAndAdultsDTO result =
                contactInformationService.getChildrenListByAddress(child.getAddress());

        assertEquals(1, result.children().size());
        assertEquals(child.getFirstName(), result.children().getFirst().firstName());
        assertEquals(child.getLastName(), result.children().getFirst().lastName());
        assertEquals(1, result.children().getFirst().age());
    }
}
