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

import com.deloitte.elrr.entity.Organization;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.OrganizationDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(OrganizationController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class OrganizationControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String ORGANIZATION_API = "/api/organization";

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

    private static final UUID ORGANIZATION_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllOrganizationsTest() throws Exception {

        Mockito.doReturn(getOrganizationList()).when(getOrganizationSvc())
                .findOrganizationsWithFilters(any(Organization.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ORGANIZATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<OrganizationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<OrganizationDto>>() {
                });
        assertEquals(ORGANIZATION_ID, result.get(0).getId());
    }

    /**
     * Test getting all organizations when no organizations exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllOrganizationsEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getOrganizationSvc()).findAll();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ORGANIZATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<OrganizationDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<OrganizationDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getOrganizationByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getOrganizationList().iterator().next()))
                .when(getOrganizationSvc()).get(ORGANIZATION_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ORGANIZATION_API + "/" + ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        OrganizationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<OrganizationDto>() {
                });
        assertEquals(result.getId(), ORGANIZATION_ID);
    }

    @Test
    void getOrganizationByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ORGANIZATION_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getOrganizationByIdParameterTest() throws Exception {

        Mockito.doReturn(getOrganizationList()).when(getOrganizationSvc())
                .findOrganizationsWithFilters(any(Organization.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ORGANIZATION_API + "?id=" + ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<OrganizationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<OrganizationDto>>() {
                });
        assertEquals(ORGANIZATION_ID, result.get(0).getId());
    }

    @Test
    void createOrganizationTest() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(ORGANIZATION_ID);
        organizationDto.setName("Test Org");
        Mockito.doReturn(getOrganizationList().iterator().next())
                .when(getOrganizationSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ORGANIZATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(organizationDto))
                .headers(getHeaders("organization|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        OrganizationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<OrganizationDto>() {
                });
        assertEquals(result.getId(), ORGANIZATION_ID);
    }

    @Test
    void createOrganizationTestBadArg() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(ORGANIZATION_ID);
        Mockito.doReturn(getOrganizationList().iterator().next())
                .when(getOrganizationSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ORGANIZATION_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(organizationDto))
                .headers(getHeaders("organization|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void updateOrganizationTest() throws Exception {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(ORGANIZATION_ID);
        organizationDto.setName("Test Org");
        Mockito.doReturn(Optional.of(getOrganizationList().iterator().next()))
                .when(getOrganizationSvc()).get(ORGANIZATION_ID);
        Mockito.doReturn(getOrganizationList().iterator().next())
                .when(getOrganizationSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(ORGANIZATION_API + "/" + ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(organizationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        OrganizationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<OrganizationDto>() {
                });
        assertEquals(result.getId(), ORGANIZATION_ID);
    }

    @Test
    void deleteOrganizationTest() throws Exception {
        Mockito.doReturn(Optional.of(getOrganizationList().iterator().next()))
                .when(getOrganizationSvc()).get(ORGANIZATION_ID);
        Mockito.doNothing().when(getOrganizationSvc()).delete(ORGANIZATION_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(ORGANIZATION_API + "/" + ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("organization|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<OrganizationDto>
     */
    private static Iterable<Organization> getOrganizationList() {
        List<Organization> organizationList = new ArrayList<>();
        Organization organization = new Organization();
        organization.setId(ORGANIZATION_ID);
        organizationList.add(organization);

        return organizationList;
    }
}
