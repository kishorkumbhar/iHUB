package com.ihub.ihub;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @RequestMapping ("/ihub")
    public String index(){
        return "Springboot Application iHUB-Demo-v2 :)";
    }
}
