package edu.tinkoff.ninjamireaclone.service.storage;

import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageException;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageFileNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Document;
import edu.tinkoff.ninjamireaclone.model.DocumentType;
import edu.tinkoff.ninjamireaclone.model.QDocument;
import edu.tinkoff.ninjamireaclone.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class S3StorageService implements StorageService {
    private final StorageProperties storageProperties;
    private final S3Client s3Client;
    private final DocumentRepository documentRepository;
    private String bucketName;

    @Override
    public void init() {
        bucketName = storageProperties.getBucketName();

        if (Objects.isNull(bucketName) || bucketName.isBlank()) {
            throw new StorageException("You should specify bucket name to use S3 storage");
        }

        boolean doesBucketExists = s3Client.listBuckets().buckets().stream().anyMatch(bucket -> bucket.name().equals(bucketName));
        if (!doesBucketExists) {
            throw new StorageException("Bucket with given name does not exists in S3");
        }

        log.info("S3 storage has successfully initialized");
    }

    @Override
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

            PutObjectResponse putObjectResponse = s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filename)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.debug(putObjectResponse.checksumCRC32());

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
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        var bytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filename)
                        .build()
        );


        return new ByteArrayResource(bytes.asByteArray());
    }

    @Override
    @Transactional
    public String getOriginalName(String filename) {
        return documentRepository.findOne(QDocument.document.filename.eq(filename))
                .map(Document::getOriginalName)
                .orElseThrow(() -> new StorageFileNotFoundException("Failed to read file: " + filename));
    }

    @Override
    public void deleteAll() {
        List<S3Object> listObjects = s3Client
                .listObjects(
                        ListObjectsRequest.builder()
                                .bucket(bucketName)
                                .build())
                .contents();

        List<ObjectIdentifier> objectIdentifiers = listObjects.stream()
                .map(S3Object::key)
                .map(s -> ObjectIdentifier.builder().key(s).build())
                .toList();

        s3Client.deleteObjects(DeleteObjectsRequest.builder().delete(
                        Delete.builder()
                                .objects(objectIdentifiers)
                                .build())
                .build()
        );
    }
}
