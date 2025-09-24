package com.deloitte.elrr.services.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationDto extends ExtensibleDto {

    @Size(max = 255)
    private String streetNumberAndName;

    @Size(max = 255)
    private String apartmentRoomSuiteNumber;

    @Size(max = 255)
    private String city;

    @Size(max = 255)
    private String stateAbbreviation;

    @Size(max = 255)
    private String postalCode;

    @Size(max = 255)
    private String county;

    @Size(max = 255)
    private String countryCode;

    @Size(max = 255)
    private String latitude;

    @Size(max = 255)
    private String longitude;

}
