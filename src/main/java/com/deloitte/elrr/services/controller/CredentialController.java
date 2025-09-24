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

import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.services.dto.CredentialDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class CredentialController {
    /**
     *
     */
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
     * @return ResponseEntity<List<CredentialDto>>
     */
    @PreAuthorize("hasPermission('credential', 'READ')")
    @GetMapping("/credential")
    public ResponseEntity<List<CredentialDto>> getAllCredentials(
            @ModelAttribute final Credential.Filter filters) {
        List<Credential> credentials = credentialSvc
                .findCredentialsWithFilters(filters);

        List<CredentialDto> credentialDtos = credentials.stream()
                .map(credential -> mapper.map(credential, CredentialDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(credentialDtos);
    }

    /**
     *
     * @param credentialId
     * @return ResponseEntity<CredentialDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('credential', 'READ')")
    @GetMapping("/credential/{id}")
    public ResponseEntity<CredentialDto> getCredentialById(
            @PathVariable(value = "id") final UUID credentialId)
            throws ResourceNotFoundException {
        log.debug("Get Credential id:........." + credentialId);
        Credential credential = credentialSvc.get(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credential not found for this id :: "
                                + credentialId));
        CredentialDto credentialDto = mapper.map(credential,
                CredentialDto.class);
        return ResponseEntity.ok().body(credentialDto);
    }

    /**
     *
     * @param credentialDto
     * @return ResponseEntity<CredentialDto>
     */
    @PreAuthorize("hasPermission('credential', 'CREATE')")
    @PostMapping("/credential")
    public ResponseEntity<CredentialDto> createCredential(
            @Valid @RequestBody final CredentialDto credentialDto) {
        Credential credential = mapper.map(credentialDto, Credential.class);
        CredentialDto response = mapper.map(credentialSvc.save(credential),
                CredentialDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param credentialId
     * @param credentialDto
     * @return ResponseEntity<CredentialDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('credential', 'UPDATE')")
    @PutMapping("/credential/{id}")
    public ResponseEntity<CredentialDto> updateCredential(
            @PathVariable(value = "id") final UUID credentialId,
            @Valid @RequestBody final CredentialDto credentialDto)
            throws ResourceNotFoundException {
        log.info("Updating  Credential:.........");
        log.info("Updating Credential id:........." + credentialId);
        Credential credential = credentialSvc.get(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credential not found for this id to update :: "
                                + credentialId));
        log.info("Update Credential:........." + credentialDto);
        // Assigning values from request
        mapper.map(credentialDto, credential);
        // Reset Id / Primary key from query parameter
        credential.setId(credentialId);
        log.info("Update Credential:........." + credential);
        return ResponseEntity.ok(mapper.map(credentialSvc.save(credential),
                CredentialDto.class));

    }

    /**
     *
     * @param credentialId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('credential', 'DELETE')")
    @DeleteMapping("/credential/{id}")
    public ResponseEntity<HttpStatus> deleteCredential(
            @PathVariable(value = "id") final UUID credentialId)
            throws ResourceNotFoundException {
        log.info("Deleting  Credential:.........");
        log.info("Deleting Credential id:........." + credentialId);
        credentialSvc.get(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credential not found for this id to delete :: "
                                + credentialId));
        credentialSvc.delete(credentialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
