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

import com.deloitte.elrr.entity.Location;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.LocationDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(LocationController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class LocationControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String LOCATION_API = "/api/location";

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

    private static final UUID LOCATION_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllLocationsTest() throws Exception {

        Mockito.doReturn(getLocationList()).when(getLocationSvc()).findLocationsWithFilters(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOCATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LocationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LocationDto>>() {
                });
        assertEquals(LOCATION_ID, result.get(0).getId());
    }

    /**
     * Test getting all locations when no locations exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllLocationsEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getLocationSvc()).findLocationsWithFilters(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOCATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<LocationDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LocationDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getLocationByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getLocationList().iterator().next()))
                .when(getLocationSvc()).get(LOCATION_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOCATION_API + "/" + LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        LocationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LocationDto>() {
                });
        assertEquals(result.getId(), LOCATION_ID);
    }

    @Test
    void getLocationByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOCATION_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getLocationByIdParameterTest() throws Exception {

        Mockito.doReturn(getLocationList())
                .when(getLocationSvc()).findLocationsWithFilters(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOCATION_API + "?id=" + LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LocationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LocationDto>>() {
                });
        assertEquals(LOCATION_ID, result.get(0).getId());
    }

    @Test
    void createLocationTest() throws Exception {
        LocationDto locationDto = new LocationDto();
        locationDto.setId(LOCATION_ID);
        Mockito.doReturn(getLocationList().iterator().next())
                .when(getLocationSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOCATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(locationDto))
                .headers(getHeaders("location|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        LocationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LocationDto>() {
                });
        assertEquals(result.getId(), LOCATION_ID);
    }

    @Test
    void updateLocationTest() throws Exception {
        LocationDto locationDto = new LocationDto();
        locationDto.setId(LOCATION_ID);
        Mockito.doReturn(Optional.of(getLocationList().iterator().next()))
                .when(getLocationSvc()).get(LOCATION_ID);
        Mockito.doReturn(getLocationList().iterator().next())
                .when(getLocationSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(LOCATION_API + "/" + LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(locationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        LocationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<LocationDto>() {
                });
        assertEquals(result.getId(), LOCATION_ID);
    }

    @Test
    void deleteLocationTest() throws Exception {
        Mockito.doReturn(Optional.of(getLocationList().iterator().next()))
                .when(getLocationSvc()).get(LOCATION_ID);
        Mockito.doNothing().when(getLocationSvc()).delete(LOCATION_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(LOCATION_API + "/" + LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("location|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<LocationDto>
     */
    private static Iterable<Location> getLocationList() {
        List<Location> locationList = new ArrayList<>();
        Location location = new Location();
        location.setId(LOCATION_ID);
        locationList.add(location);

        return locationList;
    }
}
