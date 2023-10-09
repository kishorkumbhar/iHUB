package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @RequestMapping ("/iHUB")
    public String index(){
        return "Springboot Application iHUB-Demo :)";
    }
}
