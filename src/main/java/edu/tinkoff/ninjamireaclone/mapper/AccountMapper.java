package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.account.response.AccountResponseDto;
import edu.tinkoff.ninjamireaclone.dto.auth.request.SignUpRequestDto;
import edu.tinkoff.ninjamireaclone.dto.common.PageResponseDto;
import edu.tinkoff.ninjamireaclone.model.Account;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toAccount(SignUpRequestDto requestDto);

    AccountResponseDto toAccountResponseDto(Account account);

    PageResponseDto<AccountResponseDto> toPageResponseDto(Page<Account> page);
}
