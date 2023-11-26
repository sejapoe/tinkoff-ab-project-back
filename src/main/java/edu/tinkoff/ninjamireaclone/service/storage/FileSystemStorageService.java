package edu.tinkoff.ninjamireaclone.service.storage;

import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageException;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageFileNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Document;
import edu.tinkoff.ninjamireaclone.model.DocumentType;
import edu.tinkoff.ninjamireaclone.model.QDocument;
import edu.tinkoff.ninjamireaclone.repository.DocumentRepository;
import jakarta.transaction.Transactional;
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

// Там другие команды уже с s3 начали знакомиться - можете тоже попробовать сделать реализацию.
// Это НЕ обязательно к следующей встрече
@Service
public class FileSystemStorageService implements StorageService {
    private final DocumentRepository documentRepository;
    private final Path rootLocation;

    /**
     * Constructor for the FileSystemStorageService class.
     *
     * @param documentRepository the document repository to be used for storing and retrieving documents
     * @param storageProperties  the storage properties containing the file upload location
     * @throws StorageException if the file upload location is empty
     */
    @Autowired
    public FileSystemStorageService(DocumentRepository documentRepository, StorageProperties storageProperties) {
        this.documentRepository = documentRepository;

        if (Objects.isNull(storageProperties.getLocation()) || storageProperties.getLocation().isBlank()) {
            throw new StorageException("File upload location can not be empty");
        }

        rootLocation = Paths.get(storageProperties.getLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Document store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file");
            }

            String filename = file.getOriginalFilename();
            if (Objects.isNull(filename)) {
                throw new StorageException("Failed to store file with empty name");
            }
            String originalFilename = filename;

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

                document.setOriginalName(originalFilename);
                document.setFilename(filename);
                return documentRepository.save(document);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Failed to read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Failed to read file: " + filename, e);
        }
    }

    @Override
    @Transactional
    public String getOriginalName(String filename) {
        return documentRepository.findOne(QDocument.document.filename.eq(filename))
                .map(Document::getOriginalName)
                .orElseThrow(() -> new StorageFileNotFoundException("Failed to read file: " + filename));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        documentRepository.deleteAll();
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
