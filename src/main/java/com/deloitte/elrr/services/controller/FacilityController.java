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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.deloitte.elrr.services.dto.FacilityDto;

import com.deloitte.elrr.entity.Facility;
import com.deloitte.elrr.jpa.svc.FacilitySvc;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class FacilityController {

    @Autowired
    private FacilitySvc facilitySvc;

    @Autowired
    private ModelMapper mapper;

    /**
     * Get facilities with optional filtering by id and extensions.
     * @param filters filter criteria (ids, extension filters)
     * @return ResponseEntity containing list of FacilityDto
     */
    @PreAuthorize("hasPermission('facility', 'READ')")
    @GetMapping("/facility")
    public ResponseEntity<List<FacilityDto>> getAllFacilitys(
            @ModelAttribute final Facility.Filter filters) {
        List<Facility> facilities = facilitySvc
                .findFacilitiesWithFilters(filters);

        List<FacilityDto> facilityDtos = facilities.stream()
                .map(facility -> mapper.map(facility, FacilityDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(facilityDtos);
    }

    /**
     *
     * @param facilityId
     * @return ResponseEntity<FacilityDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('facility', 'READ')")
    @GetMapping("/facility/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(
            @PathVariable(value = "id") final UUID facilityId)
            throws ResourceNotFoundException {
        log.debug("Get Facility id:........." + facilityId);
        Facility facility = facilitySvc.get(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found for this id :: "
                                + facilityId));
        FacilityDto facilityDto = mapper.map(facility,
                FacilityDto.class);
        return ResponseEntity.ok().body(facilityDto);
    }

    /**
     *
     * @param facilityDto
     * @return ResponseEntity<FacilityDto>
     */
    @PreAuthorize("hasPermission('facility', 'CREATE')")
    @PostMapping("/facility")
    public ResponseEntity<FacilityDto> createFacility(
            @Valid @RequestBody final FacilityDto facilityDto) {
        Facility facility = mapper.map(facilityDto, Facility.class);
        FacilityDto response = mapper.map(facilitySvc.save(facility),
                FacilityDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param facilityId
     * @param facilityDto
     * @return ResponseEntity<FacilityDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('facility', 'UPDATE')")
    @PutMapping("/facility/{id}")
    public ResponseEntity<FacilityDto> updateFacility(
            @PathVariable(value = "id") final UUID facilityId,
            @Valid @RequestBody final FacilityDto facilityDto)
            throws ResourceNotFoundException {
        log.info("Updating  Facility:.........");
        log.info("Updating Facility id:........." + facilityId);
        Facility facility = facilitySvc.get(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found for this id to update :: "
                                + facilityId));
        log.info("Update Facility:........." + facilityDto);
        // Assigning values from request
        mapper.map(facilityDto, facility);
        // Reset Id / Primary key from query parameter
        facility.setId(facilityId);
        log.info("Update Facility:........." + facility);
        return ResponseEntity.ok(mapper.map(facilitySvc.save(facility),
                FacilityDto.class));

    }

    /**
     *
     * @param facilityId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('facility', 'DELETE')")
    @DeleteMapping("/facility/{id}")
    public ResponseEntity<HttpStatus> deleteFacility(
            @PathVariable(value = "id") final UUID facilityId)
            throws ResourceNotFoundException {
        log.info("Deleting  Facility:.........");
        log.info("Deleting Facility id:........." + facilityId);
        facilitySvc.get(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found for this id to delete :: "
                                + facilityId));
        facilitySvc.delete(facilityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
