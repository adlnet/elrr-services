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

import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.CredentialDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(CredentialController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class CredentialControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String CRED_API = "/api/credential";

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

    private static final UUID CREDENTIAL_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllCredentialsTest() throws Exception {

        Mockito.doReturn(getCredentialList()).when(getCredentialSvc())
                .findCredentialsWithFilters(any(Credential.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CRED_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("credential|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<CredentialDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CredentialDto>>() {
                });
        assertEquals(CREDENTIAL_ID, result.get(0).getId());
    }

    /**
     * Test getting all credentials when no credentials exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllCredentialsEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getCredentialSvc()).findCredentialsWithFilters(any(Credential.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CRED_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("credential|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<CredentialDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CredentialDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getCredentialByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getCredentialList().iterator().next()))
                .when(getCredentialSvc()).get(CREDENTIAL_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CRED_API + "/" + CREDENTIAL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("credential|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        CredentialDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CredentialDto>() {
                });
        assertEquals(result.getId(), CREDENTIAL_ID);
    }

    @Test
    void getCredentialByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CRED_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("credential|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getCredentialByIdParameterTest() throws Exception {

        Mockito.doReturn(getCredentialList()).when(getCredentialSvc())
                .findCredentialsWithFilters(any(Credential.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CRED_API + "?id=" + CREDENTIAL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("credential|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<CredentialDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CredentialDto>>() {
                });
        assertEquals(CREDENTIAL_ID, result.get(0).getId());
    }

    @Test
    void createCredentialTest() throws Exception {
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setId(CREDENTIAL_ID);
        Mockito.doReturn(getCredentialList().iterator().next())
                .when(getCredentialSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CRED_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(credentialDto))
                .headers(this.getHeaders("credential|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        CredentialDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CredentialDto>() {
                });
        assertEquals(result.getId(), CREDENTIAL_ID);
    }

    @Test
    void updateCredentialTest() throws Exception {
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setId(CREDENTIAL_ID);
        Mockito.doReturn(Optional.of(getCredentialList().iterator().next()))
                .when(getCredentialSvc()).get(CREDENTIAL_ID);
        Mockito.doReturn(getCredentialList().iterator().next())
                .when(getCredentialSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(CRED_API + "/" + CREDENTIAL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(credentialDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("credential|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        CredentialDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CredentialDto>() {
                });
        assertEquals(result.getId(), CREDENTIAL_ID);
    }

    @Test
    void deleteCredentialTest() throws Exception {
        Mockito.doReturn(Optional.of(getCredentialList().iterator().next()))
                .when(getCredentialSvc()).get(CREDENTIAL_ID);
        Mockito.doNothing().when(getCredentialSvc()).delete(CREDENTIAL_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(CRED_API + "/" + CREDENTIAL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("credential|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<CredentialDto>
     */
    private static Iterable<Credential> getCredentialList() {
        List<Credential> credentialList = new ArrayList<>();
        Credential credential = new Credential();
        credential.setId(CREDENTIAL_ID);
        credentialList.add(credential);

        return credentialList;
    }
}
