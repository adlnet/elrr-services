package com.deloitte.elrr.services.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.deloitte.elrr.entity.EmploymentRecord;
import com.deloitte.elrr.entity.Organization;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.EmploymentRecordSvc;
import com.deloitte.elrr.jpa.svc.FacilitySvc;
import com.deloitte.elrr.jpa.svc.LocationSvc;
import com.deloitte.elrr.services.dto.CompetencyDto;
import com.deloitte.elrr.services.dto.CredentialDto;
import com.deloitte.elrr.services.dto.EmploymentRecordDto;
import com.deloitte.elrr.services.dto.FacilityDto;
import com.deloitte.elrr.services.dto.LocationDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class EmploymentRecordController {
    /**
     *
     */
    @Autowired
    private EmploymentRecordSvc employmentRecordSvc;

    @Autowired
    private LocationSvc locationSvc;

    @Autowired
    private FacilitySvc facilitySvc;

    @Autowired
    private CompetencySvc competencySvc;

    @Autowired
    private CredentialSvc credentialSvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

    /**
     *
     * @param filters
     * @return ResponseEntity<List<EmploymentRecordDto>>
     */
    @PreAuthorize("hasPermission('employmentrecord', 'READ')")
    @GetMapping("/employmentrecord")
    public ResponseEntity<List<EmploymentRecordDto>> getAllEmploymentRecords(
            @ModelAttribute final EmploymentRecord.Filter filters) {
        List<EmploymentRecord> employmentRecords = employmentRecordSvc
                .findEmploymentRecordsWithFilters(filters);

        List<EmploymentRecordDto> employmentRecordDtos = employmentRecords
                .stream()
                .map(employmentRecord -> mapper.map(employmentRecord,
                        EmploymentRecordDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(employmentRecordDtos);
    }

    /**
     *
     * @param employmentRecordId
     * @return ResponseEntity<EmploymentRecordDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('employmentrecord', 'READ')")
    @GetMapping("/employmentrecord/{id}")
    public ResponseEntity<EmploymentRecordDto> getEmploymentRecordById(
            @PathVariable(value = "id") final UUID employmentRecordId)
            throws ResourceNotFoundException {
        log.debug("Get EmploymentRecord id:........." + employmentRecordId);
        EmploymentRecord employmentRecord = employmentRecordSvc
                .get(employmentRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmploymentRecord not found for this id :: "
                                + employmentRecordId));
        EmploymentRecordDto employmentRecordDto = mapper.map(employmentRecord,
                EmploymentRecordDto.class);
        return ResponseEntity.ok().body(employmentRecordDto);
    }

    /**
     *
     * @param employmentRecordId
     * @param employmentRecordDto
     * @return ResponseEntity<EmploymentRecordDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('employmentrecord', 'UPDATE')")
    @PutMapping("/employmentrecord/{id}")
    public ResponseEntity<EmploymentRecordDto> updateEmploymentRecord(
            @PathVariable(value = "id") final UUID employmentRecordId,
            @Valid @RequestBody final EmploymentRecordDto employmentRecordDto)
            throws ResourceNotFoundException {
        log.info("Updating  EmploymentRecord:.........");
        log.info("Updating EmploymentRecord id:........." + employmentRecordId);
        EmploymentRecord employmentRecord = employmentRecordSvc
                .get(employmentRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmploymentRecord not found for this id to update :: "
                                + employmentRecordId));
        Person employee = employmentRecord.getEmployee();
        Organization organization = employmentRecord.getEmployerOrganization();

        log.info("Update EmploymentRecord:........." + employmentRecordDto);

        LocationDto newLocation = employmentRecordDto.getEmploymentLocation();
        FacilityDto newFacility = employmentRecordDto.getEmploymentFacility();
        Set<CompetencyDto> newCompetencies = employmentRecordDto
                .getCompetencies();
        Set<CredentialDto> newCredentials = employmentRecordDto
                .getCredentials();
        // temporarily null associated objects so that mapper call does not
        // corrupt entities
        employmentRecordDto.setEmploymentFacility(null);
        employmentRecordDto.setEmploymentLocation(null);
        employmentRecordDto.setCompetencies(null);
        employmentRecordDto.setCredentials(null);

        mapper.map(employmentRecordDto, employmentRecord);

        employmentRecord.setId(employmentRecordId);
        employmentRecord.setEmployee(employee);
        employmentRecord.setEmployerOrganization(organization);

        // Set Facility and Location
        employmentRecord.setEmploymentLocation(
                (newLocation != null && newLocation.getId() != null)
                        ? locationSvc.get(newLocation.getId())
                                .orElseThrow(
                                        () -> new ResourceNotFoundException(
                                            "Location not found for this id :: "
                                            + newLocation.getId()))
                        : null);
        employmentRecord.setEmploymentFacility(
                (newFacility != null && newFacility.getId() != null)
                        ? facilitySvc.get(newFacility.getId())
                                .orElseThrow(
                                        () -> new ResourceNotFoundException(
                                            "Facility not found for this id :: "
                                            + newFacility.getId()))
                        : null);

        // Set Competencies and Credentials
        if (newCompetencies != null)
            employmentRecord.setCompetencies(newCompetencies.stream()
                    .map(c -> competencySvc.get(c.getId()).orElse(null))
                    .collect(Collectors.toSet()));
        if (newCredentials != null)
            employmentRecord.setCredentials(newCredentials.stream()
                    .map(c -> credentialSvc.get(c.getId()).orElse(null))
                    .collect(Collectors.toSet()));

        log.info("Update EmploymentRecord:........." + employmentRecord);
        return ResponseEntity
                .ok(mapper.map(employmentRecordSvc.save(employmentRecord),
                        EmploymentRecordDto.class));

    }

    /**
     *
     * @param employmentRecordId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('employmentrecord', 'DELETE')")
    @DeleteMapping("/employmentrecord/{id}")
    public ResponseEntity<HttpStatus> deleteEmploymentRecord(
            @PathVariable(value = "id") final UUID employmentRecordId)
            throws ResourceNotFoundException {
        log.info("Deleting  EmploymentRecord:.........");
        log.info("Deleting EmploymentRecord id:........." + employmentRecordId);
        employmentRecordSvc.get(employmentRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmploymentRecord not found for this id to delete :: "
                                + employmentRecordId));
        employmentRecordSvc.delete(employmentRecordId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
