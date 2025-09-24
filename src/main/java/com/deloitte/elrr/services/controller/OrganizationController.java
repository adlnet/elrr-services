package com.deloitte.elrr.services.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.deloitte.elrr.entity.Organization;
import com.deloitte.elrr.jpa.svc.OrganizationSvc;
import com.deloitte.elrr.services.dto.OrganizationDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mnelakurti
 *
 */
@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class OrganizationController {
    /**
     *
     */
    @Autowired
    private OrganizationSvc organizationSvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

    /**
     * Get organizations with optional filtering by id and extensions.
     * @param filters filter criteria (ids, extension filters)
     * @return ResponseEntity containing list of OrganizationDto
     */
    @PreAuthorize("hasPermission('organization', 'READ')")
    @GetMapping("/organization")
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations(
            @ModelAttribute final Organization.Filter filters) {
        List<Organization> organizations = organizationSvc
                .findOrganizationsWithFilters(filters);

        List<OrganizationDto> organizationDtos = organizations.stream()
                .map(organization -> mapper.map(organization,
                        OrganizationDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(organizationDtos);
    }

    /**
     *
     * @param organizationid
     * @return ResponseEntity<OrganizationDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('organization', 'READ')")
    @GetMapping("/organization/{id}")
    public ResponseEntity<OrganizationDto> getOrganizationById(
            @PathVariable(value = "id") final UUID organizationid)
            throws ResourceNotFoundException {
        log.info("GetMapping  Organization:.........");
        log.info("GetMapping Organization id:........." + organizationid);
        Organization organization = organizationSvc.get(organizationid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found for this id :: "
                                + organizationid));
        OrganizationDto organizationDto = mapper.map(organization,
                OrganizationDto.class);
        return ResponseEntity.ok().body(organizationDto);
    }

    /**
     *
     * @param organizationDto
     * @return ResponseEntity<OrganizationDto>
     */
    @PreAuthorize("hasPermission('organization', 'CREATE')")
    @PostMapping("/organization")
    public ResponseEntity<OrganizationDto> createOrganization(
            @Valid @RequestBody final OrganizationDto organizationDto) {
        Organization org = mapper.map(organizationDto, Organization.class);
        OrganizationDto response = mapper.map(organizationSvc.save(org),
                OrganizationDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param organizationid
     * @param organizationDto
     * @return ResponseEntity<OrganizationDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('organization', 'UPDATE')")
    @PutMapping("/organization/{id}")
    public ResponseEntity<OrganizationDto> updateOrganization(
            @PathVariable(value = "id") final UUID organizationid,
            @Valid @RequestBody final OrganizationDto organizationDto)
            throws ResourceNotFoundException {
        log.info("Updating  Organization:.........");
        log.info("Updating Organization id:........." + organizationid);
        Organization organization = organizationSvc.get(organizationid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found for this id to update :: "
                                + organizationid));
        log.info("Update Organization:........." + organizationDto);
        // Assigning values from request
        mapper.map(organizationDto, organization);
        // Reset Id / Primary key from query parameter
        organization.setId(organizationid);
        log.info("Update Organization:........." + organization);
        return ResponseEntity.ok(mapper.map(organizationSvc.save(organization),
                OrganizationDto.class));

    }

    /**
     *
     * @param organizationId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('organization', 'DELETE')")
    @DeleteMapping("/organization/{id}")
    public ResponseEntity<HttpStatus> deleteOrganization(
            @PathVariable(value = "id") final UUID organizationId)
            throws ResourceNotFoundException {
        log.info("Deleting  Organization:.........");
        log.info("Deleting Organization id:........." + organizationId);
        organizationSvc.get(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found for this id to delete :: "
                                + organizationId));
        organizationSvc.delete(organizationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
