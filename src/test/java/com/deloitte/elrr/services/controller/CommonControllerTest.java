package com.deloitte.elrr.services.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;

import com.deloitte.elrr.jpa.svc.AssociationSvc;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.EmploymentRecordSvc;
import com.deloitte.elrr.jpa.svc.FacilitySvc;
import com.deloitte.elrr.jpa.svc.GoalSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.LocationSvc;
import com.deloitte.elrr.jpa.svc.OrganizationSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.deloitte.elrr.jpa.svc.PhoneSvc;
import com.deloitte.elrr.jpa.svc.ClientTokenSvc;
import com.deloitte.elrr.repository.OrganizationRepository;
import com.deloitte.elrr.services.security.JwtUtil;
import com.deloitte.elrr.services.dto.PermissionDto;
import com.deloitte.elrr.entity.types.ActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author mnelakurti
 *
 */
@Getter
@Setter
@NoArgsConstructor
class CommonControllerTest {

    @MockitoBean
    private OrganizationSvc organizationSvc;

    @MockitoBean
    private PersonSvc personSvc;

    @MockitoBean
    private PhoneSvc phoneSvc;

    @MockitoBean
    private EmailSvc emailSvc;

    @MockitoBean
    private IdentitySvc identitySvc;

    @MockitoBean
    private AssociationSvc associationSvc;

    @MockitoBean
    private LocationSvc locationSvc;

    @MockitoBean
    private FacilitySvc facilitySvc;

    @MockitoBean
    private GoalSvc goalSvc;

    @MockitoBean
    private EmploymentRecordSvc employmentRecordSvc;

    @MockitoBean
    private CompetencySvc competencySvc;

    @MockitoBean
    private PersonalCompetencySvc personalCompetencySvc;

    @MockitoBean
    private CredentialSvc credentialSvc;

    @MockitoBean
    private PersonalCredentialSvc personalCredentialSvc;

    @MockitoBean
    private LearningResourceSvc learningResourceSvc;

    @MockitoBean
    private LearningRecordSvc learningRecordSvc;

    @MockitoBean
    private ClientTokenSvc clientTokenSvc;

    @MockitoBean
    private OrganizationRepository organizationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String testJwt;

    @BeforeEach
    void setUp() {
        // Mock clientTokenSvc.existsByJwtId to always return true
        when(clientTokenSvc.existsByJwtId(any())).thenReturn(true);
    }

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

    /**
     * Get a test JWT header with a comma-separated list of resource|action
     * pairs.
     *
     * @param resourceActions a comma-separated list of resource|action pairs
     * @return String formatted JWT header
     */
    public String getTestJwtHeader(String resourceActions) {
        List<PermissionDto> permissions = new ArrayList<>();
        for (String resourceAction : resourceActions.split(",")) {
            String[] parts = resourceAction.split("\\|");
            if (parts.length == 2) {
                String resource = parts[0];
                String action = parts[1];
                permissions.add(new PermissionDto(resource, null,
                        List.of(ActionType.valueOf(action))));
            }
        }
        // TODO: we probably need to make a real entity ID here
        UUID tokenId = UUID.randomUUID();
        return String.format("Bearer %s", jwtUtil.createToken(tokenId, permissions));
    }

    /**
     * Get all headers for a request.
     *
     * @param resourceActions a comma-separated list of resource|action pairs
     * @return HttpHeaders
     */
    public HttpHeaders getHeaders(String resourceActions) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Proto", "https");
        headers.set("Authorization", this.getTestJwtHeader(resourceActions));
        return headers;
    }

    public static <T> T resultsAsObject(String results, TypeReference<T> type)
            throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return (T) mapper.readValue(results, type);
    }
}
