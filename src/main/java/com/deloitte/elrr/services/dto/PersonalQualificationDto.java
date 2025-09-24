package com.deloitte.elrr.services.dto;

import java.io.Serializable;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class PersonalQualificationDto<Q extends QualificationDto>
        implements Serializable {

    @Valid
    private Q qualification;

    private Boolean hasRecord;
}
