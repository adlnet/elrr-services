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

import com.deloitte.elrr.entity.EmploymentRecord;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.EmploymentRecordDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(EmploymentRecordController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class EmploymentRecordControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String EMPLOYMENT_RECORD_API = "/api/employmentrecord";

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

    private static final UUID EMPLOYMENT_RECORD_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllEmploymentRecordsTest() throws Exception {

        Mockito.doReturn(getEmploymentRecordList())
                .when(getEmploymentRecordSvc())
                .findEmploymentRecordsWithFilters(any(EmploymentRecord.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMPLOYMENT_RECORD_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<EmploymentRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmploymentRecordDto>>() {
                });
        assertEquals(EMPLOYMENT_RECORD_ID, result.get(0).getId());
    }

    /**
     * Test getting all employment records when no employment records exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllEmploymentRecordsEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getEmploymentRecordSvc())
                .findEmploymentRecordsWithFilters(any(EmploymentRecord.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMPLOYMENT_RECORD_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<EmploymentRecordDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmploymentRecordDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getEmploymentRecordByIdTest() throws Exception {

        Mockito.doReturn(
                Optional.of(getEmploymentRecordList().iterator().next()))
                .when(getEmploymentRecordSvc()).get(EMPLOYMENT_RECORD_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMPLOYMENT_RECORD_API + "/" + EMPLOYMENT_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        EmploymentRecordDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<EmploymentRecordDto>() {
                });
        assertEquals(result.getId(), EMPLOYMENT_RECORD_ID);
    }

    @Test
    void getEmploymentRecordByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMPLOYMENT_RECORD_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getEmploymentRecordByIdParameterTest() throws Exception {

        Mockito.doReturn(getEmploymentRecordList())
                .when(getEmploymentRecordSvc())
                .findEmploymentRecordsWithFilters(any(EmploymentRecord.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMPLOYMENT_RECORD_API + "?id=" + EMPLOYMENT_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<EmploymentRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmploymentRecordDto>>() {
                });
        assertEquals(EMPLOYMENT_RECORD_ID, result.get(0).getId());
    }

    @Test
    void updateEmploymentRecordTest() throws Exception {
        EmploymentRecordDto employmentRecordDto = new EmploymentRecordDto();
        employmentRecordDto.setId(EMPLOYMENT_RECORD_ID);
        Mockito.doReturn(
                Optional.of(getEmploymentRecordList().iterator().next()))
                .when(getEmploymentRecordSvc()).get(EMPLOYMENT_RECORD_ID);
        Mockito.doReturn(getEmploymentRecordList().iterator().next())
                .when(getEmploymentRecordSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(EMPLOYMENT_RECORD_API + "/" + EMPLOYMENT_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(employmentRecordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        EmploymentRecordDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<EmploymentRecordDto>() {
                });
        assertEquals(result.getId(), EMPLOYMENT_RECORD_ID);
    }

    @Test
    void deleteEmploymentRecordTest() throws Exception {
        Mockito.doReturn(
                Optional.of(getEmploymentRecordList().iterator().next()))
                .when(getEmploymentRecordSvc()).get(EMPLOYMENT_RECORD_ID);
        Mockito.doNothing().when(getEmploymentRecordSvc())
                .delete(EMPLOYMENT_RECORD_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(EMPLOYMENT_RECORD_API + "/" + EMPLOYMENT_RECORD_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("employmentrecord|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<EmploymentRecordDto>
     */
    private static Iterable<EmploymentRecord> getEmploymentRecordList() {
        List<EmploymentRecord> employmentRecordList = new ArrayList<>();
        EmploymentRecord employmentRecord = new EmploymentRecord();
        employmentRecord.setId(EMPLOYMENT_RECORD_ID);
        employmentRecordList.add(employmentRecord);

        return employmentRecordList;
    }
}
