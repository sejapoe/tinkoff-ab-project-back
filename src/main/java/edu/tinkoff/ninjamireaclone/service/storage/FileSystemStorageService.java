package edu.tinkoff.ninjamireaclone.service.storage;

import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageException;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageFileNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Document;
import edu.tinkoff.ninjamireaclone.model.DocumentType;
import edu.tinkoff.ninjamireaclone.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {
    private final DocumentRepository documentRepository;
    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(DocumentRepository documentRepository, StorageProperties storageProperties) {
        this.documentRepository = documentRepository;

        if (storageProperties.getLocation().isBlank()) {
            throw new StorageException("File upload location can not be empty");
        }

        rootLocation = Paths.get(storageProperties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public Document store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file");
            }

            String filename = file.getOriginalFilename();
            if (Objects.isNull(filename)) {
                filename = "";
            }

            filename = UUID.randomUUID() + "-" + filename;

            Path destinationFile = this.rootLocation
                    .resolve(Paths.get(filename))
                    .normalize()
                    .toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Unable to store file outside current directory");
            }

            try (InputStream is = file.getInputStream()) {
                Files.copy(is, destinationFile, StandardCopyOption.REPLACE_EXISTING);

                Document document = new Document();

                String contentType = file.getContentType();
                document.setDocumentType(
                        Objects.nonNull(contentType) && contentType.startsWith("image/")
                                ? DocumentType.IMAGE
                                : DocumentType.FILE
                );

                document.setName(filename);
                return documentRepository.save(document);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        documentRepository.deleteAll();
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
