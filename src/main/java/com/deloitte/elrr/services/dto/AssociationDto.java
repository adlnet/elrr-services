package com.deloitte.elrr.services.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AssociationDto extends AuditableDto {

    @Valid
    private OrganizationDto organization;

    private String associationType;

}
