package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.account.request.UpdateAccountRequestDto;
import edu.tinkoff.ninjamireaclone.dto.account.response.AccountResponseDto;
import edu.tinkoff.ninjamireaclone.dto.auth.request.SignUpRequestDto;
import edu.tinkoff.ninjamireaclone.dto.common.PageResponseDto;
import edu.tinkoff.ninjamireaclone.model.AccountEntity;
import edu.tinkoff.ninjamireaclone.model.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = DocumentMapper.class)
public interface AccountMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "gender", expression = "java(Gender.NOT_SPECIFIED)")
    @Mapping(target = "description", expression = "java(\"\")")
    @Mapping(target = "avatar", ignore = true)
    AccountEntity toAccount(SignUpRequestDto requestDto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "displayName", source = "name")
    AccountEntity toAccount(UpdateAccountRequestDto requestDto);

    AccountResponseDto toAccountResponseDto(AccountEntity account);

    PageResponseDto<AccountResponseDto> toPageResponseDto(Page<AccountEntity> page);

    default String roleToString(RoleEntity role) {
        return role.getName();
    }
}
