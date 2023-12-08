package edu.tinkoff.ninjamireaclone;

import edu.tinkoff.ninjamireaclone.config.MinIOTestConfig;
import edu.tinkoff.ninjamireaclone.config.PostgreTestConfig;
import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.controller.PostController;
import edu.tinkoff.ninjamireaclone.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

class NinjaMireaCloneApplicationTests extends AbstractBaseTest {
    @Test
    void contextLoads() {
    }
}
