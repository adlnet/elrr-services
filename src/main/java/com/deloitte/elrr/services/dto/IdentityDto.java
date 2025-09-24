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
public class IdentityDto extends AuditableDto {

    @Size(max = 255)
    private String mboxSha1Sum;

    @Size(max = 255)
    private String mbox;

    @Size(max = 255)
    private String openid;

    @Size(max = 255)
    private String homePage;

    @Size(max = 255)
    private String name;

}
