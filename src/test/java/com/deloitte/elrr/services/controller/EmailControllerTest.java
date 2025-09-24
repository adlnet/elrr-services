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

import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.EmailDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(EmailController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class EmailControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String EMAIL_API = "/api/email";

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

    private static final UUID EMAIL_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllEmailsTest() throws Exception {

        Mockito.doReturn(getEmailList()).when(getEmailSvc()).findAll();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMAIL_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("email|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<EmailDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmailDto>>() {
                });
        assertEquals(EMAIL_ID, result.get(0).getId());
    }

    /**
     * Test getting all emails when no emails exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllEmailsEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getEmailSvc()).findAll();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMAIL_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("email|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<EmailDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmailDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getEmailByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getEmailList().iterator().next()))
                .when(getEmailSvc()).get(EMAIL_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMAIL_API + "/" + EMAIL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("email|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        EmailDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<EmailDto>() {
                });
        assertEquals(result.getId(), EMAIL_ID);
    }

    @Test
    void getEmailByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMAIL_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("email|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getEmailByIdParameterTest() throws Exception {

        Mockito.doReturn(Optional.of(getEmailList().iterator().next()))
                .when(getEmailSvc()).get(EMAIL_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(EMAIL_API + "?id=" + EMAIL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("email|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<EmailDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmailDto>>() {
                });
        assertEquals(EMAIL_ID, result.get(0).getId());
    }

    @Test
    void createEmailTest() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setId(EMAIL_ID);
        Mockito.doReturn(getEmailList().iterator().next()).when(getEmailSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(EMAIL_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(emailDto))
                .headers(this.getHeaders("email|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        EmailDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<EmailDto>() {
                });
        assertEquals(result.getId(), EMAIL_ID);
    }

    @Test
    void updateEmailTest() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setId(EMAIL_ID);
        Mockito.doReturn(Optional.of(getEmailList().iterator().next()))
                .when(getEmailSvc()).get(EMAIL_ID);
        Mockito.doReturn(getEmailList().iterator().next()).when(getEmailSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(EMAIL_API + "/" + EMAIL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(emailDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("email|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        EmailDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<EmailDto>() {
                });
        assertEquals(result.getId(), EMAIL_ID);
    }

    @Test
    void deleteEmailTest() throws Exception {
        Mockito.doReturn(Optional.of(getEmailList().iterator().next()))
                .when(getEmailSvc()).get(EMAIL_ID);
        Mockito.doNothing().when(getEmailSvc()).delete(EMAIL_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(EMAIL_API + "/" + EMAIL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("email|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<EmailDto>
     */
    private static Iterable<Email> getEmailList() {
        List<Email> emailList = new ArrayList<>();
        Email email = new Email();
        email.setId(EMAIL_ID);
        emailList.add(email);

        return emailList;
    }
}
