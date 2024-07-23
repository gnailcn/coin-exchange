package com.gnailcn.mappers;

import com.gnailcn.domain.UserBank;
import com.gnailcn.dto.UserBankDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserBankDdtoMapper {
    UserBankDdtoMapper INSTANCE = Mappers.getMapper(UserBankDdtoMapper.class);

    UserBank toConvertEntity(UserBankDto source);

    List<UserBank> toConvertEntity(List<UserBankDto> source);

    UserBankDto toConvertDto(UserBank source);

    List<UserBankDto> toConvertDto(List<UserBank> source);

}
