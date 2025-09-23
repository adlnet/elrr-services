package com.deloitte.elrr.services.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.deloitte.elrr.entity.Goal;
import com.deloitte.elrr.entity.types.GoalType;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.GoalDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(GoalController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class GoalControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String GOAL_API = "/api/goal";

    /**
     * Convert object to JSON string
     *
     * @param obj the object to convert
     * @return String JSON representation
     * @throws JsonProcessingException
     */
    public static String asJsonString(final Object obj)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(obj);
    }

    private static final UUID GOAL_ID = UUID.randomUUID();
    private static final UUID PERSON_ID = UUID.randomUUID();

    /**
     * Test getting all goals
     *
     * @throws Exception
     */
    @Test
    void getAllGoalsTest() throws Exception {
        Mockito.doReturn(getGoalList()).when(getGoalSvc()).findGoalsWithFilters(
                any(com.deloitte.elrr.entity.Goal.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GOAL_API).accept(MediaType.APPLICATION_JSON).contentType(
                        MediaType.APPLICATION_JSON).headers(getHeaders(
                                "goal|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        List<GoalDto> results = resultsAsObject(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<List<GoalDto>>() {
                });

        assertEquals(2, results.size());
    }

    /**
     * Test getting goals with a specific ID
     *
     * @throws Exception
     */
    @Test
    void getAllGoalsByIdTest() throws Exception {
        // Controller now uses filter method, return single list element
        Mockito.doReturn(List.of(getGoal())).when(getGoalSvc())
                .findGoalsWithFilters(any(
                        com.deloitte.elrr.entity.Goal.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GOAL_API + "?id=" + GOAL_ID).accept(
                        MediaType.APPLICATION_JSON).contentType(
                                MediaType.APPLICATION_JSON).headers(getHeaders(
                                        "goal|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        List<GoalDto> results = resultsAsObject(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<List<GoalDto>>() {
                });

        assertEquals(1, results.size());
        assertEquals(GOAL_ID, results.get(0).getId());
    }

    /**
     * Test getting goals with a specific ID that does not exist
     *
     * @throws Exception
     */
    @Test
    void getAllGoalsByIdNotFoundTest() throws Exception {
        Mockito.doReturn(new ArrayList<>()).when(getGoalSvc())
                .findGoalsWithFilters(any(
                        com.deloitte.elrr.entity.Goal.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GOAL_API + "?id=" + GOAL_ID).accept(
                        MediaType.APPLICATION_JSON).contentType(
                                MediaType.APPLICATION_JSON).headers(getHeaders(
                                        "goal|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());
    }

    /**
     * Test getting a goal by ID
     *
     * @throws Exception
     */
    @Test
    void getGoalByIdTest() throws Exception {
        Mockito.doReturn(Optional.of(getGoal())).when(getGoalSvc()).get(
                GOAL_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GOAL_API + "/" + GOAL_ID).accept(
                        MediaType.APPLICATION_JSON).contentType(
                                MediaType.APPLICATION_JSON).headers(getHeaders(
                                        "goal|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        GoalDto result = resultsAsObject(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<GoalDto>() {
                });

        assertEquals(GOAL_ID, result.getId());
        assertEquals("Test Goal", result.getName());
    }

    /**
     * Test creating a goal
     *
     * @throws Exception
     */
    @Test
    void createGoalTest() throws Exception {
        // Arrange
        GoalDto goalDto = getGoalDto();
        Goal savedGoal = getGoal();

        // Mock the setXXXFromIds methods to return the goal unchanged
        Mockito.doReturn(savedGoal).when(getGoalSvc()).setCompetenciesFromIds(
                any(Goal.class), any());
        Mockito.doReturn(savedGoal).when(getGoalSvc()).setCredentialsFromIds(
                any(Goal.class), any());
        Mockito.doReturn(savedGoal).when(getGoalSvc())
                .setLearningResourcesFromIds(any(Goal.class), any());
        Mockito.doReturn(savedGoal).when(getGoalSvc()).save(any(Goal.class));

        // Act
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(GOAL_API).content(asJsonString(goalDto)).accept(
                        MediaType.APPLICATION_JSON).contentType(
                                MediaType.APPLICATION_JSON).headers(getHeaders(
                                        "goal|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        // Assert
        assertEquals(201, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        GoalDto result = resultsAsObject(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<GoalDto>() {
                });

        assertEquals("Test Goal", result.getName());
        assertEquals(GoalType.SELF, result.getType());

        // Verify that the setXXXFromIds methods were called
        verify(getGoalSvc()).setCompetenciesFromIds(any(Goal.class), any());
        verify(getGoalSvc()).setCredentialsFromIds(any(Goal.class), any());
        verify(getGoalSvc()).setLearningResourcesFromIds(any(Goal.class),
                any());
        verify(getGoalSvc()).save(any(Goal.class));
        // Verify that related entity ids are present on the saved goal
        assertNotNull(result.getCompetencyIds());
        assertNotNull(result.getCredentialIds());
        assertNotNull(result.getLearningResourceIds());
    }

    /**
     * Test updating a goal
     *
     * @throws Exception
     */
    @Test
    void updateGoalTest() throws Exception {
        GoalDto goalDto = getGoalDto();
        goalDto.setName("Updated Goal");
        Goal existingGoal = getGoal();
        Goal updatedGoal = getGoal();
        updatedGoal.setName("Updated Goal");

        // Mock the setXXXFromIds methods to return the goal unchanged
        Mockito.doReturn(updatedGoal).when(getGoalSvc()).setCompetenciesFromIds(
                any(Goal.class), any());
        Mockito.doReturn(updatedGoal).when(getGoalSvc()).setCredentialsFromIds(
                any(Goal.class), any());
        Mockito.doReturn(updatedGoal).when(getGoalSvc())
                .setLearningResourcesFromIds(any(Goal.class), any());
        Mockito.doReturn(Optional.of(existingGoal)).when(getGoalSvc()).get(
                GOAL_ID);
        Mockito.doReturn(updatedGoal).when(getGoalSvc()).save(any(Goal.class));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(GOAL_API + "/" + GOAL_ID).content(asJsonString(goalDto))
                .accept(MediaType.APPLICATION_JSON).contentType(
                        MediaType.APPLICATION_JSON).headers(getHeaders(
                                "goal|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        GoalDto result = resultsAsObject(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<GoalDto>() {
                });

        assertEquals("Updated Goal", result.getName());

        // Verify that the setXXXFromIds methods were called
        verify(getGoalSvc()).setCompetenciesFromIds(any(Goal.class), any());
        verify(getGoalSvc()).setCredentialsFromIds(any(Goal.class), any());
        verify(getGoalSvc()).setLearningResourcesFromIds(any(Goal.class),
                any());
        verify(getGoalSvc()).get(GOAL_ID);
        verify(getGoalSvc()).save(any(Goal.class));
    }

    /**
     * Test deleting a goal
     *
     * @throws Exception
     */
    @Test
    void deleteGoalTest() throws Exception {
        Mockito.doReturn(Optional.of(getGoal())).when(getGoalSvc()).get(
                GOAL_ID);
        Mockito.doNothing().when(getGoalSvc()).delete(GOAL_ID);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(GOAL_API + "/" + GOAL_ID).accept(
                        MediaType.APPLICATION_JSON).contentType(
                                MediaType.APPLICATION_JSON).headers(getHeaders(
                                        "goal|DELETE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     * Test getting goal by ID - not found
     *
     * @throws Exception
     */
    @Test
    void getGoalByIdNotFoundTest() throws Exception {
        Mockito.doReturn(Optional.empty()).when(getGoalSvc()).get(GOAL_ID);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GOAL_API + "/" + GOAL_ID).accept(
                        MediaType.APPLICATION_JSON).contentType(
                                MediaType.APPLICATION_JSON).headers(getHeaders(
                                        "goal|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    /**
     * Test getting all goals when no goals exist - should return 200 with empty
     * array
     *
     * @throws Exception
     */
    @Test
    void getAllGoalsEmptyListTest() throws Exception {
        Mockito.doReturn(new ArrayList<>()).when(getGoalSvc())
                .findGoalsWithFilters(any(
                        com.deloitte.elrr.entity.Goal.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(GOAL_API).accept(MediaType.APPLICATION_JSON).contentType(
                        MediaType.APPLICATION_JSON).headers(getHeaders(
                                "goal|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<GoalDto> results = resultsAsObject(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<List<GoalDto>>() {
                });
        assertEquals(0, results.size());
    }

    /**
     * Create a test Goal entity
     *
     * @return Goal
     */
    private Goal getGoal() {
        Goal goal = new Goal();
        goal.setId(GOAL_ID);
        goal.setName("Test Goal");
        goal.setDescription("Test Description");
        goal.setType(GoalType.SELF);
        goal.setStartDate(ZonedDateTime.now());
        goal.setAchievedByDate(ZonedDateTime.now().plusMonths(6));
        goal.setCompetencies(new HashSet<>());
        goal.setCredentials(new HashSet<>());
        goal.setLearningResources(new HashSet<>());
        return goal;
    }

    /**
     * Create a test GoalDto
     *
     * @return GoalDto
     */
    private GoalDto getGoalDto() {
        GoalDto goalDto = new GoalDto();
        goalDto.setPersonId(PERSON_ID);
        goalDto.setName("Test Goal");
        goalDto.setDescription("Test Description");
        goalDto.setType(GoalType.SELF);
        goalDto.setStartDate(LocalDateTime.now());
        goalDto.setAchievedByDate(LocalDateTime.now().plusMonths(6));
        goalDto.setCompetencyIds(new HashSet<>(List.of(UUID.randomUUID())));
        goalDto.setCredentialIds(new HashSet<>(List.of(UUID.randomUUID())));
        goalDto.setLearningResourceIds(new HashSet<>(List.of(UUID
                .randomUUID())));
        return goalDto;
    }

    /**
     * Create a list of test Goal entities
     *
     * @return List<Goal>
     */
    private List<Goal> getGoalList() {
        List<Goal> goals = new ArrayList<>();

        Goal goal1 = getGoal();
        goals.add(goal1);

        Goal goal2 = new Goal();
        goal2.setId(UUID.randomUUID());
        goal2.setName("Second Goal");
        goal2.setDescription("Second Description");
        goal2.setType(GoalType.ASSIGNED);
        goal2.setStartDate(ZonedDateTime.now());
        goal2.setAchievedByDate(ZonedDateTime.now().plusMonths(3));
        goal2.setCompetencies(new HashSet<>());
        goal2.setCredentials(new HashSet<>());
        goal2.setLearningResources(new HashSet<>());
        goals.add(goal2);

        return goals;
    }
}
