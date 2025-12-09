package com.openclassrooms.safetynet.alert.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.safetynet.alert.configuration.DataStoreProperties;
import com.openclassrooms.safetynet.alert.model.Database;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;

/** Component responsible for loading and persisting the application's JSON database file. */
@Slf4j
@Component
public class JsonFileDataStore {
    /** Path to the initial JSON file. */
    private final String initial;

    /** Path to the current JSON file used for reading/writing. */
    @Getter private final String current;

    /** Abstraction for file operations to facilitate testing. */
    private final FilesOperations fileOperations;

    /** ObjectMapper instance for JSON serialization/deserialization. */
    private final ObjectMapper objectMapper;

    /** Cached in-memory representation of the database. */
    @Getter @Setter @NotNull private Database cachedDatabase;

    public JsonFileDataStore(
            ObjectMapper objectMapper,
            DataStoreProperties properties,
            FilesOperations fileOperations) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper = objectMapper;
        this.current = properties.getCurrent();
        this.initial = properties.getInitial();
        this.fileOperations = fileOperations;
    }

    /**
     * Ensure the current file exists by copying the initial file if needed.
     *
     * @throws IllegalStateException if the database file cannot be initialized
     */
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
            log.error("Failed to initialize database file from {}", initial, e);
            throw new IllegalStateException("Cannot start application: failed to load database", e);
        }
    }

    /** Cache the database in memory for faster access. */
    private void cacheDatabase() {
        var maybeDb = readAll();
        if (maybeDb.isEmpty()) {
            log.warn("Database is empty after loading from file {}", current);
            setCachedDatabase(
                    new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        } else {
            log.info("Database loaded successfully from file {}", current);
            setCachedDatabase(maybeDb.get());
        }
    }

    /** Initialize the data store by loading and caching the database. */
    public void init() {
        load();
        cacheDatabase();
    }

    /** Initialize the data store when the application is ready. */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Initializing data store...");
        init();
    }

    /** Persist the cached database to file before application shutdown. */
    @EventListener(ContextClosedEvent.class)
    public void onContextClosed() {
        log.info("Persisting data store to file before shutdown...");
        writeAll(getCachedDatabase());
    }

    /**
     * Read the full database from the current file.
     *
     * @return Optional containing the database if present and readable
     * @throws IllegalStateException if reading the file fails
     */
    Optional<Database> readAll() {
        try {
            File jsonFile = fileOperations.getFile(current);

            if (jsonFile.exists() && jsonFile.isFile() && jsonFile.length() > 0) {
                // JSON file exists and is not empty
                Database db = objectMapper.readValue(jsonFile, Database.class);
                return Optional.ofNullable(db);
            } else {
                log.warn("Database file {} is missing or empty", current);
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Failed to read JSON file from {}", current, e);
            throw new IllegalStateException("Cannot read database file", e);
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
            log.info("Database successfully persisted to {}", current);
        } catch (IOException e) {
            log.error("Failed to persist database to {}", current, e);
            throw new RuntimeException("Failed to write database file", e);
        }
    }
}
