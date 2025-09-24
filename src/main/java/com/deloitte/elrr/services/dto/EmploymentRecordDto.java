package com.deloitte.elrr.services.dto;

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
public class EmploymentRecordDto extends ExtensibleDto {

    @Valid
    private OrganizationDto employerOrganization;

    @Size(max = 255)
    private String customEmploymentRecordId;

    @Size(max = 255)
    private String employeeType;

    private LocalDate hireDate;

    @Size(max = 255)
    private String hireType;

    private LocalDate employmentStartDate;

    private LocalDate employmentEndDate;

    @Size(max = 255)
    private String position;

    @Size(max = 255)
    private String positionTitle;

    private String positionDescription;

    @Size(max = 255)
    private String jobLevel;

    @Size(max = 255)
    private String occupation;

    @Valid
    private LocationDto employmentLocation;

    @Valid
    private FacilityDto employmentFacility;

    @Valid
    private Set<CredentialDto> credentials;

    @Valid
    private Set<CompetencyDto> competencies;

}
