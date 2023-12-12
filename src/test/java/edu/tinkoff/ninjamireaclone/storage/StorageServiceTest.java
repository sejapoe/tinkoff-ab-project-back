package edu.tinkoff.ninjamireaclone.storage;

import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageException;
import edu.tinkoff.ninjamireaclone.exception.storage.StorageFileNotFoundException;
import edu.tinkoff.ninjamireaclone.model.DocumentEntity;
import edu.tinkoff.ninjamireaclone.repository.DocumentRepository;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageServiceTest extends AbstractBaseTest {
    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageProperties storageProperties;
    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        storageService.deleteAll();
        storageService.init();
    }

    @Test
    @Transactional
    public void storeFileTest() throws IOException {
        // given
        MultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Lorem ipsum dolores".getBytes(Charset.defaultCharset())
        );

        // when
        storageService.store(file);

        // then
//        Path location = Paths.get(storageProperties.getLocation());
//        assertThat(Files.exists(location)).isTrue();
//        assertThat(Files.list(location)).anySatisfy(path ->
//                assertThat(path.getFileName().toString()).endsWith("test.txt"));
    }

    @Test
    @Transactional
    public void storeFileOutsideDirectoryTest() throws IOException {
        // given
//        MultipartFile file = new MockMultipartFile(
//                "../test.txt",
//                "../test.txt",
//                "text/plain",
//                "Lorem ipsum dolores".getBytes(Charset.defaultCharset())
//        );
//
//        // when
//        Exception e = assertThrows(Exception.class, () -> storageService.store(file));
//
//        // then
//        Path location = Paths.get(storageProperties.getLocation());
//        assertThat(e).isInstanceOf(StorageException.class);
//        assertThat(e.getMessage()).isEqualTo("Unable to store file outside current directory");
//        assertThat(Files.exists(location)).isTrue();
//        assertThat(Files.list(location)).noneSatisfy(path ->
//                assertThat(path.getFileName().toString()).endsWith("test.txt"));
    }

    @Test
    @Transactional
    public void storeEmptyFileTest() throws IOException {
        // given
        MultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "".getBytes(Charset.defaultCharset())
        );

        // when
        Exception e = assertThrows(Exception.class, () -> storageService.store(file));

        // then
//        Path location = Paths.get(storageProperties.getLocation());
        assertThat(e).isInstanceOf(StorageException.class);
        assertThat(e.getMessage()).isEqualTo("Failed to store empty file");
//        assertThat(Files.exists(location)).isTrue();
//        assertThat(Files.list(location)).noneSatisfy(path ->
//                assertThat(path.getFileName().toString()).endsWith("test.txt"));
    }

    @Test
    @Transactional
    public void loadFileAsResourceTest() {
        // given
        MultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Lorem ipsum dolores".getBytes(Charset.defaultCharset())
        );
        storageService.store(file);
        DocumentEntity document = documentRepository.findAll().get(0);

        // when
        Resource resource = storageService.loadAsResource(document.getFilename());

        // then
        assertThat(resource.exists()).isTrue();
        assertThat(resource.isReadable()).isTrue();
    }

    @Test
    @Transactional
    public void loadFileAsResourceNotFoundTest() {
        // given
        MultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Lorem ipsum dolores".getBytes(Charset.defaultCharset())
        );
        storageService.store(file);

        // when
        Exception e = assertThrows(Exception.class, () -> storageService.loadAsResource("not-found.txt"));

        // then
        assertThat(e).isInstanceOf(StorageFileNotFoundException.class);
        assertThat(e.getMessage()).isEqualTo("Failed to read file: not-found.txt");
    }
}
