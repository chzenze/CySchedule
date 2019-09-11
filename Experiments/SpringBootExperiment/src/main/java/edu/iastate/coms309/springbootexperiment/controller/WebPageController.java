package edu.iastate.coms309.springbootexperiment.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class WebPageController {

    @RequestMapping(value = "/index")
    public String index(Model model){

        return "index";
    }


}
