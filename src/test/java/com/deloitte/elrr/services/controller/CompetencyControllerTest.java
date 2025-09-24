package com.deloitte.elrr.services.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.services.dto.CompetencyDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.deloitte.elrr.services.TestAppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(CompetencyController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class CompetencyControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String COMP_API = "/api/competency";

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

    private static final UUID COMPETENCY_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllCompetenciesTest() throws Exception {

        Mockito.doReturn(getCompetencyList()).when(getCompetencySvc())
                .findCompetenciesWithFilters(any(Competency.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(COMP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("competency|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<CompetencyDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CompetencyDto>>() {
                });
        assertEquals(COMPETENCY_ID, result.get(0).getId());
    }

    /**
     * Test getting all competencies when no competencies exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllCompetenciesEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getCompetencySvc()).findAll();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(COMP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("competency|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<CompetencyDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CompetencyDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getCompetencyByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getCompetencyList().iterator().next()))
                .when(getCompetencySvc()).get(COMPETENCY_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(COMP_API + "/" + COMPETENCY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("competency|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        CompetencyDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CompetencyDto>() {
                });
        assertEquals(result.getId(), COMPETENCY_ID);
    }

    @Test
    void getCompetencyByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(COMP_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("competency|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getCompetencyByIdParameterTest() throws Exception {

        Mockito.doReturn(getCompetencyList()).when(getCompetencySvc())
                .findCompetenciesWithFilters(any(Competency.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(COMP_API + "?id=" + COMPETENCY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("competency|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<CompetencyDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CompetencyDto>>() {
                });
        assertEquals(COMPETENCY_ID, result.get(0).getId());
    }

    @Test
    void createCompetencyTest() throws Exception {
        CompetencyDto competencyDto = new CompetencyDto();
        competencyDto.setId(COMPETENCY_ID);
        Mockito.doReturn(getCompetencyList().iterator().next())
                .when(getCompetencySvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(COMP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(competencyDto))
                .headers(this.getHeaders("competency|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        CompetencyDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CompetencyDto>() {
                });
        assertEquals(result.getId(), COMPETENCY_ID);
    }

    @Test
    void updateCompetencyTest() throws Exception {
        CompetencyDto competencyDto = new CompetencyDto();
        competencyDto.setId(COMPETENCY_ID);
        Mockito.doReturn(Optional.of(getCompetencyList().iterator().next()))
                .when(getCompetencySvc()).get(COMPETENCY_ID);
        Mockito.doReturn(getCompetencyList().iterator().next())
                .when(getCompetencySvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(COMP_API + "/" + COMPETENCY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(competencyDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("competency|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        CompetencyDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CompetencyDto>() {
                });
        assertEquals(result.getId(), COMPETENCY_ID);
    }

    @Test
    void deleteCompetencyTest() throws Exception {
        Mockito.doReturn(Optional.of(getCompetencyList().iterator().next()))
                .when(getCompetencySvc()).get(COMPETENCY_ID);
        Mockito.doNothing().when(getCompetencySvc()).delete(COMPETENCY_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(COMP_API + "/" + COMPETENCY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("competency|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<CompetencyDto>
     */
    private static Iterable<Competency> getCompetencyList() {
        List<Competency> competencyList = new ArrayList<>();
        Competency competency = new Competency();
        competency.setId(COMPETENCY_ID);
        competencyList.add(competency);

        return competencyList;
    }
}
