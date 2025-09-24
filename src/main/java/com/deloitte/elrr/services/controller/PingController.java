package com.deloitte.elrr.services.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PingController {

    /**
     * Controller endpoint just to provide health check.
     *
     * @return Map<String, String>
     */
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        log.info("Inside PING Controller.........");
        pong.put("pong", "Hello 1, ELRR!");
        return pong;
    }
}
