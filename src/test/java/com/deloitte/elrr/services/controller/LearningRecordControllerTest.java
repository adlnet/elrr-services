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

import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.LearningRecordDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(LearningRecordController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class LearningRecordControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String LEARNING_RECORD_API = "/api/learningrecord";

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

    private static final UUID LEARNING_RECORD_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllLearningRecordsTest() throws Exception {
        Mockito.doReturn(getLearningRecordList()).when(getLearningRecordSvc())
                .findLearningRecordsWithFilters(any(LearningRecord.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RECORD_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LearningRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningRecordDto>>() {
                });
        assertEquals(LEARNING_RECORD_ID, result.get(0).getId());
    }

    /**
     * Test getting all learning records when no learning records exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllLearningRecordsEmptyListTest() throws Exception {
        // Mock empty list
                Mockito.doReturn(new ArrayList<>()).when(getLearningRecordSvc())
                        .findLearningRecordsWithFilters(any(LearningRecord.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RECORD_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<LearningRecordDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningRecordDto>>() {});
        assertEquals(0, results.size());
    }

    @Test
    void getLearningRecordByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getLearningRecordList().iterator().next()))
                .when(getLearningRecordSvc()).get(LEARNING_RECORD_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RECORD_API + "/" + LEARNING_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        LearningRecordDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LearningRecordDto>() {
                });
        assertEquals(result.getId(), LEARNING_RECORD_ID);
    }

    @Test
    void getLearningRecordByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RECORD_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getLearningRecordByIdParameterTest() throws Exception {
                Mockito.doReturn(getLearningRecordList()).when(getLearningRecordSvc())
                        .findLearningRecordsWithFilters(any(LearningRecord.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LEARNING_RECORD_API + "?id=" + LEARNING_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LearningRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningRecordDto>>() {
                });
        assertEquals(LEARNING_RECORD_ID, result.get(0).getId());
    }

    @Test
    void updateLearningRecordTest() throws Exception {
        LearningRecordDto learningRecordDto = new LearningRecordDto();
        learningRecordDto.setId(LEARNING_RECORD_ID);
        Mockito.doReturn(Optional.of(getLearningRecordList().iterator().next()))
                .when(getLearningRecordSvc()).get(LEARNING_RECORD_ID);
        Mockito.doReturn(getLearningRecordList().iterator().next())
                .when(getLearningRecordSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(LEARNING_RECORD_API + "/" + LEARNING_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(learningRecordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        LearningRecordDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LearningRecordDto>() {
                });
        assertEquals(result.getId(), LEARNING_RECORD_ID);
    }

    @Test
    void deleteLearningRecordTest() throws Exception {
        Mockito.doReturn(Optional.of(getLearningRecordList().iterator().next()))
                .when(getLearningRecordSvc()).get(LEARNING_RECORD_ID);
        Mockito.doNothing().when(getLearningRecordSvc())
                .delete(LEARNING_RECORD_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(LEARNING_RECORD_API + "/" + LEARNING_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("learningrecord|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<LearningRecordDto>
     */
    private static Iterable<LearningRecord> getLearningRecordList() {
        List<LearningRecord> learningRecordList = new ArrayList<>();
        LearningRecord learningRecord = new LearningRecord();
        learningRecord.setId(LEARNING_RECORD_ID);
        learningRecordList.add(learningRecord);

        return learningRecordList;
    }
}
