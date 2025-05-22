package com.test.dps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {

    @ResponseBody
    @RequestMapping("")
    public Map<String, String> health() {
        return Map.of("message", "The Dps service is running");
    }

}
