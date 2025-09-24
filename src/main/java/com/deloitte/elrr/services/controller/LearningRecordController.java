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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.services.dto.LearningRecordDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class LearningRecordController {
    /**
     *
     */
    @Autowired
    private LearningRecordSvc learningRecordSvc;
    /**
     *
     */
    @Autowired
    private ModelMapper mapper;

   /**
    * Retrieve learning records optionally filtered by id or
    * extension criteria.
    *
    * @param filters filter object populated from query parameters
    * @return ResponseEntity containing list of LearningRecordDto
    */
    @PreAuthorize("hasPermission('learningrecord', 'READ')")
    @GetMapping("/learningrecord")
        public ResponseEntity<List<LearningRecordDto>> getAllLearningRecords(
                        @ModelAttribute final LearningRecord.Filter filters) {
        List<LearningRecordDto> learningRecordList = new ArrayList<>();
        learningRecordSvc.findLearningRecordsWithFilters(filters)
                .forEach(lr -> learningRecordList.add(
                        mapper.map(lr, LearningRecordDto.class)));
        return ResponseEntity.ok(learningRecordList);
    }

    /**
     *
     * @param learningRecordId
     * @return ResponseEntity<LearningRecordDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningrecord', 'READ')")
    @GetMapping("/learningrecord/{id}")
    public ResponseEntity<LearningRecordDto> getLearningRecordById(
            @PathVariable(value = "id") final UUID learningRecordId)
            throws ResourceNotFoundException {
        log.debug("Get LearningRecord id:........." + learningRecordId);
        LearningRecord learningRecord = learningRecordSvc.get(learningRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LearningRecord not found for this id :: "
                                + learningRecordId));
        LearningRecordDto learningRecordDto = mapper.map(learningRecord,
                LearningRecordDto.class);
        return ResponseEntity.ok().body(learningRecordDto);
    }

    /**
     *
     * @param learningRecordId
     * @param learningRecordDto
     * @return ResponseEntity<LearningRecordDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningrecord', 'UPDATE')")
    @PutMapping("/learningrecord/{id}")
    public ResponseEntity<LearningRecordDto> updateLearningRecord(
            @PathVariable(value = "id") final UUID learningRecordId,
            @Valid @RequestBody final LearningRecordDto learningRecordDto)
            throws ResourceNotFoundException {
        log.info("Updating  LearningRecord:.........");
        log.info("Updating LearningRecord id:........." + learningRecordId);
        LearningRecord learningRecord = learningRecordSvc.get(learningRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LearningRecord not found for this id to update :: "
                                + learningRecordId));
        Person person = learningRecord.getPerson();
        LearningResource learningResource = learningRecord
                .getLearningResource();
        log.info("Update LearningRecord:........." + learningRecordDto);
        // Assigning values from request
        mapper.map(learningRecordDto, learningRecord);
        // Reset Id / Primary key from query parameter
        learningRecord.setId(learningRecordId);
        learningRecord.setPerson(person);
        learningRecord.setLearningResource(learningResource);
        log.info("Update LearningRecord:........." + learningRecord);
        return ResponseEntity
                .ok(mapper.map(learningRecordSvc.save(learningRecord),
                        LearningRecordDto.class));

    }

    /**
     *
     * @param learningRecordId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningrecord', 'DELETE')")
    @DeleteMapping("/learningrecord/{id}")
    public ResponseEntity<HttpStatus> deleteLearningRecord(
            @PathVariable(value = "id") final UUID learningRecordId)
            throws ResourceNotFoundException {
        log.info("Deleting  LearningRecord:.........");
        log.info("Deleting LearningRecord id:........." + learningRecordId);
        learningRecordSvc.get(learningRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LearningRecord not found for this id to delete :: "
                                + learningRecordId));
        learningRecordSvc.delete(learningRecordId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
