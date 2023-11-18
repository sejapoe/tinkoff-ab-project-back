package edu.tinkoff.ninjamireaclone.service.storage;

import edu.tinkoff.ninjamireaclone.model.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void init();

    Document store(MultipartFile file);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
