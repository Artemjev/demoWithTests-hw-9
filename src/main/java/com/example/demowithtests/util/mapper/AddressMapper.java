package com.example.demowithtests.util.mapper;


import com.example.demowithtests.domain.Address;
import com.example.demowithtests.dto.address.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDto addressToAddressDto(Address address);
//    Address addressDtoToAddress(AddressDto addressDto);
}