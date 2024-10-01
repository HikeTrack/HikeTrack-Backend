package com.hiketrackbackend.hiketrackbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TestController {
    @GetMapping("/")
    public List<String> getTrails() {
        return Arrays.asList("Trail 1", "Trail 2", "Trail 4");
    }
}

