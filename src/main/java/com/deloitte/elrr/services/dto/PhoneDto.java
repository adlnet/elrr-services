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
public class PhoneDto extends AuditableDto {

    @Size(max = 255)
    private String telephoneNumber;

    @Size(max = 255)
    private String telephoneNumberType;

}
