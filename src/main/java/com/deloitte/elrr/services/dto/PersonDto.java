package com.deloitte.elrr.services.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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
public class PersonDto extends ExtensibleDto {

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String middleName;

    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String namePrefix;

    @Size(max = 255)
    private String titleAffixCode;

    @Size(max = 255)
    private String nameSuffix;

    @Size(max = 255)
    private String qualificationAffixCode;

    @Size(max = 255)
    private String maidenName;

    private LocalDate birthdate;

    @Size(max = 255)
    private String citizenship;

    private BigDecimal height;

    @Size(max = 255)
    private String heightUnit;

    private BigDecimal weight;

    @Size(max = 255)
    private String weightUnit;

    private Long interpupillaryDistance;

    @Size(max = 255)
    private String handedness;

    @Size(max = 255)
    private String primaryLanguage;

    @Size(max = 255)
    private String currentSecurityClearance;

    @Size(max = 255)
    private String highestSecurityClearance;

    private Boolean unionMembership;

    @Valid
    private LocationDto mailingAddress;

    @Valid
    private LocationDto physicalAddress;

    @Valid
    private LocationDto shippingAddress;

    @Valid
    private LocationDto billingAddress;

    @Valid
    private LocationDto onCampusAddress;

    @Valid
    private LocationDto offCampusAddress;

    @Valid
    private LocationDto temporaryAddress;

    @Valid
    private LocationDto permanentStudentAddress;

    @Valid
    private LocationDto employmentAddress;

    @Valid
    private LocationDto timeOfAdmissionAddress;

    @Valid
    private LocationDto fatherAddress;

    @Valid
    private LocationDto motherAddress;

    @Valid
    private LocationDto guardianAddress;

    @Valid
    private LocationDto birthplaceAddress;

    @Valid
    private Set<EmailDto> emailAddresses;

    @Valid
    private Set<PhoneDto> phoneNumbers;
}
