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

import com.deloitte.elrr.entity.Phone;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.PhoneDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(PhoneController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class PhoneControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String PHONE_API = "/api/phone";

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

    private static final UUID PHONE_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllPhonesTest() throws Exception {

        Mockito.doReturn(getPhoneList()).when(getPhoneSvc()).findAll();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PHONE_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PhoneDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<PhoneDto>>() {
                });
        assertEquals(PHONE_ID, result.get(0).getId());
    }

    /**
     * Test getting all phones when no phones exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllPhonesEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getPhoneSvc()).findAll();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PHONE_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<PhoneDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<PhoneDto>>() {
                });
        assertEquals(0, results.size());
    }

    @Test
    void getPhoneByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getPhoneList().iterator().next()))
                .when(getPhoneSvc()).get(PHONE_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PHONE_API + "/" + PHONE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        PhoneDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PhoneDto>() {
                });
        assertEquals(result.getId(), PHONE_ID);
    }

    @Test
    void getPhoneByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PHONE_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getPhoneByIdParameterTest() throws Exception {

        Mockito.doReturn(Optional.of(getPhoneList().iterator().next()))
                .when(getPhoneSvc()).get(PHONE_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PHONE_API + "?id=" + PHONE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PhoneDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<PhoneDto>>() {
                });
        assertEquals(PHONE_ID, result.get(0).getId());
    }

    @Test
    void createPhoneTest() throws Exception {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setId(PHONE_ID);
        Mockito.doReturn(getPhoneList().iterator().next()).when(getPhoneSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PHONE_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(phoneDto))
                .headers(getHeaders("phone|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        PhoneDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PhoneDto>() {
                });
        assertEquals(result.getId(), PHONE_ID);
    }

    @Test
    void updatePhoneTest() throws Exception {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setId(PHONE_ID);
        Mockito.doReturn(Optional.of(getPhoneList().iterator().next()))
                .when(getPhoneSvc()).get(PHONE_ID);
        Mockito.doReturn(getPhoneList().iterator().next()).when(getPhoneSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(PHONE_API + "/" + PHONE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(phoneDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        PhoneDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PhoneDto>() {
                });
        assertEquals(result.getId(), PHONE_ID);
    }

    @Test
    void deletePhoneTest() throws Exception {
        Mockito.doReturn(Optional.of(getPhoneList().iterator().next()))
                .when(getPhoneSvc()).get(PHONE_ID);
        Mockito.doNothing().when(getPhoneSvc()).delete(PHONE_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PHONE_API + "/" + PHONE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("phone|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Iterable<PhoneDto>
     */
    private static Iterable<Phone> getPhoneList() {
        List<Phone> phoneList = new ArrayList<>();
        Phone phone = new Phone();
        phone.setId(PHONE_ID);
        phoneList.add(phone);

        return phoneList;
    }
}
