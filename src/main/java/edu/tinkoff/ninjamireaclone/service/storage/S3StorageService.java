package edu.tinkoff.ninjamireaclone.service.storage;

import com.google.common.collect.Streams;
import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageException;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageFileNotFoundException;
import edu.tinkoff.ninjamireaclone.model.DocumentEntity;
import edu.tinkoff.ninjamireaclone.model.DocumentType;
import edu.tinkoff.ninjamireaclone.model.QDocumentEntity;
import edu.tinkoff.ninjamireaclone.repository.DocumentRepository;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {
    private final StorageProperties storageProperties;
    private final MinioClient minioClient;
    private final DocumentRepository documentRepository;
    private String bucketName;

    @SneakyThrows
    @Override
    public void init() {
        bucketName = storageProperties.getBucketName();

        if (Objects.isNull(bucketName) || bucketName.isBlank()) {
            throw new StorageException("You should specify bucket name to use S3 storage");
        }

        boolean doesBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!doesBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
//            throw new StorageException("Bucket with given name does not exists in S3");
        }

        log.info("S3 storage has successfully initialized");
    }

    @SneakyThrows
    @Override
    public DocumentEntity store(MultipartFile file) {
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

            ObjectWriteResponse putObjectResponse = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );

            log.debug(putObjectResponse.object());

            DocumentEntity document = new DocumentEntity();

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

    @SneakyThrows
    @Override
    public Resource loadAsResource(String filename) {
        try {
            var bytes = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            ).readAllBytes();

            return new ByteArrayResource(bytes);
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new StorageFileNotFoundException("Failed to read file: " + filename);
            } else {
                throw e;
            }
        }
    }

    @Override
    @Transactional
    public String getOriginalName(String filename) {
        return documentRepository.findOne(QDocumentEntity.documentEntity.filename.eq(filename))
                .map(DocumentEntity::getOriginalName)
                .orElseThrow(() -> new StorageFileNotFoundException("Failed to read file: " + filename));
    }

    @Override
    public void deleteAll() {
        var objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );


        List<DeleteObject> deleteObjects = Streams.stream(objects)
                .map(itemResult -> {
                    try {
                        return itemResult.get().objectName();
                    } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(DeleteObject::new)
                .toList();

        minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(bucketName)
                        .objects(deleteObjects)
                        .build()
        );
    }
}
