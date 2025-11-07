package com.openclassrooms.safetynet.alert.store;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;

/**
 * Abstraction of file operations used by the application.
 * This interface decouples business logic from the concrete implementation
 * ({@link DefaultFilesOperations}) and makes unit testing easier by
 * allowing filesystem operations to be mocked without touching the real
 * filesystem.
 */
public interface FilesOperations {

    /**
     * Checks if the specified path does not exist.
     * Equivalent to {@code File::notExists} from {@link java.nio.file.Files}.
     *
     * @param path path to check
     * @return {@code true} if the path does not exist
     */
    boolean notExists(Path path);

    /**
     * Creates directories if necessary.
     * Equivalent to {@code File::createDirectories} from {@link java.nio.file.Files}.
     *
     * @param dir directory to create
     * @throws IOException if directory creation fails
     */
    void createDirectories(Path dir) throws IOException;

    /**
     * Copies a source file to a target location with optional copy options.
     * Equivalent to {@code File::copy} from {@link java.nio.file.Files}.
     *
     * @param source  source path
     * @param target  target path
     * @param options copy options
     * @throws IOException if the copy fails
     */
    void copy(Path source, Path target, CopyOption... options) throws IOException;

    /**
     * Retrieves a {@link File} object for the specified path.
     *
     * @param path file path
     * @return {@link File} object representing the path
     */
    File getFile(String path);

}
