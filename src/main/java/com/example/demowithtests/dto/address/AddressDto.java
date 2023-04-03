package com.example.demowithtests.dto.address;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AddressDto {
    public Long id;
    public Boolean addressHasActive = Boolean.TRUE;
    public String country;
    public String city;
    public String street;
    public LocalDateTime datetime = LocalDateTime.now();
}
