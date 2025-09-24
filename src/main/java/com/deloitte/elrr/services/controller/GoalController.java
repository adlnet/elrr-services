package com.deloitte.elrr.services.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.deloitte.elrr.entity.Goal;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.GoalSvc;
import com.deloitte.elrr.services.dto.GoalDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class GoalController {

    @Autowired
    private GoalSvc goalSvc;

    @Autowired
    private CompetencySvc competencySvc;

    @Autowired
    private CredentialSvc credentialSvc;

    @Autowired
    private LearningResourceSvc learningResourceSvc;

    @Autowired
    private ModelMapper mapper;

    /**
     * Get goals with optional filtering by id and extensions.
     * @param filters filter criteria (ids, extension filters)
     * @return list of GoalDto
     */
    @PreAuthorize("hasPermission('goal', 'READ')")
    @GetMapping("/goal")
    public ResponseEntity<List<GoalDto>> getAllGoals(
                    @ModelAttribute final Goal.Filter filters) {
        List<Goal> goals = goalSvc.findGoalsWithFilters(filters);
        List<GoalDto> goalDtos = goals.stream()
                        .map(goal -> mapper.map(goal, GoalDto.class))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(goalDtos);
    }

    /**
     * Get a Goal by ID.
     *
     * @param goalId the Goal ID
     * @return ResponseEntity<GoalDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('goal', 'READ')")
    @GetMapping("/goal/{id}")
    public ResponseEntity<GoalDto> getGoalById(
            @PathVariable(value = "id") final UUID goalId)
            throws ResourceNotFoundException {
        log.debug("Get Goal id: {}", goalId);
        Goal goal = goalSvc.get(goalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goal not found for this id :: " + goalId));
        GoalDto goalDto = mapper.map(goal, GoalDto.class);
        return ResponseEntity.ok().body(goalDto);
    }

    /**
     * Create a new Goal.
     *
     * @param goalDto the Goal data
     * @return ResponseEntity<GoalDto>
     */
    @PreAuthorize("hasPermission('goal', 'CREATE')")
    @PostMapping("/goal")
    public ResponseEntity<GoalDto> createGoal(
            @Valid @RequestBody final GoalDto goalDto)
            throws ResourceNotFoundException {
        log.debug("Creating goal: {}", goalDto);
        Goal goal = mapper.map(goalDto, Goal.class);
        try {
            // Find and associate any competencies if provided
            goal = goalSvc.setCompetenciesFromIds(goal,
                    goalDto.getCompetencyIds());
            // Find and associate any credentials if provided
            goal = goalSvc.setCredentialsFromIds(goal,
                    goalDto.getCredentialIds());
            // Find and associate any learning resources if provided
            goal = goalSvc.setLearningResourcesFromIds(goal,
                    goalDto.getLearningResourceIds());
        } catch (RuntimeServiceException e) {
            log.error("Error setting related entities for goal", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        GoalDto response = mapper.map(goalSvc.save(goal), GoalDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing Goal.
     *
     * @param goalId the Goal ID
     * @param goalDto the updated Goal data
     * @return ResponseEntity<GoalDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('goal', 'UPDATE')")
    @PutMapping("/goal/{id}")
    public ResponseEntity<GoalDto> updateGoal(
            @PathVariable(value = "id") final UUID goalId,
            @Valid @RequestBody final GoalDto goalDto)
            throws ResourceNotFoundException {
        log.info("Updating Goal id: {}", goalId);
        Goal goal = goalSvc.get(goalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goal not found for this id to update :: " + goalId));
        log.debug("Update Goal: {}", goalDto);

        // Assigning values from request
        mapper.map(goalDto, goal);

        try {
            // Find and associate any competencies if provided
            goal = goalSvc.setCompetenciesFromIds(goal,
                    goalDto.getCompetencyIds());
            // Find and associate any credentials if provided
            goal = goalSvc.setCredentialsFromIds(goal,
                    goalDto.getCredentialIds());
            // Find and associate any learning resources if provided
            goal = goalSvc.setLearningResourcesFromIds(goal,
                    goalDto.getLearningResourceIds());
        } catch (RuntimeServiceException e) {
            log.error("Error setting related entities for goal", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Reset Id / Primary key from query parameter
        goal.setId(goalId);
        log.debug("Updated Goal entity: {}", goal);
        return ResponseEntity.ok(mapper.map(goalSvc.save(goal), GoalDto.class));
    }

    /**
     * Delete a Goal.
     *
     * @param goalId the Goal ID.
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('goal', 'DELETE')")
    @DeleteMapping("/goal/{id}")
    public ResponseEntity<HttpStatus> deleteGoal(
            @PathVariable(value = "id") final UUID goalId)
            throws ResourceNotFoundException {
        log.info("Deleting Goal id: {}", goalId);
        goalSvc.get(goalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goal not found for this id to delete :: " + goalId));
        goalSvc.delete(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
