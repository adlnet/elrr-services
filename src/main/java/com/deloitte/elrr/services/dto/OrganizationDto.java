package com.deloitte.elrr.services.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author mnelakurti
 *
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationDto extends ExtensibleDto {

    @Size(max = 255)
    @NotNull
    private String name;

    private String description;

    @Size(max = 255)
    private String profitType;

    @Size(max = 255)
    private String department;

    @Size(max = 255)
    private String industryCode;

    @Size(max = 255)
    private String industryCategory;

    @Size(max = 255)
    private String verticalSpecialization;

    @Size(max = 255)
    private String organizationIdentifier;

    @Size(max = 255)
    private String organizationDUNS;

    @Size(max = 255)
    private String organizationFEIN;

    @Size(max = 255)
    private String schoolOPEID;

    @Size(max = 255)
    private String ipedsType;

    @Size(max = 255)
    private String organizationISIC;

    @Size(max = 255)
    private String organizationImage;

    @Size(max = 255)
    private String organizationWebsite;

    @Size(max = 255)
    private String institutionLevel;

    @Size(max = 255)
    private String institutionRevocationList;

    private Boolean hasVerificationService;

    private String institutionVerification;

    @Size(max = 255)
    private String organizationalResource;

    @Size(max = 255)
    private String qualityAssuranceType;

}
