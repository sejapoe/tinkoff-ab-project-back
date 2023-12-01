package edu.tinkoff.ninjamireaclone.dto.account.request;

import edu.tinkoff.ninjamireaclone.model.Gender;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * DTO for {@link edu.tinkoff.ninjamireaclone.model.Account}
 */
public record UpdateAccountRequestDto(
        Long id,
        @NotBlank String name,
        String description,
        Gender gender,
        @Nullable MultipartFile avatar
) implements Serializable {
}