package com.deloitte.elrr.services.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.LearningResourceDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(LearningResourceController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class LearningResourceControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String LEARNING_RESOURCE_API = "/api/learningresource";

    /**
     *
     * @param obj
     * @return String
     * @throws JsonProcessingException
     */
    public static String asJsonString(final Object obj)
            throws JsonProcessingException {

        return new ObjectMapper().writeValueAsString(obj);

    }

    private static final UUID LEARNING_RESOURCE_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllLearningResourcesTest() throws Exception {

        Mockito.doReturn(getLearningResourceList())
                .when(getLearningResourceSvc()).findLearningResourcesWithFilters(any(LearningResource.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RESOURCE_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LearningResourceDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningResourceDto>>() {
                });
        assertEquals(LEARNING_RESOURCE_ID, result.get(0).getId());
    }

    /**
     * Test getting all learning resources when no learning resources exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllLearningResourcesEmptyListTest() throws Exception {
        // Mock empty list
                Mockito.doReturn(new ArrayList<>())
                        .when(getLearningResourceSvc())
                        .findLearningResourcesWithFilters(any(LearningResource.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RESOURCE_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<LearningResourceDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningResourceDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getLearningResourceByIdTest() throws Exception {

        Mockito.doReturn(
                Optional.of(getLearningResourceList().iterator().next()))
                .when(getLearningResourceSvc()).get(LEARNING_RESOURCE_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RESOURCE_API + "/" + LEARNING_RESOURCE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        LearningResourceDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LearningResourceDto>() {
                });
        assertEquals(result.getId(), LEARNING_RESOURCE_ID);
    }

    @Test
    void getLearningResourceByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RESOURCE_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getLearningResourceByIdParameterTest() throws Exception {

        Mockito.doReturn(getLearningResourceList())
                .when(getLearningResourceSvc()).findLearningResourcesWithFilters(any(LearningResource.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RESOURCE_API + "?id=" + LEARNING_RESOURCE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LearningResourceDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningResourceDto>>() {
                });
        assertEquals(LEARNING_RESOURCE_ID, result.get(0).getId());
    }

    @Test
    void createLearningResourceTest() throws Exception {
        LearningResourceDto learningResourceDto = new LearningResourceDto();
        learningResourceDto.setId(LEARNING_RESOURCE_ID);
        learningResourceDto.setIri("http://test.edlm/resources/class1");
        learningResourceDto.setTitle("Class 1");
        Mockito.doReturn(getLearningResourceList().iterator().next())
                .when(getLearningResourceSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LEARNING_RESOURCE_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(learningResourceDto))
                .headers(getHeaders("learningresource|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        LearningResourceDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LearningResourceDto>() {
                });
        assertEquals(result.getId(), LEARNING_RESOURCE_ID);
    }

    @Test
    void updateLearningResourceTest() throws Exception {
        LearningResourceDto learningResourceDto = new LearningResourceDto();
        learningResourceDto.setId(LEARNING_RESOURCE_ID);
        learningResourceDto.setIri("http://test.edlm/resources/class1");
        learningResourceDto.setTitle("Class 1");
        Mockito.doReturn(
                Optional.of(getLearningResourceList().iterator().next()))
                .when(getLearningResourceSvc()).get(LEARNING_RESOURCE_ID);
        Mockito.doReturn(getLearningResourceList().iterator().next())
                .when(getLearningResourceSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(LEARNING_RESOURCE_API + "/" + LEARNING_RESOURCE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(learningResourceDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        LearningResourceDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LearningResourceDto>() {
                });
        assertEquals(result.getId(), LEARNING_RESOURCE_ID);
    }

    @Test
    void deleteLearningResourceTest() throws Exception {
        Mockito.doReturn(
                Optional.of(getLearningResourceList().iterator().next()))
                .when(getLearningResourceSvc()).get(LEARNING_RESOURCE_ID);
        Mockito.doNothing().when(getLearningResourceSvc())
                .delete(LEARNING_RESOURCE_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(LEARNING_RESOURCE_API + "/" + LEARNING_RESOURCE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningresource|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<LearningResourceDto>
     */
    private static Iterable<LearningResource> getLearningResourceList() {
        List<LearningResource> learningResourceList = new ArrayList<>();
        LearningResource learningResource = new LearningResource();
        learningResource.setId(LEARNING_RESOURCE_ID);
        learningResourceList.add(learningResource);

        return learningResourceList;
    }
}
