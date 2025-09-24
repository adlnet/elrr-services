package com.deloitte.elrr.services.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientTokenDto extends AuditableDto {

    private String token;
    private UUID jwtId;
    private String label;

}
