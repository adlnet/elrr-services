package com.deloitte.elrr.services.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityDto extends ExtensibleDto {


    @Size(max = 255)
    private String name;

    private String description;

    @Size(max = 255)
    private String operationalStatus;

    @Size(max = 255)
    private String facilitySecurityLevel;

    @Valid
    private LocationDto location;
}
