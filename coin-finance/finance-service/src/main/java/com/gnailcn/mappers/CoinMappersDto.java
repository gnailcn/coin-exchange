package com.gnailcn.mappers;

import com.gnailcn.domain.Coin;
import com.gnailcn.dto.CoinDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoinMappersDto {
    CoinMappersDto INSTANCE = Mappers.getMapper(CoinMappersDto.class);

    Coin toConveertEntity(CoinDto source);

    List<Coin> toConveertEntityList(List<CoinDto> source);

    CoinDto toConveertDto(Coin source);

    List<CoinDto> toConveertDtoList(List<Coin> source);
}
