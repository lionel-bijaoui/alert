package com.openclassrooms.safetynet.alert.store;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.model.Database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * Component responsible for loading and persisting the application's JSON
 * database file.
 */
@Component
public class JsonFileDataStore {

    private static final ObjectMapper mapper;

    // Static initializer to configure the ObjectMapper
    static {
        mapper = new ObjectMapper();
        // Use the date format "dd/MM/yyyy" for serialization and deserialization
        mapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
        // Ignore unknown properties during deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Path to the initial JSON file.
     */
    private final String initial;

    /**
     * Path to the current JSON file used for reading/writing.
     */
    private final String current;

    /**
     * Abstraction for file operations to facilitate testing.
     */
    private final FilesOperations fileOperations;

    public JsonFileDataStore(@Value("${data.store.path.current}") String current, @Value("${data.store.path.initial}") String initial, FilesOperations fileOperations) {
        this.current = current;
        this.initial = initial;
        this.fileOperations = fileOperations;
    }

    /**
     * Returns the configured current file path.
     */
    public String current() {
        return current;
    }

    /**
     * Ensure the current file exists by copying the initial file if needed.
     */
    public void load() {
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
                Database db = mapper.readValue(jsonFile, Database.class);
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
            mapper.writeValue(fileOperations.getFile(current), database);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file", e);
        }
    }

}
