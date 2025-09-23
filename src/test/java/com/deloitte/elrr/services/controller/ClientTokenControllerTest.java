package com.deloitte.elrr.services.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

import com.deloitte.elrr.services.TestAppConfig;
import com.deloitte.elrr.services.dto.PermissionDto;
import com.deloitte.elrr.entity.types.ActionType;
import com.deloitte.elrr.entity.ClientToken;
import com.deloitte.elrr.services.security.JwtUtil;
import com.deloitte.elrr.services.security.MethodSecurityConfig;
import com.deloitte.elrr.services.security.SecurityConfig;


import lombok.extern.slf4j.Slf4j;

@WebMvcTest(ClientTokenController.class)
@ContextConfiguration
@AutoConfigureMockMvc(addFilters = true)
@Import({TestAppConfig.class, SecurityConfig.class, MethodSecurityConfig.class})
@Slf4j
public class ClientTokenControllerTest extends CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private HttpHeaders headers;

    private static final String TOKEN_API = "/admin/token";

    @BeforeEach
    void addHeaders() {
        headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("X-Forwarded-Proto", "https");
        // Create an admin token with ROLE_ADMIN
        headers.set("Authorization",
        "Bearer " + jwtUtil.createAdminToken("external-secret"));
    }

    @Test
    void testCreateToken() throws Exception {
        // Arrange
        PermissionDto permission1 = new PermissionDto("resource1", null,
                Arrays.asList(ActionType.CREATE, ActionType.READ));
        PermissionDto permission2 = new PermissionDto("resource2", null,
                Arrays.asList(ActionType.UPDATE, ActionType.DELETE));
        List<PermissionDto> permissions =
                Arrays.asList(permission1, permission2);

        // Act
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(TOKEN_API)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"label\":\"test-label\",\"permissions\": %s}",
                asJsonString(permissions)));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        assertEquals(200, mvcResult.getResponse().getStatus());
        // Should have a jwtId in the response
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains("jwtId"));
    }

    @Test
    void testRevokeTokenSuccess() throws Exception {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        
        // Act
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(TOKEN_API + "/" + tokenId)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        assertEquals(204, mvcResult.getResponse().getStatus());
    }

    @Test
    void testRevokeTokenNotFound() throws Exception {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        
        // Mock the service to throw RuntimeServiceException when delete is called
        org.mockito.Mockito.doThrow(new com.deloitte.elrr.exception.RuntimeServiceException("Token not found"))
                .when(getClientTokenSvc()).delete(tokenId);

        // Act
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(TOKEN_API + "/" + tokenId)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    void testListTokens() throws Exception {
        // Arrange
        UUID tokenId1 = UUID.randomUUID();
        UUID tokenId2 = UUID.randomUUID();
        
        ClientToken token1 = new ClientToken();
        token1.setId(tokenId1);
        token1.setLabel("test-token-1");
        
        ClientToken token2 = new ClientToken();
        token2.setId(tokenId2);
        token2.setLabel("test-token-2");
        
        List<ClientToken> tokens = Arrays.asList(token1, token2);
        
        // Mock the service to return test tokens
        org.mockito.Mockito.when(getClientTokenSvc().findAll()).thenReturn(tokens);

        // Act
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(TOKEN_API + "s")  // Note: endpoint is /tokens
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        assertEquals(200, mvcResult.getResponse().getStatus());
        
        // Verify response contains both tokens
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains(tokenId1.toString()));
        assertTrue(responseContent.contains("test-token-1"));
        assertTrue(responseContent.contains(tokenId2.toString()));
        assertTrue(responseContent.contains("test-token-2"));
    }

    @Test
    void testGetTokenByJwtId() throws Exception {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        UUID jwtId = UUID.randomUUID();
        ClientToken clientToken = new ClientToken();
        clientToken.setJwtId(jwtId);
        clientToken.setLabel("test-token");
        clientToken.setId(tokenId);
        // Mock the service to return the test token
        org.mockito.Mockito.when(getClientTokenSvc().findByJwtId(jwtId))
                .thenReturn(clientToken);
        // Act
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(TOKEN_API + "?jwtId=" + jwtId)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        assertEquals(200, mvcResult.getResponse().getStatus());
        // Verify that the tokenId is in the response
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains(tokenId.toString()));
    }

}
