package com.deloitte.elrr.services.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.services.dto.LearningResourceDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class LearningResourceController {
    /**
     *
     */
    @Autowired
    private LearningResourceSvc learningResourceSvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

    /**
     * Retrieve learning resources optionally filtered by id or
     * extension criteria.
     *
     * @param filters filter object populated from query parameters
     * @return ResponseEntity containing list of LearningResourceDto
     */
    @PreAuthorize("hasPermission('learningresource', 'READ')")
    @GetMapping("/learningresource")
    public ResponseEntity<List<LearningResourceDto>> getAllLearningResources(
            @ModelAttribute final LearningResource.Filter filters) {
        List<LearningResourceDto> learningResourceList = new ArrayList<>();
        learningResourceSvc.findLearningResourcesWithFilters(filters)
            .forEach(lr -> learningResourceList.add(
                    mapper.map(lr, LearningResourceDto.class)));
        return ResponseEntity.ok(learningResourceList);
    }

    /**
     *
     * @param learningResourceId
     * @return ResponseEntity<LearningResourceDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningresource', 'READ')")
    @GetMapping("/learningresource/{id}")
    public ResponseEntity<LearningResourceDto> getLearningResourceById(
            @PathVariable(value = "id") final UUID learningResourceId)
            throws ResourceNotFoundException {
        log.debug("Get LearningResource id:........." + learningResourceId);
        LearningResource learningResource = learningResourceSvc.get(
                learningResourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LearningResource not found for this id :: "
                                + learningResourceId));
        LearningResourceDto learningResourceDto = mapper.map(learningResource,
                LearningResourceDto.class);
        return ResponseEntity.ok().body(learningResourceDto);
    }

    /**
     *
     * @param learningResourceDto
     * @return ResponseEntity<LearningResourceDto>
     */
    @PreAuthorize("hasPermission('learningresource', 'CREATE')")
    @PostMapping("/learningresource")
    public ResponseEntity<LearningResourceDto> createLearningResource(
            @Valid @RequestBody final LearningResourceDto learningResourceDto) {
        LearningResource learningResource = mapper.map(learningResourceDto,
            LearningResource.class);
        LearningResourceDto response = mapper.map(learningResourceSvc.save(
                learningResource), LearningResourceDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param learningResourceId
     * @param learningResourceDto
     * @return ResponseEntity<LearningResourceDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningresource', 'UPDATE')")
    @PutMapping("/learningresource/{id}")
    public ResponseEntity<LearningResourceDto> updateLearningResource(
            @PathVariable(value = "id") final UUID learningResourceId,
            @Valid @RequestBody final LearningResourceDto learningResourceDto)
            throws ResourceNotFoundException {
        log.info("Updating  LearningResource:.........");
        log.info("Updating LearningResource id:........." + learningResourceId);
        LearningResource learningResource = learningResourceSvc.get(
            learningResourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LearningResource not found for this id to update :: "
                                + learningResourceId));
        log.info("Update LearningResource:........." + learningResourceDto);
        // Assigning values from request
        mapper.map(learningResourceDto, learningResource);
        // Reset Id / Primary key from query parameter
        learningResource.setId(learningResourceId);
        log.info("Update LearningResource:........." + learningResource);
        return ResponseEntity.ok(mapper.map(
            learningResourceSvc.save(learningResource),
                LearningResourceDto.class));

    }

    /**
     *
     * @param learningResourceId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningresource', 'DELETE')")
    @DeleteMapping("/learningresource/{id}")
    public ResponseEntity<HttpStatus> deleteLearningResource(
            @PathVariable(value = "id") final UUID learningResourceId)
            throws ResourceNotFoundException {
        log.info("Deleting  LearningResource:.........");
        log.info("Deleting LearningResource id:........." + learningResourceId);
        learningResourceSvc.get(learningResourceId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "LearningResource not found for this id to delete :: "
                + learningResourceId));
        learningResourceSvc.delete(learningResourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
