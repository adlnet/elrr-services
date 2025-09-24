package com.deloitte.elrr.services.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

import com.deloitte.elrr.entity.Association;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.EmploymentRecord;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Organization;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCompetency;
import com.deloitte.elrr.entity.PersonalCredential;
import com.deloitte.elrr.entity.Phone;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.AssociationDto;
import com.deloitte.elrr.services.dto.CompetencyDto;
import com.deloitte.elrr.services.dto.CredentialDto;
import com.deloitte.elrr.services.dto.EmailDto;
import com.deloitte.elrr.services.dto.EmploymentRecordDto;
import com.deloitte.elrr.services.dto.IdentityDto;
import com.deloitte.elrr.services.dto.LearningRecordDto;
import com.deloitte.elrr.services.dto.LearningResourceDto;
import com.deloitte.elrr.services.dto.OrganizationDto;
import com.deloitte.elrr.services.dto.PersonDto;
import com.deloitte.elrr.services.dto.PersonalQualificationDto;
import com.deloitte.elrr.services.dto.PhoneDto;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@WebMvcTest(PersonController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class PersonControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private HttpHeaders headers;

    private static final String PERSON_API = "/api/person";

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

    private static final UUID PERSON_ID = UUID.randomUUID();
    private static final UUID IDENTITY_ID = UUID.randomUUID();
    private static final UUID PHONE_ID = UUID.randomUUID();
    private static final UUID EMAIL_ID = UUID.randomUUID();
    private static final UUID COMPETENCY_ID = UUID.randomUUID();
    private static final UUID CREDENTIAL_ID = UUID.randomUUID();
    private static final UUID LEARNING_RESOURCE_ID = UUID.randomUUID();
    private static final UUID MILITARY_RECORD_ID = UUID.randomUUID();
    private static final UUID EMPLOYER_ID = UUID.randomUUID();
    private static final UUID EMPLOYMENT_RECORD_ID = UUID.randomUUID();
    private static final UUID ORGANIZATION_ID = UUID.randomUUID();
    private static final UUID ASSOCIATION_ID = UUID.randomUUID();

    /**
     *
     * @throws Exception
     */
    @Test
    void getAllPersonsTest() throws Exception {

        Mockito.doReturn(getPersonList()).when(getPersonSvc())
                .findPersonsWithFilters(any(Person.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<PersonDto>>() {
                });
        assertEquals(PERSON_ID, result.get(0).getId());
    }

    /**
     * Test getting all persons when no persons exist - should return 200 with empty array
     *
     * @throws Exception
     */
    @Test
    void getAllPersonsEmptyListTest() throws Exception {
        // Mock empty list
        Mockito.doReturn(new ArrayList<>()).when(getPersonSvc())
                .findPersonsWithFilters(any(Person.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());

        // Verify empty array is returned
        List<PersonDto> results = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<PersonDto>>() {});
        assertEquals(0, results.size());
    }

    @Test
    void getPersonByIdTest() throws Exception {

        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        PersonDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PersonDto>() {
                });
        assertEquals(result.getId(), PERSON_ID);
    }

    @Test
    void getPersonByIdErrorTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void getPersonByIdParameterTest() throws Exception {
        Mockito.doReturn(getPersonList()).when(getPersonSvc())
                .findPersonsWithFilters(any(Person.Filter.class));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "?id=" + PERSON_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<PersonDto>>() {
                });
        assertEquals(PERSON_ID, result.get(0).getId());
    }

    @Test
    void createPersonTest() throws Exception {
        PersonDto personDto = new PersonDto();
        personDto.setId(PERSON_ID);
        Mockito.doReturn(getTestPerson()).when(getPersonSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(personDto))
                .headers(this.getHeaders("person|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(201, mvcResult.getResponse().getStatus());
        PersonDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PersonDto>() {
                });
        assertEquals(result.getId(), PERSON_ID);
    }

    @Test
    void updatePersonTest() throws Exception {
        PersonDto personDto = new PersonDto();
        personDto.setId(PERSON_ID);
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        Mockito.doReturn(getTestPerson()).when(getPersonSvc())
                .save(any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(PERSON_API + "/" + PERSON_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(personDto))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        PersonDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PersonDto>() {
                });
        assertEquals(result.getId(), PERSON_ID);
    }

    @Test
    void deletePersonTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        Mockito.doNothing().when(getPersonSvc()).delete(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|DELETE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /*
     * IDENTITY
     */

    @Test
    void getIdentitiesTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/identity")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<IdentityDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<IdentityDto>>() {
                });
        assertEquals(IDENTITY_ID, result.get(0).getId());
    }

    @Test
    void addIdentityToPersonTest() throws Exception {
        IdentityDto identityDto = new IdentityDto();
        identityDto.setId(IDENTITY_ID);
        Person mockPerson = getTestPerson();
        mockPerson.setIdentities(new HashSet<Identity>());
        Mockito.doReturn(Optional.of(mockPerson)).when(getPersonSvc())
                .get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/identity")
                .content(asJsonString(identityDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|UPDATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<IdentityDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<IdentityDto>>() {
                });
        assertEquals(IDENTITY_ID, result.get(0).getId());
    }

    @Test
    void deleteIdentityTest() throws Exception {
        Person mockPerson = getTestPerson();
        Identity mockIdentity = mockPerson.getIdentities().iterator().next();
        Mockito.when(getIdentitySvc().get(IDENTITY_ID))
                .thenReturn(Optional.of(mockIdentity));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID + "/identity/"
                    + IDENTITY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|UPDATE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /*
     * PHONE
     */
    @Test
    void getAllPhonesTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/phone")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
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
    void postPersonPhoneTest() throws Exception {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setId(PHONE_ID);
        Person mockPerson = getTestPerson();
        mockPerson.setPhoneNumbers(new HashSet<Phone>());

        Mockito.doReturn(Optional.of(mockPerson)).when(getPersonSvc())
                .get(PERSON_ID);
        Mockito.when(getPhoneSvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/phone")
                .content(asJsonString(phoneDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/phone|ASSOCIATE,phone|CREATE"));
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
    void associatePhoneWithPersonTest() throws Exception {
        Person mockPerson = getTestPerson();
        Phone mockPhone = mockPerson.getPhoneNumbers().iterator().next();
        mockPerson.setPhoneNumbers(new HashSet<Phone>());

        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getPhoneSvc().get(PHONE_ID))
                .thenReturn(Optional.of(mockPhone));
        Mockito.when(getPersonSvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/phone/" + PHONE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/phone|ASSOCIATE"));
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
    void removePhoneFromPersonTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID + "/phone/" + PHONE_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/phone|DISASSOCIATE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /*
     * EMAIL
     */
    @Test
    void getAllEmailsTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/email")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
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
    void postPersonEmailTest() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setId(EMAIL_ID);
        Person mockPerson = getTestPerson();
        mockPerson.setEmailAddresses(new HashSet<Email>());

        Mockito.doReturn(Optional.of(mockPerson)).when(getPersonSvc())
                .get(PERSON_ID);
        Mockito.when(getEmailSvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/email")
                .content(asJsonString(emailDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/email|ASSOCIATE,email|CREATE"));
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
    void associateEmailWithPersonTest() throws Exception {
        Person mockPerson = getTestPerson();
        Email mockEmail = mockPerson.getEmailAddresses().iterator().next();
        mockPerson.setEmailAddresses(new HashSet<Email>());

        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getEmailSvc().get(EMAIL_ID))
                .thenReturn(Optional.of(mockEmail));
        Mockito.when(getPersonSvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/email/" + EMAIL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/email|ASSOCIATE"));
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
    void removeEmailFromPersonTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID + "/email/" + EMAIL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/email|DISASSOCIATE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /*
     * COMPETENCY
     */
    @Test
    void getCompetenciesTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/competency")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonalQualificationDto<CompetencyDto>> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<
                    PersonalQualificationDto<CompetencyDto>>>() { });
        assertEquals(COMPETENCY_ID, result.get(0).getQualification().getId());
    }

    @Test
    void getCompetencyTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/competency/"
                        + COMPETENCY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        PersonalQualificationDto<CompetencyDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PersonalQualificationDto<CompetencyDto>>() {
                });
        assertEquals(COMPETENCY_ID, result.getQualification().getId());
    }

    @Test
    void associateCompetencyTest() throws Exception {
        CompetencyDto competencyDto = new CompetencyDto();
        competencyDto.setId(COMPETENCY_ID);
        PersonalQualificationDto<CompetencyDto> pqd
            = new PersonalQualificationDto<CompetencyDto>(competencyDto, true);

        Person mockPerson = getTestPerson();
        PersonalCompetency pc = mockPerson.getCompetencies().iterator().next();
        mockPerson.setCompetencies(new HashSet<PersonalCompetency>());

        Mockito.doReturn(Optional.of(mockPerson)).when(getPersonSvc())
                .get(PERSON_ID);

        Mockito.when(getCompetencySvc().get(COMPETENCY_ID))
                .thenReturn(Optional.of(pc.getCompetency()));
        Mockito.when(getPersonalCompetencySvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/competency/"
                        + COMPETENCY_ID)
                .content(asJsonString(pqd))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/competency|ASSOCIATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonalQualificationDto<CompetencyDto>> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<
                    PersonalQualificationDto<CompetencyDto>>>() { });
        assertEquals(COMPETENCY_ID, result.get(0).getQualification().getId());
        assertTrue(result.get(0).getHasRecord());
    }

    @Test
    void updateCompetencyAssociationTest() throws Exception {
        CompetencyDto competencyDto = new CompetencyDto();
        competencyDto.setId(COMPETENCY_ID);
        PersonalQualificationDto<CompetencyDto> pqd
            = new PersonalQualificationDto<CompetencyDto>(competencyDto, false);

        Person mockPerson = getTestPerson();
        PersonalCompetency pc = mockPerson.getCompetencies().iterator().next();

        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getCompetencySvc().get(COMPETENCY_ID))
                .thenReturn(Optional.of(pc.getCompetency()));
        Mockito.when(getPersonalCompetencySvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(PERSON_API + "/" + PERSON_ID + "/competency/"
                        + COMPETENCY_ID)
                .content(asJsonString(pqd))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/competency|ASSOCIATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonalQualificationDto<CompetencyDto>> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<
                    PersonalQualificationDto<CompetencyDto>>>() { });
        assertEquals(COMPETENCY_ID, result.get(0).getQualification().getId());
        assertFalse(result.get(0).getHasRecord());
    }

    @Test
    void deleteCompetencyAssociationTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID + "/competency/"
                        + COMPETENCY_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/competency|DISASSOCIATE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /*
     * CREDENTIAL
     */
    @Test
    void getCredentialsTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/credential")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonalQualificationDto<CredentialDto>> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<
                    PersonalQualificationDto<CredentialDto>>>() { });
        assertEquals(CREDENTIAL_ID, result.get(0).getQualification().getId());
    }

    @Test
    void getCredentialTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/credential/"
                        + CREDENTIAL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        PersonalQualificationDto<CredentialDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<PersonalQualificationDto<CredentialDto>>() {
                });
        assertEquals(CREDENTIAL_ID, result.getQualification().getId());
    }

    @Test
    void associateCredentialTest() throws Exception {
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setId(CREDENTIAL_ID);
        PersonalQualificationDto<CredentialDto> pqd
            = new PersonalQualificationDto<CredentialDto>(
                credentialDto, true);

        Person mockPerson = getTestPerson();
        PersonalCredential pc = mockPerson.getCredentials().iterator().next();
        mockPerson.setCredentials(new HashSet<PersonalCredential>());

        Mockito.doReturn(Optional.of(mockPerson)).when(getPersonSvc())
                .get(PERSON_ID);

        Mockito.when(getCredentialSvc().get(CREDENTIAL_ID))
                .thenReturn(Optional.of(pc.getCredential()));
        Mockito.when(getPersonalCredentialSvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/credential/"
                        + CREDENTIAL_ID)
                .content(asJsonString(pqd))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person/credential|ASSOCIATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonalQualificationDto<CredentialDto>> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<
                    PersonalQualificationDto<CredentialDto>>>() { });
        assertEquals(CREDENTIAL_ID, result.get(0).getQualification().getId());
        assertTrue(result.get(0).getHasRecord());
    }

    @Test
    void updateCredentialAssociationTest() throws Exception {
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setId(CREDENTIAL_ID);
        PersonalQualificationDto<CredentialDto> pqd
            = new PersonalQualificationDto<CredentialDto>(
                credentialDto, false);

        Person mockPerson = getTestPerson();
        PersonalCredential pc = mockPerson.getCredentials().iterator().next();

        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getCredentialSvc().get(CREDENTIAL_ID))
                .thenReturn(Optional.of(pc.getCredential()));
        Mockito.when(getPersonalCredentialSvc().save(any()))
                .thenAnswer(i -> i.getArgument(0));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(PERSON_API + "/" + PERSON_ID + "/credential/"
                        + CREDENTIAL_ID)
                .content(asJsonString(pqd))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person/credential|ASSOCIATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<PersonalQualificationDto<CredentialDto>> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<
                    PersonalQualificationDto<CredentialDto>>>() { });
        assertEquals(CREDENTIAL_ID, result.get(0).getQualification().getId());
        assertFalse(result.get(0).getHasRecord());
    }

    @Test
    void deleteCredentialAssociationTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID + "/credential/"
                        + CREDENTIAL_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person/credential|DISASSOCIATE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /*
     * LEARNING RECORD
     */
    @Test
    void getLearningRecordsTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/learningrecord")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LearningRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningRecordDto>>() {
                });
        assertEquals(LEARNING_RESOURCE_ID,
                result.get(0).getLearningResource().getId());
        assertEquals(LearningStatus.ATTEMPTED, result.get(0).getRecordStatus());
    }

    @Test
    void addLearningRecordTest() throws Exception {
        Person mockPerson = getTestPerson();
        LearningResource mockLearningResource = mockPerson.getLearningRecords()
                .iterator().next().getLearningResource();

        LearningResourceDto learningResourceDto = new LearningResourceDto();
        learningResourceDto.setId(LEARNING_RESOURCE_ID);
        learningResourceDto.setIri("http://test.edlm/resources/class1");
        learningResourceDto.setTitle("Class 1");
        LearningRecordDto learningRecordDto = new LearningRecordDto();
        learningRecordDto.setLearningResource(learningResourceDto);
        learningRecordDto.setRecordStatus(LearningStatus.ATTEMPTED);

        mockPerson.setLearningRecords(new HashSet<LearningRecord>());
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getLearningResourceSvc().get(LEARNING_RESOURCE_ID))
                .thenReturn(Optional.of(mockLearningResource));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/learningrecord")
                .content(asJsonString(learningRecordDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("learningrecord|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<LearningRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LearningRecordDto>>() {
                });
        assertEquals(LEARNING_RESOURCE_ID,
                result.get(0).getLearningResource().getId());
    }

    /*
     * EMPLOYMENT RECORD
     */
    @Test
    void getEmploymentRecordsTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/employmentrecord")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<EmploymentRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmploymentRecordDto>>() {
                });
        EmploymentRecordDto employmentRecordDto = result.get(0);
        assertEquals(EMPLOYMENT_RECORD_ID, employmentRecordDto.getId());
        assertEquals(EMPLOYER_ID,
                employmentRecordDto.getEmployerOrganization().getId());
    }

    @Test
    void addEmploymentRecordTest() throws Exception {
        Person mockPerson = getTestPerson();
        EmploymentRecord mockEmploymentRecord = mockPerson
                .getEmploymentRecords().iterator().next();

        OrganizationDto employerDto = new OrganizationDto();
        employerDto.setId(EMPLOYER_ID);
        employerDto.setName("EmploymentCorp");
        EmploymentRecordDto employmentRecordDto = new EmploymentRecordDto();
        employmentRecordDto.setEmployerOrganization(employerDto);
        employmentRecordDto.setId(EMPLOYMENT_RECORD_ID);

        mockPerson.setEmploymentRecords(new HashSet<EmploymentRecord>());
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getOrganizationSvc().get(EMPLOYER_ID))
                .thenReturn(Optional
                        .of(mockEmploymentRecord.getEmployerOrganization()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/employmentrecord")
                .content(asJsonString(employmentRecordDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("employmentrecord|CREATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<EmploymentRecordDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmploymentRecordDto>>() {
                });
        assertEquals(EMPLOYMENT_RECORD_ID, result.get(0).getId());
        assertEquals(EMPLOYER_ID,
                result.get(0).getEmployerOrganization().getId());
    }

    /*
     * ASSOCIATION
     */
    @Test
    void getOrganizationsByPersonTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/organization")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<AssociationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<AssociationDto>>() {
                });
        assertEquals(ASSOCIATION_ID, result.get(0).getId());
        assertEquals("MEMBER", result.get(0).getAssociationType());
        assertEquals(ORGANIZATION_ID, result.get(0).getOrganization().getId());
        assertEquals("Assoc Org", result.get(0).getOrganization().getName());
    }

    @Test
    void getAssociationByPersonAndOrgTest() throws Exception {
        Mockito.doReturn(Optional.of(getTestPerson()))
                .when(getPersonSvc()).get(PERSON_ID);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(PERSON_API + "/" + PERSON_ID + "/organization/"
                        + ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person|READ"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        AssociationDto result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<AssociationDto>() {
                });
        assertEquals(ASSOCIATION_ID, result.getId());
        assertEquals("MEMBER", result.getAssociationType());
        assertEquals(ORGANIZATION_ID, result.getOrganization().getId());
        assertEquals("Assoc Org", result.getOrganization().getName());
    }

    @Test
    void associateOrgTest() throws Exception {

        Person mockPerson = getTestPerson();
        Association association = mockPerson.getAssociations().iterator()
                .next();
        Organization organization = association.getOrganization();
        mockPerson.setAssociations(new HashSet<Association>());

        AssociationDto associationDto = new AssociationDto();
        String assocType = "PROFESSIONAL ORG";
        associationDto.setAssociationType(assocType);

        Mockito.doReturn(Optional.of(mockPerson)).when(getPersonSvc())
                .get(PERSON_ID);

        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getOrganizationSvc().get(ORGANIZATION_ID))
                .thenReturn(Optional.of(organization));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(PERSON_API + "/" + PERSON_ID + "/organization/"
                        + ORGANIZATION_ID)
                .content(asJsonString(associationDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person/organization|ASSOCIATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<AssociationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<AssociationDto>>() {
                });
        assertEquals(assocType, result.get(0).getAssociationType());
        assertEquals(ORGANIZATION_ID, result.get(0).getOrganization().getId());
        assertEquals("Assoc Org", result.get(0).getOrganization().getName());
    }

    @Test
    void updateOrgAssociationTest() throws Exception {
        AssociationDto associationDto = new AssociationDto();
        String assocType = "NEWASSOC";
        associationDto.setAssociationType(assocType);

        Person mockPerson = getTestPerson();
        Organization mockOrganization = mockPerson.getAssociations().iterator()
                .next().getOrganization();

        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));
        Mockito.when(getOrganizationSvc().get(ORGANIZATION_ID))
                .thenReturn(Optional.of(mockOrganization));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(PERSON_API + "/" + PERSON_ID + "/organization/"
                        + ORGANIZATION_ID)
                .content(asJsonString(associationDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders("person/organization|ASSOCIATE"));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<AssociationDto> result = resultsAsObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<AssociationDto>>() {
                });
        assertEquals(assocType, result.get(0).getAssociationType());
        assertEquals(ORGANIZATION_ID, result.get(0).getOrganization().getId());
        assertEquals("Assoc Org", result.get(0).getOrganization().getName());
    }

    @Test
    void deleteOrgAssociationTest() throws Exception {
        Person mockPerson = getTestPerson();
        Mockito.when(getPersonSvc().get(PERSON_ID))
                .thenReturn(Optional.of(mockPerson));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(PERSON_API + "/" + PERSON_ID + "/organization/"
                        + ORGANIZATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(this.getHeaders(
                        "person/organization|DISASSOCIATE"));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertNotNull(mvcResult);
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    /**
     *
     * @return Person
     */
    private static Person getTestPerson() {

        Person person = new Person();
        person.setId(PERSON_ID);

        Identity identity = new Identity();
        identity.setId(IDENTITY_ID);
        identity.setPerson(person);
        person.setIdentities(Collections.singleton(identity));

        Phone phone = new Phone();
        phone.setId(PHONE_ID);
        person.setPhoneNumbers(Collections.singleton(phone));

        Email email = new Email();
        email.setId(EMAIL_ID);
        person.setEmailAddresses(Collections.singleton(email));

        Competency competency = new Competency();
        competency.setId(COMPETENCY_ID);
        PersonalCompetency personalCompetency = new PersonalCompetency(person,
                competency, false);
        Set<PersonalCompetency> comps = new HashSet<PersonalCompetency>();
        comps.add(personalCompetency);
        person.setCompetencies(comps);

        Credential credential = new Credential();
        credential.setId(CREDENTIAL_ID);
        PersonalCredential personalCredential = new PersonalCredential(person,
                credential, false);
        Set<PersonalCredential> creds = new HashSet<PersonalCredential>();
        creds.add(personalCredential);
        person.setCredentials(creds);

        LearningResource learningResource = new LearningResource();
        learningResource.setId(LEARNING_RESOURCE_ID);
        learningResource.setIri("http://test.edlm/resources/class1");
        learningResource.setTitle("Class 1");
        LearningRecord learningRecord = new LearningRecord();
        learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
        learningRecord.setLearningResource(learningResource);
        person.setLearningRecords(Collections.singleton(learningRecord));

        Organization employer = new Organization();
        employer.setId(EMPLOYER_ID);
        employer.setName("EmploymentCorp");
        EmploymentRecord employmentRecord = new EmploymentRecord();
        employmentRecord.setId(EMPLOYMENT_RECORD_ID);
        employmentRecord.setEmployerOrganization(employer);
        person.setEmploymentRecords(Collections.singleton(employmentRecord));

        Organization organization = new Organization();
        organization.setId(ORGANIZATION_ID);
        organization.setName("Assoc Org");
        Association association = new Association();
        association.setId(ASSOCIATION_ID);
        association.setOrganization(organization);
        association.setAssociationType("MEMBER");
        Set<Association> associations = new HashSet<Association>();
        associations.add(association);
        person.setAssociations(associations);

        return person;
    }

    /**
     *
     * @return Iterable<Person>
     */
    private static Iterable<Person> getPersonList() {

        List<Person> personList = new ArrayList<>();
        personList.add(getTestPerson());
        return personList;
    }
}
