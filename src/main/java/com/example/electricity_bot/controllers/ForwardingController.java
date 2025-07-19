package com.example.electricity_bot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {
    @RequestMapping("/{path:[^\\.]*}")
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}