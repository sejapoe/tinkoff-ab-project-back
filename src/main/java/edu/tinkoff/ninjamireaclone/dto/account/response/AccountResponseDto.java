package edu.tinkoff.ninjamireaclone.dto.account.response;

import edu.tinkoff.ninjamireaclone.dto.document.response.DocumentResponseDto;
import edu.tinkoff.ninjamireaclone.model.AccountEntity;
import edu.tinkoff.ninjamireaclone.model.Gender;

import java.util.List;

/**
 * DTO for {@link AccountEntity}
 */
public record AccountResponseDto(
        Long id,
        String name,
        String displayName,
        String description,
        Gender gender,
        DocumentResponseDto avatar,
        boolean enabled,
        List<String> roles
) {
}