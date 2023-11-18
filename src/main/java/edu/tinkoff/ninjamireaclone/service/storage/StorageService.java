package edu.tinkoff.ninjamireaclone.service.storage;

import edu.tinkoff.ninjamireaclone.model.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    /**
     * Initializes the storage service.
     */
    void init();

    /**
     * Stores the given file in the storage.
     *
     * @param file the file to be stored
     * @return stored document entity
     */
    Document store(MultipartFile file);

    /**
     * Loads the file with the specified filename from the storage and returns its path.
     *
     * @param filename the name of the file to be loaded
     * @return the path of the loaded file
     */
    Path load(String filename);

    /**
     * Loads the file with the specified filename from the storage and returns it as a Resource object.
     *
     * @param filename the name of the file to be loaded
     * @return the loaded file as a Resource object
     */
    Resource loadAsResource(String filename);

    /**
     * Deletes all files and directories from the storage.
     * <p>
     * This method deletes all files and directories from the storage.
     * </p>
     */
    void deleteAll();
}
