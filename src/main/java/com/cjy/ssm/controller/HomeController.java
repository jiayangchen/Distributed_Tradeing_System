package com.cjy.ssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class HomeController {

    @RequestMapping("")
    public String getIndex(Model model) throws IOException {

        return "index";
    }
}
