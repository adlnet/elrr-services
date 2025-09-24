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

import com.deloitte.elrr.entity.Facility;
import com.deloitte.elrr.entity.Facility.Filter;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.FacilityDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(FacilityController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class FacilityControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String FACILITY_API = "/api/facility";

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

    private static final UUID FACILITY_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllFacilitysTest() throws Exception {

        Mockito.doReturn((List<Facility>) getFacilityList())
                .when(getFacilitySvc()).findFacilitiesWithFilters(any(Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(FACILITY_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<FacilityDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<FacilityDto>>() {
                });
        assertEquals(FACILITY_ID, result.get(0).getId());
    }

    /**
     * Test getting all facilities when no facilities exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllFacilitiesEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>())
                .when(getFacilitySvc()).findFacilitiesWithFilters(any(Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(FACILITY_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<FacilityDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<FacilityDto>>() {});
        assertEquals(0, results.size());
    }

    @Test
    void getFacilityByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getFacilityList().iterator().next()))
                .when(getFacilitySvc()).get(FACILITY_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(FACILITY_API + "/" + FACILITY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        FacilityDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<FacilityDto>() {
                });
        assertEquals(result.getId(), FACILITY_ID);
    }

    @Test
    void getFacilityByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(FACILITY_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getFacilityByIdParameterTest() throws Exception {
        Mockito.doReturn((List<Facility>) getFacilityList())
                .when(getFacilitySvc()).findFacilitiesWithFilters(any(Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(FACILITY_API + "?id=" + FACILITY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<FacilityDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<FacilityDto>>() {
                });
        assertEquals(FACILITY_ID, result.get(0).getId());
    }

    @Test
    void createFacilityTest() throws Exception {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setId(FACILITY_ID);
        Mockito.doReturn(getFacilityList().iterator().next())
                .when(getFacilitySvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(FACILITY_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(facilityDto))
                .headers(getHeaders("facility|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        FacilityDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<FacilityDto>() {
                });
        assertEquals(result.getId(), FACILITY_ID);
    }

    @Test
    void updateFacilityTest() throws Exception {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setId(FACILITY_ID);
        Mockito.doReturn(Optional.of(getFacilityList().iterator().next()))
                .when(getFacilitySvc()).get(FACILITY_ID);
        Mockito.doReturn(getFacilityList().iterator().next())
                .when(getFacilitySvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(FACILITY_API + "/" + FACILITY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(facilityDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        FacilityDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<FacilityDto>() {
                });
        assertEquals(result.getId(), FACILITY_ID);
    }

    @Test
    void deleteFacilityTest() throws Exception {
        Mockito.doReturn(Optional.of(getFacilityList().iterator().next()))
                .when(getFacilitySvc()).get(FACILITY_ID);
        Mockito.doNothing().when(getFacilitySvc()).delete(FACILITY_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(FACILITY_API + "/" + FACILITY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("facility|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<FacilityDto>
     */
    private static Iterable<Facility> getFacilityList() {
        List<Facility> facilityList = new ArrayList<>();
        Facility facility = new Facility();
        facility.setId(FACILITY_ID);
        facilityList.add(facility);

        return facilityList;
    }
}
