package com.openclassrooms.safetynet.alert.store;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

/** Default implementation of {@link FilesOperations} backed by {@link java.nio.file.Files}. */
@Component
public class DefaultFilesOperations implements FilesOperations {

    @Override
    public boolean notExists(Path path) {
        return Files.notExists(path);
    }

    @Override
    public void createDirectories(Path dir) throws IOException {
        Files.createDirectories(dir);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        Files.copy(source, target, options);
    }

    @Override
    public File getFile(String path) {
        return new File(path);
    }
}
