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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.services.dto.CompetencyDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class CompetencyController {
    /**
     *
     */
    @Autowired
    private CompetencySvc competencySvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

    /**
     *
     * @param filters
     * @return ResponseEntity<List<CompetencyDto>>
     */
    @PreAuthorize("hasPermission('competency', 'READ')")
    @GetMapping("/competency")
    public ResponseEntity<List<CompetencyDto>> getAllCompetencies(
            @ModelAttribute final Competency.Filter filters) {
        List<Competency> competencies = competencySvc
                .findCompetenciesWithFilters(filters);

        List<CompetencyDto> competencyDtos = competencies.stream()
                .map(competency -> mapper.map(competency, CompetencyDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(competencyDtos);
    }

    /**
     *
     * @param competencyId
     * @return ResponseEntity<CompetencyDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('competency', 'READ')")
    @GetMapping("/competency/{id}")
    public ResponseEntity<CompetencyDto> getCompetencyById(
            @PathVariable(value = "id") final UUID competencyId)
            throws ResourceNotFoundException {
        log.debug("Get Competency id:........." + competencyId);
        Competency competency = competencySvc.get(competencyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Competency not found for this id :: "
                                + competencyId));
        CompetencyDto competencyDto = mapper.map(competency,
                CompetencyDto.class);
        return ResponseEntity.ok().body(competencyDto);
    }

    /**
     *
     * @param competencyDto
     * @return ResponseEntity<CompetencyDto>
     */
    @PreAuthorize("hasPermission('competency', 'CREATE')")
    @PostMapping("/competency")
    public ResponseEntity<CompetencyDto> createCompetency(
            @Valid @RequestBody final CompetencyDto competencyDto) {
        Competency org = mapper.map(competencyDto, Competency.class);
        CompetencyDto response = mapper.map(competencySvc.save(org),
                CompetencyDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param competencyId
     * @param competencyDto
     * @return ResponseEntity<CompetencyDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('competency', 'UPDATE')")
    @PutMapping("/competency/{id}")
    public ResponseEntity<CompetencyDto> updateCompetency(
            @PathVariable(value = "id") final UUID competencyId,
            @Valid @RequestBody final CompetencyDto competencyDto)
            throws ResourceNotFoundException {
        log.info("Updating  Competency:.........");
        log.info("Updating Competency id:........." + competencyId);
        Competency competency = competencySvc.get(competencyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Competency not found for this id to update :: "
                                + competencyId));
        log.info("Update Competency:........." + competencyDto);
        // Assigning values from request
        mapper.map(competencyDto, competency);
        // Reset Id / Primary key from query parameter
        competency.setId(competencyId);
        log.info("Update Competency:........." + competency);
        return ResponseEntity.ok(mapper.map(competencySvc.save(competency),
                CompetencyDto.class));

    }

    /**
     *
     * @param competencyId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('competency', 'DELETE')")
    @DeleteMapping("/competency/{id}")
    public ResponseEntity<HttpStatus> deleteCompetency(
            @PathVariable(value = "id") final UUID competencyId)
            throws ResourceNotFoundException {
        log.info("Deleting  Competency:.........");
        log.info("Deleting Competency id:........." + competencyId);
        competencySvc.get(competencyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Competency not found for this id to delete :: "
                                + competencyId));
        competencySvc.delete(competencyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
