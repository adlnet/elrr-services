package com.deloitte.elrr.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;

public class FilterTest {

    private final SanitizingFilter sf = new SanitizingFilter();
    private WrappedHttp http;

    private static final String ANYTHING = "anything";
    private static final String UNWISE_NAP = "{Unwise: nap}";

    @Test
    void testIllegalBodyNotJson() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        http = new WrappedHttp(req, "not allowed");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertEquals(res.getStatus(), 400);
        assertTrue(res.isCommitted());
    }

    @Test
    void testeeIllegalBodyNotJson() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(req, res, chain);
        assertFalse(res.isCommitted());
    }

    @Test
    void testIllegalBodyWhitelist() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        // not allowed illegal \0
        http = new WrappedHttp(req, "{Unwise: afsd,.e\0nab}");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertTrue(res.isCommitted());
    }

    @Test
    void testIllegalParam() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("file./iofa\0je%00\\0/0/00efwho", ANYTHING);
        http = new WrappedHttp(req, UNWISE_NAP);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertEquals(res.getStatus(), 400);
        assertTrue(res.isCommitted());
    }

    @Test
    void testIllegalParamValue() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter(ANYTHING, "file./iofaje%00\0/0/00efwho");
        http = new WrappedHttp(req, UNWISE_NAP);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertTrue(res.isCommitted());
    }

    @Test
    void testSanatizerOk() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter(ANYTHING, "goes");
        http = new WrappedHttp(req, UNWISE_NAP);

        // next lines are simply to increase coverage of wrappedhttp
        http.getInputStream().available();
        http.getInputStream().isReady();
        http.getInputStream().read();

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertFalse(res.isCommitted());
    }
}
