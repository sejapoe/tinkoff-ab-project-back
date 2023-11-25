package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.auth.request.SignUpRequestDto;
import edu.tinkoff.ninjamireaclone.dto.auth.response.SignUpResponseDto;
import edu.tinkoff.ninjamireaclone.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toAccount(SignUpRequestDto requestDto);

    SignUpResponseDto toAccountDto(Account account);
}
