package edu.tinkoff.ninjamireaclone.storage;

import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StorageServiceTest {
    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageProperties storageProperties;

    @Test
    public void mockFileStoreTest() {
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
        Path location = Paths.get(storageProperties.getLocation());
        assertThat(Files.exists(location)).isTrue();
    }
}
