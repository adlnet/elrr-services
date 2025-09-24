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

import com.deloitte.elrr.entity.Location;
import com.deloitte.elrr.jpa.svc.LocationSvc;
import com.deloitte.elrr.services.dto.LocationDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class LocationController {
    /**
     *
     */
    @Autowired
    private LocationSvc locationSvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

    /**
     * Get locations with optional filtering by id and extensions.
     * @param filters filter criteria (ids, extension filters)
     * @return ResponseEntity containing list of LocationDto
     */
    @PreAuthorize("hasPermission('location', 'READ')")
    @GetMapping("/location")
    public ResponseEntity<List<LocationDto>> getAllLocations(
            @ModelAttribute final Location.Filter filters) {
        List<Location> locations = locationSvc
                .findLocationsWithFilters(filters);

        List<LocationDto> locationDtos = locations.stream()
                .map(location -> mapper.map(location, LocationDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(locationDtos);
    }

    /**
     *
     * @param locationId
     * @return ResponseEntity<LocationDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('location', 'READ')")
    @GetMapping("/location/{id}")
    public ResponseEntity<LocationDto> getLocationById(
            @PathVariable(value = "id") final UUID locationId)
            throws ResourceNotFoundException {
        log.debug("Get Location id:........." + locationId);
        Location location = locationSvc.get(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found for this id :: "
                                + locationId));
        LocationDto locationDto = mapper.map(location,
                LocationDto.class);
        return ResponseEntity.ok().body(locationDto);
    }

    /**
     *
     * @param locationDto
     * @return ResponseEntity<LocationDto>
     */
    @PreAuthorize("hasPermission('location', 'CREATE')")
    @PostMapping("/location")
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody final LocationDto locationDto) {
        Location location = mapper.map(locationDto, Location.class);
        LocationDto response = mapper.map(locationSvc.save(location),
                LocationDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param locationId
     * @param locationDto
     * @return ResponseEntity<LocationDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('location', 'UPDATE')")
    @PutMapping("/location/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable(value = "id") final UUID locationId,
            @Valid @RequestBody final LocationDto locationDto)
            throws ResourceNotFoundException {
        log.info("Updating  Location:.........");
        log.info("Updating Location id:........." + locationId);
        Location location = locationSvc.get(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found for this id to update :: "
                                + locationId));
        log.info("Update Location:........." + locationDto);
        // Assigning values from request
        mapper.map(locationDto, location);
        // Reset Id / Primary key from query parameter
        location.setId(locationId);
        log.info("Update Location:........." + location);
        return ResponseEntity.ok(mapper.map(locationSvc.save(location),
                LocationDto.class));

    }

    /**
     *
     * @param locationId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('location', 'DELETE')")
    @DeleteMapping("/location/{id}")
    public ResponseEntity<HttpStatus> deleteLocation(
            @PathVariable(value = "id") final UUID locationId)
            throws ResourceNotFoundException {
        log.info("Deleting  Location:.........");
        log.info("Deleting Location id:........." + locationId);
        locationSvc.get(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found for this id to delete :: "
                                + locationId));
        locationSvc.delete(locationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
