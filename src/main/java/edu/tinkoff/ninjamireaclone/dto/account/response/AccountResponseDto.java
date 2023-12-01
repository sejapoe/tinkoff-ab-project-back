package edu.tinkoff.ninjamireaclone.dto.account.response;

import edu.tinkoff.ninjamireaclone.dto.document.response.DocumentResponseDto;
import edu.tinkoff.ninjamireaclone.model.Gender;

/**
 * DTO for {@link edu.tinkoff.ninjamireaclone.model.Account}
 */
public record AccountResponseDto(
        Long id,
        String name,
        String displayName,
        String description,
        Gender gender,
        DocumentResponseDto avatar
) {
}