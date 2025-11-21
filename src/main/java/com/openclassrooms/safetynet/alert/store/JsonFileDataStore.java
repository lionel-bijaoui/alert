package com.openclassrooms.safetynet.alert.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.model.Database;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/** Component responsible for loading and persisting the application's JSON database file. */
@Slf4j
@Component
public class JsonFileDataStore {
    /** Path to the initial JSON file. */
    private final String initial;

    /** Path to the current JSON file used for reading/writing. */
    private final String current;

    /** Abstraction for file operations to facilitate testing. */
    private final FilesOperations fileOperations;

    /** ObjectMapper instance for JSON serialization/deserialization. */
    private final ObjectMapper objectMapper;

    public JsonFileDataStore(
        ObjectMapper objectMapper,
            @Value("${data.store.path.current}") String current,
            @Value("${data.store.path.initial}") String initial,
            FilesOperations fileOperations) {
        this.objectMapper = objectMapper;
        this.current = current;
        this.initial = initial;
        this.fileOperations = fileOperations;
    }

    /** Returns the configured current file path. */
    public String current() {
        return current;
    }

    /** Ensure the current file exists by copying the initial file if needed. */
    public void load() {
        log.debug("Loading data from {}", initial);
        Path currentPath = Path.of(current);
        Path initialPath = Path.of(initial);
        try {
            if (fileOperations.notExists(currentPath.getParent())) {
                fileOperations.createDirectories(currentPath.getParent());
            }
            if (fileOperations.notExists(currentPath)) {
                fileOperations.copy(initialPath, currentPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON file", e);
        }
    }

    /** Load the data store when the application is ready. */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Initializing data store...");
        load();
    }

    /**
     * Read the full database from the current file.
     *
     * @return Optional containing the database if present and readable
     */
    public Optional<Database> readAll() {
        try {
            File jsonFile = fileOperations.getFile(current);

            if (jsonFile.exists() && jsonFile.isFile() && jsonFile.length() > 0) {
                // JSON file exists and is not empty
                Database db = objectMapper.readValue(jsonFile, Database.class);
                return Optional.ofNullable(db);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }

    /**
     * Write the provided database to the current file.
     *
     * @param database database to persist
     */
    public void writeAll(Database database) {
        try {
            objectMapper.writeValue(fileOperations.getFile(current), database);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file", e);
        }
    }
}
