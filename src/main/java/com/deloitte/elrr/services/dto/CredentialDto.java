package com.deloitte.elrr.services.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CredentialDto extends QualificationDto {
    private final String type = "CREDENTIAL";
}
