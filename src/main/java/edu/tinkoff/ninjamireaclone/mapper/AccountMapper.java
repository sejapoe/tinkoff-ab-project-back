package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.account.request.UpdateAccountRequestDto;
import edu.tinkoff.ninjamireaclone.dto.account.response.AccountResponseDto;
import edu.tinkoff.ninjamireaclone.dto.auth.request.SignUpRequestDto;
import edu.tinkoff.ninjamireaclone.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DocumentMapper.class})
public interface AccountMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayName", source = "requestDto.name")
    @Mapping(target = "gender", expression = "java(Gender.NOT_SPECIFIED)")
    @Mapping(target = "description", expression = "java(\"\")")
    @Mapping(target = "avatar", ignore = true)
    Account toAccount(SignUpRequestDto requestDto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "displayName", source = "requestDto.name")
    Account toAccount(UpdateAccountRequestDto requestDto);


    AccountResponseDto toAccountDto(Account account);
}
