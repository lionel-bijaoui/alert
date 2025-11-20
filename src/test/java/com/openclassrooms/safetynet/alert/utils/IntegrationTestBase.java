package com.openclassrooms.safetynet.alert.utils;

import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;

/** Base class for integration tests, providing setup and cleanup for the JSON data store. */
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired protected JsonFileDataStore store;

    @BeforeEach
    void setUpStore() {
        store.load();
    }

    @AfterEach
    void cleanUpStore() throws Exception {
        Path current = Path.of(store.current());
        if (Files.exists(current)) {
            Files.delete(current);
        }
    }
}
