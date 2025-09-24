package com.deloitte.elrr.services.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class QualificationDto extends ExtensibleDto {

    @Size(max = 100)
    private String identifier;

    private String identifierUrl;

    @Size(max = 100)
    private String code;

    @Size(max = 100)
    private String taxonomyId;

    private ZonedDateTime validStartDate;

    private ZonedDateTime validEndDate;

    @Size(max = 100)
    private String parentId;

    private String parentUrl;

    @Size(max = 100)
    private String parentCode;

    private String typeUrl;

    private String statement;

    @Size(max = 100)
    private String frameworkTitle;

    @Size(max = 100)
    private String frameworkVersion;

    @Size(max = 100)
    private String frameworkIdentifier;

    private String frameworkDescription;

    @Size(max = 100)
    private String frameworkSubject;

    private LocalDate frameworkValidStartDate;

    private LocalDate frameworkValidEndDate;

    @Size(max = 10)
    private String recordStatus;
}
