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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.deloitte.elrr.entity.Phone;
import com.deloitte.elrr.jpa.svc.PhoneSvc;
import com.deloitte.elrr.services.dto.PhoneDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class PhoneController {
    /**
     *
     */
    @Autowired
    private PhoneSvc phoneSvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

    /**
     *
     * @param phoneId
     * @return ResponseEntity<List<PhoneDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('phone', 'READ')")
    @GetMapping("/phone")
    public ResponseEntity<List<PhoneDto>> getAllPhones(
            @RequestParam(value = "id", required = false) final UUID phoneId) {
        log.debug("Get Phone id:........." + phoneId);
        List<PhoneDto> phoneList = new ArrayList<>();
        if (phoneId == null) {
            phoneSvc.findAll().forEach(phn -> phoneList.add(
                    mapper.map(phn, PhoneDto.class)));
        } else {
            phoneSvc.get(phoneId).ifPresent(phone -> {
                PhoneDto phoneDto = mapper.map(phone, PhoneDto.class);
                phoneList.add(phoneDto);
            });
        }

        return ResponseEntity.ok(phoneList);
    }

    /**
     *
     * @param phoneId
     * @return ResponseEntity<PhoneDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('phone', 'READ')")
    @GetMapping("/phone/{id}")
    public ResponseEntity<PhoneDto> getPhoneById(
            @PathVariable(value = "id") final UUID phoneId)
            throws ResourceNotFoundException {
        log.debug("Get Phone id:........." + phoneId);
        Phone phone = phoneSvc.get(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phone not found for this id :: "
                                + phoneId));
        PhoneDto phoneDto = mapper.map(phone,
                PhoneDto.class);
        return ResponseEntity.ok().body(phoneDto);
    }

    /**
     *
     * @param phoneDto
     * @return ResponseEntity<PhoneDto>
     */
    @PreAuthorize("hasPermission('phone', 'CREATE')")
    @PostMapping("/phone")
    public ResponseEntity<PhoneDto> createPhone(
            @Valid @RequestBody final PhoneDto phoneDto) {
        Phone phone = mapper.map(phoneDto, Phone.class);
        PhoneDto response = mapper.map(phoneSvc.save(phone), PhoneDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param phoneId
     * @param phoneDto
     * @return ResponseEntity<PhoneDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('phone', 'UPDATE')")
    @PutMapping("/phone/{id}")
    public ResponseEntity<PhoneDto> updatePhone(
            @PathVariable(value = "id") final UUID phoneId,
            @Valid @RequestBody final PhoneDto phoneDto)
            throws ResourceNotFoundException {
        log.info("Updating  Phone:.........");
        log.info("Updating Phone id:........." + phoneId);
        Phone phone = phoneSvc.get(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phone not found for this id to update :: "
                                + phoneId));
        log.info("Update Phone:........." + phoneDto);
        // Assigning values from request
        mapper.map(phoneDto, phone);
        // Reset Id / Primary key from query parameter
        phone.setId(phoneId);
        log.info("Update Phone:........." + phone);
        return ResponseEntity.ok(mapper.map(phoneSvc.save(phone),
                PhoneDto.class));

    }

    /**
     *
     * @param phoneId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('phone', 'DELETE')")
    @DeleteMapping("/phone/{id}")
    public ResponseEntity<HttpStatus> deletePhone(
            @PathVariable(value = "id") final UUID phoneId)
            throws ResourceNotFoundException {
        log.info("Deleting  Phone:.........");
        log.info("Deleting Phone id:........." + phoneId);
        phoneSvc.get(phoneId).orElseThrow(() -> new ResourceNotFoundException(
                "Phone not found for this id to delete :: " + phoneId));
        phoneSvc.delete(phoneId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
