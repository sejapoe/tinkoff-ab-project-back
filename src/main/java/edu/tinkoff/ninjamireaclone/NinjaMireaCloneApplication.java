package edu.tinkoff.ninjamireaclone;

import edu.tinkoff.ninjamireaclone.config.StorageProperties;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

// если вдруг на репо в гите еще нет CheckStyle или иного линтера -
// попробуйте повесить и потом покажете, стригерился ли он на что-то или нет
@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class})
public class NinjaMireaCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(NinjaMireaCloneApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> storageService.init();
    }
}
